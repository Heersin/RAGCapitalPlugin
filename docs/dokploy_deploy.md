# Dokploy 部署说明

这份项目已经准备好了可直接用于 Dokploy 的基础文件：

- `Dockerfile`
- `docker-compose.yml`
- `.env.example`
- `docker/start.sh`

## 1. 推荐部署方式

推荐在 Dokploy 里使用 `Docker Compose` 项目部署。

这样可以直接利用：

- `docker-compose.yml`
- `.env` 环境变量
- `raglocal_runtime` 持久卷

在 Dokploy 场景下，Compose 现在只 `expose 8000`，不再把端口绑定到宿主机，避免 `already bind` 冲突。

## 2. 需要填写的环境变量

把 `.env.example` 复制成 `.env` 后，至少按需填写这些值：

- `LLM_BASE_URL`
- `LLM_API_KEY`
- `LLM_MODEL`

如果你暂时不接外部 LLM，也可以先保留为空，系统会回退到本地 mock generation。

如果你希望容器启动时一并导入 PDF，把：

- `INGEST_ENABLE_PDF=true`

即可，项目镜像里已经包含 PDF 解析依赖。

## 3. 启动时会发生什么

容器入口是：

- `docker/start.sh`

默认行为：

1. 如果 `AUTO_INGEST=true`
   容器启动时会自动执行一次本地 ingest

2. 然后启动 Web 服务：
   - Host: `0.0.0.0`
   - Port: `8000`

## 4. 推荐环境变量

```env
HOST=0.0.0.0
PORT=8000
LLM_BASE_URL=https://your-openai-compatible-host/v1
LLM_API_KEY=replace-with-your-llm-api-key
LLM_MODEL=replace-with-your-model-name
LLM_TIMEOUT_SECONDS=60
AUTO_INGEST=true
INGEST_DOC_ROOT=sample/plugin
INGEST_REBUILD=false
INGEST_ENABLE_PDF=false
```

## 5. Dokploy 中的建议配置

### Docker Compose 项目

仓库连接后，直接使用根目录下的：

- `docker-compose.yml`

### 端口

容器内部监听端口：

- `8000`

在 Dokploy GUI 里：

- `Domains -> Container Port` 填 `8000`
- 不要再单独映射宿主机端口

如果你之前遇到 `Bind for 8000 already in use`，就是因为旧配置把容器端口映射到了宿主机。现在这套 Compose 已经移除了 `ports:`。

### 持久化

建议保留：

- `/app/runtime`

这样可以持久保存：

- SQLite 数据库
- dense index

## 6. 部署后的首次检查

部署完成后，检查：

1. `GET /health`
2. 打开首页 `/`
3. 尝试提一个简单问题

`/health` 现在会返回 `llm_enabled`，可以用来确认容器是否已经读到你的 LLM 配置。

如果没有配置 LLM，页面也会正常返回，只是结果偏 mock/template。
