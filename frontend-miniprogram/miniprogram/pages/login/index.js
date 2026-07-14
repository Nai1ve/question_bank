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
    return new Promise((resolve, reject) => {
      if (typeof wx.login !== 'function') {
        reject(new Error('当前环境不支持微信登录'));
        return;
      }

      wx.login({
        success: (res) => {
          if (res.code) {
            resolve(res.code);
            return;
          }
          reject(new Error('未获取到微信登录凭证'));
        },
        fail: () => reject(new Error('微信登录凭证获取失败'))
      });
    });
  }
});
