const {
  startPracticeSession,
  findActivePracticeSession,
  abandonPracticeSession
} = require('../../services/practice');
const { listTags } = require('../../services/tag');

function buildCountOptions(activeCount) {
  return [20, 40, 60, 80, 100].map((value) => ({
    value,
    label: `${value} 题`,
    className: value === activeCount ? 'filter-chip filter-chip-active' : 'filter-chip'
  }));
}

function buildModeOptions(activeMode) {
  const modes = [
    { value: 'immediate', label: '即时反馈' },
    { value: 'summary', label: '统一反馈' }
  ];

  return modes.map((item) => ({
    value: item.value,
    label: item.label,
    className: item.value === activeMode ? 'filter-chip filter-chip-active' : 'filter-chip'
  }));
}

function buildTagOptions(tags, selectedValues) {
  return tags.map((item) => ({
    value: item.name,
    label: item.name,
    className: selectedValues.indexOf(item.name) >= 0 ? 'filter-chip filter-chip-active' : 'filter-chip'
  }));
}

Page({
  data: {
    categoryId: '',
    categoryName: '',
    questionCount: 20,
    feedbackMode: 'immediate',
    selectedTags: [],
    availableTags: [],
    countOptions: buildCountOptions(20),
    modeOptions: buildModeOptions('immediate'),
    tagOptions: []
  },

  async onLoad(options) {
    const categoryName = options.categoryName || '当前分类';
    const selectedTags = [];
    const categoryId = options.categoryId || '';

    this.setData({
      categoryId,
      categoryName,
      selectedTags,
      availableTags: [],
      countOptions: buildCountOptions(20),
      modeOptions: buildModeOptions('immediate'),
      tagOptions: []
    });

    await this.loadTagOptions(categoryId);
  },

  handleBack() {
    wx.navigateBack();
  },

  handleSelectCount(event) {
    const { value } = event.currentTarget.dataset;
    const questionCount = Number(value);
    this.setData({
      questionCount,
      countOptions: buildCountOptions(questionCount)
    });
  },

  handleSelectMode(event) {
    const { value } = event.currentTarget.dataset;
    this.setData({
      feedbackMode: value,
      modeOptions: buildModeOptions(value)
    });
  },

  handleToggleTag(event) {
    const { value } = event.currentTarget.dataset;
    const selectedTags = this.data.selectedTags.slice();
    const targetIndex = selectedTags.indexOf(value);

    if (targetIndex >= 0) {
      selectedTags.splice(targetIndex, 1);
    } else {
      selectedTags.push(value);
    }

    this.setData({
      selectedTags,
      tagOptions: buildTagOptions(this.data.availableTags, selectedTags)
    });
  },

  async loadTagOptions(categoryId) {
    try {
      const availableTags = await listTags(categoryId);
      this.setData({
        availableTags,
        tagOptions: buildTagOptions(availableTags, this.data.selectedTags)
      });
    } catch (error) {
      this.setData({
        availableTags: [],
        tagOptions: []
      });
      wx.showToast({
        title: (error && error.message) || '标签加载失败',
        icon: 'none'
      });
    }
  },

  async handleStartPractice() {
    try {
      const activeSession = await findActivePracticeSession({
        entryType: 'normal',
        categoryId: this.data.categoryId
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
        entryType: 'normal',
        categoryId: this.data.categoryId,
        categoryName: this.data.categoryName,
        questionCount: this.data.questionCount,
        feedbackMode: this.data.feedbackMode,
        selectedTags: this.data.selectedTags
      });

      if (!result || !result.ok) {
        wx.showToast({
          title: (result && result.message) || '当前暂无可练习题目',
          icon: 'none'
        });
        return;
      }

      this.openPracticeSession(result.sessionId);
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '刷题会话启动失败',
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
        title: '继续上次练习',
        content: `发现一轮未完成的 ${session.categoryName} 练习，当前进度 ${session.currentSequence}/${session.totalCount}。`,
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
