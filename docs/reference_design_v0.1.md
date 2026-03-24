# 参考文档（v0.1）：闭源插件文档驱动的 RAG 代码生成与评估设计

## 1. 目标与范围
- 目标：基于闭源文档（JavaDoc HTML + 示例 Java + 部分 PDF），通过对话生成可用的插件代码。
- 范围：仅覆盖 `action / drc / constraint` 三类插件。
- 约束：LLM 通过 API 调用，不能再训练或微调；重点靠 RAG、提示词工程和后验校验提效。

## 2. 设计思路
- 核心策略：`检索约束生成`。
- 三层闭环：
  1. Ingest：结构化 API 事实
  2. Generate：基于证据生成核心类+模板
  3. Evaluate：指标化评测和迭代

## 3. 系统架构
- 数据层：HTML JavaDoc、Java 示例、可选 PDF
- 检索层：任务路由、BM25 + Dense、RRF 融合、证据卡输出
- 生成层：需求结构化 -> 代码生成 -> 自检修复

## 4. 关键接口
- `GET /`
- `POST /chat`
- `POST /ingest`
- `POST /retrieve`
- `POST /generate`
- `POST /evaluate/run`

## 4.1 当前 AI 调用策略
- 如果 `LLM_BASE_URL / LLM_API_KEY / LLM_MODEL` 已配置，则生成层调用外部 OpenAI-compatible `chat/completions` API。
- 如果未配置或调用失败，则回退到本地 mock generation。
- 自检发现问题时，可触发第二次 LLM 修订调用。

## 4.2 当前聊天状态
- 首页已提供聊天式 UI。
- 当前前端会保留可见聊天记录。
- 后端 `/chat` 仍按单轮请求处理，尚未实现持久会话记忆。

## 5. 常规评价指标
### 检索指标
- Recall@K：相关证据召回比例
- MRR@K：首个正确证据排名质量
- nDCG@K：排序整体质量

### 生成指标
- Symbol Precision / Recall / F1
- Hallucinated API Rate
- Requirement Coverage

### 工业指标
- FPAR (First Pass Accept Rate)
- Edit Effort
- Turn-to-Accept
- Latency / Cost per accepted task

## 6. 实验设计
- 数据集：20-30 条任务，`action/drc/constraint` 分层抽样
- 对比组：B0/B1/B2/B3
- 统计：配对比较 + Bootstrap CI + Wilcoxon

## 7. 首期验收门槛（建议）
- Hallucinated API Rate：较 B0 下降 >= 40%
- Requirement Coverage：>= 0.8
- FPAR：>= 0.5
- Turn-to-Accept：中位数 <= 3
- Latency：20-60 秒

## 8. 风险与缓解
- PDF 未结构化：先 HTML+Java 主驱动
- 需求含糊：低置信度先澄清
- 输出波动：固定模板 + 低温度 + 自检

## 9. 默认假设
- 产物：核心类 + 模板
- 类型：仅 action/drc/constraint
- 首期验收：符号级 + 规则级

## 10. 多轮上下文记忆演进建议
- Phase 1：为 `/chat` 增加 `conversation_id`，保存最近消息并拼入 prompt。
- Phase 2：对长会话做摘要压缩，控制 token 成本。
- Phase 3：对历史消息建立检索，按当前问题召回相关历史片段。
