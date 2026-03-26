from __future__ import annotations

from typing import Dict, List


def build_chat_answer(result: Dict) -> str:
    raw_answer = (result.get("raw_answer") or "").strip()
    if result.get("used_remote_llm") and raw_answer:
        return raw_answer

    lines = [f"插件类型：{result['plugin_type']}", ""]

    lines.extend(["需求分析：", result["analysis"]])
    report = result["self_check_report"]
    if report.get("invalid_symbols") or report.get("missing_required_methods"):
        lines.extend(["", "自检提醒："])
        if report.get("invalid_symbols"):
            lines.append("无效符号：" + ", ".join(report["invalid_symbols"]))
        if report.get("missing_required_methods"):
            lines.append("缺失方法：" + ", ".join(report["missing_required_methods"]))
    if result.get("code_blocks"):
        lines.extend(["", "生成代码："])
        for i, block in enumerate(result["code_blocks"], start=1):
            lines.extend(["", f"代码块 {i}:", "```java", block.strip(), "```"])
    if result.get("used_symbols"):
        lines.extend(["", "关键符号：", ", ".join(result["used_symbols"])])
    return "\n".join(lines)


def source_cards_for_chat(evidence_cards: List[Dict]) -> List[Dict]:
    return [
        {
            "title": card["title"],
            "source_path": card["source_path"],
            "plugin_type": card["plugin_type"],
            "score": card["score"],
        }
        for card in evidence_cards[:6]
    ]


