# API Contract

## Auth

### `POST /api/student/auth/login`

请求体：

```json
{
  "code": "wechat-login-code"
}
```

响应体：

```json
{
  "success": true,
  "data": {
    "token": "jwt-token",
    "user": {
      "id": 1001,
      "displayName": "微信用户",
      "avatarUrl": ""
    }
  }
}
```

## Student Dashboard

### `GET /api/student/dashboard`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 返回当前学生的看板数据，包括个人信息、当前刷题库、当前背诵计划和看板模板文本块

## Category

### `GET /api/student/categories?parentId=...`

- `parentId` 为空时返回一级分类
- `parentId` 不为空时返回指定节点的子分类

## Vocabulary

### `GET /api/student/vocabulary/books`

- 返回当前可选词库
- 第一版用于背诵计划页词库选择

## Wrong Book

### `GET /api/student/wrong-book?limit=20`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 返回当前学生按累计答错次数排序的错题列表
- 用于“我的错题本”页面展示
- 第一版只做列表展示和 TopXX 跳转入口

## Tag

### `GET /api/student/tags?categoryId=...`

- `categoryId` 为空时返回全局可用标签
- `categoryId` 不为空时，返回该分类范围内可用标签
- 第一版返回标签名和题目数，供刷题筛选页展示

响应体：

```json
{
  "success": true,
  "data": [
    {
      "name": "基础概念",
      "questionCount": 2
    },
    {
      "name": "高频选择题",
      "questionCount": 1
    }
  ]
}
```

## Practice

### `POST /api/student/practice/sessions`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 普通刷题时 `categoryId` 必须是叶子分类 ID
- TopXX 选择“全部分类”时 `categoryId` 可为空字符串，表示全局错题范围
- 会话恢复的第一版匹配范围按 `entryType + categoryId`
- 若同一恢复范围下已存在未完成会话，后端会拒绝重复新建并返回提示

请求体：

```json
{
  "entryType": "normal",
  "categoryId": "politics-basic",
  "categoryName": "基础阶段",
  "questionCount": 20,
  "feedbackMode": "immediate",
  "selectedTags": ["基础概念", "高频选择题"]
}
```

响应体：

```json
{
  "success": true,
  "data": {
    "ok": true,
    "sessionId": "mock-backend-session",
    "totalCount": 20,
    "entryType": "normal",
    "categoryName": "基础阶段",
    "feedbackMode": "immediate",
    "message": null
  }
}
```

### `GET /api/student/practice/sessions/{sessionId}`

- 需要携带 `Authorization: Bearer <jwt-token>`
响应体：

```json
{
  "success": true,
  "data": {
    "sessionId": "practice-demo",
    "entryType": "normal",
    "categoryId": "politics-basic",
    "categoryName": "基础阶段",
    "feedbackMode": "immediate",
    "status": "ONGOING",
    "currentIndex": 0,
    "currentSequence": 1,
    "totalCount": 10,
    "completed": false,
    "startedAt": "2026-04-30T09:00:00Z",
    "lastActiveAt": "2026-04-30T09:03:00Z",
    "completedAt": null,
    "expiredAt": null,
    "currentQuestion": {
      "id": "politics-basic-1",
      "type": "single",
      "tags": ["基础概念", "高频选择题"],
      "stem": "马克思主义哲学认为，物质和意识的关系中，哪一项表述正确？",
      "options": [
        { "key": "A", "content": "意识决定物质" },
        { "key": "B", "content": "物质决定意识" }
      ],
      "userAnswer": []
    }
  }
}
```

### `GET /api/student/practice/sessions/active?entryType=...&categoryId=...`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 查询当前学生在同一刷题入口下是否存在未完成会话
- 当前第一版按 `entryType + categoryId` 匹配恢复范围
- 若不存在，则返回 `data: null`

### `POST /api/student/practice/sessions/{sessionId}/answers`

- 需要携带 `Authorization: Bearer <jwt-token>`
请求体：

```json
{
  "questionId": "politics-basic-1",
  "selectedOptions": ["B"]
}
```

响应体：

```json
{
  "success": true,
  "data": {
    "correct": true,
    "standardAnswer": "B",
    "userAnswer": "B",
    "analysis": "唯物论的基本立场是物质决定意识。"
  }
}
```

