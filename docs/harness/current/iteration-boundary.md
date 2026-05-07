# Iteration 002 Boundary

- 迭代编号：`iteration-002`
- 版本目标：`v0.3.0-student-test-baseline`
- 迭代状态：`active`
- 起始日期：`2026-04-30`

## 当前边界

- 将学生端联调口径统一到真实 MySQL，`dev` 默认不再落回 mock
- 建立稳定、可重复重置的学生端测试基线
- 保证首页、我的页、题库、普通刷题、TopXX、错题本、词库、背诵计划、Day 列表、默写、复盘全部可测
- 以 `sql/schema.sql + sql/seed.sql + sql/reset-student-test-baseline.sql` 维护数据库测试基线
- 将 Harness 反馈统一收口到 `feedback-inbox.md`，已处理内容归档
- 收口学生端样式、背诵学习态、自定义 TabBar、容器化部署与本地 git 定稿

## 本轮明确做了什么

- `dev` profile 默认切到真实 MySQL，mock 只保留显式 override
- 补齐词库、背诵计划、背诵记录、错题统计测试数据
- 将题库、错题榜、错题本、词库、背诵主链路全部对齐到真实数据口径
- 将刷题接口从写死学生 `1001` 改为 JWT 学生鉴权
- 固定测试学生为 `1001 / 微信用户`
- 建立统一测试清单与固定测试数据文档
- 增加本地测试基线重置脚本
- 将待处理反馈与已处理归档从旧 `bug-feedback.md` 拆分
- 补齐背诵学习页、学习完成接口、最近一次结果回看与 Day 三态展示
- 将原生 tabBar 切为自定义 TabBar，并统一标题/正文字体与底部安全区
- 为后端补齐 `Dockerfile / docker-compose.yml / .env.example`

## 本轮不做

- Markdown 导入
- 真实微信登录联调
- TopXX 模型分析与错因推断
- 教师端、支付、AI 判题

## 验收标准

- 启动后端 `dev` 时，不额外传 mock 开关也能返回：
  - `GET /api/student/vocabulary/books`
  - `GET /api/student/wrong-book`
  - `GET /api/student/recite/plans/active`
- 小程序首页 4 个入口都能进入真实页面
- 我的页 3 个入口都能进入真实页面
- 普通刷题支持标签、即时反馈、统一反馈、继续上次练习、重新开始
- TopXX 支持榜单范围与三级分类筛选
- 错题本按累计答错次数排序展示真实列表，并能跳转 TopXX
- 背诵支持：
  - 词库列表
  - 激活计划
  - Day 列表
  - 学习页
  - 默写
  - 复盘
- 统一测试前可通过一次基线重置恢复固定数据状态
- 当前待处理问题只看 `feedback-inbox.md`