CHAT_UI_HTML = """<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>RAGLocal 聊天工作台</title>
  <style>
    :root{
      --bg-1:#f7efe3;
      --bg-2:#e9f4ef;
      --panel:rgba(255,255,255,.84);
      --panel-strong:#fffdfa;
      --ink:#1f2933;
      --muted:#61707d;
      --line:rgba(31,41,51,.12);
      --accent:#0f766e;
      --accent-strong:#115e59;
      --user:#1b4d6b;
      --user-bg:#dff0fb;
      --assistant-bg:#fffaf2;
      --shadow:0 24px 72px rgba(31,41,51,.14);
      --mono:Menlo,Consolas,monospace;
    }
    *{box-sizing:border-box}
    body{
      margin:0;
      min-height:100vh;
      font-family:"Avenir Next","PingFang SC",sans-serif;
      background:
        radial-gradient(circle at top left, rgba(15,118,110,.14), transparent 30%),
        radial-gradient(circle at top right, rgba(217,119,6,.10), transparent 28%),
        linear-gradient(180deg,var(--bg-1),var(--bg-2));
      color:var(--ink)
    }
    .wrap{
      max-width:1240px;
      margin:0 auto;
      padding:24px;
      display:grid;
      grid-template-columns:320px 1fr;
      gap:18px;
      min-height:100vh
    }
    .card{
      background:var(--panel);
      border:1px solid var(--line);
      border-radius:26px;
      box-shadow:var(--shadow);
      backdrop-filter:blur(14px)
    }
    .sidebar{padding:24px;position:sticky;top:18px;height:fit-content}
    .eyebrow{
      display:inline-flex;
      padding:8px 12px;
      border-radius:999px;
      background:rgba(15,118,110,.10);
      color:var(--accent-strong);
      font-size:12px;
      letter-spacing:.08em;
      text-transform:uppercase
    }
    h1{margin:16px 0 10px;font-size:36px;line-height:1.02}
    .lead{margin:0 0 18px;color:var(--muted);line-height:1.7}
    label{display:block;margin:14px 0 8px;font-size:13px;color:var(--muted);font-weight:700}
    textarea,input,select{
      width:100%;
      padding:14px 16px;
      border-radius:16px;
      border:1px solid rgba(31,41,51,.16);
      background:#fff;
      font:inherit;
      color:var(--ink)
    }
    .settings textarea{min-height:110px;resize:vertical;line-height:1.6}
    .row{display:grid;grid-template-columns:1fr 112px;gap:12px}
    .actions,.composer-actions{display:flex;gap:10px;flex-wrap:wrap}
    button{
      border:none;
      border-radius:14px;
      padding:12px 16px;
      font:inherit;
      font-weight:700;
      cursor:pointer;
      transition:transform .14s ease, opacity .14s ease
    }
    button:hover{transform:translateY(-1px)}
    button:disabled{opacity:.7;cursor:wait;transform:none}
    .primary{background:linear-gradient(135deg,var(--accent),#0b5f59);color:#fff}
    .secondary{background:#fff;border:1px solid var(--line);color:var(--ink)}
    .note{
      margin-top:16px;
      padding:14px 16px;
      border-radius:18px;
      background:rgba(255,255,255,.66);
      border:1px solid rgba(31,41,51,.08);
      color:var(--muted);
      line-height:1.65;
      font-size:14px
    }
    .workspace{
      display:grid;
      grid-template-rows:auto 1fr auto;
      min-height:calc(100vh - 48px)
    }
    .workspace-head{
      display:flex;
      justify-content:space-between;
      gap:12px;
      align-items:center;
      padding:18px 22px;
      border-bottom:1px solid var(--line)
    }
    .workspace-head strong{font-size:18px}
    .workspace-head span{color:var(--muted);font-size:14px}
    .thread{
      padding:22px;
      display:flex;
      flex-direction:column;
      gap:16px;
      overflow:auto;
      min-height:460px;
      max-height:calc(100vh - 250px)
    }
    .message{
      display:flex;
      gap:12px;
      align-items:flex-start
    }
    .message.user{justify-content:flex-end}
    .avatar{
      width:38px;
      height:38px;
      border-radius:14px;
      display:flex;
      align-items:center;
      justify-content:center;
      font-size:12px;
      font-weight:800;
      letter-spacing:.06em;
      flex:0 0 auto
    }
    .avatar.assistant{background:rgba(15,118,110,.12);color:var(--accent-strong)}
    .avatar.user{background:rgba(27,77,107,.12);color:var(--user)}
    .bubble{
      max-width:min(760px, 86%);
      border-radius:22px;
      border:1px solid var(--line);
      padding:16px 18px;
      background:var(--assistant-bg)
    }
    .user .bubble{
      background:var(--user-bg);
      border-color:rgba(27,77,107,.12)
    }
    .bubble-head{
      display:flex;
      justify-content:space-between;
      gap:12px;
      margin-bottom:10px;
      color:var(--muted);
      font-size:12px
    }
    .bubble-body{line-height:1.75;word-break:break-word}
    .bubble-body pre{
      margin:12px 0 0;
      padding:14px;
      overflow:auto;
      border-radius:16px;
      background:#162029;
      color:#f3f7f8;
      font-family:var(--mono)
    }
    .bubble-body code{font-family:var(--mono)}
    .assistant-meta{
      display:flex;
      gap:8px;
      flex-wrap:wrap;
      margin-top:14px
    }
    .chip{
      padding:7px 10px;
      border-radius:999px;
      background:#fff;
      border:1px solid var(--line);
      color:var(--muted);
      font-size:12px
    }
    .sources{
      margin-top:14px;
      display:grid;
      gap:10px
    }
    .source{
      padding:12px 14px;
      border-radius:16px;
      background:#fff;
      border:1px solid var(--line)
    }
    .source strong{display:block;margin-bottom:6px}
    .path{
      font-family:var(--mono);
      font-size:12px;
      color:var(--muted);
      word-break:break-all;
      line-height:1.55
    }
    .loading{
      color:var(--muted);
      font-style:italic
    }
    details.trace-panel{
      margin-top:14px;
      border:1px solid var(--line);
      border-radius:16px;
      background:rgba(255,255,255,.88);
      overflow:hidden
    }
    details.trace-panel summary{
      cursor:pointer;
      list-style:none;
      padding:12px 14px;
      font-size:13px;
      font-weight:700;
      color:var(--accent-strong);
      border-bottom:1px solid transparent
    }
    details.trace-panel[open] summary{
      border-bottom-color:var(--line);
      background:rgba(15,118,110,.05)
    }
    .trace-content{
      padding:14px;
      display:grid;
      gap:12px
    }
    .trace-block{
      padding:12px;
      border-radius:14px;
      background:#fff;
      border:1px solid var(--line)
    }
    .trace-title{
      margin-bottom:8px;
      font-size:12px;
      font-weight:700;
      color:var(--muted);
      text-transform:uppercase;
      letter-spacing:.06em
    }
    .trace-block pre{
      margin:0;
      white-space:pre-wrap;
      word-break:break-word;
      font-size:12px;
      line-height:1.6;
      font-family:var(--mono)
    }
    .trace-grid{
      display:grid;
      gap:10px
    }
    .trace-message{
      padding:10px 12px;
      border-radius:12px;
      background:#f8fbfb;
      border:1px solid rgba(31,41,51,.08)
    }
    .trace-role{
      margin-bottom:6px;
      font-size:12px;
      font-weight:700;
      color:var(--accent-strong);
      text-transform:uppercase
    }
    .composer{
      border-top:1px solid var(--line);
      padding:18px 22px;
      background:rgba(255,253,248,.9)
    }
    .composer textarea{
      min-height:96px;
      resize:vertical;
      line-height:1.6
    }
    .composer-bar{
      display:grid;
      grid-template-columns:1fr auto;
      gap:12px;
      align-items:end
    }
    .subline{
      margin-top:10px;
      display:flex;
      justify-content:space-between;
      gap:12px;
      color:var(--muted);
      font-size:13px
    }
    .empty{
      padding:22px;
      border:1px dashed rgba(31,41,51,.16);
      border-radius:18px;
      color:var(--muted);
      text-align:center;
      background:rgba(255,255,255,.52)
    }
    @media (max-width:980px){
      .wrap{grid-template-columns:1fr}
      .sidebar{position:static}
      .workspace{min-height:auto}
      .thread{max-height:none}
    }
    @media (max-width:640px){
      .wrap{padding:14px}
      .workspace-head,.thread,.composer,.sidebar{padding:16px}
      .row,.composer-bar{grid-template-columns:1fr}
      .bubble{max-width:100%}
    }
  </style>
</head>
<body>
  <div class="wrap">
    <aside class="card sidebar">
      <div class="eyebrow">Local RAG Chat</div>
      <h1>RAGLocal 聊天工作台</h1>
      <p class="lead">现在首页更像聊天应用了。你可以连续提问、保留会话记录，并在每次回复里直接看到代码、自检提示和来源。</p>
      <div class="settings">
        <label for="preset-question">示例问题</label>
        <textarea id="preset-question" placeholder="例如：生成一个只读 action，在输出窗口打印选中对象属性。"></textarea>
        <div class="row">
          <div>
            <label for="plugin_type">插件类型</label>
            <select id="plugin_type" name="plugin_type">
              <option value="">自动判断</option>
              <option value="action">action</option>
              <option value="drc">drc</option>
              <option value="constraint">constraint</option>
            </select>
          </div>
          <div>
            <label for="context_budget">检索条数</label>
            <input id="context_budget" name="context_budget" type="number" min="1" max="20" value="10" />
          </div>
        </div>
      </div>
      <div class="actions" style="margin-top:14px">
        <button class="secondary" id="example-btn" type="button">填入示例</button>
        <button class="secondary" id="clear-btn" type="button">清空对话</button>
      </div>
      <div class="note">
        首次使用前，先运行一次 <code>/ingest</code> 导入文档。当前聊天界面是多轮前端会话，后端每一轮仍按“当前问题”独立检索和生成。
      </div>
    </aside>
    <main class="card workspace">
      <div class="workspace-head">
        <div>
          <strong>对话</strong>
          <div><span id="status">等待提问</span></div>
        </div>
        <span id="session-meta">plugin_type=自动判断 · top_k=10</span>
      </div>
      <div class="thread" id="thread">
        <div class="empty" id="empty-state">先执行一次文档导入，然后在底部输入框里开始聊天。</div>
      </div>
      <form class="composer" id="chat-form">
        <div class="composer-bar">
          <textarea id="question" name="question" placeholder="继续提问，例如：再给我补一个模板类，并解释关键接口为什么这样选。"></textarea>
          <div class="composer-actions">
            <button class="primary" id="submit-btn" type="submit">发送</button>
          </div>
        </div>
        <div class="subline">
          <span>Enter 发送，Shift + Enter 换行</span>
          <span id="composer-hint">当前会保留页面内聊天记录</span>
        </div>
      </form>
    </main>
  </div>
  <script>
    const form = document.getElementById("chat-form");
    const statusEl = document.getElementById("status");
    const threadEl = document.getElementById("thread");
    const emptyStateEl = document.getElementById("empty-state");
    const sessionMetaEl = document.getElementById("session-meta");
    const submitBtn = document.getElementById("submit-btn");
    const presetQuestionEl = document.getElementById("preset-question");
    const questionEl = document.getElementById("question");
    const pluginTypeEl = document.getElementById("plugin_type");
    const contextBudgetEl = document.getElementById("context_budget");
    let messageCounter = 0;

    document.getElementById("example-btn").addEventListener("click", () => {
      const sample = "生成一个只读 action，在输出窗口打印选中对象属性";
      presetQuestionEl.value = sample;
      questionEl.value = sample;
      pluginTypeEl.value = "action";
      contextBudgetEl.value = "10";
      questionEl.focus();
    });

    document.getElementById("clear-btn").addEventListener("click", () => {
      threadEl.innerHTML = "";
      threadEl.appendChild(emptyStateEl);
      emptyStateEl.hidden = false;
      statusEl.textContent = "已清空，等待提问";
      questionEl.focus();
    });

    const esc = (s) => String(s).replaceAll("&","&amp;").replaceAll("<","&lt;").replaceAll(">","&gt;");
    const formatJson = (value) => esc(JSON.stringify(value ?? {}, null, 2));
    const renderMarkdownish = (s) => esc(s)
      .replace(/```(?:\\w+)?\\n([\\s\\S]*?)```/g, (_, c) => "<pre><code>"+c.trim()+"</code></pre>")
      .replace(/\\n/g, "<br />");

    function nowTime() {
      return new Date().toLocaleTimeString("zh-CN", { hour: "2-digit", minute: "2-digit" });
    }

    function ensureThreadActive() {
      if (emptyStateEl.parentNode === threadEl) {
        emptyStateEl.hidden = true;
        emptyStateEl.remove();
      }
    }

    function buildSourceList(items) {
      if (!items || !items.length) {
        return '<div class="source"><strong>参考来源</strong><div class="path">本次没有返回来源。</div></div>';
      }
      return items.map(item => `
        <div class="source">
          <strong>${esc(item.title)}</strong>
          <div class="path">${esc(item.source_path)}</div>
          <div class="path">plugin_type=${esc(item.plugin_type)} | score=${Number(item.score).toFixed(4)}</div>
        </div>
      `).join("");
    }

    function renderTraceMessages(messages) {
      if (!messages || !messages.length) {
        return '<div class="trace-block"><div class="trace-title">Prompt</div><pre>本次没有记录请求消息。</pre></div>';
      }
      return `
        <div class="trace-block">
          <div class="trace-title">Prompt Messages</div>
          <div class="trace-grid">
            ${messages.map((item) => `
              <div class="trace-message">
                <div class="trace-role">${esc(item.role || "unknown")}</div>
                <pre>${esc(item.content || "")}</pre>
              </div>
            `).join("")}
          </div>
        </div>
      `;
    }

    function renderLlmTrace(trace, usedRemote) {
      if (!trace || (!trace.enabled && !trace.fallback_reason)) {
        return "";
      }
      const revision = trace.revision || null;
      return `
        <details class="trace-panel">
          <summary>LLM 调用详情 ${usedRemote ? "· 已调用远程模型" : "· 未成功调用远程模型"}</summary>
          <div class="trace-content">
            <div class="trace-block">
              <div class="trace-title">Summary</div>
              <pre>${formatJson({
                enabled: Boolean(trace.enabled),
                used_remote_llm: Boolean(trace.used_remote_llm),
                fallback_reason: trace.fallback_reason || null,
                usage: trace.usage || null,
                request_payload: trace.request_payload ? {
                  model: trace.request_payload.model,
                  temperature: trace.request_payload.temperature,
                  max_tokens: trace.request_payload.max_tokens
                } : null
              })}</pre>
            </div>
            ${renderTraceMessages(trace.request_messages || trace.request_payload?.messages || [])}
            <div class="trace-block">
              <div class="trace-title">Response</div>
              <pre>${esc(trace.response_text || "本轮没有远程响应文本。")}</pre>
            </div>
            ${revision ? `
              <div class="trace-block">
                <div class="trace-title">Revision Summary</div>
                <pre>${formatJson({
                  used_remote_llm: Boolean(revision.used_remote_llm),
                  fallback_reason: revision.fallback_reason || null,
                  usage: revision.usage || null
                })}</pre>
              </div>
              ${renderTraceMessages(revision.request_messages || revision.request_payload?.messages || [])}
              <div class="trace-block">
                <div class="trace-title">Revision Response</div>
                <pre>${esc(revision.response_text || "没有修订响应。")}</pre>
              </div>
            ` : ""}
          </div>
        </details>
      `;
    }

    function renderRetrievalTrace(trace) {
      if (!trace || !Object.keys(trace).length) {
        return "";
      }
      return `
        <details class="trace-panel">
          <summary>检索规划与召回详情</summary>
          <div class="trace-content">
            <div class="trace-block">
              <div class="trace-title">Plan</div>
              <pre>${formatJson(trace.plan || {})}</pre>
            </div>
            <div class="trace-block">
              <div class="trace-title">Retrieval Trace</div>
              <pre>${formatJson({
                mode: trace.mode,
                plugin_type: trace.plugin_type,
                route_confidence: trace.route_confidence,
                retrieval_query: trace.retrieval_query,
                query_expansions: trace.query_expansions,
                planner: trace.planner,
                stage_candidates: trace.stage_candidates,
                fused_candidates: trace.fused_candidates,
                expanded_candidates: trace.expanded_candidates,
                second_hop_candidates: trace.second_hop_candidates,
                final_candidates: trace.final_candidates,
                selected_titles: trace.selected_titles
              })}</pre>
            </div>
          </div>
        </details>
      `;
    }

    function appendUserMessage(text) {
      ensureThreadActive();
      const node = document.createElement("div");
      node.className = "message user";
      node.innerHTML = `
        <div class="bubble">
          <div class="bubble-head"><strong>你</strong><span>${nowTime()}</span></div>
          <div class="bubble-body">${renderMarkdownish(text)}</div>
        </div>
        <div class="avatar user">YOU</div>
      `;
      threadEl.appendChild(node);
      threadEl.scrollTop = threadEl.scrollHeight;
    }

    function appendLoadingMessage() {
      ensureThreadActive();
      const id = `msg-${++messageCounter}`;
      const node = document.createElement("div");
      node.className = "message assistant";
      node.id = id;
      node.innerHTML = `
        <div class="avatar assistant">RAG</div>
        <div class="bubble">
          <div class="bubble-head"><strong>助手</strong><span>${nowTime()}</span></div>
          <div class="bubble-body loading">正在检索证据并整理回复...</div>
        </div>
      `;
      threadEl.appendChild(node);
      threadEl.scrollTop = threadEl.scrollHeight;
      return id;
    }

    function renderAssistantMessage(targetId, data, errorMessage) {
      const target = document.getElementById(targetId);
      if (!target) return;

      if (errorMessage) {
        target.innerHTML = `
          <div class="avatar assistant">RAG</div>
          <div class="bubble">
            <div class="bubble-head"><strong>助手</strong><span>${nowTime()}</span></div>
            <div class="bubble-body">${esc(errorMessage)}</div>
          </div>
        `;
        return;
      }

      target.innerHTML = `
        <div class="avatar assistant">RAG</div>
        <div class="bubble">
          <div class="bubble-head"><strong>助手</strong><span>${nowTime()}</span></div>
          <div class="bubble-body">${renderMarkdownish(data.answer)}</div>
          <div class="assistant-meta">
            <span class="chip">${data.used_remote_llm ? "remote LLM" : "local mock"}</span>
            <span class="chip">plugin_type=${esc(data.plugin_type)}</span>
            <span class="chip">latency=${Number(data.latency_seconds).toFixed(3)}s</span>
            <span class="chip">symbols=${(data.used_symbols || []).length}</span>
          </div>
          ${renderLlmTrace(data.llm_trace || {}, Boolean(data.used_remote_llm))}
          ${renderRetrievalTrace(data.retrieval_trace || {})}
          <div class="sources">${buildSourceList(data.sources || [])}</div>
        </div>
      `;
      threadEl.scrollTop = threadEl.scrollHeight;
    }

    function syncSessionMeta() {
      sessionMetaEl.textContent = `plugin_type=${pluginTypeEl.value || "自动判断"} · top_k=${contextBudgetEl.value || "10"}`;
    }

    pluginTypeEl.addEventListener("change", syncSessionMeta);
    contextBudgetEl.addEventListener("input", syncSessionMeta);
    presetQuestionEl.addEventListener("input", () => {
      if (!questionEl.value.trim()) questionEl.value = presetQuestionEl.value;
    });
    syncSessionMeta();

    questionEl.addEventListener("keydown", (event) => {
      if (event.key === "Enter" && !event.shiftKey) {
        event.preventDefault();
        form.requestSubmit();
      }
    });

    form.addEventListener("submit", async (event) => {
      event.preventDefault();
      const payload = {
        question: questionEl.value.trim(),
        plugin_type: pluginTypeEl.value || null,
        context_budget: Number(contextBudgetEl.value || 10)
      };
      if(!payload.question){
        statusEl.textContent = "请先输入问题";
        questionEl.focus();
        return;
      }

      appendUserMessage(payload.question);
      const loadingId = appendLoadingMessage();
      submitBtn.disabled = true;
      statusEl.textContent = '正在检索和生成...';
      try{
        const resp = await fetch('/chat',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(payload)});
        const data = await resp.json();
        if(!resp.ok) throw new Error(data.detail || '请求失败');
        renderAssistantMessage(loadingId, data);
        statusEl.textContent = '生成完成';
        questionEl.value = '';
        questionEl.focus();
      }catch(error){
        renderAssistantMessage(loadingId, null, error.message || '请求失败');
        statusEl.textContent='出错了';
      }finally{
        submitBtn.disabled = false;
      }
    });
  </script>
</body>
</html>
"""
