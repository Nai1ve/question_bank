const { loginWithWechatCode } = require('../../services/auth');
const { setAuth, getAuth } = require('../../utils/auth');

Page({
  data: {
    loading: false,
    loginButtonText: '微信登录'
  },

  onShow() {
    const auth = getAuth();
    if (auth && auth.token) {
      wx.switchTab({
        url: '/pages/study-home/index'
      });
    }
  },

  async handleWechatLogin() {
    if (this.data.loading) {
      return;
    }

    this.setData({
      loading: true,
      loginButtonText: '登录中...'
    });

    try {
      const code = await this.fetchWechatCode();
      const auth = await loginWithWechatCode(code);
      setAuth(auth);
      getApp().globalData.auth = auth;

      wx.showToast({
        title: '登录成功',
        icon: 'success'
      });

      setTimeout(() => {
        wx.switchTab({
          url: '/pages/study-home/index'
        });
      }, 200);
    } catch (error) {
      wx.showToast({
        title: '登录失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({
        loading: false,
        loginButtonText: '微信登录'
      });
    }
  },

  fetchWechatCode() {
    return new Promise((resolve) => {
      if (typeof wx.login !== 'function') {
        resolve('mock-wechat-code');
        return;
      }

      wx.login({
        success: (res) => resolve(res.code || 'mock-wechat-code'),
        fail: () => resolve('mock-wechat-code')
      });
    });
  }
});
