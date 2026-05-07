const { MOCK_FLAGS } = require('./config');
const { request } = require('../utils/request');
const { logInfo } = require('../utils/debug');

function listWrongBook(limit) {
  if (MOCK_FLAGS.wrongBook) {
    logInfo('wrong-book', 'using mock wrong-book list', { limit });
    return Promise.resolve([]);
  }

  return request({
    url: '/api/student/wrong-book',
    method: 'GET',
    data: {
      limit: limit || 20
    }
  });
}

module.exports = {
  listWrongBook
};
