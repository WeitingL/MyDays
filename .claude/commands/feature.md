# Feature Discussion

引導使用者從模糊的功能想法，逐步釐清成完整的 feature 文件，最終識別出需要進入 spec 討論的子項目。

## 參數

使用者可帶入參數：`/feature <參數>`

| 參數 | 行為 |
|---|---|
| `init` | 建立一份新的 feature 文件，進入 Step 1 起始流程 |
| 關鍵字 / 日期 / 檔名片段 | 搜尋 `doc/feature/` 下符合的文件，載入後進入 Review 流程 |
| 無參數 | 列出 `doc/feature/` 下所有文件，讓使用者選擇要 init 還是 review 哪份 |

參數由 `$ARGUMENTS` 取得。

---

## 入口判斷

執行 skill 時，先讀取 `$ARGUMENTS`，依以下邏輯決定走哪條路：

```
若 $ARGUMENTS == "init"
  → 進入【Init 流程】

若 $ARGUMENTS 不為空（非 "init"）
  → 搜尋 .claude/doc/feature/ 下檔名或內容含該字串的文件
  → 若找到唯一符合 → 載入，進入【Review 流程】
  → 若找到多份 → 列出清單，請使用者確認要開哪份
  → 若找不到 → 告知查無文件，詢問是否改為 init

若 $ARGUMENTS 為空
  → 列出 .claude/doc/feature/ 下所有 .md（排除 _template.md）
  → 顯示檔名與第一行標題
  → 請使用者輸入編號 / 關鍵字，或輸入 init 建立新文件
```

---

## Init 流程

核心模式：**使用者傾倒需求 → Agent 提供分析與方案供選擇**。不採逐題問答。

### Step 1 — 接收 Feature Brief
- 請使用者描述：想要什麼功能、它要具備哪些能力、預期使用情境
- 使用者不需一次講完整，能講多少算多少
- 以 `doc/feature/_template.md` 為基礎建立文件，命名 `YYYY-MM-DD_<slug>.md`，slug 依 Brief 主題決定
- 將 Brief 原文整理後寫入 `Feature Brief`
- 告知使用者文件路徑

### Step 2 — 產出分析
根據 Brief 一次性產出以下三段，寫入文件後呈現給使用者：

- **Design Review** — 整理 Brief 中已經清楚、可直接定案的設計方向
- **Ambiguities** — 指出定義不清、有歧義或缺少的資訊；**每項附上 2 個以上方案並給出建議**，讓使用者選擇而非開放式回答
- **Proposed Phases** — 以 MVP 優先原則提出階段拆分方案，每階段可獨立交付

### Step 3 — 收斂決議
- 使用者針對 Ambiguities 選擇方案或提出調整
- 逐項將決議寫回文件；未決議者轉入 `Open Questions`（附 `答：`）
- 依決議更新 `Design Review` 與 `Proposed Phases`

### Step 4 — 識別 Spec Items
- Phases 穩定後，從中識別需要個別撰寫 spec 的子功能
- 列出建議清單並說明各 spec 範圍，寫入 `Spec Items`（標記 `[ ]` 待建立）

---

## Review 流程

載入既有 feature 文件，協助使用者繼續或審閱。

1. 讀取文件內容，摘要目前各章節的填寫狀況
2. 標示哪些章節已完成、哪些仍有空白或待確認的 `答：`
3. 詢問使用者：「要繼續填寫哪個部分，還是有新的討論要加入？」
4. 依使用者指示更新對應章節
5. 若 Open Questions 有新增答案，同步更新文件

---

## 注意事項

- 每個步驟結束後，立即更新文件，不要等到全部討論完才一次寫入
- 若使用者的回答引出新的問題，先記錄在 Open Questions，不要打斷主流程
- 顆粒度保持在「功能方向」層次，實作細節留給 spec 討論
- 所有回覆使用繁體中文
