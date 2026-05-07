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
