const PRACTICE_QUESTION_BANK = [
  {
    id: 'english-core-1',
    categoryId: 'english-core',
    categoryPathIds: ['root-kaoyan', 'kaoyan-english', 'english-core'],
    type: 'single',
    tags: ['高频词汇', '易错辨析'],
    stem: '以下哪个单词表示“放弃”？',
    options: [
      { key: 'A', content: 'issue' },
      { key: 'B', content: 'abandon' },
      { key: 'C', content: 'maintain' },
      { key: 'D', content: 'require' }
    ],
    answer: ['B'],
    analysis: 'abandon 表示“放弃”，是考研英语核心词汇中的高频考点。'
  },
  {
    id: 'english-core-2',
    categoryId: 'english-core',
    categoryPathIds: ['root-kaoyan', 'kaoyan-english', 'english-core'],
    type: 'multiple',
    tags: ['高频词汇', '阅读技巧'],
    stem: '以下哪些单词可以作动词使用？',
    options: [
      { key: 'A', content: 'issue' },
      { key: 'B', content: 'abandon' },
      { key: 'C', content: 'maintain' },
      { key: 'D', content: 'requirement' }
    ],
    answer: ['A', 'B', 'C'],
    analysis: 'issue、abandon、maintain 都可作动词，requirement 通常作名词。'
  },
  {
    id: 'english-reading-1',
    categoryId: 'english-reading',
    categoryPathIds: ['root-kaoyan', 'kaoyan-english', 'english-reading'],
    type: 'single',
    tags: ['阅读技巧', '易错辨析'],
    stem: '阅读题中出现转折词 however 时，通常优先关注哪一部分信息？',
    options: [
      { key: 'A', content: '转折词之前的铺垫' },
      { key: 'B', content: '转折词之后的真正观点' },
      { key: 'C', content: '段落中的专有名词' },
      { key: 'D', content: '所有数字信息' }
    ],
    answer: ['B'],
    analysis: 'however 常提示作者态度或主旨转折，转折后信息通常更关键。'
  },
  {
    id: 'english-reading-2',
    categoryId: 'english-reading',
    categoryPathIds: ['root-kaoyan', 'kaoyan-english', 'english-reading'],
    type: 'multiple',
    tags: ['阅读技巧', '核心知识点'],
    stem: '做细节题时，以下哪些方法更稳妥？',
    options: [
      { key: 'A', content: '回原文定位关键词' },
      { key: 'B', content: '对比选项与原文表述差异' },
      { key: 'C', content: '只凭语感快速作答' },
      { key: 'D', content: '排除绝对化表达' }
    ],
    answer: ['A', 'B', 'D'],
    analysis: '细节题优先回文定位和比对表述，绝对化选项通常风险更高。'
  },
  {
    id: 'politics-basic-1',
    categoryId: 'politics-basic',
    categoryPathIds: ['root-kaoyan', 'kaoyan-politics', 'politics-basic'],
    type: 'single',
    tags: ['基础概念', '高频选择题'],
    stem: '马克思主义哲学认为，物质和意识的关系中，哪一项表述正确？',
    options: [
      { key: 'A', content: '意识决定物质' },
      { key: 'B', content: '物质决定意识' },
      { key: 'C', content: '二者没有联系' },
      { key: 'D', content: '意识先于物质存在' }
    ],
    answer: ['B'],
    analysis: '唯物论的基本立场是物质决定意识。'
  },
  {
    id: 'politics-basic-2',
    categoryId: 'politics-basic',
    categoryPathIds: ['root-kaoyan', 'kaoyan-politics', 'politics-basic'],
    type: 'multiple',
    tags: ['基础概念', '强化阶段'],
    stem: '下列哪些属于实践的基本特征？',
    options: [
      { key: 'A', content: '客观物质性' },
      { key: 'B', content: '主观随意性' },
      { key: 'C', content: '自觉能动性' },
      { key: 'D', content: '社会历史性' }
    ],
    answer: ['A', 'C', 'D'],
    analysis: '实践具有客观物质性、自觉能动性和社会历史性。'
  },
  {
    id: 'politics-advanced-1',
    categoryId: 'politics-advanced',
    categoryPathIds: ['root-kaoyan', 'kaoyan-politics', 'politics-advanced'],
    type: 'single',
    tags: ['强化阶段', '高频选择题'],
    stem: '新发展理念中，强调解决发展不平衡问题的是哪一项？',
    options: [
      { key: 'A', content: '创新' },
      { key: 'B', content: '协调' },
      { key: 'C', content: '绿色' },
      { key: 'D', content: '开放' }
    ],
    answer: ['B'],
    analysis: '协调发展主要对应解决发展不平衡问题。'
  },
  {
    id: 'politics-advanced-2',
    categoryId: 'politics-advanced',
    categoryPathIds: ['root-kaoyan', 'kaoyan-politics', 'politics-advanced'],
    type: 'multiple',
    tags: ['强化阶段', '基础概念'],
    stem: '关于社会基本矛盾运动，以下哪些说法正确？',
    options: [
      { key: 'A', content: '推动社会形态更替' },
      { key: 'B', content: '与生产力和生产关系无关' },
      { key: 'C', content: '是社会发展的根本动力之一' },
      { key: 'D', content: '表现为生产关系与生产力、上层建筑与经济基础的矛盾' }
    ],
    answer: ['A', 'C', 'D'],
    analysis: '社会基本矛盾贯穿社会发展全过程，是社会发展的根本动力。'
  },
  {
    id: 'fund-subject-1-1',
    categoryId: 'fund-subject-1',
    categoryPathIds: ['root-cert', 'cert-fund', 'fund-subject-1'],
    type: 'single',
    tags: ['高频题', '核心知识点'],
    stem: '基金管理人应当遵循的核心原则不包括下列哪一项？',
    options: [
      { key: 'A', content: '诚实信用' },
      { key: 'B', content: '勤勉尽责' },
      { key: 'C', content: '风险隔离' },
      { key: 'D', content: '随意承诺收益' }
    ],
    answer: ['D'],
    analysis: '基金从业规范中不得向投资者随意承诺收益。'
  },
  {
    id: 'fund-subject-1-2',
    categoryId: 'fund-subject-1',
    categoryPathIds: ['root-cert', 'cert-fund', 'fund-subject-1'],
    type: 'multiple',
    tags: ['高频题', '易错题'],
    stem: '以下哪些属于基金销售适当性管理的要求？',
    options: [
      { key: 'A', content: '了解投资者风险承受能力' },
      { key: 'B', content: '向所有客户统一推荐高风险产品' },
      { key: 'C', content: '匹配产品风险等级' },
      { key: 'D', content: '充分揭示风险' }
    ],
    answer: ['A', 'C', 'D'],
    analysis: '适当性管理强调识别客户风险能力、匹配产品并充分揭示风险。'
  },
  {
    id: 'fund-subject-2-1',
    categoryId: 'fund-subject-2',
    categoryPathIds: ['root-cert', 'cert-fund', 'fund-subject-2'],
    type: 'single',
    tags: ['高频题', '核心知识点'],
    stem: '债券价格与市场利率通常呈现什么关系？',
    options: [
      { key: 'A', content: '同向变动' },
      { key: 'B', content: '反向变动' },
      { key: 'C', content: '完全无关' },
      { key: 'D', content: '固定不变' }
    ],
    answer: ['B'],
    analysis: '利率上升时，债券价格通常下降，二者一般呈反向关系。'
  },
  {
    id: 'fund-subject-2-2',
    categoryId: 'fund-subject-2',
    categoryPathIds: ['root-cert', 'cert-fund', 'fund-subject-2'],
    type: 'multiple',
    tags: ['高频题', '易错题'],
    stem: '关于基金净值，下列哪些说法正确？',
    options: [
      { key: 'A', content: '单位净值反映每份基金份额的价值' },
      { key: 'B', content: '累计净值不受分红影响' },
      { key: 'C', content: '净值计算通常以基金资产净值为基础' },
      { key: 'D', content: '开放式基金需要定期披露净值信息' }
    ],
    answer: ['A', 'C', 'D'],
    analysis: '累计净值会受分红等因素影响，因此 B 错误。'
  },
  {
    id: 'teacher-comprehensive-1',
    categoryId: 'teacher-comprehensive',
    categoryPathIds: ['root-cert', 'cert-teacher', 'teacher-comprehensive'],
    type: 'single',
    tags: ['高频题', '核心知识点'],
    stem: '教师职业道德中最基本、最核心的要求是：',
    options: [
      { key: 'A', content: '爱岗敬业' },
      { key: 'B', content: '关爱学生' },
      { key: 'C', content: '教书育人' },
      { key: 'D', content: '终身学习' }
    ],
    answer: ['B'],
    analysis: '关爱学生是教师职业道德的重要核心要求之一。'
  },
  {
    id: 'teacher-comprehensive-2',
    categoryId: 'teacher-comprehensive',
    categoryPathIds: ['root-cert', 'cert-teacher', 'teacher-comprehensive'],
    type: 'multiple',
    tags: ['高频题', '易错题'],
    stem: '下列哪些行为符合教师职业规范？',
    options: [
      { key: 'A', content: '尊重学生人格' },
      { key: 'B', content: '体罚学生' },
      { key: 'C', content: '公平对待学生' },
      { key: 'D', content: '主动进行专业提升' }
    ],
    answer: ['A', 'C', 'D'],
    analysis: '体罚学生违反教师职业规范，其余三项符合要求。'
  },
  {
    id: 'teacher-education-1',
    categoryId: 'teacher-education',
    categoryPathIds: ['root-cert', 'cert-teacher', 'teacher-education'],
    type: 'single',
    tags: ['高频题', '核心知识点'],
    stem: '在教学原则中，强调从学生实际出发的是：',
    options: [
      { key: 'A', content: '直观性原则' },
      { key: 'B', content: '因材施教原则' },
      { key: 'C', content: '启发性原则' },
      { key: 'D', content: '循序渐进原则' }
    ],
    answer: ['B'],
    analysis: '因材施教要求根据学生差异开展教学。'
  },
  {
    id: 'teacher-education-2',
    categoryId: 'teacher-education',
    categoryPathIds: ['root-cert', 'cert-teacher', 'teacher-education'],
    type: 'multiple',
    tags: ['高频题', '易错题'],
    stem: '以下哪些属于有效课堂提问的基本要求？',
    options: [
      { key: 'A', content: '问题有明确目标' },
      { key: 'B', content: '问题难度完全一致' },
      { key: 'C', content: '给学生思考时间' },
      { key: 'D', content: '鼓励学生表达与追问' }
    ],
    answer: ['A', 'C', 'D'],
    analysis: '有效提问强调目标清晰、等待时间和互动追问，B 不正确。'
  }
];

const DEFAULT_QUESTION_STATS = {
  'english-core-1': { answeredCount: 5, wrongCount: 3 },
  'english-core-2': { answeredCount: 4, wrongCount: 2 },
  'politics-basic-1': { answeredCount: 6, wrongCount: 4 },
  'politics-advanced-1': { answeredCount: 5, wrongCount: 2 },
  'fund-subject-1-2': { answeredCount: 3, wrongCount: 2 },
  'teacher-comprehensive-2': { answeredCount: 4, wrongCount: 3 }
};

module.exports = {
  PRACTICE_QUESTION_BANK,
  DEFAULT_QUESTION_STATS
};

