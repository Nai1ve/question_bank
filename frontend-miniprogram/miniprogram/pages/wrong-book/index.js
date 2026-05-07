const { listWrongBook } = require('../../services/wrong-book');

Page({
  data: {
    items: []
  },

  onShow() {
    this.loadWrongBook();
  },

  async loadWrongBook() {
    try {
      const items = await listWrongBook(20);
      this.setData({ items });
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '错题本加载失败',
        icon: 'none'
      });
    }
  },

  handleBack() {
    wx.navigateBack();
  },

  handleGoTopxx() {
    wx.navigateTo({
      url: '/pages/topxx/index'
    });
  }
});
