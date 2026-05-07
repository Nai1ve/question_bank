# Harness Workflow

这套 Harness 用文件系统维护每一轮迭代，而不是把边界、反馈和回归状态散落在对话里。

## 目录

- `current/`：当前迭代唯一真源
- `archive/`：已完成内容和已处理反馈归档
- `templates/`：新迭代模板

## 当前迭代维护规则

1. 先写 `iteration-boundary.md`
2. 再写 `todo-list.md`
3. 待处理反馈统一进入 `feedback-inbox.md`
4. 版本维护和 git 状态进入 `git-version.md`

## 当前文件职责

- `iteration-boundary.md`：定义本轮做什么、不做什么、验收边界
- `todo-list.md`：当前执行项和后续动作，不作为用户反馈入口
- `feedback-inbox.md`：当前待处理 / 待复验 / 新发现问题唯一入口
  - 使用 Markdown checkbox：`[ ]`
- `git-version.md`：当前版本口径、建议提交粒度、git 维护状态
- `student-test-checklist.md`：统一手工测试步骤
- `student-test-data.md`：固定测试数据与预期结果

## 归档规则

- 已处理反馈统一归档到 `archive/iteration-XXX-feedback-resolved.md`
  - 归档项统一使用 Markdown checkbox：`[x]`
- 已完成迭代总结保留在 `archive/iteration-XXX-*.md`
- `current/bug-feedback.md` 不再继续维护，仅保留迁移说明

## 完成迭代后的动作

1. 将本轮已处理反馈写入对应 archive
2. 更新版本记录
3. 确认下一轮边界与 TODO
4. 清理 `feedback-inbox.md`，只保留下一轮仍待处理的问题

## 说明

当前工作区尚未初始化 git 仓库，因此版本维护先用文件记录，等仓库初始化后再切换到真实分支、提交和 tag。
