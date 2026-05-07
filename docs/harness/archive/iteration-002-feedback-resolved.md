# Iteration 002 Resolved Feedback

- [x] `REAL-DATA-001`
  - 前端默认走真实接口，但后端 `dev` 默认仍是 mock，导致 `词库 / 背诵 / 我的错题本` 返回 400；已改为 `dev` 默认真实 MySQL，并补齐统一测试 seed

- [x] `REAL-DATA-002`
  - 词库页缺少可用真实词库，无法进行背诵联调；已补齐 2 个词库、30 条词汇与激活/历史背诵计划种子

- [x] `REAL-DATA-003`
  - 我的错题本当前无法访问；已补齐真实错题统计种子，并完成错题本接口与页面联调基线

- [x] `HARNESS-002`
  - 当前反馈文件分散，待处理与已处理不易区分；已改为 `feedback-inbox.md` 统一收口待处理问题，并将已处理项归档

- [x] `AUTH-PRACTICE-001`
  - 刷题接口仍在服务内部写死学生 `1001`，会导致不同用户共享会话与错题链路；已改为通过 JWT 解析真实学生身份

- [x] `SEED-PRACTICE-001`
  - 普通刷题默认跳过已答题后，部分叶子分类可练习题数过少；已重新分布错题种子，保留完整 3 题分类用于普通刷题回归

- [x] `FB-002-002`
  - TopXX 在真实库模式下已完成 `筛选 -> 练习 -> 统计 -> 返回` 回归

- [x] `FB-002-003`
  - 普通刷题在真实库模式下已完成筛选、练习、统计与会话恢复回归

- [x] `MOCK-IMPORT-001`
  - mock 文件在 `miniprogramRoot` 外导致 require 失败，已修复

- [x] `UI-NAV-001`
  - 题库第二层点击层级过深，已改为二层展开式交互

- [x] `UI-TOPXX-001`
  - TopXX 分类颗粒过粗，已补到第三级筛选

- [x] `UI-PRACTICE-001`
  - 练习页 WXML 直接调用数组方法存在模板风险，已改为预计算渲染数据

- [x] `PRACTICE-SESSION-001`
  - 未完成会话缺少继续/重开/过期规则，已补齐

- [x] `PRACTICE-SESSION-002`
  - 前端刷题链路仍停留在本地 mock session，已切到后端会话接口

- [x] `AUTH-DASHBOARD-001`
  - 登录和学生看板仍停留在前端 mock，已切到真实后端接口并补充请求日志

- [x] `DATA-SOURCE-001`
  - 分类、题库和学生看板静态内容硬编码在后端代码里，已外置到 `resources/mock-data`

- [x] `MYSQL-DESIGN-001`
  - 分类、题目与学生看板尚无稳定 MySQL 设计和 seed，已补齐 `schema.sql / seed.sql / Mapper`

- [x] `MYSQL-RUNTIME-001`
  - 练习会话当前仍为单实例内存存储，已切到真实 MySQL 运行态并验证写入 `practice_session / practice_session_question / practice_answer_record`

- [x] `MYSQL-RUNTIME-002`
  - 本地 MySQL 运行态尚未联调，已安装 `mysql@8.4`、导入 `schema.sql / seed.sql` 并完成接口烟测

- [x] `TAG-FILTER-001`
  - 刷题筛选页标签仍为前端硬编码，已切到后端真实标签接口

- [x] `MYSQL-PERF-001`
  - MySQL 模式下错题统计和已答题判断逐题查库，已改为批量查询
