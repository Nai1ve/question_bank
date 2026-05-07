const { MOCK_FLAGS } = require('./config');
const { request } = require('../utils/request');
const { PRACTICE_QUESTION_BANK, DEFAULT_QUESTION_STATS } = require('../mock/practice');
const { logInfo } = require('../utils/debug');

const SESSION_STORAGE_KEY = 'onepass-practice-sessions';
const STATS_STORAGE_KEY = 'onepass-practice-stats';
const HISTORY_STORAGE_KEY = 'onepass-practice-history';
const SESSION_RETENTION_DAYS = 7;

function startPracticeSession(payload) {
  if (MOCK_FLAGS.practice) {
    logInfo('practice', 'using mock start session', {
      entryType: payload.entryType,
      categoryId: payload.categoryId || ''
    });
    return Promise.resolve(createMockPracticeSession(payload));
  }

  return request({
    url: '/api/student/practice/sessions',
    method: 'POST',
    data: payload
  });
}

function findActivePracticeSession(query) {
  if (MOCK_FLAGS.practice) {
    logInfo('practice', 'using mock active session lookup', query);
    return Promise.resolve(findActiveMockPracticeSession(query));
  }

  return request({
    url: '/api/student/practice/sessions/active',
    method: 'GET',
    data: query
  }).then(normalizeSessionView);
}

function abandonPracticeSession(sessionId) {
  if (MOCK_FLAGS.practice) {
    logInfo('practice', 'using mock abandon session', {
      sessionId
    });
    return Promise.resolve(abandonMockPracticeSession(sessionId));
  }

  return request({
    url: `/api/student/practice/sessions/${sessionId}/abandon`,
    method: 'POST'
  });
}

function getPracticeSession(sessionId) {
  if (MOCK_FLAGS.practice) {
    logInfo('practice', 'using mock session detail', {
      sessionId
    });
    return Promise.resolve(getMockPracticeSession(sessionId));
  }

  return request({
    url: `/api/student/practice/sessions/${sessionId}`
  }).then(normalizeSessionView);
}

function submitPracticeAnswer(sessionId, questionId, selectedOptions) {
  if (MOCK_FLAGS.practice) {
    logInfo('practice', 'using mock answer submit', {
      sessionId,
      questionId,
      selectedCount: Array.isArray(selectedOptions) ? selectedOptions.length : 0
    });
    return Promise.resolve(submitMockPracticeAnswer(sessionId, questionId, selectedOptions));
  }

  return request({
    url: `/api/student/practice/sessions/${sessionId}/answers`,
    method: 'POST',
    data: {
      questionId,
      selectedOptions
    }
  });
}

function moveToNextPracticeQuestion(sessionId) {
  if (MOCK_FLAGS.practice) {
    logInfo('practice', 'using mock next question', {
      sessionId
    });
    return Promise.resolve(moveToNextMockPracticeQuestion(sessionId));
  }

  return request({
    url: `/api/student/practice/sessions/${sessionId}/next`,
    method: 'POST'
  }).then(normalizeSessionView);
}

function finalizePracticeSession(sessionId) {
  if (MOCK_FLAGS.practice) {
    logInfo('practice', 'using mock finalize session', {
      sessionId
    });
    return Promise.resolve(finalizeMockPracticeSession(sessionId));
  }

  return request({
    url: `/api/student/practice/sessions/${sessionId}/complete`,
    method: 'POST'
  }).then(normalizeSummaryView);
}

function getPracticeSummary(sessionId) {
  if (MOCK_FLAGS.practice) {
    logInfo('practice', 'using mock session summary', {
      sessionId
    });
    return Promise.resolve(getMockPracticeSummary(sessionId));
  }

  return request({
    url: `/api/student/practice/sessions/${sessionId}/summary`
  }).then(normalizeSummaryView);
}

function normalizeSessionView(session) {
  if (!session) {
    return null;
  }

  return {
    ...session,
    completed: !!session.completed,
    currentIndex: Number(session.currentIndex || 0),
    currentSequence: Number(session.currentSequence || 0),
    totalCount: Number(session.totalCount || 0),
    currentQuestion: normalizeQuestionView(session.currentQuestion)
  };
}

function normalizeQuestionView(question) {
  if (!question) {
    return null;
  }

  return {
    ...question,
    tags: Array.isArray(question.tags) ? question.tags : [],
    options: Array.isArray(question.options) ? question.options : [],
    userAnswer: Array.isArray(question.userAnswer) ? question.userAnswer : []
  };
}

