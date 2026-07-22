# Spec Discussion

將已填 Overview / Requirements 的 spec 文件，透過閱覽補齊框架、疑問、功能設計與 coding 範圍，收斂成可直接實作的規格。

## 參數

使用者可帶入參數：`/spec <參數>`

| 參數 | 行為 |
|---|---|
| `init` | 以 `doc/spec/_template.md` 建立一份新的空白 spec，供使用者填 Overview / Requirements |
| 關鍵字 / 日期 / 檔名片段 | 搜尋 `doc/spec/` 下符合的文件，載入後進入 Review 流程 |
| 無參數 | 列出 `doc/spec/` 下所有文件，讓使用者選擇要 init 還是 review 哪份 |

參數由 `$ARGUMENTS` 取得。

---

## 入口判斷

```
若 $ARGUMENTS == "init"
  → 進入【Init 流程】

若 $ARGUMENTS 不為空（非 "init"）
  → 搜尋 .claude/doc/spec/ 下檔名或內容含該字串的文件
  → 唯一符合 → 載入，進入【Review 流程】
  → 多份符合 → 列出清單請使用者確認
  → 找不到 → 告知查無，詢問是否改為 init

若 $ARGUMENTS 為空
  → 列出 .claude/doc/spec/ 下所有 .md（排除 _template.md）
  → 顯示檔名與第一行標題
  → 請使用者選擇或輸入 init
```

---

## Init 流程

1. 以 `doc/spec/_template.md` 為基礎建立文件，命名 `YYYY-MM-DD_<slug>.md`
2. 告知使用者路徑，並說明：**請先填寫 Overview 與 Requirements**，完成後執行 `/spec <關鍵字>` 進行 review
3. 不主動代填 Overview / Requirements，除非使用者明確要求

---

## Review 流程

前提：使用者已填好 Overview 與 Requirements。閱覽後依序補齊以下四段，每段寫回文件後呈現。

### Step 1 — Framework
- 對照現有 codebase 與架構（參考 `MyDays/CLAUDE.md` 的套件結構與元件）
- 說明這份 spec 會落在哪個 package、與既有元件的關係、可沿用什麼
- 寫入 `Framework`

### Step 2 — Open Questions
- 檢視 Requirements，指出不足、有歧義或缺少的資訊
- **每項附 2 個以上方案並給出建議**，讓使用者選擇
- 寫入 `Open Questions`（附 `決議：` 待填）

### Step 3 — Functional Design
- 定義功能運作方式：資料模型、狀態流轉、UI 互動、邊界條件
- 若牽涉未決議的 Open Question，先以建議方案為前提設計並註明
- 寫入 `Functional Design`

### Step 4 — Coding Scope
- 列出預計新增 / 修改的檔案與範圍
- 對照現有檔案結構，明確標示 package 路徑
- 寫入 `Coding Scope`

### 收斂
- 使用者針對 Open Questions 選擇方案後，回頭更新 Functional Design 與 Coding Scope
- 補齊 Acceptance Criteria

---

## 注意事項

- Overview / Requirements 是使用者的輸入，Agent 不主動改寫
- 每段分析完成後立即寫回文件，不累積到最後
- Coding Scope 需對照真實檔案結構，不虛構路徑；不確定時先查再寫
- 顆粒度為「可實作規格」，比 feature 層更細
- 所有回覆使用繁體中文
