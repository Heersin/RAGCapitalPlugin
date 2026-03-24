# RAGLocal 技术教程

这份文档面向开发者，重点解释三件事：

1. 当前系统是否真的调用了 AI
2. 现有聊天界面的“上下文”到底到哪一层
3. 如果要做真正的多轮上下文记忆，应该怎么设计

## 1. 当前是否存在 AI 调用

存在，但它是“可选启用”的。

当前代码路径是：

1. `src/rag_codegen/app.py`
   `POST /chat` 和 `POST /generate` 都会进入生成链路。

2. `src/rag_codegen/generate.py`
   `Generator.generate(...)` 会调用 `_generate_text(...)`。

3. `src/rag_codegen/llm.py`
   如果检测到 LLM 配置存在，就会发起一次 OpenAI-compatible `chat/completions` HTTP 请求。

### 什么时候会真的调用外部 AI

只有当下面 3 个环境变量都配置了，`LLMClient.enabled` 才会是 `True`：

- `LLM_BASE_URL`
- `LLM_API_KEY`
- `LLM_MODEL`

相关代码：
- `src/rag_codegen/config.py`
- `src/rag_codegen/llm.py`

### 没配置时会发生什么

如果没有配置这些变量，或者请求失败，系统会退回本地 mock generation。

也就是说：

- 服务仍然能正常工作
- `/chat` 和 `/generate` 仍然会返回结果
- 但结果更偏模板和示例，不是强定制的真实 LLM 输出

### 当前 AI 调用的两个位置

#### 第一次生成

在 `Generator._generate_text(...)` 里：

- 先把需求、插件类型、analysis、evidence cards 组织成 prompt
- 再调用 `self.llm.chat(...)`

#### 第二次修订

在 `Generator._revise(...)` 里：

- 如果自检发现无效符号或缺失方法
- 并且 LLM 已启用
- 会再次调用 `self.llm.chat(...)` 尝试修订草稿

所以真实 LLM 场景下，一次请求最多可能触发两次外部 AI 调用：

1. 初次生成
2. 自检后修订

## 2. 当前聊天界面的上下文现状

当前首页已经是聊天式 UI，但它的“上下文”主要是前端展示层面的，不是后端真正的会话记忆。

### 现在实际发生的事情

每次你在页面里发送一条消息：

1. 前端把当前这一条问题发到 `POST /chat`
2. 后端按这条问题独立执行：
   - plugin type 判断
   - retrieval
   - generation
   - self-check
3. 前端把这次回复追加到聊天记录里

### 这意味着什么

现在的聊天界面具备：

- 多轮消息展示
- 页面内历史可见
- 连续提问体验更自然

但当前还不具备：

- 后端 session 级历史记忆
- 自动携带上一轮问答进入下一轮 prompt
- 长对话压缩总结
- 跨刷新持久会话

一句话说：

当前是“聊天外观 + 单轮后端”。

## 3. `PYTHONPATH=src uv run uvicorn rag_codegen.app:app --host 0.0.0.0 --port 8000` 在做什么

这是启动服务的命令。

分开看：

### `PYTHONPATH=src`

把 `src` 目录加进 Python 的模块搜索路径。

因为代码放在：

- `src/rag_codegen/app.py`

如果不加这段，Python 可能找不到 `rag_codegen` 这个包。

### `uv run`

表示用 `uv` 管理的项目环境来运行后面的命令。

作用是：

- 使用当前项目安装好的依赖
- 避免混用系统 Python 环境

### `uvicorn`

这是一个 ASGI 服务器，用来运行 FastAPI 应用。

### `rag_codegen.app:app`

意思是：

- 导入模块 `rag_codegen.app`
- 找到里面名为 `app` 的 FastAPI 对象
- 把这个对象作为 Web 服务入口启动

### `--host 0.0.0.0`

监听所有网络接口。

这样不仅能通过本机 `localhost` 访问，在同网段场景下也更容易被外部访问到。

### `--port 8000`

把服务绑定到 `8000` 端口。

所以访问地址就是：

- `http://localhost:8000/`

## 4. `/ingest` 这条 curl 命令在做什么

命令如下：

```bash
curl -X POST http://localhost:8000/ingest \
  -H 'Content-Type: application/json' \
  -d '{"doc_root":"sample/plugin","rebuild":true,"enable_pdf":false}'
```

它的作用是“导入文档并重建检索索引”。

### `POST /ingest`

调用后端导入接口。

### `doc_root: "sample/plugin"`

告诉系统去扫描 `sample/plugin` 目录下的文档和示例代码。

### `rebuild: true`

表示先清空旧数据，再完整重建。

当前会影响：

- SQLite chunks
- SQLite symbols
- FTS 数据
- dense index

### `enable_pdf: false`

本次不解析 PDF，只处理：

- HTML JavaDoc
- Java 示例

项目现在已经正式依赖 `pypdf`，所以把 `enable_pdf` 改成 `true` 时，容器和本地环境都会尝试解析 PDF。

需要注意：

- 当前 PDF 是否纳入索引，还取决于文件名能否映射到支持的插件类型
- 目前主要识别：
  - `action`
  - `drc`
  - `constraint`
- 其他更泛化的 PDF 指南如果没有映射到这些类型，当前仍可能跳过

### 执行之后会产出什么

系统会把结果写到：

- `runtime/raglocal.db`
- `runtime/dense_index.npz`