function normalizeSummaryView(summary) {
  if (!summary) {
    return null;
  }

  return {
    ...summary,
    questionResults: Array.isArray(summary.questionResults)
      ? summary.questionResults.map((item) => ({
        ...item,
        tags: Array.isArray(item.tags) ? item.tags : [],
        options: Array.isArray(item.options) ? item.options : [],
        userAnswer: Array.isArray(item.userAnswer) ? item.userAnswer : [],
        standardAnswer: Array.isArray(item.standardAnswer) ? item.standardAnswer : []
      }))
      : []
  };
}

function findActiveMockPracticeSession(query) {
  expireMockSessions();

  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  const matchedSession = Object.values(sessions)
    .filter((session) => session.status === 'ONGOING')
    .filter((session) => session.entryType === query.entryType)
    .filter((session) => normalizeCategoryId(session.categoryId) === normalizeCategoryId(query.categoryId))
    .sort((left, right) => {
      return new Date(right.lastActiveAt).getTime() - new Date(left.lastActiveAt).getTime();
    })[0];

  return matchedSession ? buildSessionView(matchedSession) : null;
}

function abandonMockPracticeSession(sessionId) {
  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  const session = sessions[sessionId];
  if (!session) {
    return null;
  }

  expireMockSession(session);
  if (session.status !== 'ONGOING') {
    return null;
  }

  const now = nowAsIso();
  session.status = 'ABANDONED';
  session.abandonedAt = now;
  session.lastActiveAt = now;
  sessions[sessionId] = session;
  writeStorage(SESSION_STORAGE_KEY, sessions);
  return null;
}

function getMockPracticeSession(sessionId) {
  expireMockSessions();

  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  const session = sessions[sessionId];
  if (!session) {
    return null;
  }

  if (session.status === 'ONGOING') {
    session.lastActiveAt = nowAsIso();
    sessions[sessionId] = session;
    writeStorage(SESSION_STORAGE_KEY, sessions);
  }

  return buildSessionView(session);
}

function submitMockPracticeAnswer(sessionId, questionId, selectedOptions) {
  expireMockSessions();

  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  const session = sessions[sessionId];
  if (!session || session.status !== 'ONGOING') {
    return null;
  }

  const question = session.questions.find((item) => item.id === questionId);
  if (!question) {
    return null;
  }

  const normalizedUserAnswer = normalizeAnswer(selectedOptions);
  const normalizedStandardAnswer = normalizeAnswer(question.answer);
  const correct = isAnswerCorrect(normalizedUserAnswer, normalizedStandardAnswer);

  question.userAnswer = normalizedUserAnswer;
  question.submitted = true;
  question.correct = correct;
  question.answerLabel = normalizedStandardAnswer.join(', ');
  question.userAnswerLabel = normalizedUserAnswer.length ? normalizedUserAnswer.join(', ') : '未作答';
  session.lastActiveAt = nowAsIso();

  sessions[sessionId] = session;
  writeStorage(SESSION_STORAGE_KEY, sessions);

  return {
    correct,
    standardAnswer: question.answerLabel,
    userAnswer: question.userAnswerLabel,
    analysis: question.analysis
  };
}

function moveToNextMockPracticeQuestion(sessionId) {
  expireMockSessions();

  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  const session = sessions[sessionId];
  if (!session || session.status !== 'ONGOING') {
    return null;
  }

  const nextIndex = Math.min(session.currentIndex + 1, session.questions.length - 1);
  session.currentIndex = nextIndex;
  session.lastActiveAt = nowAsIso();
  sessions[sessionId] = session;
  writeStorage(SESSION_STORAGE_KEY, sessions);
  return buildSessionView(session);
}

