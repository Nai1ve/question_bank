const categoryNodes = [
  { id: 'root-kaoyan', parentId: null, name: '考研', subtitle: '必刷题', isLeaf: false },
  { id: 'root-cert', parentId: null, name: '证书', subtitle: '金融类', isLeaf: false },
  { id: 'kaoyan-english', parentId: 'root-kaoyan', name: '英语', subtitle: '考研方向', isLeaf: false },
  { id: 'kaoyan-politics', parentId: 'root-kaoyan', name: '政治', subtitle: '考研方向', isLeaf: false },
  { id: 'cert-fund', parentId: 'root-cert', name: '基金从业', subtitle: '资格考试', isLeaf: false },
  { id: 'cert-teacher', parentId: 'root-cert', name: '教师资格', subtitle: '资格考试', isLeaf: false },
  { id: 'english-core', parentId: 'kaoyan-english', name: '核心词汇', subtitle: '叶子分类', isLeaf: true },
  { id: 'english-reading', parentId: 'kaoyan-english', name: '阅读理解', subtitle: '叶子分类', isLeaf: true },
  { id: 'politics-basic', parentId: 'kaoyan-politics', name: '基础阶段', subtitle: '叶子分类', isLeaf: true },
  { id: 'politics-advanced', parentId: 'kaoyan-politics', name: '强化阶段', subtitle: '叶子分类', isLeaf: true },
  { id: 'fund-subject-1', parentId: 'cert-fund', name: '科目一', subtitle: '叶子分类', isLeaf: true },
  { id: 'fund-subject-2', parentId: 'cert-fund', name: '科目二', subtitle: '叶子分类', isLeaf: true },
  { id: 'teacher-comprehensive', parentId: 'cert-teacher', name: '综合素质', subtitle: '叶子分类', isLeaf: true },
  { id: 'teacher-education', parentId: 'cert-teacher', name: '教育知识', subtitle: '叶子分类', isLeaf: true }
];

function getMockCategories(parentId) {
  return categoryNodes.filter((item) => {
    if (!parentId) {
      return item.parentId === null;
    }
    return item.parentId === parentId;
  });
}

module.exports = {
  getMockCategories
};

