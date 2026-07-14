# Page Flow

## 全部页面（15 页）

| 序号 | 页面 | 目录 | 说明 |
|------|------|------|------|
| 1 | 登录页 | `pages/login/index` | 微信登录入口，已登录自动跳转首页 |
| 2 | 学习首页（Tab） | `pages/study-home/index` | 主入口，展示头像、姓名，导航到四大功能区 |
| 3 | 我的页（Tab） | `pages/mine/index` | 个人信息、在刷题库、在背单词、刷题总结模板 |
| 4 | 我的错题本 | `pages/wrong-book/index` | 按累计答错次数排序的错题列表 |
| 5 | TopXX | `pages/topxx/index` | 错题复练，选择 Top N 范围 + 分类筛选 |
| 6 | 题库方向列表 | `pages/question-bank-list/index` | 一级分类列表 |
| 7 | 题库分类列表 | `pages/question-category-list/index` | 子分类浏览，支持多层展开 |
| 8 | 刷题筛选页 | `pages/practice-filter/index` | 选择题量、反馈模式、标签筛选 |
| 9 | 刷题练习页 | `pages/practice-session/index` | 核心答题页，支持单选/多选/不定项/判断 |
| 10 | 刷题统计页 | `pages/practice-summary/index` | 答题结果、逐题对错与解析 |
| 11 | 背诵计划页 | `pages/recite-plan/index` | 选择词库和每日数量，创建/查看背诵计划 |
| 12 | Day 列表 | `pages/recite-days/index` | 展示计划中各 Day 状态（待学习/待测试/已完成） |
| 13 | 背诵学习页 | `pages/recite-study/index` | 展示英文、中文、词性，学习后选择测试模式 |
| 14 | 背诵默写页 | `pages/recite-session/index` | 逐行输入答案，支持中译英/英译中 |
| 15 | 背诵复盘页 | `pages/recite-summary/index` | 默写结果统计、逐词对错与标准答案 |

## 页面流转

### 主干

```
登录
 └─> 学习首页 (Tab) <──> 我的 (Tab)
```

### 刷题流程

```
学习首页
 ├─> 题库方向列表 ─> 题库分类列表 ─> 刷题筛选页 ─> 刷题练习页 ─> 刷题统计页
 └─> TopXX ─> 刷题练习页 ─> 刷题统计页
```

### 背诵流程

```
学习首页
 └─> 背诵计划页 ─> Day 列表
                      ├─ 待学习 Day ─> 背诵学习页 ─> 背诵默写页 ─> 背诵复盘页 ─> Day 列表
                      ├─ 待测试 Day ─> 背诵默写页 ─> 背诵复盘页 ─> Day 列表
                      └─ 已完成 Day ─> 弹窗选择
                           ├─ 查看最近一次结果 ─> 背诵复盘页
                           └─ 重新学习并测试 ─> 背诵学习页
```

### 我的页二级导航

```
我的
 ├─> 我的错题本 ─> TopXX
 ├─> 题库方向列表 ─> ...
 └─> 背诵计划页 ─> ...
```

### 会话恢复

刷题筛选页和 TopXX 入口启动练习前，会检查是否存在未完成会话。若存在，提示"继续上次练习 / 重新开始"。