function finalizeMockPracticeSession(sessionId) {
  expireMockSessions();

  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  const session = sessions[sessionId];
  if (!session) {
    return null;
  }

  if (session.completed && session.summary) {
    return session.summary;
  }

  if (session.status !== 'ONGOING') {
    return null;
  }

  const history = readStorage(HISTORY_STORAGE_KEY, {});
  const stats = getQuestionStats();

  let correctCount = 0;
  let wrongCount = 0;

  session.questions.forEach((question) => {
    const answeredCorrectly = !!question.correct;
    if (answeredCorrectly) {
      correctCount += 1;
    } else {
      wrongCount += 1;
    }

    history[question.id] = true;
    const current = stats[question.id] || { answeredCount: 0, wrongCount: 0 };
    stats[question.id] = {
      answeredCount: current.answeredCount + 1,
      wrongCount: current.wrongCount + (answeredCorrectly ? 0 : 1)
    };
  });

  writeStorage(HISTORY_STORAGE_KEY, history);
  writeStorage(STATS_STORAGE_KEY, stats);

  const totalCount = session.questions.length;
  const accuracy = totalCount ? `${Math.round((correctCount / totalCount) * 100)}%` : '0%';
  const summary = {
    sessionId: session.sessionId,
    entryType: session.entryType,
    categoryName: session.categoryName,
    feedbackMode: session.feedbackMode,
    totalCount,
    correctCount,
    wrongCount,
    accuracy,
    questionResults: session.questions.map((question, index) => ({
      id: question.id,
      sequence: index + 1,
      type: question.type,
      stem: question.stem,
      tags: question.tags,
      options: question.options,
      userAnswer: question.userAnswer || [],
      userAnswerLabel: question.userAnswerLabel || '未作答',
      standardAnswer: question.answer,
      standardAnswerLabel: normalizeAnswer(question.answer).join(', '),
      correct: !!question.correct,
      analysis: question.analysis
    }))
  };

  const now = nowAsIso();
  session.status = 'COMPLETED';
  session.completed = true;
  session.completedAt = now;
  session.lastActiveAt = now;
  session.summary = summary;
  sessions[sessionId] = session;
  writeStorage(SESSION_STORAGE_KEY, sessions);

  return summary;
}

function getMockPracticeSummary(sessionId) {
  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  const session = sessions[sessionId];
  if (!session) {
    return null;
  }

  if (!session.summary) {
    return finalizeMockPracticeSession(sessionId);
  }

  return session.summary;
}

function createMockPracticeSession(payload) {
  expireMockSessions();

  const entryType = payload.entryType || 'normal';
  const questionCount = Number(payload.questionCount || 20);
  const selectedTags = normalizeAnswer(Array.isArray(payload.selectedTags) ? payload.selectedTags : []);

  let questionPool = [];
  if (entryType === 'topxx') {
    questionPool = buildTopxxQuestionPool(payload.categoryId, selectedTags);
  } else {
    questionPool = buildNormalQuestionPool(payload.categoryId, selectedTags);
  }

  if (entryType === 'normal') {
    questionPool = filterAnsweredQuestions(questionPool);
    questionPool = shuffleArray(questionPool);
  }

  questionPool = questionPool.slice(0, questionCount).map(cloneQuestion);

  if (!questionPool.length) {
    return {
      ok: false,
      message: entryType === 'topxx' ? '当前筛选范围还没有可复练错题' : '当前筛选范围暂无可练习题目'
    };
  }

  const now = nowAsIso();
  const sessionId = `practice-${Date.now()}`;
  const session = {
    sessionId,
    entryType,
    categoryId: normalizeCategoryId(payload.categoryId),
    categoryName: payload.categoryName || '当前分类',
    feedbackMode: payload.feedbackMode || 'immediate',
    selectedTags,
    status: 'ONGOING',
    currentIndex: 0,
    completed: false,
    startedAt: now,
    lastActiveAt: now,
    completedAt: null,
    expiredAt: null,
    abandonedAt: null,
    summary: null,
    questions: questionPool.map((question, index) => ({
      ...question,
      order: index + 1,
      submitted: false,
      correct: false,
      userAnswer: [],
      userAnswerLabel: ''
    }))
  };

  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  sessions[sessionId] = session;
  writeStorage(SESSION_STORAGE_KEY, sessions);

  return {
    ok: true,
    sessionId,
    totalCount: session.questions.length,
    entryType: session.entryType,
    categoryName: session.categoryName,
    feedbackMode: session.feedbackMode
  };
}

function buildNormalQuestionPool(categoryId, selectedTags) {
  return PRACTICE_QUESTION_BANK.filter((question) => {
    return question.categoryId === categoryId && matchTags(question.tags, selectedTags);
  });
}

function buildTopxxQuestionPool(categoryId, selectedTags) {
  const stats = getQuestionStats();

  return PRACTICE_QUESTION_BANK
    .filter((question) => {
      const categoryMatched = !categoryId || question.categoryPathIds.includes(categoryId);
      const tagMatched = matchTags(question.tags, selectedTags);
      return categoryMatched && tagMatched;
    })
    .map((question) => ({
      ...question,
      wrongCount: (stats[question.id] && stats[question.id].wrongCount) || 0
    }))
    .filter((question) => question.wrongCount > 0)
    .sort((left, right) => {
      if (right.wrongCount !== left.wrongCount) {
        return right.wrongCount - left.wrongCount;
      }
      return left.id.localeCompare(right.id);
    });
}