之后 `/retrieve`、`/generate`、`/chat` 才能基于这些数据做 RAG。

## 5. 带上下文记忆应该怎么做

如果要做“真正的多轮聊天”，建议分三层来做。

## 5.1 第一层：最小可用记忆

目标：

- 不改太多后端
- 尽快让下一轮能看到上一轮

做法：

1. 前端保存最近 N 轮对话
2. 新增 `messages` 字段到 `POST /chat`
3. 后端把最近几轮对话拼进 prompt

例如请求结构可以变成：

```json
{
  "question": "再补一个模板类",
  "plugin_type": "action",
  "context_budget": 10,
  "messages": [
    {"role": "user", "content": "生成一个只读 action"},
    {"role": "assistant", "content": "..." }
  ]
}
```

优点：

- 改动小
- 很快能看到效果

缺点：

- prompt 会越来越长
- 成本和延迟会上升
- 长对话容易退化

## 5.2 第二层：服务端会话记忆

目标：

- 让会话成为后端的一等对象
- 支持刷新页面后继续聊

建议新增：

- `conversation_id`
- `messages` 表
- `conversation_summary` 表或字段

### 推荐数据结构

可以直接放进当前 SQLite：

#### `conversations`

- `conversation_id`
- `title`
- `created_at`
- `updated_at`
- `plugin_type_last`
- `summary_text`

#### `conversation_messages`

- `id`
- `conversation_id`
- `role`
- `content`
- `created_at`
- `meta_json`

### 请求流程建议

1. 前端第一次发消息时，不传 `conversation_id`
2. 后端创建一个会话并返回 `conversation_id`
3. 后续每轮都带上这个 `conversation_id`
4. 后端从数据库取最近几轮历史，再参与生成

## 5.3 第三层：摘要记忆 + 检索记忆

目标：

- 长对话也稳定
- 控制 prompt 长度

推荐把上下文分成三块：

1. `recent turns`
   最近 3 到 6 轮原始对话

2. `conversation summary`
   系统自动生成的会话摘要，例如：
   - 当前插件类型
   - 已确定的约束
   - 用户偏好的输出形式
   - 已生成过哪些类

3. `retrieved memory`
   从历史消息中检索和当前问题最相关的内容

最终 prompt 可以变成：

1. system prompt
2. developer prompt
3. conversation summary
4. recent turns
5. current question
6. retrieved evidence cards

## 6. 真正落地时建议怎么改代码

最稳妥的顺序是：

1. 扩展 schema
   在 `src/rag_codegen/schemas.py` 给 `ChatRequest` 增加：
   - `conversation_id`
   - `messages`

2. 加会话存储
   在 `src/rag_codegen/storage.py` 增加会话表和消息表

3. 抽一个 chat service
   新建例如：
   - `src/rag_codegen/chat_service.py`

   负责：
   - 会话创建
   - 历史加载
   - 摘要压缩
   - 组装最终 prompt

4. 调整 `app.py`
   让 `POST /chat` 返回：
   - `conversation_id`
   - `answer`
   - `sources`
   - 可选 `memory_summary`

5. 调整前端
   页面保存当前 `conversation_id`
   每一轮继续带回后端

## 7. 推荐的记忆策略

如果现在开始做，我建议先这样：

### Phase 1

- 先做 `conversation_id`
- 保存消息到 SQLite
- 每轮带最近 4 轮上下文

### Phase 2

- 对超过阈值的历史做摘要
- prompt 中只保留“摘要 + 最近 4 轮”

### Phase 3

- 对历史消息建立轻量检索
- 针对当前问题召回最相关历史片段

这样改动节奏最稳，也比较适合当前这个项目体量。

## 8. 本次 RAG 优化做了什么

这一版没有引入新的向量库或外部检索服务，而是先优化了当前本地检索链路：

1. 查询扩展
   对中文/英文混合需求补充 API 提示词，例如：
   - `只读` -> `isReadOnly`
   - `输出窗口` -> `IXOutputWindow`, `println`
   - `菜单` -> `Trigger.MainMenu`, `Trigger.ContextMenu`

2. 元数据重排
   在 RRF 融合后，进一步考虑：
   - title / class / method / signature 的匹配
   - `java_example` 是否更适合当前问题
   - method-level chunk 是否更适合当前问题
   - read-only / output window 等意图提示

3. 结果去重
   避免最终 top-k 被同一个 source file 的多个近似 chunk 占满。

4. 索引一致性修复
   repeated ingest 后，`chunks / chunks_fts / symbols` 现在会重新同步，避免 FTS 漂移。

## 9. 开发脚本

现在可以使用：

- `scripts/raglocal.py`

常见命令：

```bash
uv run python scripts/raglocal.py setup
uv run python scripts/raglocal.py ingest --doc-root sample/plugin
uv run python scripts/raglocal.py serve --host 0.0.0.0 --port 8000
uv run python scripts/raglocal.py dev --doc-root sample/plugin
```

## 10. Dokploy 部署文件

已经准备好这些文件：

- `Dockerfile`
- `docker-compose.yml`
- `.env.example`
- `docker/start.sh`

说明文档在：

- `docs/dokploy_deploy.md`

## 11. 当前状态一句话总结

当前已经有可选的外部 AI 调用，但聊天记忆还只是前端展示层；如果要做真正多轮，需要把会话状态放到后端，并把“最近消息 + 摘要 + 检索记忆”一起送进生成链路。
