# Content Import Spec

本阶段导入目标：运营上传标准化 `docx` 文件，系统抽取为标准 Markdown 中间格式，预览校验通过后再确认写入 MySQL。Markdown 是内部中间格式，但允许下载，便于人工检查和批量修正。

## 支持范围

- 正式入库并进入学生刷题链路：`单选`、`多选`、`不定项选择`、`判断题`
- 可解析但暂不入库：`简答`、`填空`、`材料题`
- 第一版只支持 `.docx`
- 旧版 `.doc` 先人工或脚本转为 `.docx`
- 导入分类以管理页叶子分类下拉选择为准
- 源文档或 Markdown 内的分类字段不覆盖本次选择的导入分类
- 题目必须挂载到叶子分类

## Docx 解析规则

- 题块优先按 `#####` 分割
- 若文档未显式使用 `#####`，按类似 `1.【单选题】...` 的编号题头拆分
- 简单 Word 表格转换为 Markdown table
- 图片抽取到 `assets/`，在 Markdown 中使用相对路径引用
- 复杂图表、嵌入对象、形状图第一版尽量按图片处理；无法抽取时进入错误报告或人工修正
- 文档内无法稳定识别的题块仍生成 Markdown 预览，但会标记 `ERROR` 或 `UNSUPPORTED`

## 标准 Markdown 格式

每道题以单独一行 `#####` 开始。

```markdown
#####
来源题号【1】
分类【考研/政治/基础阶段】
标签【】
题型【单选】

题干：
题干正文，可以包含图片：
![image](assets/image1.png)

选项：
A. 选项 A
B. 选项 B
C. 选项 C
D. 选项 D

答案：
B

解析：
解析正文。

知识点【马克思主义基本原理/唯物论】
难易度【基础】
微课ID【】
来源【101-10.docx】
```

## 题型归一化

- `单选` -> `single`
- `多选` -> `multiple`
- `不定项选择` -> `indefinite`
- `判断题` -> `judge`
- `简答` -> `short_answer`，暂不入库
- `填空` -> `fill_blank`，暂不入库
- `材料题` -> `material`，暂不入库

判断题入库时按选择题结构归一化：

```markdown
选项：
A. 正确
B. 错误
```

## 标签规则

- 当前导入阶段不自动设置题目标签
- `docx -> Markdown` 会保留 `标签【】` 空字段作为格式占位
- 即使源文档或 Markdown 中出现标签值，确认导入时也不写入 `question_tag`
- 标签后续通过后台统一设置，未来再接入 LLM 自动配置

## 校验规则

- 分类必须存在，且必须是叶子分类
- 支持题型必须有题干、至少 2 个选项、答案
- 单选和判断题必须只有 1 个正确答案
- 多选和不定项允许多个正确答案
- 答案选项必须存在于选项列表中
- 同一批次内重复题干默认只警告，不阻止导入
- `ERROR` 不允许导入
- `UNSUPPORTED` 不写入学生刷题主链路
- `READY` 和 `WARNING` 可在确认后写入正式题库

## 导入批次状态

- `PARSED`：已上传并完成解析预览，尚未写入正式题库
- `IMPORTED`：已确认导入支持题型
- `CANCELED`：已取消，不写入正式题库

## 存储规则

- 本地默认：`backend-java/import-storage/{batchId}/questions.md`
- 图片默认：`backend-java/import-storage/{batchId}/assets/`
- 容器部署可通过 `IMPORT_STORAGE_ROOT` 配置
- `import-storage/` 不进入 git
- 标准 Markdown 下载内容保持相对路径，例如 `![image](assets/image1.jpeg)`
- 确认导入正式题库时，题干/解析/选项内的图片链接会改写为 `/api/student/question-assets/{questionId}/{filename}`
- 小程序刷题页和统计页会将 Markdown 图片语法渲染为图片节点

## 验收样例

- `docs/prototypes/questions/101-10.docx`：用于验证考研政治真题 docx 抽取、选择题识别、解析和 Markdown 下载
- `docs/prototypes/questions/import_question.docx`：用于验证多题型识别，支持题型进入可导入队列，简答/填空/材料题进入暂不支持报告
- `docs/prototypes/vocabulary/import_words.docx`：用于验证词库 docx 抽取并写入 `vocabulary_book / vocabulary_word`

## 词库导入格式

词库导入和题库导入分离，词库文档不进入题目解析器。词库导入先生成预览批次，确认后才写入词库表；同一 `词库ID` 重复导入时，会更新词库名称/说明，并覆盖该词库下原有单词明细，避免重复导入产生重复词条。

词库文档第一版只支持 `.docx`，旧版 `.doc` 先人工或脚本转为 `.docx`。

标准字段：

```markdown
词库ID【kaoyan-test-vocab】
词库名称【考研测试词库】
词库说明【用于验证单词导入和背诵计划联调】

#####
英文【abandon】
中文【放弃】
词性【v.】
排序【10】
```

单词条目规则：

- 每个单词推荐用单独一行 `#####` 分割
- 每个单词必须包含 `英文【】` 和 `中文【】`
- `词性【】` 可为空
- `排序【】` 可为空；为空时按条目顺序自动补 `10, 20, 30...`
- 同一文档内英文重复会拒绝导入
- 词库 ID 只能包含英文、数字、下划线和短横线，长度 2-64

## 管理入口

- 页面：`GET /admin/import/questions`
- 保护方式：`APP_ADMIN_IMPORT_TOKEN`
- 本地默认 Token：`dev-admin-import-token`
- 页面首次输入 token 后保存在浏览器本地
- 页面通过 Tab 区分 `题库导入` 和 `词库导入`
- 导入分类通过叶子分类下拉选择
- 预览详情展示题干、选项、答案、解析、知识点和难易度
- 词库预览展示词库元信息、单词、词性、排序、状态和错误/警告
- 分类新增/删除在同一管理页完成，删除只允许空叶子分类
- 上传后默认只解析预览，必须点击“确认导入”才写入 MySQL
- 确认导入不写入题目标签
