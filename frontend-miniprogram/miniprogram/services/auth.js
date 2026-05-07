const { MOCK_FLAGS } = require('./config');
const { request } = require('../utils/request');
const { createMockLoginResult } = require('../mock/auth');
const { logInfo } = require('../utils/debug');

function loginWithWechatCode(code) {
  if (MOCK_FLAGS.auth) {
    logInfo('auth', 'using mock login result');
    return Promise.resolve(createMockLoginResult(code));
  }

  return request({
    url: '/api/student/auth/login',
    method: 'POST',
    data: { code }
  });
}

module.exports = {
  loginWithWechatCode
};
