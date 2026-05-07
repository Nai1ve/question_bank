# backend-java

Spring Boot 单体服务。

## 当前状态

- 已提供学生端登录、分类、标签、刷题、TopXX、错题本、词库、背诵计划、Day 列表、默写、复盘、学生看板接口
- 已补齐 MySQL 8.x 表结构、MyBatis Mapper 和统一测试 seed
- 已保留 mock 数据回退能力，但 `dev` 环境默认走真实 MySQL

## 运行模式

- `dev` 默认模式：真实 MySQL
  - `SPRING_PROFILES_ACTIVE=dev`
  - `app.mock.enabled` 默认是 `false`
  - 当前推荐的小程序联调口径
- 显式 mock 模式：
  - `APP_MOCK_ENABLED=true`
  - 仅在需要脱离数据库排查页面问题时使用

示例：

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

如需显式切 mock：

```bash
APP_MOCK_ENABLED=true SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

## 数据库维护口径

- `sql/schema.sql`：当前数据库结构唯一维护源
- `sql/seed.sql`：学生端统一联调测试基线唯一维护源
- `sql/reset-student-test-baseline.sql`：清空本地测试运行态，恢复到可重跑 seed 的状态

后续开发中，只要以下任一内容发生变化，都必须同步维护上述 SQL：

- 分类树
- 题目/标签/答案
- 错题统计基线
- 词库与背诵计划
- 学生看板测试内容

## 本地数据库初始化

```bash
mysql -h127.0.0.1 -uroot -e "CREATE DATABASE IF NOT EXISTS onepass_practice CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -h127.0.0.1 -uroot onepass_practice < sql/schema.sql
mysql -h127.0.0.1 -uroot onepass_practice < sql/seed.sql
```

## 学生端测试基线重置

统一测试前，按下面顺序执行：

```bash
mysql -h127.0.0.1 -uroot onepass_practice < sql/reset-student-test-baseline.sql
mysql -h127.0.0.1 -uroot onepass_practice < sql/seed.sql
```

重置后，基线口径应为：

- 测试学生：`1001 / 微信用户`
- 词库：
  - `考研核心词汇` 18 词
  - `基金从业高频词` 12 词
- 当前激活背诵计划：`考研核心词汇 · Day 2`
- 历史计划：存在 1 条 `SUPERSEDED`
- 错题本与 TopXX：具备 10 条真实错题统计基线

## Docker / Compose 启动

先复制环境变量模板：

```bash
cd backend-java
cp .env.example .env
```

然后直接启动：

```bash
cd backend-java
docker compose up --build
```

说明：

- `mysql` 首次启动会自动执行 `sql/schema.sql` 和 `sql/seed.sql`
- `backend` 默认使用 `SPRING_PROFILES_ACTIVE=dev`，并显式以真实 MySQL 模式运行
- 小程序开发者工具联调时，默认仍可访问 `http://127.0.0.1:8080`

## Docker 模式下重置测试基线

```bash
cd backend-java
docker compose exec -T mysql sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < /docker-entrypoint-initdb.d/03-reset-student-test-baseline.sql && mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < /docker-entrypoint-initdb.d/02-seed.sql'
```

## 说明

当前这台机器使用的是 Homebrew 安装的 `mysql@8.4`，本地 `root` 默认无密码。
