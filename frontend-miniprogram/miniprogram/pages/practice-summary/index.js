const { getPracticeSummary } = require('../../services/practice');

function decorateSummary(summary) {
  return {
    ...summary,
    subtitle: summary.entryType === 'topxx' ? 'TopXX 复练统计' : '普通刷题统计',
    questionResults: (summary.questionResults || []).map((item) => ({
      ...item,
      badgeLabel: item.correct ? '正确' : '错误',
      badgeClassName: item.correct ? 'result-badge result-badge-correct' : 'result-badge result-badge-wrong'
    }))
  };
}

function showSummaryError(error, fallbackTitle) {
  wx.showToast({
    title: (error && error.message) || fallbackTitle,
    icon: 'none'
  });
}

Page({
  data: {
    sessionId: '',
    summary: null,
    questionResults: []
  },

  onLoad(options) {
    this.setData({
      sessionId: options.sessionId || ''
    });
  },

  async onShow() {
    try {
      await this.loadSummary();
    } catch (error) {
      showSummaryError(error, '统计数据加载失败');
    }
  },

  async loadSummary() {
    const summary = await getPracticeSummary(this.data.sessionId);
    if (!summary) {
      wx.showToast({
        title: '统计数据不存在',
        icon: 'none'
      });
      setTimeout(() => wx.navigateBack(), 300);
      return;
    }

    const decoratedSummary = decorateSummary(summary);
    this.setData({
      summary: decoratedSummary,
      questionResults: decoratedSummary.questionResults
    });
  },

  handleBack() {
    wx.navigateBack();
  },

  handleContinuePractice() {
    wx.navigateBack();
  },

  handleGoHome() {
    wx.switchTab({
      url: '/pages/study-home/index'
    });
  }
});
