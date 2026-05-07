const { listCategories } = require('../../services/category');

function showLoadError(error, fallbackTitle) {
  wx.showToast({
    title: (error && error.message) || fallbackTitle,
    icon: 'none'
  });
}

Page({
  data: {
    title: '题库',
    categories: []
  },

  onShow() {
    this.loadCategories();
  },

  async loadCategories() {
    try {
      const categories = await listCategories();
      this.setData({ categories });
    } catch (error) {
      showLoadError(error, '题库分类加载失败');
    }
  },

  handleBack() {
    wx.navigateBack();
  },

  handleOpenCategory(event) {
    const { id, name, isLeaf } = event.currentTarget.dataset;
    if (isLeaf) {
      wx.showToast({
        title: '下一步进入刷题筛选页',
        icon: 'none'
      });
      return;
    }

    wx.navigateTo({
      url: `/pages/question-category-list/index?parentId=${id}&title=${name}`
    });
  }
});
