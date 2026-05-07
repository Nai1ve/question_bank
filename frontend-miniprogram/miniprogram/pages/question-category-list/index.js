const { listCategories } = require('../../services/category');

function showLoadError(error, fallbackTitle) {
  wx.showToast({
    title: (error && error.message) || fallbackTitle,
    icon: 'none'
  });
}

Page({
  data: {
    title: '分类',
    parentId: '',
    directLeaves: [],
    sections: []
  },

  onLoad(options) {
    this.setData({
      parentId: options.parentId || '',
      title: options.title || '分类'
    });
  },

  onShow() {
    this.loadCategories();
  },

  async loadCategories() {
    try {
      const categories = await listCategories(this.data.parentId);
      const directLeaves = categories.filter((item) => item.isLeaf);
      const branchNodes = categories.filter((item) => !item.isLeaf);

      const sections = await Promise.all(
        branchNodes.map(async (branch) => {
          const children = await listCategories(branch.id);
          return {
            id: branch.id,
            name: branch.name,
            subtitle: branch.subtitle,
            leaves: children
          };
        })
      );

      this.setData({
        directLeaves,
        sections
      });
    } catch (error) {
      showLoadError(error, '分类内容加载失败');
    }
  },

  handleBack() {
    wx.navigateBack();
  },

  handleStartPractice(event) {
    const { id, name } = event.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/practice-filter/index?categoryId=${id}&categoryName=${name}`
    });
  }
});
