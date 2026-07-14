# Current TODO List

## In Progress

- 实现并验证 `docx -> Markdown -> MySQL` 题目导入基线
- 用 `docs/prototypes/questions/101-10.docx` 验证真题导入
- 用 `docs/prototypes/questions/import_question.docx` 验证多题型识别和暂不支持报告

## Next

- 管理页上传、预览、下载 Markdown、确认导入、取消批次联调
- 管理页分类下拉、新增空分类、删除空分类联调
- 确认导入后的题目能进入普通刷题链路
- 带图题在练习页和统计页继续做小程序真机/开发者工具视觉复验
- 导入错误报告字段继续根据运营 docx 样例补齐
- 评估 `.doc -> .docx` 的人工流程或后续脚本方案
- 持续维护 `schema.sql / seed.sql / reset-student-test-baseline.sql / 05-content-import-spec.md`

## Deferred

- 真实微信登录联调
- TopXX 定期模型分析、错因总结与练后展示
- `.doc` 直接解析
- 材料题子题进入学生刷题主链路
- 题目导入异步任务中心
