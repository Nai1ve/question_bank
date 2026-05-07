const AUTH_STORAGE_KEY = 'onepass-auth';

function getAuth() {
  try {
    return wx.getStorageSync(AUTH_STORAGE_KEY) || null;
  } catch (error) {
    return null;
  }
}

function setAuth(auth) {
  wx.setStorageSync(AUTH_STORAGE_KEY, auth);
}

function clearAuth() {
  wx.removeStorageSync(AUTH_STORAGE_KEY);
}

module.exports = {
  getAuth,
  setAuth,
  clearAuth
};

