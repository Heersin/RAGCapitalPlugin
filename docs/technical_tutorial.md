# RAGLocal 技术教程

这份文档面向开发者，解释当前真实实现的技术链路，而不是概念版流程。

## 1. 当前是否真的存在 AI 调用

存在，但分两种模式。

### 1.1 RAG 模式

入口：
- `src/rag_codegen/app.py`
- `POST /chat` with `mode="rag"`
- `POST /generate`

链路：
1. `Generator.generate(...)`
2. `Retriever.retrieve(...)`
3. `Generator._generate_text(...)`
4. 如果启用 LLM，就发 OpenAI-compatible `chat/completions`
5. 如果自检失败且 LLM 可用，可能触发一次修订调用

### 1.2 Direct 模式

入口：
- `src/rag_codegen/app.py`
- `POST /chat` with `mode="direct"`

链路：
1. `Generator.direct_chat(...)`
2. 不走检索
3. 只把当前用户输入发给 LLM

## 2. 哪些环境变量决定 LLM 是否可用

文件：
- `src/rag_codegen/config.py`
- `src/rag_codegen/llm.py`

启用条件：
- `LLM_BASE_URL`
- `LLM_API_KEY`
- `LLM_MODEL`

可选：
- `LLM_TIMEOUT_SECONDS`

`LLMClient.enabled` 只有在前三个都存在时才为 `True`。

## 3. 当前聊天接口的真实行为

文件：
- `src/rag_codegen/schemas.py`
- `src/rag_codegen/app.py`

`ChatRequest` 现在包含：
- `question`
- `plugin_type`
- `context_budget`
- `mode`

其中：
- `mode="rag"`
  进入标准检索增强流程
- `mode="direct"`
  直接转发当前输入给大模型

`ChatResponse` 现在会包含：
- `mode`
- `answer`
- `sources`
- `retrieval_trace`
- `used_remote_llm`
- `llm_trace`

## 4. RAG 模式内部做了什么

文件：
- `src/rag_codegen/retrieve.py`
- `src/rag_codegen/generate.py`

### 4.1 检索规划

系统会先构建 query profile，包括：
- `subgoals`
- `english_terms`
- `api_terms`
- `relation_terms`
- `example_terms`

这个阶段的目的，是把中文需求转成更接近 API 文档与示例检索习惯的检索计划。

### 4.2 分阶段检索

当前检索不是一次性搜索，而是分阶段：
- general lookup
- API lookup
- example lookup
- guide lookup

然后再做：
- 融合排序
- 关系扩展
- 二跳扩展
- 元数据重排

### 4.3 Reasoning Cards

检索层会产出 `reasoning_cards`，用来总结：
- 当前识别出的目标对象
- 推荐访问路径
- 可能要调用的方法
- 可以支撑答案的关键证据标题

这些卡片会进入：
- `retrieval_trace.reasoning_cards`
- 网页界面展示
- RAG prompt

### 4.4 生成

RAG prompt 由这些信息组成：
- 原始需求
- plugin type
- analysis
- retrieval plan
- reasoning cards
- evidence cards

如果 LLM 不可用，则回退到本地 mock generation。

### 4.5 自检

文件：
- `src/rag_codegen/generate.py`

当前自检会检查：
- 代码里用到的符号是否在索引里
- 插件类型对应的关键方法是否缺失

如果 LLM 可用并发现问题，会尝试做一次 revision。

## 5. Direct 模式内部做了什么

文件：
- `src/rag_codegen/generate.py`

`Generator.direct_chat(...)` 的目标很明确：

1. 不做 retrieval
2. 不拼 evidence
3. 只发送当前用户消息
4. 返回模型原始回答

这条链路更适合：
- 普通对话
- 开放式需求分析
- 验证模型效果

如果没有配置 LLM，Direct 模式不会走本地 mock，而是直接提示你补齐 LLM 配置。

## 6. 当前“聊天上下文”到哪一层

文件：
- `src/rag_codegen/webui.py`

当前页面已经是聊天式 UI，但后端依旧是单轮接口。

也就是说：
- 浏览器里保留历史消息
- 每次都追加显示
- 但后端只处理本轮输入

当前尚未实现：
- `conversation_id`
- 消息持久化
- 历史压缩摘要
- 多轮记忆参与下一轮 prompt

## 7. 为什么当前 RAG 更接近人工写插件流程

你前面描述的人类工作流大致是：
1. 拆子目标
2. 把目标转成英文/API 概念
3. 找相关类和方法
4. 再补父类、关联类、继承链
5. 最后组合代码

当前系统已经把其中大部分动作显式化了：

- query profile 负责拆子目标
- API-first 检索负责先找接口定义
- relation expansion 负责补父类、关联类、方法链
- example lookup 负责补代码示例
- reasoning cards 负责把“为什么这样找”显式化

所以它已经不再是“粗暴搜几个 API 名字”，而是在向“可解释的检索规划器”演进。

## 8. 现在怎么排查效果问题

### 8.1 回答太偏、太泛

先看：
- `retrieval_trace.plan`
- `retrieval_trace.selected_titles`
- `retrieval_trace.reasoning_cards`

问题通常在：
- query plan 不够准
- 证据不够完整
- 文档本身没 ingest 到

### 8.2 LLM 似乎没拿到足够信息

看：
- `llm_trace.request_messages`
- `llm_trace.response_text`

这样可以确认：
- 到底发了哪些证据
- reasoning cards 有没有进 prompt
- 模型实际怎样响应

### 8.3 Direct 模式没有回答

通常说明：
- `LLM_BASE_URL`
- `LLM_API_KEY`
- `LLM_MODEL`

至少有一个没配置对。

## 9. 如果以后要做真正的上下文记忆

建议最小实现如下：

1. 给 `POST /chat` 增加 `conversation_id`
2. 新增消息存储表
3. 每轮把最近 N 轮消息拼进 prompt
4. 超长时做历史摘要
5. 区分：
   - RAG 证据上下文
   - 会话历史上下文

这样可以避免把“用户历史”与“检索证据”混成一团。

## 10. 关键文件总览

- `src/rag_codegen/app.py`
  FastAPI 路由与模式分流
- `src/rag_codegen/ingest.py`
  文档解析与索引构建
- `src/rag_codegen/storage.py`
  SQLite 存储与查询
- `src/rag_codegen/retrieve.py`
  staged retrieval planner 与 evidence selection
- `src/rag_codegen/generate.py`
  RAG 生成、Direct 聊天、自检与修订
- `src/rag_codegen/llm.py`
  OpenAI-compatible LLM 客户端
- `src/rag_codegen/webui.py`
  单页聊天界面与 trace 展示
