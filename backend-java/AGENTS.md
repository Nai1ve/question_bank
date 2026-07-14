# Backend Notes

- 根包：`com.onepass.practice`
- 当前运行模式：dev profile 默认 MySQL 持久化（`app.mock.enabled=false`），mock 模式可作为回退
- MyBatis Mapper + XML 已就位，覆盖全部业务表
- `schema.sql` 维护结构，`seed.sql` 维护联调数据，`reset-student-test-baseline.sql` 维护测试基线
- 真实微信 `code2session` 在后续联调阶段接入

