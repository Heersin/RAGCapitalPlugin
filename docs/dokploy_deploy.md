# Dokploy 部署说明

这份项目已经准备好了可直接用于 Dokploy 的基础文件：

- `Dockerfile`
- `docker-compose.yml`
- `.env.example`
- `docker/start.sh`

说明：

- Dokploy 推荐直接在 GUI 里填写环境变量
- 现在 `docker-compose.yml` 不再强依赖仓库中的 `.env` 文件
- `.env.example` 主要用于本地参考，不再是线上启动前置条件

## 1. 推荐部署方式

推荐在 Dokploy 里使用 `Docker Compose` 项目部署。

这样可以直接利用：

- `docker-compose.yml`
- `raglocal_runtime` 持久卷

在 Dokploy 场景下，Compose 现在只 `expose 8000`，不再把端口绑定到宿主机，避免 `already bind` 冲突。
同时默认 `AUTO_INGEST=false`，优先保证容器先成功启动，再由你手动触发一次 `/ingest`。

## 2. 需要填写的环境变量

如果你是本地运行，可以把 `.env.example` 复制成 `.env`。

如果你是 Dokploy 部署，直接在 GUI 的 Environment 页面填写这些值即可：

- `LLM_BASE_URL`
- `LLM_API_KEY`
- `LLM_MODEL`

如果你暂时不接外部 LLM，也可以先保留为空，系统会回退到本地 mock generation。

如果你希望启动后手动导入 PDF，把：

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
AUTO_INGEST=false
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

### 环境变量

在 Dokploy GUI 中填写：

- `HOST=0.0.0.0`
- `PORT=8000`
- `LLM_BASE_URL=...`
- `LLM_API_KEY=...`
- `LLM_MODEL=...`
- `LLM_TIMEOUT_SECONDS=60`
- `AUTO_INGEST=false`
- `INGEST_DOC_ROOT=sample/plugin`
- `INGEST_REBUILD=false`
- `INGEST_ENABLE_PDF=true` 或 `false`

推荐先用 `AUTO_INGEST=false`，等服务稳定启动后，再手动调用一次 `/ingest`。

## 6. 部署后的首次检查

部署完成后，检查：

1. `GET /health`
2. 打开首页 `/`
3. 尝试提一个简单问题

`/health` 现在会返回 `llm_enabled`，可以用来确认容器是否已经读到你的 LLM 配置。

如果没有配置 LLM，页面也会正常返回，只是结果偏 mock/template。

## 7. 如果部署后看到 502，优先检查什么

最常见的是这几类：

1. Dokploy 仍在使用旧的 Compose 配置
   需要重新部署到最新提交。

2. 之前的 `docker-compose.yml` 依赖 `.env`
   如果线上工作区没有 `.env`，Compose 会直接启动失败。

3. `AUTO_INGEST=true` 导致启动太慢
   容器在 ingest 结束前不会监听 8000，反代会先报 502。

4. Domain 的 Container Port 不是 `8000`
   必须对准应用内部端口 `8000`。

5. 查看 Dokploy 日志
   重点搜：
   - `Couldn't find env file`
   - `Connection refused`
   - `Auto ingest enabled`
   - `ModuleNotFoundError`
