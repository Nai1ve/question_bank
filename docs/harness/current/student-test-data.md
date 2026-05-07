# Student Test Data

## 登录

- 当前使用 mock 微信登录
- 任意一次点击登录按钮都会落到本地学生 `1001`

## 学生资料

- 学生 ID：`1001`
- 显示名称：`微信用户`

## 分类测试数据

- 一级分类：
  - `考研`
  - `证书`
- 考研二级：
  - `英语`
  - `政治`
- 证书二级：
  - `基金从业`
  - `教师资格`
- 主测试叶子分类：
  - `english-core`
  - `english-reading`
  - `politics-basic`
  - `politics-advanced`
  - `fund-subject-1`
  - `teacher-comprehensive`
- 以上 6 个主测试叶子分类当前都具备 `3` 道可用题

## 普通刷题测试分类

- 推荐测试 1：
  - 分类：`考研 -> 英语 -> 核心词汇`
  - 预期：至少 `3` 题
- 推荐测试 2：
  - 分类：`考研 -> 英语 -> 阅读理解`
  - 标签：`阅读技巧`
  - 预期：能筛到真实标签题目
- 推荐测试 3：
  - 分类：`证书 -> 基金从业 -> 科目一`
  - 标签：`适当性管理`
  - 预期：能筛到多选题

## TopXX 测试数据

- 当前高频错题统计共 `10` 条
- 当前排序靠前的题预期包括：
  - `马克思主义哲学认为，物质和意识的关系中，哪一项表述正确？`
  - `以下哪个单词表示“放弃”？`
  - `下列哪些行为符合教师职业规范？`
- 推荐测试范围：
  - 榜单：`Top20`
  - 分类：`考研`

## 错题本测试数据

- 错题本页默认能看到真实错题列表
- 当前排序前 5 的题预期包含：
  - `马克思主义哲学认为，物质和意识的关系中，哪一项表述正确？`
  - `以下哪个单词表示“放弃”？`
  - `下列哪些行为符合教师职业规范？`
  - `以下哪些属于基金销售适当性管理的要求？`
  - `中国式现代化的重要特征包括哪些？`

## 词库测试数据

- 词库 1：
  - ID：`kaoyan-core-vocab`
  - 名称：`考研核心词汇`
  - 单词数：`18`
- 词库 2：
  - ID：`cert-fund-vocab`
  - 名称：`基金从业高频词`
  - 单词数：`12`

## 背诵计划种子数据

- 当前激活计划：
  - 计划 ID：`1`
  - 词库：`考研核心词汇`
  - 每日数量：`6`
  - 总天数：`3`
- 当前历史计划：
  - 计划 ID：`2`
  - 状态：`SUPERSEDED`
  - 词库：`基金从业高频词`
- 当前初始化状态：
  - `Day 1`：`COMPLETED`
  - `Day 2`：`PENDING_STUDY`
  - `Day 3`：`PENDING_STUDY`

## 背诵 Day 行为基线

- `Day 1`
  - 点击后弹出：
    - `查看最近一次结果`
    - `重新学习并测试`
  - 最近一次结果应指向 `recordId=1`
- `Day 2`
  - 首次点击应先进入学习页
  - 学习页展示：
    - `accurate / 准确的 / adj.`
    - `approach / 方法 / n.`
    - `consume / 消耗 / v.`
    - `constant / 持续的 / adj.`
    - `distinguish / 区分 / v.`
    - `significant / 重要的 / adj.`
- `Day 3`
  - 首次点击也应先进入学习页

## 背诵默写推荐验证数据

- `Day 2` 中译英推荐标准答案：
  - `准确的 -> accurate`
  - `方法 -> approach`
  - `消耗 -> consume`
  - `持续的 -> constant`
  - `区分 -> distinguish`
  - `重要的 -> significant`

- `Day 3` 中译英推荐标准答案：
  - `获得 -> acquire`
  - `适应 -> adapt`
  - `评估 -> evaluate`
  - `阐明 -> illustrate`
  - `证明合理 -> justify`
  - `保留 -> retain`

## 背诵历史记录样例

- 当前已存在 2 条已完成背诵记录样例：
  - `考研核心词汇 · Day 1`，`recordId=1`
  - `基金从业高频词 · Day 1`

## 统一测试前重置步骤

```bash
mysql -h127.0.0.1 -uroot onepass_practice < backend-java/sql/reset-student-test-baseline.sql
mysql -h127.0.0.1 -uroot onepass_practice < backend-java/sql/seed.sql
```

重置后，首页当前背诵计划、Day 状态、错题榜前几名都应与本文件一致。
