const dashboard = {
  currentQuestionBank: '考研英语 · 核心词汇',
  currentRecitePlan: '考研核心词汇 · Day 2',
  summaryTemplate: {
    title: '我的刷题总结',
    templateName: '学习复盘模板',
    blocks: [
      {
        key: 'rhythm',
        label: '当前节奏',
        content: '最近主要在刷考研英语核心词汇，题量先稳定在 20 题一组，优先把高频知识点刷熟。'
      },
      {
        key: 'focus',
        label: '近期重点',
        content: '当前错题主要集中在词义辨析和阅读细节判断，接下来会先回看高频错题，再补基础薄弱点。'
      },
      {
        key: 'next',
        label: '下一步安排',
        content: '继续保持常刷题库的连续练习，同时跟进正在进行中的背诵计划，保证刷题和记忆同步推进。'
      }
    ]
  }
};

module.exports = {
  dashboard
};
