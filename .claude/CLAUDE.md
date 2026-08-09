# 協作方式

本文件補充說明 Agent 在 MyDays 專案中的協作行為，與全域 `~/CLAUDE.md` 搭配使用。

## 角色定位

- **我**（weiting）主導所有設計與架構決策
- **Agent** 負責實作、研究、提案，不自行做重大決定

## 工作流程

### 一般任務
1. 我提出需求 → Agent 直接實作並回報異動
2. 程式碼修改以 bullet list 摘要呈現

### 探索 / 設計討論
1. 我提問 → Agent 提供比較表或方案分析，**不主動實作**
2. 我確認後才動手

### 重大變更（刪檔、重構、架構調整）
1. Agent 先說明計畫並等待確認
2. 確認後才執行

## 討論記錄

文件依用途分四層存放：

| 路徑 | 用途 | 觸發方式 |
|---|---|---|
| `.claude/doc/CONVENTIONS.md` | 跨 spec 的長期通則，所有 spec 與 agent 都受約束 | agent 提議、我批准 |
| `.claude/doc/LOOP_LOG.md` | 跨 spec 的 loop 病歷：各角色的行為模式與 orchestrator 對策 | 每輪 loop 收尾時由 orchestrator 更新 |
| `.claude/doc/feature/` | 功能方向規劃：問題定義、User Stories、設計準備、開發階段拆解 | `/feature` skill |
| `.claude/doc/spec/` | 子功能實作規格：需求細節、功能設計、coding 範圍 | `/spec` skill |

- 檔名格式：`YYYY-MM-DD_<主題>.md`
- 各目錄下均有 `_template.md` 作為建立新文件的基礎
- Agent 在被要求時建立或更新，不主動新增

## 記憶管理

- 使用者背景與偏好存於 `~/.claude/projects/.../memory/`
- 每次對話開始前，Agent 應讀取 `MEMORY.md` 索引以取回上下文

## 溝通語言

- 所有回覆使用**繁體中文**
- 程式碼、檔名、指令保持英文原文

## 禁止行為

- 不主動 push 或開 PR
- 不在未被要求時新增文件或 `.md` 檔
- 不加入未被要求的 error handling、fallback、validation
- 不在 git commit 前詢問「要我 commit 嗎？」— 等待我下 `/commit`
