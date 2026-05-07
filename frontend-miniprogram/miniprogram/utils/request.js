const { BASE_URL } = require('../services/config');
const { getAuth } = require('./auth');
const {
  nextRequestSequence,
  logInfo,
  logWarn,
  logError
} = require('./debug');

function request(options) {
  return new Promise((resolve, reject) => {
    const auth = getAuth();
    const requestSequence = nextRequestSequence();
    const header = {
      ...(options.header || {})
    };

    if (auth && auth.token && !header.Authorization) {
      header.Authorization = `Bearer ${auth.token}`;
    }

    logInfo(`request#${requestSequence}`, 'dispatch', {
      method: options.method || 'GET',
      url: `${BASE_URL}${options.url}`,
      hasAuth: !!(auth && auth.token),
      payloadKeys: options.data ? Object.keys(options.data) : []
    });

    wx.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header,
      success(res) {
        const payload = res.data || {};
        const responseRequestId = res.header
          ? (res.header['X-Request-Id'] || res.header['x-request-id'] || '')
          : '';
        if (res.statusCode >= 200 && res.statusCode < 300 && payload.success !== false) {
          logInfo(`request#${requestSequence}`, 'success', {
            statusCode: res.statusCode,
            requestId: responseRequestId,
            url: options.url
          });
          resolve(payload.data !== undefined ? payload.data : payload);
          return;
        }
        logWarn(`request#${requestSequence}`, 'business-fail', {
          statusCode: res.statusCode,
          requestId: responseRequestId,
          url: options.url,
          message: payload.message || 'Request failed'
        });
        reject(new Error(payload.message || 'Request failed'));
      },
      fail(error) {
        logError(`request#${requestSequence}`, 'network-fail', {
          url: options.url,
          message: error && error.errMsg ? error.errMsg : 'Unknown request failure'
        });
        reject(error);
      }
    });
  });
}

module.exports = {
  request
};
