# RAGLocal 当前工作流教程

这份教程说明当前仓库是怎么工作的，以及现在新增的简易问答界面该怎么用。

## 1. 这个项目在做什么

这是一个本地优先的 RAG 原型，用来基于已有文档和示例代码，生成插件代码。

目前支持三类插件：
- `action`
- `drc`
- `constraint`

文档来源主要是：
- JavaDoc HTML
- Java 示例代码
- 可选 PDF

现在 PDF 解析依赖已经内置到项目里，开启 `enable_pdf=true` 就可以参与导入。

系统目标不是直接“瞎写”代码，而是尽量先检索文档证据，再带着证据去生成，从而减少 API 幻觉。

## 2. 当前工作流总览

整个流程可以理解成 5 步：

1. `ingest`
   把 `sample/plugin` 这类文档目录扫描一遍，切成结构化 chunk，写入 SQLite 和向量索引。

2. `retrieve`
   当你提问时，系统先判断你更像在问 `action / drc / constraint` 中的哪一类插件，再从索引中召回相关证据。

3. `generate`
   生成层拿着需求、插件类型和检索到的 evidence cards，调用 LLM 或本地 mock 生成结果。

4. `self-check`
   生成后会检查代码里引用的符号是否存在、关键方法是否缺失；如果配置了 LLM，还会尝试自动修正一次。

5. `return`
   以前主要返回结构化 JSON。现在新增了网页问答页和 `POST /chat`，会把结果整理成更直接的答案。

## 3. 代码结构

核心目录在 `src/rag_codegen`：

- `app.py`
  FastAPI 入口，暴露所有 HTTP 接口。

- `ingest.py`
  文档导入逻辑。负责扫描 `.html`、`.java`、可选 `.pdf`，并切分成 chunk。

- `storage.py`
  SQLite 存储层。维护：
  - `chunks`
  - `chunks_fts`
  - `symbols`

- `retrieve.py`
  检索层。组合 BM25、dense search、RRF 融合和 symbol overlap 加权。

- `generate.py`
  生成层。先分析需求，再调用 LLM 或 mock 输出代码，并做自检。

- `llm.py`
  OpenAI-compatible Chat Completions 客户端。

- `schemas.py`
  所有请求和响应模型。

- `webui.py`
  新增的简易网页界面和聊天结果格式化逻辑。

## 4. ingest 阶段到底做了什么

`POST /ingest` 会调用 `Ingestor.ingest(...)`。

它主要做这些事：

1. 扫描目录下的 HTML / Java / PDF 文件。
2. 从路径判断这些文件属于哪类插件：
   - `action`
   - `drc`
   - `constraint`
   - 或 `core`
3. 生成不同粒度的 chunk：
   - 类级 chunk
   - 方法级 chunk
   - 示例代码 chunk
4. 提取符号列表 `symbols`
5. 写入 SQLite
6. 构建 `runtime/dense_index.npz`

产物位置：
- SQLite: `runtime/raglocal.db`
- 向量索引: `runtime/dense_index.npz`

如果你还没有先执行 `ingest`，后面的检索和生成仍然能跑，但效果会明显变差，因为缺少证据。

## 5. retrieve 阶段怎么选资料

`POST /retrieve` 会进入 `Retriever.retrieve(...)`。

这里的逻辑是：

1. 根据问题文字推断插件类型，或者使用你显式传入的 `plugin_type_hint`
2. 在允许的类型范围里检索：
   - 当前类型
   - `core`
3. 走两套召回：
   - BM25 关键词检索
   - dense 向量检索
4. 用 RRF 融合两边结果
5. 再用 query 和 symbols 的重合做一点加权
6. 产出 `evidence_cards`

这些 `evidence_cards` 就是后面生成时给模型看的参考材料。

## 6. generate 阶段怎么出答案

`POST /generate` 会进入 `Generator.generate(...)`。

生成过程大致如下：

1. 确定 `plugin_type`
2. 调用检索拿上下文
3. 做一个轻量 requirement analysis
4. 调用 LLM 生成文本
5. 如果没配 LLM，就使用本地 mock generation
6. 提取代码块
7. 执行 self-check
8. 有问题时尝试自动修订

`/generate` 返回的还是结构化结果，适合程序消费，包括：
- `analysis`
- `code_blocks`
- `used_symbols`
- `self_check_report`
- `evidence_cards`

