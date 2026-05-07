const { getAuth, clearAuth } = require('./utils/auth');
const { BASE_URL, MOCK_FLAGS } = require('./services/config');
const { logInfo } = require('./utils/debug');

App({
  globalData: {
    auth: null
  },

  onLaunch() {
    const auth = getAuth();
    if (!MOCK_FLAGS.auth && auth && auth.token && String(auth.token).indexOf('mock-jwt-') === 0) {
      clearAuth();
      this.globalData.auth = null;
      logInfo('runtime', 'cleared stale mock auth token before real backend login');
    } else {
      this.globalData.auth = auth;
    }
    logInfo('runtime', 'app launch config', {
      baseUrl: BASE_URL,
      mockFlags: MOCK_FLAGS
    });
  }
});
