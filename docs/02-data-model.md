# Data Model Notes

当前仅确定核心领域：

- student
- category
- tag
- question
- practice_session
- practice_session_question
- practice_answer_record
- question_wrong_stat
- topxx
- vocabulary
- recite_plan
- content_import

## Practice Session Notes

- 刷题需要以“会话”作为服务端持久化单位，而不是只靠前端页面状态
- 会话至少应包含：
  - `session_id`
  - `student_id`
  - `entry_type`
  - `category_id`
  - `feedback_mode`
  - `status`
  - `current_index`
  - `total_count`
  - `started_at`
  - `last_active_at`
  - `completed_at`
  - `expired_at`
- 会话状态建议包含：
  - `ongoing`
  - `completed`
  - `expired`
  - `abandoned`
- 学生中途退出后，服务端需要保留：
  - 已生成的题目顺序快照
  - 已提交答案
  - 当前进行到第几题
- 当前题“已选择但未提交”的草稿状态，第一版可只保存在前端本地，不强制写服务端
- 恢复策略建议：
  - 恢复匹配范围先按 `student_id + entry_type + category_id`
  - 学生再次进入同一练习入口时，优先检查是否存在未完成会话
  - 若存在，则提示“继续上次练习 / 重新开始”
  - 若重新开始，则旧会话标记为 `abandoned`
- 保留时长建议：
  - 未完成会话：按 `last_active_at` 保留 `7 天`
  - 超过 `7 天` 未继续，则标记为 `expired`
  - 已完成练习记录与错题统计：长期保留，用于 TopXX 和后续分析
- TopXX 和普通刷题都应基于“会话快照”恢复，不能在恢复时重新抽题或重新排序

## MySQL Cutover Tables

- `student`
  - 保存学生基础身份信息，当前最小字段为 `display_name` 和 `avatar_url`
- `category`
  - 保存三层以内分类树，支持题库导航与 TopXX 按分类筛选
- `question`
  - 保存题目主表，按叶子分类挂载
- `question_option`
  - 保存题目选项，支持单选和多选
- `question_tag`
  - 保存题目标签，支持筛选页多标签并集过滤
- `question_answer`
  - 保存标准答案，避免把多选答案揉进单字段
- `student_dashboard_template`
  - 保存“我的刷题总结”和当前状态的展示模板
- `student_dashboard_block`
  - 保存模板中的文本块，便于后续扩展多模板
- `practice_session`
  - 保存一轮练习会话的范围、状态、当前进度、活跃时间和乐观锁版本
  - 恢复未完成会话时，主查询索引按 `student_id + entry_type + category_id + status`
- `practice_session_question`
  - 保存本轮题目快照，避免恢复时重新抽题或重新排序
  - 题干、选项、标准答案和标签都按快照落库
- `practice_answer_record`
  - 保存每次提交答案的流水，便于后续分析重复提交和答题轨迹
- `question_wrong_stat`
  - 保存学生维度的累计答题次数与累计错题次数
  - TopXX 和后续模型分析都优先依赖这张统计表
- `recite_plan_day`
  - 继续使用 `PENDING / COMPLETED` 作为数据库状态
  - 额外使用 `study_completed_at` 标记“是否已完成学习模式”
  - 前端展示态按以下规则计算：
    - `COMPLETED`
    - `PENDING_TEST`
    - `PENDING_STUDY`
- `recite_day_record`
  - 保存每次 Day 测试记录
  - 已完成 Day 的“查看最近一次结果”按 `plan_day_id + student_id + created_at DESC` 获取最新记录
- `question_import_batch`
  - 保存一次 docx 上传导入批次
  - 记录原文件名、批次状态、统计信息、错误报告、生成 Markdown 路径和导入时间
  - 确认导入前只写 import 表，不污染正式题库
- `question_import_item`
  - 保存每个题块的解析结果、标准化题型、分类、状态、错误/警告、原始 Markdown 块和目标 question_id
  - 状态包含 `READY / WARNING / ERROR / UNSUPPORTED / IMPORTED`
- `question_asset`
  - 保存图片、图表等资源的相对路径、类型、大小和目标题目关系
  - 第一版资产存本地文件系统，后续可切对象存储
  - 确认导入后，正式题干中的图片链接通过 `question_id + relative_path` 反查本地文件

## Current Cutover Boundary

- 当前 `PracticeService` 已通过 store 抽象工作，不再直接持有内存 Map
- 默认运行模式仍可保持 mock-first 联调稳定
- 分类、题库、错题统计种子与学生看板模板已从 Java 硬编码迁移到 `resources/mock-data`
- 同步维护的 MySQL 落点已扩展到学生资料、分类、题目、标签、选项、答案、学生看板模板和刷题会话
- `schema.sql`、`seed.sql`、MyBatis Mapper 和条件化数据源配置已补齐，`app.mock.enabled=false` 时已验证可切到 MySQL 持久化模式
- 本地真实库模式已经验证：
  - 登录读取 MySQL 学生资料
  - 分类读取 MySQL 分类树
  - 学生看板读取 MySQL 模板数据
  - 刷题会话、题目快照、答题流水会写入 MySQL
- 当前后续维护原则：
  - `schema.sql` 维护结构
  - `seed.sql` 维护最小联调数据
  - `resources/mock-data` 仅维护 mock 模式回退数据
  - 字段或契约变化时，SQL、Mapper、服务层和前端调用要同步更新

数据库设计在后续阶段细化。
