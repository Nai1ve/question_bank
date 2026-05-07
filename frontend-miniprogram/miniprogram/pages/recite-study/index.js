const { getReciteStudy, completeReciteStudy } = require('../../services/recite');

Page({
  data: {
    planId: '',
    dayNumber: '',
    study: null,
    loadingMode: ''
  },

  onLoad(options) {
    this.setData({
      planId: options.planId || '',
      dayNumber: options.dayNumber || ''
    });
  },

  onShow() {
    this.loadStudy();
  },

  async loadStudy() {
    try {
      const study = await getReciteStudy(this.data.planId, this.data.dayNumber);
      this.setData({ study });
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '学习页加载失败',
        icon: 'none'
      });
    }
  },

  handleBack() {
    wx.navigateBack();
  },

  async handleStartMode(event) {
    const { mode } = event.currentTarget.dataset;
    if (this.data.loadingMode) {
      return;
    }

    this.setData({ loadingMode: mode });
    try {
      await completeReciteStudy(this.data.planId, this.data.dayNumber);
      wx.redirectTo({
        url: `/pages/recite-session/index?planId=${this.data.planId}&dayNumber=${this.data.dayNumber}&mode=${mode}`
      });
    } catch (error) {
      this.setData({ loadingMode: '' });
      wx.showToast({
        title: (error && error.message) || '开始测试失败',
        icon: 'none'
      });
    }
  }
});
