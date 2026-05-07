const { MOCK_FLAGS } = require('./config');
const { request } = require('../utils/request');
const { logInfo } = require('../utils/debug');

function getActiveRecitePlan() {
  if (MOCK_FLAGS.recite) {
    logInfo('recite', 'using mock active recite plan');
    return Promise.resolve(null);
  }

  return request({
    url: '/api/student/recite/plans/active',
    method: 'GET'
  });
}

function createRecitePlan(payload) {
  if (MOCK_FLAGS.recite) {
    logInfo('recite', 'using mock create recite plan', payload);
    return Promise.resolve({
      planId: 1,
      bookId: payload.bookId,
      bookName: '考研核心词汇',
      dailyCount: payload.dailyCount,
      totalDays: 3
    });
  }

  return request({
    url: '/api/student/recite/plans',
    method: 'POST',
    data: payload
  });
}

function listRecitePlanDays(planId) {
  if (MOCK_FLAGS.recite) {
    logInfo('recite', 'using mock recite days', { planId });
    return Promise.resolve({
      plan: {
        planId,
        bookId: 'kaoyan-core-vocab',
        bookName: '考研核心词汇',
        dailyCount: 5,
        totalWords: 12,
        totalDays: 3,
        completedDays: 1,
        currentDayLabel: 'Day 2',
        status: 'ACTIVE'
      },
      days: []
    });
  }

  return request({
    url: `/api/student/recite/plans/${planId}/days`,
    method: 'GET'
  });
}

function getReciteStudy(planId, dayNumber) {
  if (MOCK_FLAGS.recite) {
    logInfo('recite', 'using mock recite study', { planId, dayNumber });
    return Promise.resolve(null);
  }

  return request({
    url: `/api/student/recite/plans/${planId}/days/${dayNumber}/study`,
    method: 'GET'
  });
}

function completeReciteStudy(planId, dayNumber) {
  if (MOCK_FLAGS.recite) {
    logInfo('recite', 'using mock recite study complete', { planId, dayNumber });
    return Promise.resolve(null);
  }

  return request({
    url: `/api/student/recite/plans/${planId}/days/${dayNumber}/study-complete`,
    method: 'POST'
  });
}

function getReciteSession(planId, dayNumber, mode) {
  if (MOCK_FLAGS.recite) {
    logInfo('recite', 'using mock recite session', { planId, dayNumber, mode });
    return Promise.resolve(null);
  }

  return request({
    url: `/api/student/recite/plans/${planId}/days/${dayNumber}`,
    method: 'GET',
    data: { mode }
  });
}

function submitReciteSession(planId, dayNumber, payload) {
  if (MOCK_FLAGS.recite) {
    logInfo('recite', 'using mock recite submit', { planId, dayNumber, mode: payload.mode });
    return Promise.resolve({
      recordId: 1,
      totalCount: 5,
      correctCount: 4,
      wrongCount: 1,
      accuracy: '80%'
    });
  }

  return request({
    url: `/api/student/recite/plans/${planId}/days/${dayNumber}/submit`,
    method: 'POST',
    data: payload
  });
}

function getReciteSummary(recordId) {
  if (MOCK_FLAGS.recite) {
    logInfo('recite', 'using mock recite summary', { recordId });
    return Promise.resolve(null);
  }

  return request({
    url: `/api/student/recite/records/${recordId}`,
    method: 'GET'
  });
}

module.exports = {
  getActiveRecitePlan,
  createRecitePlan,
  listRecitePlanDays,
  getReciteStudy,
  completeReciteStudy,
  getReciteSession,
  submitReciteSession,
  getReciteSummary
};