## 7. 新增的简易问答界面是什么

现在新增了两个更适合人工使用的入口：

### `GET /`

这是浏览器页面。你打开：

`http://localhost:8000/`

就能直接：
- 输入问题
- 选择插件类型或自动判断
- 设置检索条数
- 点击按钮获取整理后的答案

页面会展示：
- 生成后的可读答案
- 代码块
- 关键来源

这样就不需要你手动去看原始 JSON 了。

### `POST /chat`

这是一个比 `/generate` 更贴近“问答”的接口。

请求示例：

```bash
curl -X POST http://localhost:8000/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "question":"生成一个只读 action，在输出窗口打印选中对象属性",
    "plugin_type":"action",
    "context_budget":10
  }'
```

它内部仍然复用了原有生成链路，但返回中多了一个已经格式化好的 `answer` 字段，适合 UI 直接展示。

## 8. 推荐使用方式

如果你是手动使用，建议顺序如下：

1. 安装依赖

```bash
uv sync --extra dev
```

2. 启动服务

```bash
PYTHONPATH=src uv run uvicorn rag_codegen.app:app --host 0.0.0.0 --port 8000
```

3. 打开浏览器页面

`http://localhost:8000/`

4. 先导入样例文档

当前简易页面只负责提问和展示答案，文档导入仍然走 API：

```bash
curl -X POST http://localhost:8000/ingest \
  -H 'Content-Type: application/json' \
  -d '{"doc_root":"sample/plugin","rebuild":true,"enable_pdf":false}'
```

5. 回到页面直接提问

例如：
- `生成一个只读 action，在输出窗口打印选中对象属性`
- `生成一个 drc，检查对象是否缺少某个属性`
- `生成一个 constraint，对线长做额外限制`

## 9. 如果配置了外部 LLM

如果你设置了这些环境变量：

- `LLM_BASE_URL`
- `LLM_API_KEY`
- `LLM_MODEL`
- `LLM_TIMEOUT_SECONDS`

那么生成会走真实 LLM。

如果没有配置，系统会回退到本地 deterministic mock generation。

这意味着：
- 服务总能跑起来
- 但未配置 LLM 时，答案更像模板示例，不是高质量定制生成

## 10. 当前到底有没有 AI 调用

有，但它是条件触发的，不是无条件存在。

实际行为是：

1. 如果已经配置：
   - `LLM_BASE_URL`
   - `LLM_API_KEY`
   - `LLM_MODEL`

   那么生成阶段会调用外部 OpenAI-compatible 接口。

2. 如果没有配置这些变量，或者远程调用失败：
   系统会退回到本地 mock generation。

所以你看到页面能回答问题，不代表一定已经走了外部 AI。

最准确的判断方式是看运行环境里有没有这些变量。

## 11. 当前聊天界面的上下文范围

现在首页已经是聊天式界面，但后端还不是“真正多轮记忆”。

当前状态是：

- 页面会保留本轮浏览器中的聊天记录
- 但每次 `POST /chat` 仍然只根据“当前问题”做检索和生成
- 历史消息没有进入后端 prompt
- 刷新页面后也不会从后端恢复会话

也就是说，目前是：

`聊天式 UI + 单轮后端`

## 12. 如果要做真正多轮上下文记忆

建议按这条路径做：

1. 给 `/chat` 增加 `conversation_id`
2. 后端把消息落到 SQLite
3. 每轮取最近几轮消息加入 prompt
4. 长对话再引入 summary memory
5. 最后加历史消息检索

更完整的设计细节见：

- `docs/technical_tutorial.md`

## 13. 什么时候该用哪个接口

- 用 `GET /`
  你想直接在网页里问问题，看可读答案。

- 用 `POST /chat`
  你想保留结构化返回，但不想自己拼接答案。

- 用 `POST /generate`
  你要拿完整结构化结果做二次开发。

- 用 `POST /retrieve`
  你只想检查召回证据，不做生成。

- 用 `POST /ingest`
  你更新了文档或换了一批样本，需要重建索引。

## 14. 一句话理解当前工作流

先把文档和示例代码结构化入库，再做检索增强生成，最后把生成结果做一次插件 API 自检，并通过新的聊天页直接展示给你。
