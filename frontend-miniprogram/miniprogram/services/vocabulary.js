const { MOCK_FLAGS } = require('./config');
const { request } = require('../utils/request');
const { logInfo } = require('../utils/debug');

function listVocabularyBooks() {
  if (MOCK_FLAGS.vocabulary) {
    logInfo('vocabulary', 'using mock vocabulary book list');
    return Promise.resolve([
      {
        id: 'kaoyan-core-vocab',
        name: '考研核心词汇',
        description: '基础高频单词',
        totalWords: 12
      }
    ]);
  }

  return request({
    url: '/api/student/vocabulary/books',
    method: 'GET'
  });
}

module.exports = {
  listVocabularyBooks
};
