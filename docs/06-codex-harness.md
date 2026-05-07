# Codex Harness

执行约束：

- 先 mock 后真实接口
- 按阶段推进，不主动扩功能
- 后端先保留单体结构
- 前端页面优先保证导航和基础可用

文件化迭代管理：

- 当前迭代边界：`docs/harness/current/iteration-boundary.md`
- 当前 TODO：`docs/harness/current/todo-list.md`
- 当前待处理反馈：`docs/harness/current/feedback-inbox.md`
- 当前版本维护：`docs/harness/current/git-version.md`
- 已归档迭代：`docs/harness/archive/`
- 模板目录：`docs/harness/templates/`

流程约定：

1. 先定义当前迭代边界
2. 再维护当前 TODO 与待处理反馈
3. 已处理反馈先归档，再归档当前迭代
4. 然后开启下一轮边界与 TODO

反馈维护格式：

- `feedback-inbox.md` 只保留当前待处理 / 待复验 / 新发现问题
- 当前问题使用 Markdown checkbox：`[ ]`
- 已处理归档使用 Markdown checkbox：`[x]`