### `POST /api/student/practice/sessions/{sessionId}/next`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 推进到下一题
- 如果当前已是最后一题，则仍返回最后一题所在会话视图

### `POST /api/student/practice/sessions/{sessionId}/complete`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 结束本轮练习并生成统计

### `POST /api/student/practice/sessions/{sessionId}/abandon`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 主动放弃当前未完成会话
- 放弃后该会话不可继续恢复
- 前端若选择“重新开始”，应先调用此接口再创建新会话

### `GET /api/student/practice/sessions/{sessionId}/summary`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 获取当前练习统计
- 如果统计尚未生成，后端会先完成本轮统计再返回
- 未完成会话默认按 `lastActiveAt` 保留 `7 天`
- 超过 `7 天` 未继续则会话标记为 `EXPIRED`

## Admin Question Import

管理导入接口仅供内部运营使用，第一版使用 `X-Admin-Import-Token` 轻量保护，token 来源为后端配置 `APP_ADMIN_IMPORT_TOKEN`。本地默认值是 `dev-admin-import-token`。导入默认只解析预览，必须确认后才写入正式题库。

### `GET /admin/import/questions`

- 打开后端内置题目导入管理页
- 支持上传 `.docx`、查看批次预览、下载中间 Markdown、确认导入和取消批次

### `POST /api/admin/import/questions/docx`

- Header：`X-Admin-Import-Token: <token>`
- FormData：
  - `file`: `.docx`
  - `categoryPath`: 本次导入分类，例如 `考研/政治/基础阶段`
- 导入分类以该字段为准，源文档或 Markdown 内的分类字段不覆盖本次选择

响应体：

```json
{
  "success": true,
  "data": {
    "batchId": "qib-...",
    "originalFilename": "101-10.docx",
    "status": "PARSED",
    "totalCount": 10,
    "supportedCount": 10,
    "unsupportedCount": 0,
    "errorCount": 0,
    "warningCount": 0,
    "importedCount": 0,
    "markdownPath": ".../import-storage/qib-.../questions.md",
    "items": []
  }
}
```

### `GET /api/admin/import/questions/{batchId}`

- Header：`X-Admin-Import-Token: <token>`
- 返回批次统计、逐题预览、题干、选项、答案、解析、错误和警告

### `GET /api/admin/import/questions/{batchId}/markdown`

- Header：`X-Admin-Import-Token: <token>`
- 返回生成的标准 Markdown
- 管理页下载场景也支持 `?token=...`

### `POST /api/admin/import/questions/{batchId}/confirm`

- Header：`X-Admin-Import-Token: <token>`
- 将 `READY / WARNING` 的支持题型写入 `question / question_option / question_answer`
- 当前导入阶段不写入 `question_tag`；题目标签后续通过后台统一设置
- `ERROR / UNSUPPORTED` 不写入学生刷题主链路

### `POST /api/admin/import/questions/{batchId}/cancel`

- Header：`X-Admin-Import-Token: <token>`
- 取消本次批次，不写入正式题库

## Admin Vocabulary Import

词库导入接口仅供内部运营使用，和题库导入分离。词库 docx 上传后先生成预览批次，确认导入后才写入 `vocabulary_book / vocabulary_word`；同一 `词库ID` 重复导入时，会更新词库名称/说明，并覆盖该词库下原有单词明细。

### `POST /api/admin/import/vocabulary/docx`

- Header：`X-Admin-Import-Token: <token>`
- FormData：
  - `file`: `.docx`
- 只解析预览，不直接写入词库表

词库文档字段：

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

响应体：

```json
{
  "success": true,
  "data": {
    "batchId": "vib-...",
    "originalFilename": "import_words.docx",
    "status": "PARSED",
    "bookId": "kaoyan-test-vocab",
    "bookName": "考研测试词库",
    "description": "用于验证单词导入和背诵计划联调",
    "totalCount": 20,
    "importableCount": 20,
    "errorCount": 0,
    "warningCount": 0,
    "importedCount": 0,
    "errors": [],
    "warnings": [],
    "items": []
  }
}
```

### `GET /api/admin/import/vocabulary/{batchId}`

