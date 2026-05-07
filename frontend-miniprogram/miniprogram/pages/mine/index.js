const { getDashboard } = require('../../services/student');
const { clearAuth } = require('../../utils/auth');

function showPageError(error, fallbackTitle) {
  wx.showToast({
    title: (error && error.message) || fallbackTitle,
    icon: 'none'
  });
}

function isAuthorizationError(error) {
  const message = error && error.message ? error.message : '';
  return message.indexOf('authorization') >= 0;
}

function syncTabBar(selected) {
  if (typeof this.getTabBar === 'function' && this.getTabBar()) {
    this.getTabBar().setSelected(selected);
  }
}

Page({
  data: {
    user: {
      displayName: '微信用户',
      avatarUrl: ''
    },
    avatarText: '微',
    currentQuestionBank: '',
    currentRecitePlan: '',
    summaryTemplate: {
      title: '我的刷题总结',
      templateName: '',
      blocks: []
    }
  },

  onShow() {
    syncTabBar.call(this, 1);
    this.loadDashboard();
  },

  async loadDashboard() {
    try {
      const dashboard = await getDashboard();
      this.setData({
        user: dashboard.user,
        avatarText: (dashboard.user.displayName || '微信用户').slice(0, 1),
        currentQuestionBank: dashboard.currentQuestionBank,
        currentRecitePlan: dashboard.currentRecitePlan,
        summaryTemplate: dashboard.summaryTemplate
      });
    } catch (error) {
      showPageError(error, '学习看板加载失败');
      if (isAuthorizationError(error)) {
        clearAuth();
        getApp().globalData.auth = null;
        setTimeout(() => {
          wx.reLaunch({
            url: '/pages/login/index'
          });
        }, 250);
      }
    }
  },

  handleOpenWrongBook() {
    wx.navigateTo({
      url: '/pages/wrong-book/index'
    });
  },

  handleOpenQuestionBank() {
    wx.navigateTo({
      url: '/pages/question-bank-list/index'
    });
  },

  handleOpenRecite() {
    wx.navigateTo({
      url: '/pages/recite-plan/index'
    });
  }
});
