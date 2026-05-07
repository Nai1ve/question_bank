const { dashboard } = require('../mock/student');
const { mockUser } = require('../mock/auth');
const { getAuth } = require('../utils/auth');
const { MOCK_FLAGS } = require('./config');
const { logInfo } = require('../utils/debug');
const { request } = require('../utils/request');

function getDashboard() {
  if (MOCK_FLAGS.student) {
    logInfo('student', 'using mock dashboard');

    const auth = getAuth();
    return Promise.resolve({
      user: auth && auth.user ? auth.user : mockUser,
      currentQuestionBank: dashboard.currentQuestionBank,
      currentRecitePlan: dashboard.currentRecitePlan,
      summaryTemplate: dashboard.summaryTemplate
    });
  }

  return request({
    url: '/api/student/dashboard',
    method: 'GET'
  });
}

module.exports = {
  getDashboard
};
