# RAGLocal 当前工作流教程

这份教程面向使用者，说明现在这个项目从导入文档到网页聊天的完整工作流。

## 1. 先理解系统在做什么

这个项目不是“直接问大模型写代码”。

默认推荐流程是：
1. 先把本地文档导入为知识库
2. 提问时先检索 API 与示例
3. 再把证据交给生成层
4. 最后把答案、代码、来源和 trace 返回给你

同时，系统现在也支持一个纯直连模式：

- `RAG`
  先查本地知识，再回答
- `Direct`
  不查本地，直接把当前输入发给大模型

## 2. 启动服务

安装依赖：

```bash
uv sync --extra dev
```

启动服务：

```bash
PYTHONPATH=src uv run uvicorn rag_codegen.app:app --host 0.0.0.0 --port 8000
```

浏览器打开：

```text
http://localhost:8000/
```

## 3. 第一次必须做的事：ingest

如果你还没有导入文档，RAG 效果会很弱，因为本地知识库还是空的。

执行：

```bash
curl -X POST http://localhost:8000/ingest \
  -H 'Content-Type: application/json' \
  -d '{"doc_root":"sample/plugin","rebuild":true,"enable_pdf":true}'
```

参数说明：

- `doc_root`
  要扫描的文档根目录
- `rebuild=true`
  清空旧索引并完整重建
- `enable_pdf=true`
  连 PDF 也一起解析

导入完成后，数据会落到：
- `runtime/raglocal.db`
- `runtime/dense_index.npz`

## 4. 网页聊天怎么用

首页现在已经是聊天界面。

你可以设置：
- `回答模式`
  - `RAG（检索增强）`
  - `Direct（直连大模型）`
- `插件类型`
  - 自动判断
  - `action`
  - `drc`
  - `constraint`
- `检索条数`
  只在 `RAG` 模式下生效

然后在底部输入问题直接发送。

## 5. 两种模式分别适合什么

### 5.1 RAG

适合：
- 查 API
- 找类、父类、方法关系
- 基于本地文档生成插件代码
- 想看证据来源和推理过程

它会返回：
- 整理后的答案
- 代码块
- 来源卡片
- reasoning cards
- retrieval trace
- llm trace

### 5.2 Direct

适合：
- 普通聊天
- 头脑风暴
- 不依赖当前本地知识库的问题
- 想直接测试你配置的大模型

它会返回：
- 模型原始回答
- llm trace

它不会返回：
- 本地来源
- retrieval trace

## 6. API 方式怎么用

### 6.1 `POST /retrieve`

只看检索结果，不做生成：

```bash
curl -X POST http://localhost:8000/retrieve \
  -H 'Content-Type: application/json' \
  -d '{"query":"生成一个只读action，在输出窗口打印选中对象属性","plugin_type_hint":"action","top_k":8}'
```

### 6.2 `POST /generate`

走完整 RAG 生成，但返回结构化 JSON：

```bash
curl -X POST http://localhost:8000/generate \
  -H 'Content-Type: application/json' \
  -d '{"requirement":"生成一个只读action，在输出窗口打印选中对象属性","plugin_type":"action","context_budget":10}'
```

### 6.3 `POST /chat`

更适合 UI 或人工使用。

RAG 模式：

```bash
curl -X POST http://localhost:8000/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "question":"生成一个只读 action，在输出窗口打印选中对象属性",
    "plugin_type":"action",
    "context_budget":10,
    "mode":"rag"
  }'
```

Direct 模式：

```bash
curl -X POST http://localhost:8000/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "question":"请直接帮我分析这个需求应该怎样拆解",
    "mode":"direct"
  }'
```

## 7. 当前真实工作流

### 7.1 RAG 工作流

1. 前端把当前问题发到 `POST /chat`
2. 后端先判断插件类型
3. 构建 query plan
4. 分阶段检索：
   - API docs 优先
   - 关系扩展
   - 示例补充
5. 生成答案
6. 自检代码符号和关键方法
7. 返回答案、来源和 trace

### 7.2 Direct 工作流

1. 前端把当前问题发到 `POST /chat`
2. 后端识别到 `mode=direct`
3. 跳过检索
4. 直接把当前输入发给 LLM
5. 返回模型回答和调用 trace

## 8. 如何确认本轮有没有真的调用 LLM

看返回字段：

- `used_remote_llm`
- `llm_trace`

网页里也能展开查看：
- 请求 messages
- 请求 payload 摘要
- 响应文本
- usage
- fallback 原因

## 9. 当前上下文记忆到什么程度

现在的聊天页面有“前端历史记录”，但后端仍然是单轮处理。

也就是：
- 页面里看起来像多轮聊天
- 但每次请求只处理当前问题
- 不会自动把上一轮问答拼进下一轮 prompt

## 10. 实际建议

- 做插件 API 问答和代码生成，用 `RAG`
- 测试模型本身、做开放式对话，用 `Direct`
- 如果回答不够好，先检查：
  - 是否已经 ingest 完整文档
  - 是否开启了 PDF
  - `retrieval_trace` 里找到了哪些证据
  - `llm_trace` 里实际发了什么 prompt