- Header：`X-Admin-Import-Token: <token>`
- 返回词库批次预览、统计和逐词条报告

### `POST /api/admin/import/vocabulary/{batchId}/confirm`

- Header：`X-Admin-Import-Token: <token>`
- 校验通过后写入 `vocabulary_book / vocabulary_word`
- 有错误的词库批次不能确认
- 确认后批次状态变为 `IMPORTED`

### `POST /api/admin/import/vocabulary/{batchId}/cancel`

- Header：`X-Admin-Import-Token: <token>`
- 取消本次词库导入批次，不写入词库表

### `GET /api/admin/import/categories`

- Header：`X-Admin-Import-Token: <token>`
- 返回全部分类和叶子分类列表
- 导入页下拉框只使用 `leafCategories`

响应体：

```json
{
  "success": true,
  "data": {
    "categories": [
      {
        "id": "politics-basic",
        "parentId": "kaoyan-politics",
        "name": "基础阶段",
        "path": "考研/政治/基础阶段",
        "leaf": true,
        "questionCount": 3
      }
    ],
    "leafCategories": []
  }
}
```

### `POST /api/admin/import/categories`

- Header：`X-Admin-Import-Token: <token>`
- 显式新增分类路径，最多 3 层
- 可自动补齐缺失的父级分类
- 如果要把已有叶子分类改成父级，要求该叶子分类没有题目

请求体：

```json
{
  "path": "考研/政治/2010真题",
  "subtitle": "导入测试"
}
```

### `DELETE /api/admin/import/categories/{categoryId}`

- Header：`X-Admin-Import-Token: <token>`
- 仅允许删除空叶子分类
- 已有题目或仍有子分类时会拒绝删除

### `GET /api/student/question-assets/{questionId}/{filename}`

- 返回导入题目关联的图片/图表资源
- 确认导入时，正式题库中的 Markdown 图片链接会从 `assets/image1.jpeg` 改写为该接口地址
- 第一版用于小程序刷题页和统计页渲染带图题

## Recite

### `GET /api/student/recite/plans/active`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 返回当前学生的有效背诵计划
- 若无计划，返回 `data: null`

### `POST /api/student/recite/plans`

- 需要携带 `Authorization: Bearer <jwt-token>`
请求体：

```json
{
  "bookId": "kaoyan-core-vocab",
  "dailyCount": 5
}
```

- 若存在旧的激活计划，创建新计划前会将旧计划标记为 `SUPERSEDED`

### `GET /api/student/recite/plans/{planId}/days`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 返回计划摘要和 Day 列表
- Day 状态当前支持：
  - `PENDING_STUDY`
  - `PENDING_TEST`
  - `COMPLETED`
- Day 列表额外返回：
  - `studyCompleted`
  - `latestRecordId`
  - `latestMode`

### `GET /api/student/recite/plans/{planId}/days/{dayNumber}/study`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 返回当前 Day 的学习态单词卡
- 每个词条包含：
  - `english`
  - `chinese`
  - `partOfSpeech`

### `POST /api/student/recite/plans/{planId}/days/{dayNumber}/study-complete`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 进入测试前先标记当前 Day 已完成学习
- 不会覆盖历史测试记录

### `GET /api/student/recite/plans/{planId}/days/{dayNumber}?mode=cn_to_en`

- 需要携带 `Authorization: Bearer <jwt-token>`
- `mode` 支持：
  - `cn_to_en`
  - `en_to_cn`
- 返回当前 Day 的默写题面数据
- 若当前 Day 尚未学习，后端返回错误：`请先完成学习再开始测试`

### `POST /api/student/recite/plans/{planId}/days/{dayNumber}/submit`

- 需要携带 `Authorization: Bearer <jwt-token>`
请求体：

```json
{
  "mode": "cn_to_en",
  "answers": [
    {
      "wordId": 6,
      "value": "enhance"
    }
  ]
}
```

- 第一版按整页统一提交
- 英文答案忽略大小写
- 中文答案做基础空白归一化，不做语义模糊判断

### `GET /api/student/recite/records/{recordId}`

- 需要携带 `Authorization: Bearer <jwt-token>`
- 返回某次 Day 默写的复盘结果
