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

## 8. `reasoning cards` 有论文支撑吗

有，但需要说准确一点：

- `reasoning card` 不是某一篇论文里的标准术语
- 它是本项目对“中间推理状态可视化”的工程化命名
- 更准确地说，它受几条研究脉络启发，再结合本项目的 API 检索场景做了落地改造

### 8.1 对应的研究脉络

1. 显式中间推理步骤

   [Chain-of-Thought Prompting Elicits Reasoning in Large Language Models](https://arxiv.org/abs/2201.11903)

   这篇工作证明了，把中间推理步骤显式展开，能提升复杂推理表现。我们的 `reasoning cards` 本质上也是把“当前推理到哪一步”显式化，只是我们没有直接照搬自由文本 CoT，而是把它压成更稳定的结构化卡片。

2. 中间 scratchpad / workpad

   [Show Your Work: Scratchpads for Intermediate Computation with Language Models](https://arxiv.org/abs/2112.00114)

   这篇工作强调，让模型输出中间计算过程会显著改善多步任务。`reasoning cards` 可以看成面向 RAG 检索场景的一种 scratchpad：不是暴露全部原始思维，而是保留足够有用的中间状态，例如目标对象、候选调用、证据标题、下一步访问路径。

3. 先拆子问题，再逐步求解

   [Measuring and Narrowing the Compositionality Gap in Language Models](https://arxiv.org/abs/2210.03350)

   这篇论文里的 `self-ask` 思路，是先提出 follow-up questions，再回答原问题。我们现在的 staged retrieval plan 和 reasoning cards，与它在方法论上很接近：先把需求拆成子目标，再决定应该搜什么 API、再补什么关联关系。

4. 推理与检索交替推进

   [ReAct: Synergizing Reasoning and Acting in Language Models](https://arxiv.org/abs/2210.03629)

   [Interleaving Retrieval with Chain-of-Thought Reasoning for Knowledge-Intensive Multi-Step Questions](https://arxiv.org/abs/2212.10509)

   `ReAct` 强调推理轨迹和外部动作交替进行，`IRCoT` 则更直接指出：下一步检索什么，取决于前面已经推导出了什么。我们现在的 reasoning cards 正是把这类“推理驱动检索”的中间状态显式记录下来，供后续检索和生成使用。

5. 生成后再校验

   [Large Language Models are Better Reasoners with Self-Verification](https://arxiv.org/abs/2212.09561)

   这条脉络支持“先生成，再验证”的设计。虽然它不是 reasoning card 本身的来源，但解释了为什么我们会把中间状态、候选路径、自检结果都暴露出来，因为这些结构对后续 revision 和人工排查都更友好。

### 8.2 本项目和论文的关系

如果要一句话总结：

- CoT / Scratchpad 给了“显式中间步骤”的依据
- Self-Ask 给了“先拆子目标再推进”的依据
- ReAct / IRCoT 给了“推理和检索要互相驱动”的依据
- Self-Verification 给了“中间状态应该服务于后验检查”的依据

而本项目的 `reasoning cards`，就是把这些思想压缩成更适合 API RAG 的结构化表示。

### 8.3 为什么没有直接输出自由文本 CoT

这是一个有意的工程取舍：

- 自由文本 CoT 可读性高，但不稳定
- 对前端展示、trace 对比、后续 rerank 利用不够友好
- 容易夹杂无关表述

所以这里采用的是“轻量结构化 reasoning state”，也就是：

- `title`
- `summary`
- `candidate_calls`
- `evidence_titles`

这更像“可消费的推理摘要卡片”，而不是原样暴露模型完整思维链。

## 9. 现在怎么排查效果问题

### 9.1 回答太偏、太泛

先看：
- `retrieval_trace.plan`
- `retrieval_trace.selected_titles`
- `retrieval_trace.reasoning_cards`

问题通常在：
- query plan 不够准
- 证据不够完整
- 文档本身没 ingest 到

### 9.2 LLM 似乎没拿到足够信息

看：
- `llm_trace.request_messages`
- `llm_trace.response_text`

这样可以确认：
- 到底发了哪些证据
- reasoning cards 有没有进 prompt
- 模型实际怎样响应

### 9.3 Direct 模式没有回答

通常说明：
- `LLM_BASE_URL`
- `LLM_API_KEY`
- `LLM_MODEL`

至少有一个没配置对。

## 10. 如果以后要做真正的上下文记忆

建议最小实现如下：

1. 给 `POST /chat` 增加 `conversation_id`
2. 新增消息存储表
3. 每轮把最近 N 轮消息拼进 prompt
4. 超长时做历史摘要
5. 区分：
   - RAG 证据上下文
   - 会话历史上下文

这样可以避免把“用户历史”与“检索证据”混成一团。

## 11. 关键文件总览

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
