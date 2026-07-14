# Vocabulary Import Test Checklist

## 目标

验证 `docs/prototypes/vocabulary/import_words.docx` 能通过后台词库导入入口写入 `vocabulary_book / vocabulary_word`，并能在学生端背诵计划中被读取。

## 后台导入

- [ ] 打开 `/admin/import/questions`
- [ ] 输入 `dev-admin-import-token`
- [ ] 切换到 `词库导入` Tab
- [ ] 选择 `docs/prototypes/vocabulary/import_words.docx`
- [ ] 点击 `上传并解析`
- [ ] 页面展示词库批次预览，状态为 `PARSED`
- [ ] 页面展示 `kaoyan-test-vocab / 考研测试词库`
- [ ] 页面展示 20 个单词，首个为 `abandon`，最后一个为 `evaluate`
- [ ] 点击 `确认导入`
- [ ] 页面提示导入完成，批次状态变为 `IMPORTED`

## 数据验证

- [ ] `vocabulary_book` 新增或更新 `kaoyan-test-vocab`
- [ ] `vocabulary_word` 中 `kaoyan-test-vocab` 对应 20 条启用单词
- [ ] 重复导入同一文档不会产生重复单词，只保留 20 条

## 学生端回归

- [ ] `GET /api/student/vocabulary/books` 能看到 `考研测试词库`
- [ ] 背诵计划页能选择 `考研测试词库`
- [ ] 创建背诵计划后，Day 学习页能看到导入单词

## 边界验证

- [ ] 将词库 docx 上传到 `题库导入` 区域时不会写入题库
- [ ] 缺少 `词库ID`、`词库名称`、`英文` 或 `中文` 时导入失败并显示校验错误
- [ ] 同一文档内英文重复时导入失败
