const { getReciteSummary } = require('../../services/recite');

Page({
  data: {
    recordId: '',
    planId: '',
    summary: null,
    items: []
  },

  onLoad(options) {
    this.setData({
      recordId: options.recordId || '',
      planId: options.planId || ''
    });
  },

  onShow() {
    this.loadSummary();
  },

  async loadSummary() {
    try {
      const summary = await getReciteSummary(this.data.recordId);
      this.setData({
        summary,
        items: summary.items || []
      });
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '复盘页加载失败',
        icon: 'none'
      });
    }
  },

  handleBack() {
    wx.navigateBack();
  },

  handleBackToDays() {
    wx.redirectTo({
      url: `/pages/recite-days/index?planId=${this.data.planId}`
    });
  },

  handleGoHome() {
    wx.switchTab({
      url: '/pages/study-home/index'
    });
  }
});
