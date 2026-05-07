const { MOCK_FLAGS } = require('./config');
const { request } = require('../utils/request');
const { logInfo } = require('../utils/debug');

function listTags(categoryId) {
  if (MOCK_FLAGS.tag) {
    logInfo('tag', 'using mock tag list', {
      categoryId: categoryId || ''
    });
    return Promise.resolve(buildMockTags(categoryId));
  }

  return request({
    url: '/api/student/tags',
    method: 'GET',
    data: {
      categoryId
    }
  }).then(normalizeTagList);
}

function normalizeTagList(items) {
  if (!Array.isArray(items)) {
    return [];
  }

  return items
    .map((item) => ({
      name: item.name || '',
      questionCount: Number(item.questionCount || 0)
    }))
    .filter((item) => !!item.name);
}

function buildMockTags(categoryId) {
  let names = ['高频题', '易错题', '核心知识点'];

  if ((categoryId || '').indexOf('english') >= 0) {
    names = ['高频词汇', '易错辨析', '阅读技巧'];
  } else if ((categoryId || '').indexOf('politics') >= 0) {
    names = ['基础概念', '强化阶段', '高频选择题'];
  }

  return names.map((name) => ({
    name,
    questionCount: 0
  }));
}

module.exports = {
  listTags
};
