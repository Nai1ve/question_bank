# Question Import Test Checklist

## 测试目标

验证 `docx -> Markdown -> 预览校验 -> 确认导入 MySQL -> 学生端普通刷题可用` 的题目导入闭环。

## 测试前准备

- 后端使用 `dev` profile 和真实 MySQL：

```bash
cd backend-java
JAVA_HOME=$(/usr/libexec/java_home -v 17) SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

- 默认管理 token：

```text
dev-admin-import-token
```

- 管理页地址：

```text
http://127.0.0.1:8080/admin/import/questions
```

- 建议先恢复测试基线：

```bash
mysql -h127.0.0.1 -uroot onepass_practice < backend-java/sql/reset-student-test-baseline.sql
mysql -h127.0.0.1 -uroot onepass_practice < backend-java/sql/seed.sql
```

## 样例文件

- `docs/prototypes/questions/101-10.docx`
  - 重点验证真实选择题 docx 抽取、Markdown 生成、确认导入
- `docs/prototypes/questions/import_question.docx`
  - 重点验证多题型识别、支持题型入库、简答/填空/材料题暂不支持报告

## P0 必测清单

- [ ] 打开 `/admin/import/questions` 能看到题目导入管理页
- [ ] 输入错误 token 后，分类加载、上传、确认导入等操作会被拒绝
- [ ] 输入 `dev-admin-import-token` 后能加载分类下拉
- [ ] 分类下拉只选择叶子分类，例如 `考研/政治/基础阶段`
- [ ] 上传 `101-10.docx` 后只生成预览批次，不直接写入正式题库
- [ ] 上传结果显示总题数、支持题数、暂不支持题数、错误数、警告数
- [ ] 总题块、可导入、暂不支持、错误、警告统计块均可点击筛选下方题块列表
- [ ] 能在页面看到每个题块的题型、分类、状态、题干摘要、答案、错误/警告信息
- [ ] 点击下载 Markdown，文件内容包含 `#####` 分隔题块、分类、空 `标签【】`、题型、题干、选项、答案、解析
- [ ] 点击确认导入后，批次状态变为 `IMPORTED`
- [ ] 确认导入后，支持题型写入 `question / question_option / question_answer`
- [ ] 确认导入后，本批次题目不新增 `question_tag` 记录
- [ ] 确认导入后，回到小程序普通刷题入口，选择对应分类能抽到新导入题

## 多题型验证

- [ ] 上传 `import_question.docx`
- [ ] 单选识别为 `single`，可以导入
- [ ] 多选识别为 `multiple`，可以导入，多个答案分别入 `question_answer`
- [ ] 不定项识别为 `indefinite`，可以导入，学生端展示为多选交互
- [ ] 判断题识别为 `judge`，可以导入，学生端展示为单选交互
- [ ] 简答题识别为 `short_answer`，进入 `UNSUPPORTED`，不写入正式题库
- [ ] 填空题识别为 `fill_blank`，进入 `UNSUPPORTED`，不写入正式题库
- [ ] 材料题识别为 `material`，进入 `UNSUPPORTED`，不写入学生刷题主链路

## 图片题验证

- [ ] docx 内图片能抽取到 `import-storage/{batchId}/assets/`
- [ ] Markdown 中图片链接保持为 `![...](assets/...)`
- [ ] 确认导入后，题干/选项/解析中的图片链接改写为 `/api/student/question-assets/{questionId}/{filename}`
- [ ] 浏览器直接访问图片接口能返回图片
- [ ] 小程序练习页能显示题干、选项或解析中的图片
- [ ] 小程序统计页能显示解析中的图片

## 分类管理验证

- [ ] 管理页能新增空叶子分类，例如 `考研/政治/导入测试`
- [ ] 新增后分类下拉能看到该叶子分类
- [ ] 空叶子分类可以删除
- [ ] 已有题目的叶子分类不能删除
- [ ] 非叶子分类不能作为导入目标
- [ ] 分类不存在时上传会给出可理解错误，不自动创建分类

## 错误报告验证

- [ ] 缺少答案的题块标记为 `ERROR`
- [ ] 答案不在选项内的题块标记为 `ERROR`
- [ ] 缺少题干的题块标记为 `ERROR`
- [ ] 支持题型少于 2 个选项时标记为 `ERROR`
- [ ] 同批次重复题干只给 `WARNING`，不阻止其他可导入题
- [ ] 批次内存在 `ERROR` 时，错误题不应写入正式题库

## 取消与重复操作验证

- [ ] 上传后点击取消，批次状态变为 `CANCELED`
- [ ] 已取消批次不能确认导入
- [ ] 已确认导入批次重复点击确认不会重复写入题目
- [ ] 下载已确认批次的 Markdown 仍可用

## 学生端回归

- [ ] 普通刷题接口能返回导入题目的题干、选项、题型
- [ ] 导入题不出现自动生成标签
- [ ] 单选题提交后正确判断标准答案
- [ ] 多选题提交后正确判断多个答案
- [ ] 不定项题提交后正确判断多个答案
- [ ] 判断题提交后正确判断 `正确/错误`
- [ ] 练习统计页能展示导入题的答案和解析
- [ ] 错题统计仍能正常累计，不影响 TopXX 和错题本

## 测试后检查

- [ ] 如需恢复固定数据，重新执行 reset + seed
- [ ] 检查 `import-storage/` 不进入 git
- [ ] 检查 `git status` 中没有误加入导入运行时文件
