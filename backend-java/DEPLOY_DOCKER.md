# Docker 部署说明

目标：后端和 MySQL 都用 Docker 运行。服务器只需要安装 Docker 和 Docker Compose。

## 需要上传的文件

把 `backend-java/` 目录上传到服务器，至少需要包含：

- `Dockerfile`
- `docker-compose.yml`
- `.env.production.example`
- `.dockerignore`
- `pom.xml`
- `src/`
- `sql/`

运行后会产生并需要保留：

- `.env`：服务器真实配置，不提交代码仓库
- `import-storage/`：题库/词库导入文件和图片资源
- Docker volume `mysql-data`：MySQL 数据

## 服务器准备

```bash
docker --version
docker compose version
```

如果没有 Docker，先按云服务器系统安装 Docker Engine 和 Docker Compose plugin。

## 首次部署

```bash
cd backend-java
cp .env.production.example .env
vi .env
docker compose up -d --build
docker compose logs -f backend
```

默认使用 `DOCKER_BUILD_TARGET=packaged`，服务器会在 Docker 构建阶段完成 Maven 打包，不需要提前上传 `target/practice-0.0.1-SNAPSHOT.jar`。

如果服务器已经配置了国内 Docker 镜像源，可以将 `.env` 中的 `MYSQL_IMAGE`、`MAVEN_IMAGE` 和 `RUNTIME_IMAGE` 改为对应镜像地址。`MAVEN_MIRROR_URL` 默认使用阿里云 Maven 公共仓库。

健康检查：

```bash
docker compose ps
curl -i http://127.0.0.1:8080/admin/import/questions
```

首次启动 MySQL 会自动执行：

- `sql/schema.sql`
- `sql/seed.sql`

后续同一个 `mysql-data` volume 已存在时，MySQL 不会重复执行初始化 SQL。

## 必改配置

`.env` 中这些值上线前必须修改：

```bash
MYSQL_PASSWORD=<强密码>
MYSQL_ROOT_PASSWORD=<强密码>
APP_JWT_SECRET=<32位以上随机字符串>
WECHAT_MINI_APP_ID=<微信小程序 AppID>
WECHAT_MINI_APP_SECRET=<微信小程序 AppSecret>
APP_ADMIN_IMPORT_TOKEN=<后台导入管理 token>
APP_CORS_ALLOWED_ORIGINS=https://你的接口域名,https://你的后台域名
```

说明：

- `APP_MOCK_ENABLED=false`：发布测试必须走真实 MySQL。
- `MYSQL_BIND_ADDRESS=127.0.0.1`：MySQL 只监听服务器本机，避免直接暴露公网。
- `DOCKER_BUILD_TARGET=packaged`：服务器只依赖 Docker，在 Docker 内部完成 Maven 构建。
- `SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=50MB`：支持较大的 docx 导入。
- `SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=60MB`：请求总大小上限。
- `SERVER_FORWARD_HEADERS_STRATEGY=framework`：后端放在 HTTPS 反向代理后时识别转发头。

## 小程序发布测试配置

小程序发布/体验版不能请求 `127.0.0.1`、局域网 IP 或普通 HTTP。

需要准备：

- 一个备案/可用域名，例如 `api.example.com`
- HTTPS 证书
- Nginx 或云厂商负载均衡，把 `https://api.example.com` 转发到服务器 `127.0.0.1:8080`
- 微信公众平台把 `https://api.example.com` 配置为 request 合法域名
- 小程序 `frontend-miniprogram/miniprogram/services/config.js` 中的 `BASE_URL` 改为 `https://api.example.com`

后端不需要微信回调地址。登录链路是小程序调用 `wx.login`，后端用 `WECHAT_MINI_APP_ID` 和 `WECHAT_MINI_APP_SECRET` 调微信 `code2Session`。

## Nginx 示例

```nginx
server {
    listen 443 ssl http2;
    server_name api.example.com;

    ssl_certificate /etc/nginx/certs/api.example.com.pem;
    ssl_certificate_key /etc/nginx/certs/api.example.com.key;

    client_max_body_size 60m;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto https;
    }
}
```

## 常用命令

查看状态：

```bash
docker compose ps
```

查看日志：

```bash
docker compose logs -f backend
docker compose logs -f mysql
```

重启后端：

```bash
docker compose restart backend
```

重新构建并启动：

```bash
docker compose up -d --build
```

进入 MySQL：

```bash
docker compose exec mysql mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"
```

备份数据库：

```bash
docker compose exec -T mysql mysqldump -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" > onepass_practice.sql
```

停止服务：

```bash
docker compose down
```

删除数据库数据重新初始化：

```bash
docker compose down -v
docker compose up -d --build
```

注意：`docker compose down -v` 会删除 MySQL volume，正式环境不要随意执行。

## 本地先打 jar 的替代路线

如果服务器拉 Maven 镜像慢，也可以先在本机打 jar，再上传 `target/practice-0.0.1-SNAPSHOT.jar`，并把 `.env` 改成：

```bash
DOCKER_BUILD_TARGET=local-jar
```

本机打包命令：

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -DskipTests package
```
