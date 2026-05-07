# Current TODO List

## In Progress

- 按定稿收口清单回归学生端全链路
- 验证自定义 TabBar、字体、安全区、背诵学习态与容器部署都符合当前版本边界
- 用 `sql/reset-student-test-baseline.sql + sql/seed.sql` 反复验证测试基线是否可重复恢复

## Next

- 根据统一测试结果整理新的待处理反馈，只写入 `feedback-inbox.md`
- 校验普通刷题在“已答题跳过”规则下，各叶子分类是否仍有足够题量
- 校验 TopXX 和错题本排序是否与 `student-test-data.md` 保持一致
- 校验重建背诵计划后，旧计划是否正确转为 `SUPERSEDED`
- 校验 Docker Compose 下 `mysql + backend` 是否可直接启动
- 持续维护 `schema.sql / seed.sql / reset-student-test-baseline.sql / student-test-data.md`
- 将学生看板文本模板逐步替换为更真实的统计数据模型

## Deferred

- 真实微信登录联调
- Markdown 导入工作流
- TopXX 定期模型分析、错因总结与练后展示
