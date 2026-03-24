# Ingest 前后对照样例（真实数据）

数据来源：`sample/plugin`，已执行 `/ingest`。
范围：`action / drc / constraint` + `core`。

## 1) 统一字段示例

统一字段：
`package / class / method / signature / text / symbols / plugin_type`

### 示例 A（action 方法级块）
- source: `sample/plugin/api/com/mentor/chs/plugin/action/IXAction.Trigger.html`
- plugin_type: `action`
- package: `com.mentor.chs.plugin.action`
- class: `IXAction.Trigger`
- method: `MainMenu`
- signature: `public static final IXAction.Trigger MainMenu`
- symbols: `IXAction, IXApplicationAction, IXBaseAction, ...`

Before（原文片段）:
`<h4>MainMenu</h4> <pre>public static final IXAction.Trigger MainMenu</pre> ...`

After（结构化 text）:
`IXAction.Trigger MainMenu public static final IXAction.Trigger MainMenu`

### 示例 B（drc 方法级块）
- source: `sample/plugin/api/com/mentor/chs/plugin/drc/IXDRCheck.Severity.html`
- plugin_type: `drc`
- package: `com.mentor.chs.plugin.drc`
- class: `IXDRCheck.Severity`
- method: `Information`
- signature: `public static final IXDRCheck.Severity Information`

Before:
`<h4>Information</h4> <pre>public static final IXDRCheck.Severity Information</pre> ...`

After:
`IXDRCheck.Severity Information public static final IXDRCheck.Severity Information`

### 示例 C（constraint 方法级块）
- source: `sample/plugin/api/com/mentor/chs/plugin/constraint/IXAdditionalWireLengthConstraint.html`
- plugin_type: `constraint`
- package: `com.mentor.chs.plugin.constraint`
- class: `IXAdditionalWireLengthConstraint`
- method: `match`
- signature: `boolean match( IXWire wire, IXWireLengthResult result)`

Before:
`<h4>match</h4> <pre>boolean match( IXWire wire, IXWireLengthResult result)</pre> ...`

After:
`IXAdditionalWireLengthConstraint match boolean match( IXWire wire, IXWireLengthResult result)`

## 2) 分块（chunking）示例

### 类级块（Class-level）
- action: `.../action/IXAction.Trigger.html::class::IXAction.Trigger`
- drc: `.../drc/IXDRCViolationReporter.html::class::IXDRCViolationReporter`
- constraint: `.../constraint/IXAdditionalWireLengthConstraint.html::class::IXAdditionalWireLengthConstraint`
- core: `.../plugin/IXApplicationContext.html::class::IXApplicationContext`

特征：覆盖接口定义、继承关系、方法总览，适合“先找方向”。

### 方法级块（Method-level）
- action: `...::method::IXAction.Trigger.MainMenu`
- drc: `...::method::IXDRCheck.Severity.Information`
- constraint: `...::method::IXAdditionalWireLengthConstraint.match`

特征：签名精确、噪声低，适合“强约束生成”。

### 示例类块（Java Example-level）
- action: `.../action/AllDesignsContentAction.java::java::AllDesignsContentAction`
- drc: `.../drc/BaseDRCheck.java::java::BaseDRCheck`
- constraint: `.../constraint/AbstractPropertyToCodeAssignationConstraint.java::java::AbstractPropertyToCodeAssignationConstraint`

特征：包含实现模式（`implements`、`execute/check/match`），适合“模板迁移”。

## 3) 标签化（plugin_type）示例

- action 路径命中：
  - `sample/plugin/api/com/mentor/chs/plugin/action/...`
  - `sample/plugin/examples/Java/src/com/example/plugin/action/...`
- drc 路径命中：
  - `sample/plugin/api/com/mentor/chs/plugin/drc/...`
  - `sample/plugin/examples/Java/src/com/example/plugin/drc/...`
- constraint 路径命中：
  - `sample/plugin/api/com/mentor/chs/plugin/constraint/...`
  - `sample/plugin/examples/Java/src/com/example/plugin/constraint/...`
- core 依赖：
  - `sample/plugin/api/com/mentor/chs/plugin/IXApplicationContext.html`
  - `sample/plugin/api/com/mentor/chs/plugin/IXPlugin.html`
  - `sample/plugin/api/com/mentor/chs/plugin/IXOutputWindow.html`

## 4) 建索引示例

### chunks（主表）
存结构化事实：
- 主键：`chunk_id`
- 内容：`text`
- 元数据：`source_path/source_type/plugin_type/package/class/method/signature/symbols_json`

### chunks_fts（BM25）
用于关键词召回：
- 可命中 `IXLogicAction`, `execute`, `IXDRCViolationReporter`, `IXAdditionalWireLengthConstraint`

### dense 向量索引
文件：`runtime/dense_index.npz`
- `ids`: 与 `chunk_id` 对齐
- `vectors`: 语义向量

检索时流程：
1. BM25 召回候选
2. dense 召回语义近邻
3. RRF 融合 + symbol overlap 加权
4. 形成 evidence cards 给生成层
