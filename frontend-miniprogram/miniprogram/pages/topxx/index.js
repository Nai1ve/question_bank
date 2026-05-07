const { listCategories } = require('../../services/category');
const {
  startPracticeSession,
  findActivePracticeSession,
  abandonPracticeSession
} = require('../../services/practice');

function showPageError(error, fallbackTitle) {
  wx.showToast({
    title: (error && error.message) || fallbackTitle,
    icon: 'none'
  });
}

function createLimitOptions(activeLimit) {
  return [20, 40, 60, 80, 100].map((value) => ({
    value,
    label: `Top${value}`,
    className: value === activeLimit ? 'filter-chip filter-chip-active' : 'filter-chip'
  }));
}

function markOptions(options, activeId) {
  return options.map((item) => ({
    id: item.id,
    name: item.name,
    className: item.id === activeId ? 'filter-chip filter-chip-active' : 'filter-chip'
  }));
}

Page({
  data: {
    limitOptions: createLimitOptions(20),
    activeLimit: 20,
    rootCategories: markOptions([{ id: 'all', name: '全部分类' }], 'all'),
    secondCategories: [],
    thirdCategories: [],
    activeRootCategoryId: 'all',
    activeRootCategoryName: '全部分类',
    activeSecondCategoryId: '',
    activeSecondCategoryName: '',
    activeThirdCategoryId: '',
    activeThirdCategoryName: '',
    resolvedCategoryName: '全部分类'
  },

  async onShow() {
    try {
      await this.loadRootCategories();
    } catch (error) {
      showPageError(error, 'TopXX 分类加载失败');
    }
  },

  async loadRootCategories() {
    const rootCategories = await listCategories();
    const options = [{ id: 'all', name: '全部分类' }].concat(
      rootCategories.map((item) => ({
        id: item.id,
        name: item.name
      }))
    );

    this.setData({
      rootCategories: markOptions(options, this.data.activeRootCategoryId)
    });
  },

  handleBack() {
    wx.navigateBack();
  },

  handleSelectLimit(event) {
    const { value } = event.currentTarget.dataset;
    const activeLimit = Number(value);
    this.setData({
      activeLimit,
      limitOptions: createLimitOptions(activeLimit)
    });
  },

  async handleSelectRootCategory(event) {
    try {
      const { id, name } = event.currentTarget.dataset;

      if (id === 'all') {
        this.setData({
          rootCategories: markOptions(this.data.rootCategories, id),
          secondCategories: [],
          thirdCategories: [],
          activeRootCategoryId: 'all',
          activeRootCategoryName: '全部分类',
          activeSecondCategoryId: '',
          activeSecondCategoryName: '',
          activeThirdCategoryId: '',
          activeThirdCategoryName: '',
          resolvedCategoryName: '全部分类'
        });
        return;
      }

      const secondLevel = await listCategories(id);
      const secondOptions = [{ id: 'all', name: '全部方向' }].concat(
        secondLevel.map((item) => ({
          id: item.id,
          name: item.name
        }))
      );

      this.setData({
        rootCategories: markOptions(this.data.rootCategories, id),
        secondCategories: markOptions(secondOptions, 'all'),
        thirdCategories: [],
        activeRootCategoryId: id,
        activeRootCategoryName: name,
        activeSecondCategoryId: 'all',
        activeSecondCategoryName: '全部方向',
        activeThirdCategoryId: '',
        activeThirdCategoryName: '',
        resolvedCategoryName: name
      });
    } catch (error) {
      showPageError(error, '方向列表加载失败');
    }
  },

  async handleSelectSecondCategory(event) {
    try {
      const { id, name } = event.currentTarget.dataset;
      if (id === 'all') {
        this.setData({
          secondCategories: markOptions(this.data.secondCategories, id),
          thirdCategories: [],
          activeSecondCategoryId: id,
          activeSecondCategoryName: name,
          activeThirdCategoryId: '',
          activeThirdCategoryName: '',
          resolvedCategoryName: this.data.activeRootCategoryName
        });
        return;
      }

      const thirdLevel = await listCategories(id);
      const thirdOptions = [{ id: 'all', name: '全部小类' }].concat(
        thirdLevel.map((item) => ({
          id: item.id,
          name: item.name
        }))
      );

      this.setData({
        secondCategories: markOptions(this.data.secondCategories, id),
        thirdCategories: markOptions(thirdOptions, 'all'),
        activeSecondCategoryId: id,
        activeSecondCategoryName: name,
        activeThirdCategoryId: 'all',
        activeThirdCategoryName: '全部小类',
        resolvedCategoryName: name
      });
    } catch (error) {
      showPageError(error, '小类列表加载失败');
    }
  },

  handleSelectThirdCategory(event) {
    const { id, name } = event.currentTarget.dataset;
    const resolvedCategoryName = id === 'all' ? this.data.activeSecondCategoryName : name;

    this.setData({
      thirdCategories: markOptions(this.data.thirdCategories, id),
      activeThirdCategoryId: id,
      activeThirdCategoryName: name,
      resolvedCategoryName
    });
  },

  async handleStartTopxx() {
    const scopedCategoryId = this.data.activeThirdCategoryId && this.data.activeThirdCategoryId !== 'all'
      ? this.data.activeThirdCategoryId
      : this.data.activeSecondCategoryId && this.data.activeSecondCategoryId !== 'all'
        ? this.data.activeSecondCategoryId
        : this.data.activeRootCategoryId && this.data.activeRootCategoryId !== 'all'
          ? this.data.activeRootCategoryId
          : '';

    try {
      const activeSession = await findActivePracticeSession({
        entryType: 'topxx',
        categoryId: scopedCategoryId
      });

      if (activeSession && activeSession.sessionId) {
        const shouldResume = await this.confirmResumeSession(activeSession);
        if (shouldResume) {
          this.openPracticeSession(activeSession.sessionId);
          return;
        }

        await abandonPracticeSession(activeSession.sessionId);
      }

      const result = await startPracticeSession({
        entryType: 'topxx',
        categoryId: scopedCategoryId,
        categoryName: this.data.resolvedCategoryName,
        questionCount: this.data.activeLimit,
        feedbackMode: 'immediate',
        selectedTags: []
      });

      if (!result || !result.ok) {
        wx.showToast({
          title: (result && result.message) || '当前没有可复练错题',
          icon: 'none'
        });
        return;
      }

      this.openPracticeSession(result.sessionId);
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '错题复练启动失败',
        icon: 'none'
      });
    }
  },

  openPracticeSession(sessionId) {
    wx.navigateTo({
      url: `/pages/practice-session/index?sessionId=${sessionId}`
    });
  },

  confirmResumeSession(session) {
    return new Promise((resolve) => {
      wx.showModal({
        title: '继续上次复练',
        content: `发现一轮未完成的 ${session.categoryName} 复练，当前进度 ${session.currentSequence}/${session.totalCount}。`,
        confirmText: '继续练习',
        cancelText: '重新开始',
        success(res) {
          resolve(!!res.confirm);
        },
        fail() {
          resolve(false);
        }
      });
    });
  }
});