function filterAnsweredQuestions(questions) {
  const history = readStorage(HISTORY_STORAGE_KEY, {});
  return questions.filter((question) => !history[question.id]);
}

function getQuestionStats() {
  const stored = readStorage(STATS_STORAGE_KEY, {});
  return {
    ...DEFAULT_QUESTION_STATS,
    ...stored
  };
}

function buildSessionView(session) {
  const currentQuestion = session.status === 'ONGOING'
    ? session.questions[session.currentIndex] || null
    : null;

  return {
    sessionId: session.sessionId,
    entryType: session.entryType,
    categoryId: session.categoryId,
    categoryName: session.categoryName,
    feedbackMode: session.feedbackMode,
    status: session.status,
    currentIndex: session.currentIndex,
    currentSequence: session.questions.length ? session.currentIndex + 1 : 0,
    totalCount: session.questions.length,
    completed: session.status === 'COMPLETED',
    startedAt: session.startedAt,
    lastActiveAt: session.lastActiveAt,
    completedAt: session.completedAt,
    expiredAt: session.expiredAt,
    currentQuestion: buildQuestionView(currentQuestion)
  };
}

function buildQuestionView(question) {
  if (!question) {
    return null;
  }

  return {
    id: question.id,
    type: question.type,
    tags: question.tags.slice(),
    stem: question.stem,
    options: question.options.map((option) => ({ ...option })),
    userAnswer: (question.userAnswer || []).slice()
  };
}

function expireMockSessions() {
  const sessions = readStorage(SESSION_STORAGE_KEY, {});
  let changed = false;

  Object.keys(sessions).forEach((sessionId) => {
    const session = sessions[sessionId];
    const expired = expireMockSession(session);
    if (expired) {
      changed = true;
    }
  });

  if (changed) {
    writeStorage(SESSION_STORAGE_KEY, sessions);
  }
}

function expireMockSession(session) {
  if (!session || session.status !== 'ONGOING') {
    return false;
  }

  const lastActiveTime = new Date(session.lastActiveAt).getTime();
  const expiredBefore = Date.now() - SESSION_RETENTION_DAYS * 24 * 60 * 60 * 1000;

  if (lastActiveTime < expiredBefore) {
    session.status = 'EXPIRED';
    session.expiredAt = nowAsIso();
    return true;
  }

  return false;
}

function matchTags(questionTags, selectedTags) {
  if (!selectedTags.length) {
    return true;
  }

  return selectedTags.some((tag) => questionTags.indexOf(tag) >= 0);
}

function normalizeAnswer(answerValues) {
  return (answerValues || []).slice().sort();
}

function isAnswerCorrect(userAnswer, standardAnswer) {
  if (userAnswer.length !== standardAnswer.length) {
    return false;
  }

  return userAnswer.every((value, index) => value === standardAnswer[index]);
}

function shuffleArray(items) {
  const cloned = items.slice();
  for (let index = cloned.length - 1; index > 0; index -= 1) {
    const randomIndex = Math.floor(Math.random() * (index + 1));
    const temp = cloned[index];
    cloned[index] = cloned[randomIndex];
    cloned[randomIndex] = temp;
  }
  return cloned;
}

function cloneQuestion(question) {
  return {
    ...question,
    tags: question.tags.slice(),
    answer: question.answer.slice(),
    categoryPathIds: question.categoryPathIds.slice(),
    options: question.options.map((option) => ({ ...option }))
  };
}

function normalizeCategoryId(categoryId) {
  return categoryId || '';
}

function nowAsIso() {
  return new Date().toISOString();
}

function readStorage(key, fallbackValue) {
  try {
    const value = wx.getStorageSync(key);
    return value || fallbackValue;
  } catch (error) {
    return fallbackValue;
  }
}

function writeStorage(key, value) {
  wx.setStorageSync(key, value);
}

module.exports = {
  startPracticeSession,
  findActivePracticeSession,
  abandonPracticeSession,
  getPracticeSession,
  submitPracticeAnswer,
  moveToNextPracticeQuestion,
  finalizePracticeSession,
  getPracticeSummary
};
