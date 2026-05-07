const { getReciteSession, submitReciteSession } = require('../../services/recite');

function buildEntries(items) {
  return (items || []).map((item) => ({
    ...item,
    inputValue: ''
  }));
}

Page({
  data: {
    planId: '',
    dayNumber: '',
    mode: 'cn_to_en',
    session: null,
    entries: [],
    placeholderText: '请输入答案'
  },

  onLoad(options) {
    this.setData({
      planId: options.planId || '',
      dayNumber: options.dayNumber || '',
      mode: options.mode || 'cn_to_en',
      placeholderText: (options.mode || 'cn_to_en') === 'cn_to_en' ? '请输入英文' : '请输入中文'
    });
  },

  onShow() {
    this.loadSession();
  },

  async loadSession() {
    try {
      const session = await getReciteSession(this.data.planId, this.data.dayNumber, this.data.mode);
      this.setData({
        session,
        entries: buildEntries(session.items)
      });
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '默写页加载失败',
        icon: 'none'
      });
    }
  },

  handleBack() {
    wx.navigateBack();
  },

  handleInput(event) {
    const { index } = event.currentTarget.dataset;
    const value = event.detail.value;
    const entries = this.data.entries.slice();
    entries[index] = {
      ...entries[index],
      inputValue: value
    };
    this.setData({ entries });
  },

  async handleSubmit() {
    try {
      const answers = this.data.entries.map((item) => ({
        wordId: item.wordId,
        value: item.inputValue || ''
      }));

      const result = await submitReciteSession(this.data.planId, this.data.dayNumber, {
        mode: this.data.mode,
        answers
      });

      wx.navigateTo({
        url: `/pages/recite-summary/index?recordId=${result.recordId}&planId=${this.data.planId}`
      });
    } catch (error) {
      wx.showToast({
        title: (error && error.message) || '默写提交失败',
        icon: 'none'
      });
    }
  }
});
