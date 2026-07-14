# backend-java

Spring Boot 单体服务。

## 当前状态

- 已提供学生端登录、分类、标签、刷题、TopXX、错题本、词库、背诵计划、Day 列表、默写、复盘、学生看板接口
- 已补齐 MySQL 8.x 表结构、MyBatis Mapper 和统一测试 seed
- 已保留 mock 数据回退能力，但 `dev` 环境默认走真实 MySQL
- 已提供内部题目导入管理页，支持 `docx -> Markdown -> MySQL` 两阶段导入

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

导入管理页默认 token 为 `dev-admin-import-token`，本地可通过环境变量覆盖：

```bash
APP_ADMIN_IMPORT_TOKEN=dev-admin-import-token \
IMPORT_STORAGE_ROOT=import-storage \
IMPORT_DEFAULT_CATEGORY_PATH=考研/政治/基础阶段 \
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
- 题目导入批次、题目资产和导入测试后的正式题库数据

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

构建后端镜像：

```bash
cd backend-java
docker build -t onepass-practice-backend:local .
```

如果本机 Docker 镜像源拉取 Docker Hub 基础镜像失败，可切到 public ECR 的 Docker Official Images 镜像源：

```bash
cd backend-java
docker build \
  --build-arg MAVEN_IMAGE=public.ecr.aws/docker/library/maven:3.9.9-eclipse-temurin-17 \
  --build-arg RUNTIME_IMAGE=public.ecr.aws/docker/library/eclipse-temurin:17-jre \
  -t onepass-practice-backend:local .
```

如果 Maven 基础镜像下载仍不稳定，也可以先在本机完成 jar 构建，再只打运行镜像：

```bash
cd backend-java
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -DskipTests package
docker build \
  --target local-jar \
  --build-arg RUNTIME_IMAGE=public.ecr.aws/docker/library/eclipse-temurin:17-jre \
  -t onepass-practice-backend:local .
```

然后直接启动：

```bash
cd backend-java
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -DskipTests package
docker compose up --build
```

说明：

- `mysql` 首次启动会自动执行 `sql/schema.sql` 和 `sql/seed.sql`
- `backend` 默认使用 `SPRING_PROFILES_ACTIVE=dev`，并显式以真实 MySQL 模式运行
- `backend` 在 `.env.example` 中默认使用 `DOCKER_BUILD_TARGET=local-jar`，会复用本机已经打出的 jar；如需 Docker 内全量 Maven 构建，改成 `DOCKER_BUILD_TARGET=packaged`
- `backend` 默认挂载 `./import-storage`，用于保存导入 Markdown 和图片
- 小程序开发者工具联调时，默认仍可访问 `http://127.0.0.1:8080`

## 微信登录与发布测试

真实微信登录使用小程序端 `wx.login` 获取的 `code`，后端调用微信 `code2Session` 换取 `openid`，再按 `openid` 创建或复用本地学生账号。后端不会接收微信回调，所以本地网络不需要暴露回调地址；只要求后端容器能够访问微信接口。

Docker 或发布测试前必须配置：

```bash
WECHAT_MINI_APP_ID=<小程序 appid>
WECHAT_MINI_APP_SECRET=<小程序 secret>
APP_MOCK_ENABLED=false
```

跨域仅影响浏览器中的后台管理页或 H5 调试，不影响小程序 `wx.request`。如后台管理页部署到独立域名，设置：

```bash
APP_CORS_ALLOWED_ORIGINS=https://admin.example.com,https://api.example.com
```

小程序发布/体验版不能继续请求 `http://127.0.0.1:8080` 或普通本地 IP。发布测试推荐把 Docker 后端部署到可公网访问的 HTTPS 域名，并在微信公众平台配置为 request 合法域名；真机局域网联调时，小程序端 `BASE_URL` 需要改成本机局域网 IP，并确保手机和电脑在同一网络。

## 题目导入管理页

- 页面：`http://127.0.0.1:8080/admin/import/questions`
- 默认 token：`dev-admin-import-token`
- 生产或本地覆盖：`APP_ADMIN_IMPORT_TOKEN=<your-token>`
- 导入分类通过叶子分类下拉选择
- 分类新增支持填写路径，例如 `考研/政治/2010真题`
- 分类删除只允许空叶子分类，已有题目的分类会拒绝删除
- 上传后只解析预览，确认导入后才写入正式题库
- 导入文件和中间 Markdown 存储在 `import-storage/`
- 带图题确认导入后，学生端通过 `/api/student/question-assets/{questionId}/{filename}` 读取图片资源

## Docker 模式下重置测试基线

```bash
cd backend-java
docker compose exec -T mysql sh -c 'mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < /docker-entrypoint-reset/reset-student-test-baseline.sql && mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < /docker-entrypoint-initdb.d/02-seed.sql'
```

## 说明

当前这台机器使用的是 Homebrew 安装的 `mysql@8.4`，本地 `root` 默认无密码。
