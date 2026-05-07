const { MOCK_FLAGS } = require('./config');
const { request } = require('../utils/request');
const { getMockCategories } = require('../mock/category');
const { logInfo } = require('../utils/debug');

function listCategories(parentId) {
  if (MOCK_FLAGS.category) {
    logInfo('category', 'using mock categories', {
      parentId: parentId || ''
    });
    return Promise.resolve(getMockCategories(parentId).map(normalizeCategory));
  }

  return request({
    url: '/api/student/categories',
    method: 'GET',
    data: parentId ? { parentId } : {}
  }).then((items) => items.map(normalizeCategory));
}

function normalizeCategory(item) {
  return {
    id: item.id,
    name: item.name,
    subtitle: item.subtitle,
    isLeaf: typeof item.isLeaf === 'boolean' ? item.isLeaf : item.is_leaf
  };
}

module.exports = {
  listCategories
};
