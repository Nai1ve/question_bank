const {
  getPracticeSession,
  submitPracticeAnswer,
  moveToNextPracticeQuestion,
  finalizePracticeSession
} = require('../../services/practice');

function getEntryTypeLabel(entryType) {
  return entryType === 'topxx' ? 'TopXX' : '普通刷题';
}

function getFeedbackModeLabel(feedbackMode) {
  return feedbackMode === 'immediate' ? '即时反馈模式' : '统一反馈模式';
}

function getQuestionTypeLabel(questionType) {
  return questionType === 'single' ? '单选题' : '多选题';
}

function getQuestionHint(questionType) {
  return questionType === 'single' ? '请选择 1 个答案' : '请选择全部正确答案';
}

function buildOptionItems(question, selectedOptions) {
  return (question.options || []).map((option) => {
    const selected = selectedOptions.indexOf(option.key) >= 0;
    return {
      ...option,
      selected,
      itemClassName: selected ? 'option-item option-item-selected' : 'option-item',
      keyClassName: selected ? 'option-key option-key-selected' : 'option-key'
    };
  });
}

function buildQuestionCard(question, selectedOptions) {
  if (!question) {
    return null;
  }

  return {
    ...question,
    typeLabel: getQuestionTypeLabel(question.type),
    questionHint: getQuestionHint(question.type),
    optionItems: buildOptionItems(question, selectedOptions)
  };
}

function buildFeedbackCard(result) {
  if (!result) {
    return null;
  }

  return {
    ...result,
    title: result.correct ? '回答正确' : '回答错误',
    titleClassName: result.correct ? 'feedback-title feedback-title-correct' : 'feedback-title feedback-title-wrong'
  };
}

function showPageError(error, fallbackTitle) {
  wx.showToast({
    title: (error && error.message) || fallbackTitle,
    icon: 'none'
  });
}

function getSessionStatusMessage(status) {
  if (status === 'EXPIRED') {
    return '上次练习已过期，请重新开始';
  }

  if (status === 'ABANDONED') {
    return '上次练习已结束，请重新开始';
  }

  return '练习会话不可继续';
}

Page({
  data: {
    sessionId: '',
    categoryName: '',
    feedbackMode: 'immediate',
    feedbackModeLabel: '即时反馈模式',
    entryType: 'normal',
    entryTypeLabel: '普通刷题',
    progressText: '',
    progressPercent: 0,
    currentQuestion: null,
    selectedOptions: [],
    feedbackVisible: false,
    feedbackResult: null,
    primaryButtonText: '提交答案'
  },

  onLoad(options) {
    this.setData({
      sessionId: options.sessionId || ''
    });
  },

  async onShow() {
    try {
      await this.loadCurrentQuestion();
    } catch (error) {
      showPageError(error, '练习会话加载失败');
    }
  },

  async loadCurrentQuestion() {
    const session = await getPracticeSession(this.data.sessionId);
    if (!session) {
      wx.showToast({
        title: '练习会话不存在',
        icon: 'none'
      });
      setTimeout(() => wx.navigateBack(), 300);
      return;
    }

    if (session.status === 'COMPLETED' || session.completed) {
      wx.redirectTo({
        url: `/pages/practice-summary/index?sessionId=${this.data.sessionId}`
      });
      return;
    }

    if (session.status !== 'ONGOING') {
      wx.showToast({
        title: getSessionStatusMessage(session.status),
        icon: 'none'
      });
      setTimeout(() => wx.navigateBack(), 300);
      return;
    }

    if (!session.currentQuestion) {
      wx.showToast({
        title: '当前练习题目不存在',
        icon: 'none'
      });
      setTimeout(() => wx.navigateBack(), 300);
      return;
    }

    const currentQuestion = session.currentQuestion;
    const progressText = `${session.currentSequence} / ${session.totalCount}`;
    const progressPercent = session.totalCount
      ? Math.round((session.currentSequence / session.totalCount) * 100)
      : 0;
    const selectedOptions = currentQuestion.userAnswer || [];

    this.setData({
      categoryName: session.categoryName,
      feedbackMode: session.feedbackMode,
      feedbackModeLabel: getFeedbackModeLabel(session.feedbackMode),
      entryType: session.entryType,
      entryTypeLabel: getEntryTypeLabel(session.entryType),
      currentQuestion: buildQuestionCard(currentQuestion, selectedOptions),
      selectedOptions,
      feedbackVisible: false,
      feedbackResult: null,
      progressText,
      progressPercent,
      primaryButtonText: session.feedbackMode === 'immediate' ? '提交答案' : '提交并继续'
    });
  },

  handleBack() {
    wx.navigateBack();
  },

  handleTapOption(event) {
    if (!this.data.currentQuestion || this.data.feedbackVisible) {
      return;
    }

    const optionKey = event.currentTarget.dataset.key;
    const nextSelected = this.data.selectedOptions.slice();

    if (this.data.currentQuestion.type === 'single') {
      const selectedOptions = [optionKey];
      this.setData({
        selectedOptions,
        currentQuestion: buildQuestionCard(this.data.currentQuestion, selectedOptions)
      });
      return;
    }

    const targetIndex = nextSelected.indexOf(optionKey);
    if (targetIndex >= 0) {
      nextSelected.splice(targetIndex, 1);
    } else {
      nextSelected.push(optionKey);
    }

    this.setData({
      selectedOptions: nextSelected,
      currentQuestion: buildQuestionCard(this.data.currentQuestion, nextSelected)
    });
  },

  async handleSubmitAnswer() {
    try {
      const { currentQuestion, selectedOptions, sessionId, feedbackMode } = this.data;
      if (!currentQuestion) {
        return;
      }

      if (!selectedOptions.length) {
        wx.showToast({
          title: currentQuestion.type === 'single' ? '请选择一个答案' : '请至少选择一个答案',
          icon: 'none'
        });
        return;
      }

      const result = await submitPracticeAnswer(sessionId, currentQuestion.id, selectedOptions);
      if (!result) {
        wx.showToast({
          title: '提交失败，请重试',
          icon: 'none'
        });
        return;
      }

      if (feedbackMode === 'immediate') {
        const session = await getPracticeSession(sessionId);
        const isLastQuestion = session && session.currentSequence >= session.totalCount;
        this.setData({
          feedbackVisible: true,
          feedbackResult: buildFeedbackCard(result),
          primaryButtonText: isLastQuestion ? '查看统计' : '下一题'
        });
        return;
      }

      await this.goForward();
    } catch (error) {
      showPageError(error, '提交答案失败');
      return;
    }
  },

  async handlePrimaryAction() {
    try {
      if (!this.data.feedbackVisible) {
        await this.handleSubmitAnswer();
        return;
      }

      await this.goForward();
    } catch (error) {
      showPageError(error, '练习流程推进失败');
    }
  },

  async goForward() {
    try {
      const session = await getPracticeSession(this.data.sessionId);
      if (!session) {
        return;
      }

      const isLastQuestion = session.currentSequence >= session.totalCount;
      if (isLastQuestion) {
        await finalizePracticeSession(this.data.sessionId);
        wx.redirectTo({
          url: `/pages/practice-summary/index?sessionId=${this.data.sessionId}`
        });
        return;
      }

      await moveToNextPracticeQuestion(this.data.sessionId);
      await this.loadCurrentQuestion();
    } catch (error) {
      showPageError(error, '下一题加载失败');
    }
  }
});
