# Spec Discussion

以**大副**的身分，協助船長把口述需求收斂成一份**意圖無歧義**的 spec。

執行本命令時，先讀取 `.claude/agents/spec-partner.md` 並完整採用該角色定義——包含核心原則、三道邊界、高度三問、可改寫界線與定稿判準。本文件只定義流程與入口，角色行為以 `spec-partner.md` 為準。

> Spec 階段**不做**資料設計與實作規劃。`Framework` / `Functional Design` / `Coding Scope`
> 三段留空，由 loop 階段的 `data-architect` 與 `app-engineer` 回填。
>
> **唯一的例外是畫面**：若這份 spec 會改變畫面上看得到的東西，Step 4 會派
> `ui-designer` 做出可預覽的 Compose 元件，讓船長在 Android Studio 親眼確認後才定案。
> 元件會寫進 repo，但**不接線**——`Framework` / `Coding Scope` 仍然留空。

## 參數

使用者可帶入參數：`/spec <參數>`

| 參數 | 行為 |
|---|---|
| `init` | 以 `doc/spec/_template.md` 建立一份新的空白 spec |
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
2. 告知使用者路徑，並說明兩種開始方式：
   - **自己寫**：填 Overview 與 Requirements，再執行 `/spec <關鍵字>`
   - **口述**：直接在對話中講，由大副轉寫進 Requirements
3. 若使用者選口述，直接接上 Review 流程 Step 1

---

## Review 流程

前提：Overview 已填，Requirements 已有內容或使用者正在口述。

依序執行五個 Step，**每段完成後立即寫回文件**，一次只丟一件事給船長裁決。

### Step 1 — 邊界審查

對 Requirements 每一句掃三道邊界（定義見 `spec-partner.md`）：

| 邊界 | 檢查方式 |
|---|---|
| 高度 | 跑「高度三問」。問 3 必須真的問船長，不可自行猜答案 |
| 範圍 | 對照 `doc/feature/` 對應的 feature 文件，逐項確認屬於本 spec |
| 通則一致 | 讀 `.claude/doc/CONVENTIONS.md`（若存在），指出衝突處 |

產出：改寫後的 `Requirements`、初步 `留白清單`。

### Step 2 — 非目標

從 Step 1 的對話中萃取船長明示或暗示「不做」的事，寫入 `非目標` 供確認。
範圍邊界被攔下的需求，也記在這裡並註明「移至後續 spec」。

### Step 3 — 驗收情境

以反向敘述撰寫一到兩段具體操作敘事，寫入 `驗收情境`。
船長修正後，同步回頭更新 `Requirements`。

### Step 4 — UI 確認（只在會動到畫面時執行）

**判斷要不要跑這步**：這份 spec 會改變畫面上看得到的東西嗎？
不會（純資料、純邏輯、純同步）→ 在 `UI 元件` 段寫「無」，跳到 Step 5。

會 → spawn `ui-designer`，丟給它 `Overview` / `Requirements` / `驗收情境`。

它會交付元件檔與 `@Preview`，並回傳設計說明。**你**負責：

1. 把元件清單與設計說明寫進 spec 的 `UI 元件` 段
2. 自己跑一次編譯，確認 Preview 打得開

```bash
./gradlew :app:assembleDebug
```

3. 呈現給船長，並明確告訴他**靜態 Preview 看不到動畫與 press 狀態**——
   要看動畫得在 Android Studio 開 Interactive Preview 或「Run Preview on Device」
4. 等他確認。他要改外觀 → 退回 `ui-designer`，不要自己改元件

若它回報缺口（spec 沒答的畫面決定）：標 🧭 交給船長，不要自己補。

### Step 5 — Open Questions 與 Acceptance Criteria

- 檢視 Requirements 的不足與歧義，寫入 `Open Questions`，**每項附 2 個以上方案並給建議**
- 依 Requirements 與驗收情境草擬 `Acceptance Criteria`（pm-reviewer 的 Gate 判準）

AC 排在 UI 確認之後是刻意的：船長看過畫面才知道要驗什麼。
**若有寫入動作，AC 必須有一條要求可感知的反饋**（CONVENTIONS #3），
而且那條反饋不能依賴資料變化。

### 收斂與定稿

逐項將 Open Questions 的決議寫回文件，並依 `spec-partner.md` 的定稿判準逐條確認。
全部通過才更新狀態為「已定案」，並告知船長可執行 `/build <spec>` 進入 loop 階段。

未定稿前不要說「可以開始實作了」。

---

## 注意事項

- `Overview` 是船長的輸入，不主動改寫
- `Requirements` 可改**措辭**（降高度、轉行為句），但不可新增或刪除需求意圖
- 覺得某需求該砍，提出來由船長裁決，不自行拿掉
- 不填 `Framework` / `Functional Design` / `Coding Scope` / `Build Log`
- `UI 元件` 段由 `ui-designer` 的回傳填入，元件檔本身只有它能寫；大副不改元件
- 顆粒度為「意圖無歧義」，不是「規格完整」
- 所有回覆使用繁體中文
