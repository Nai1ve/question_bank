const { listVocabularyBooks } = require('../../services/vocabulary');
const { getActiveRecitePlan, createRecitePlan } = require('../../services/recite');

function buildCountOptions(activeCount) {
  return [5, 10, 20].map((value) => ({
    value,
    label: `${value} 个/天`,
    className: value === activeCount ? 'filter-chip filter-chip-active' : 'filter-chip'
  }));
}

function buildBookOptions(books, activeBookId) {
  return books.map((item) => ({
    ...item,
    className: item.id === activeBookId ? 'book-card book-card-active surface-card' : 'book-card surface-card'
  }));
}

function buildActivePlanView(plan) {
  if (!plan) {
    return null;
  }
  const completedDays = Number(plan.completedDays || 0);
  const totalDays = Number(plan.totalDays || 0);
  const progressPercent = totalDays > 0 ? Math.round((completedDays * 100) / totalDays) : 0;
  return {
    ...plan,
    progressPercent,
    progressStyle: `width: ${progressPercent}%;`
  };
}

Page({
  data: {
    books: [],
    activePlan: null,
    selectedBookId: '',
    dailyCount: 5,
    countOptions: buildCountOptions(5)
  },

  onShow() {
    this.loadPage();
  },

  async loadPage() {
    try {
      const [books, activePlan] = await Promise.all([
        listVocabularyBooks(),
        getActiveRecitePlan()
      ]);

      const activePlanView = buildActivePlanView(activePlan);
      const selectedBookId = activePlanView && activePlanView.bookId
        ? activePlanView.bookId
        : (books[0] ? books[0].id : '');
      const dailyCount = activePlanView && activePlanView.dailyCount ? activePlanView.dailyCount : 5;

      this.setData({
        books: buildBookOptions(books, selectedBookId),
        activePlan: activePlanView,
        selectedBookId,
        dailyCount,
        countOptions: buildCountOptions(dailyCount)
      });
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '背诵计划加载失败',
        icon: 'none'
      });
    }
  },

  handleBack() {
    wx.navigateBack();
  },

  handleSelectBook(event) {
    const { id } = event.currentTarget.dataset;
    this.setData({
      selectedBookId: id,
      books: buildBookOptions(this.data.books, id)
    });
  },

  handleSelectCount(event) {
    const { value } = event.currentTarget.dataset;
    const dailyCount = Number(value);
    this.setData({
      dailyCount,
      countOptions: buildCountOptions(dailyCount)
    });
  },

  async handleCreatePlan() {
    if (!this.data.selectedBookId) {
      wx.showToast({
        title: '请先选择词库',
        icon: 'none'
      });
      return;
    }

    try {
      const result = await createRecitePlan({
        bookId: this.data.selectedBookId,
        dailyCount: this.data.dailyCount
      });

      wx.showToast({
        title: '计划已生成',
        icon: 'success'
      });

      wx.navigateTo({
        url: `/pages/recite-days/index?planId=${result.planId}`
      });
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '计划创建失败',
        icon: 'none'
      });
    }
  },

  handleOpenDays() {
    const planId = this.data.activePlan && this.data.activePlan.planId;
    if (!planId) {
      return;
    }

    wx.navigateTo({
      url: `/pages/recite-days/index?planId=${planId}`
    });
  }
});
