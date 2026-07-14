# Project Progress Overview

更新时间：2026-05-28

## 当前结论

项目已从最初的 mock 骨架阶段推进到 `v0.4.0-question-import-baseline` 迭代。学生端 MVP 主链路、后端 Spring Boot 单体、MySQL 8.x 持久化、MyBatis Mapper、统一 seed/reset 脚本和内部题目导入管理能力都已经具备基础实现。

当前最重要的在制事项是：完成并验证 `docx -> Markdown -> 预览校验 -> 确认导入 MySQL -> 普通刷题可用` 的端到端闭环。

## 版本与工作区

- 当前分支：`main`
- 当前提交：`bc80938 chore: finalize student-side test baseline`
- 当前 tag：`v0.3.0-student-test-baseline`
- 当前目标版本：`v0.4.0-question-import-baseline`
- 当前迭代：`iteration-003`
- 工作区状态：存在大量未提交改动，主要集中在题目导入、MySQL/Mapper、文档和小程序刷题展示兼容上

## 已完成清单

- [x] 建立项目目录：`backend-java/`、`frontend-miniprogram/`、`docs/`
- [x] 明确后端根包：`com.onepass.practice`
- [x] 后端采用 Spring Boot 单体服务
- [x] 数据库目标明确为 MySQL 8.x
- [x] DAO 方案采用 MyBatis Mapper + XML
- [x] `dev` profile 默认真实 MySQL，mock 模式保留为回退
- [x] 小程序学生端已登记 15 个页面
- [x] 学生端完成登录、学习首页、我的页、题库导航、刷题、TopXX、错题本、词库、背诵计划、Day 列表、学习、默写、复盘页面骨架和主流程
- [x] 后端已提供学生端 Auth、Dashboard、Category、Tag、Practice、Wrong Book、Vocabulary、Recite 接口
- [x] 刷题会话支持服务端持久化、继续上次练习、重新开始、提交答案、下一题、完成、放弃和统计
- [x] TopXX 和错题本基于学生错题统计数据
- [x] 背诵计划支持 active plan、建计划、Day 列表、学习完成、测试提交和记录复盘
- [x] MySQL 表结构、seed 数据和测试基线 reset 脚本已就位
- [x] Dockerfile 与 docker-compose 已就位，支持后端 + MySQL 本地启动
- [x] 内部题目导入表：`question_import_batch`、`question_import_item`、`question_asset`
- [x] 内部题目导入能力：上传 docx、生成 Markdown、批次预览、下载 Markdown、确认导入、取消批次
- [x] 内部导入分类能力：叶子分类下拉、新增分类路径、删除空叶子分类
- [x] 支持题型边界明确：单选、多选、不定项、判断题进入学生刷题链路；简答、填空、材料题先进入报告
- [x] 小程序刷题页和统计页已兼容 `indefinite`、`judge` 和 Markdown 图片节点

## 进行中清单

- [ ] 使用 `docs/prototypes/questions/101-10.docx` 验证真实题目导入
- [ ] 使用 `docs/prototypes/questions/import_question.docx` 验证多题型识别和暂不支持报告
- [ ] 管理页上传、预览、下载 Markdown、确认导入、取消批次做完整联调
- [ ] 管理页分类下拉、新增空分类、删除空分类做完整联调
- [ ] 确认导入后的题目能进入普通刷题链路
- [ ] 带图题在小程序练习页和统计页做视觉复验
- [ ] 导入错误报告字段继续按真实运营 docx 样例补齐
- [ ] 维护 `schema.sql`、`seed.sql`、`reset-student-test-baseline.sql` 与导入规范同步

## 待处理或需复验

- [ ] `FB-002-001`：学习首页、我的页、登录页、刷题筛选、练习统计在不同安全区机型下的无遮挡复验
- [ ] `FB-002-005`：已完成背诵 Day 支持查看最近一次结果或重新学习并测试，待复验
- [ ] `FB-002-006`：未完成背诵 Day 先学习再测试，待复验
- [x] 默认 profile 下 `mvn test` 已恢复通过：题目导入相关 MySQL 入口仅在 `app.mock.enabled=false` 时装配
- [ ] 根 README 与部分前端 README 的阶段描述仍偏早，需要和当前 MySQL + 导入迭代口径对齐

## 暂缓清单

- [ ] 真实微信登录联调
- [ ] 教师端
- [ ] 支付
- [ ] AI 解析、AI 判题、TopXX 模型分析和错因总结
- [ ] 微服务拆分
- [ ] `.doc` 直接解析
- [ ] 材料题子题进入学生刷题主链路
- [ ] 题目导入异步任务中心
- [ ] 对象存储替换本地 import-storage

## 验证结果

- `mysqladmin -h127.0.0.1 -uroot ping`：通过，本机 MySQL 可连接
- `JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn test`：通过，`Tests run: 1, Failures: 0, Errors: 0`
- `JAVA_HOME=$(/usr/libexec/java_home -v 17) SPRING_PROFILES_ACTIVE=dev mvn test`：通过，`Tests run: 1, Failures: 0, Errors: 0`

## 文档索引

### 项目入口

- `README.md`：项目顶层简介，当前描述偏早
- `backend-java/README.md`：后端运行、MySQL 初始化、Docker、题目导入管理页说明
- `frontend-miniprogram/README.md`：小程序学生端说明，当前描述偏早

### 核心产品与技术文档

- `docs/01-mvp-scope.md`：MVP 范围，明确第一阶段不做教师端、支付、AI、微服务
- `docs/02-data-model.md`：数据模型、MySQL cutover 表、会话恢复、导入表说明
- `docs/03-page-flow.md`：小程序 15 页页面清单与主流程
- `docs/04-api-contract.md`：学生端 API 与导入管理 API 契约
- `docs/05-content-import-spec.md`：docx/Markdown/MySQL 导入规范
- `docs/06-codex-harness.md`：迭代管理和反馈跟踪规则

### 当前迭代管理文档

- `docs/harness/current/iteration-boundary.md`：`iteration-003` 边界、目标和验收标准
- `docs/harness/current/todo-list.md`：当前执行项、下一步、延期项
- `docs/harness/current/feedback-inbox.md`：当前待复验和待处理反馈
- `docs/harness/current/student-test-checklist.md`：学生端统一手工测试步骤
- `docs/harness/current/question-import-test-checklist.md`：题目导入专项测试清单
- `docs/harness/current/student-test-data.md`：固定测试数据与预期结果
- `docs/harness/current/git-version.md`：版本、提交粒度和 tag 口径

### 原型与样例

- `docs/prototypes/frontend/`：小程序早期页面原型图
- `docs/prototypes/questions/101-10.docx`：真实题目导入验证样例
- `docs/prototypes/questions/import_question.docx`：多题型导入验证样例

## 建议下一步

1. 跑通两个 docx 样例的导入管理页闭环，记录批次结果。
2. 验证确认导入后的题目能被普通刷题接口抽到，尤其是判断题、不定项和带图题。
3. 用微信开发者工具做学生端视觉复验，关闭当前三个待复验反馈。
4. 对齐 README 阶段描述，再准备 `v0.4.0-question-import-baseline` 提交和 tag。
