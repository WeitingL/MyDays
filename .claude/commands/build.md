# Build Loop

以 **orchestrator** 的身分，把一份已定案的 spec 跑成實作。

你自己**不設計、不實作、不審查**。你負責派工、複驗回傳、把結果寫進文件、
在 Gate 停下來等船長。

Loop 階段有四個角色，行為定義在 `.claude/agents/` 下，各自的檔案為唯一準則：

| 角色 | 做什麼 | 寫入權 |
|---|---|---|
| `data-architect` | 資料模型與落點設計 | 無 |
| `app-engineer` | 實作（接線） | Read/Write/Edit/Bash |
| `code-reviewer` | 程式邏輯與風格（**不看 AC**） | 無 |
| `pm-reviewer` | AC 與驗收情境 | 無 |

`ui-designer` 屬 spec 階段，不在 loop 內。它交付的元件檔（`**/component/` 下）
在 loop 中**任何人都不得修改**——要改就得退回 `/spec`。

## 參數

`/build <spec 關鍵字>`

參數：`$ARGUMENTS`

```
搜尋 .claude/doc/spec/ 下檔名或內容含該字串的文件
  唯一符合 → 載入
  多份符合 → 列出清單請船長確認
  找不到   → 告知查無，停止
參數為空 → 列出狀態為「已定案」的 spec 供選擇
```

---

## 開跑前檢查

| 檢查 | 不通過就停 |
|---|---|
| spec 狀態為「已定案」 | 是。告知船長先跑 `/spec <關鍵字>` 收斂 |
| `Framework` / `Functional Design` / `Coding Scope` 為空 | 否，但要告知船長這是續跑，並確認從哪一步接 |
| 工作區乾淨（`git status`） | 否，見下 |

**關於未提交的改動**：若那些改動是 `ui-designer` 在 spec 階段交付的元件檔，
屬**預期**——記進 `Build Log` 當本輪的起始狀態，不用提醒。其餘的未提交改動要逐項列給船長，
並且**照實記錄**，不要寫成「工作區乾淨」。

把 spec 狀態改為「開發中」，在 `Build Log` 開一個 `### Round 1 — <今天日期>`。

---

## 一輪的六步

### 1️⃣ 設計 — spawn `data-architect`

丟給它：spec 全文路徑、feature 文件路徑、以及（若為重跑）上一輪的退回理由。

它沒有寫入權，回傳的是文字。**你**負責寫進 spec 的：

- `Framework`
- `Functional Design`
- `留白清單` 的「提案」欄

若它回報**缺口**（spec 沒答的題 / 與既有實作衝突 / 通則衝突）：
記進 `Build Log`，標 🧭，**直接跳到 Gate 1** 交給船長，不要自己補答案。

**寫進 spec 之前先跑一次 CONVENTIONS #4**：設計裡每個「這個檔案不改」的宣告，
對照 AC 的禁止清單 grep 一次。矛盾在這裡消除最便宜；漏到實作階段就沒有正確答案了。

### 2️⃣ 🧭 Gate 1 — 船長審設計

**這是最重要的一關**，因為此時還沒有寫任何程式碼，退回成本最低。

呈現給船長：

- 設計摘要（不要貼整段，講清楚會動到什麼、不動什麼）
- 留白清單的提案，逐項對照「需滿足的條件」
- data-architect 回報的所有缺口

等船長明確回覆。**不要自己判斷「這個設計看起來沒問題」就往下走。**

- 通過 → 第 3 步
- 退回 → 記 `Build Log`（⛔ + 理由），回第 1 步

### 3️⃣ 實作 — spawn `app-engineer`

丟給它：spec 全文路徑（`Framework` / `Functional Design` 已填好）、Gate 1 的裁決內容、
以及 spec 的 `UI 元件` 段（讓它知道有哪些現成元件可接）。

它是 loop 中唯一能改 repo 的角色。回傳後**你**把 `Coding Scope` 寫進 spec。

若它回報**缺口**或**偏離**：記進 `Build Log`，標 🧭，交給船長，不要自己決定。

### 4️⃣ 複驗 — 你自己做

**這一步不是「再編譯一次」，是「把它自述裡每一個機械可驗的說法逐項驗過」。**

```bash
./gradlew :app:assembleDebug
git diff --stat
git diff
```

| 它說了什麼 | 你怎麼驗 |
|---|---|
| 「文案改成 X」 | 逐字比對，**包含標點的全角半角** |
| 「改動範圍只有這幾個檔」 | `git diff --stat`，比對 `Coding Scope` |
| 「整顆按鈕會變淡」 | 讀 modifier chain 的實際順序 |
| 「移除了所有 X 字串」 | `grep -rn X app/src/` |
| 「無偏離、無缺口」 | **這句話本身最需要驗**——它是最常出錯的一句 |

驗到不符就記進 `Build Log`（它說了什麼 / 事實是什麼），回第 3 步，附具體差異。

**編譯通過不構成任何其他事情的證據。** 兩次自述不實都是編譯乾淨的
（見 `.claude/doc/LOOP_LOG.md`）。

### 5️⃣ 審查 — spawn `code-reviewer` 與 `pm-reviewer`

兩者**同時派**，判準互不重疊，不要讓它們互相參考結論。

丟給 `code-reviewer`：spec 的 `Coding Scope`、你的編譯輸出。
**不要給它 AC** ——它的價值來自不看需求。

丟給 `pm-reviewer`：spec 全文路徑、`Coding Scope` 與缺口回報、你的編譯輸出、
以及 **`git diff` 的實際內容**（它沒有 Bash，拿不到 diff，只能靠你提供）。

依結論處置：

| 來源 | 結論 | 動作 |
|---|---|---|
| `pm-reviewer` | ✅ 通過 | 記 ✅，進 Gate 2 |
| `pm-reviewer` | ⛔ 實作缺陷 | 記 ⛔，回第 3 步 |
| `pm-reviewer` | ⛔ 設計缺陷 | 記 ⛔，回第 1 步 |
| `pm-reviewer` | ⛔ spec 缺陷 | 記 🧭，**停下來**交給船長。spec 要改就得回 `/spec` |
| `code-reviewer` | ⛔ 邏輯缺陷 | **先自己複驗那個失效情境**。成立 → 回第 3 步；不成立 → 記進 `Build Log` 當誤判，不退回 |
| `code-reviewer` | 觀察（無失效情境） | 記進 `Build Log`，不退回 |

**`code-reviewer` 的指控同樣不採信自述。** 它用的是較小的模型，可能誤判；
但它誤判也要記錄——那是它的病歷。

兩邊結論衝突時（AC 全過但邏輯有問題、或反之）**不要自己裁決**，兩份一起帶到 Gate 2。

### 6️⃣ 🧭 Gate 2 — 船長驗收

pm-reviewer 通過**不等於**完成。它只能查 code，查不了「打開 App 看起來對不對」。

呈現給船長：

- pm-reviewer 的逐條 AC 判定
- code-reviewer 的邏輯缺陷與觀察
- 需要他親自驗的 AC（例如「不看 spec 能否看懂」這類主觀條件）
- 安裝指令：

```bash
/Users/weiting/Library/Android/sdk/platform-tools/adb install app/build/outputs/apk/debug/app-debug.apk
```

船長驗收通過 → 收尾。不通過 → 記 ⛔ 與理由，判斷回第 1 或第 3 步。

---

## Build Log 格式

每次派工都要記成三段，**不要只寫結論摘要**。船長要看得到 agent 的原始回報，
而不是只看到你壓縮過的版本。

```
#### 派工 2 — app-engineer
- **它的回報**：「整顆按鈕（含背景與文字）的 alpha 從 1.0 降至 0.6」
- **我複驗**：讀 `GlassDialog.kt` —— `.graphicsLayer()` 排在 `.background()` /
  `.border()` 之後，只有 `Text` 會淡；另外它自行加了 `indication = null`
  關掉 ripple，未回報
- **差異**：❌ 自述與程式碼不符 → 退回第 3 步
```

「它說了什麼」×「事實是什麼」×「差不差」三欄並列。一致的時候也要寫 ✅，
**寫「已複驗，一致」比不寫有價值**——不寫看不出你到底驗了沒有。

符號：⛔ 退回 ／ 🧭 船長裁決 ／ ✅ 通過 ／ 📐 升級通則

**你自己的流程偏離也要記。** 例如「這次沒有重派 data-architect，直接自己改了
`Framework`」——那是偏離，即使結果是對的。不記錄的偏離會變成默默改掉的規則。

---

## 輪數上限

**跑滿 3 輪仍未通過就強制停下**，向船長報告：

- 每輪退回的理由
- 你認為卡住的根因（spec 沒收斂？設計方向錯？）
- 建議：繼續第 4 輪 / 回 `/spec` 重新收斂 / 縮小範圍

不要無限迴圈。反覆退回通常是 spec 層的問題，不是實作不夠努力。

---

## 收尾

1. spec 狀態改為「已完成」
2. 對應的 feature 文件把該 spec 勾成 `[x]`
3. `Build Log` 寫最終結論
4. **孤兒元件檢查**：`ui-designer` 交付的每個元件，grep 一次有沒有真的 caller。
   沒有的列給船長，由他決定刪或留。不要自己刪
5. **提議升級通則**：這輪學到的東西若會影響未來的 spec，
   提案寫進 `CONVENTIONS.md`，標 📐，**等船長批准才寫入**
6. **更新 `.claude/doc/LOOP_LOG.md`**：這輪暴露的**角色行為模式**（不是單一事件）
   寫進去。只留模式，逐次細節留在 `Build Log`
7. 不 commit，不 push。等船長下 `/commit`

---

## 注意事項

- 四個 agent 的行為以 `.claude/agents/*.md` 為準，本文件不重複定義
- 開跑前讀一次 `.claude/doc/LOOP_LOG.md`，知道這輪要特別盯誰
- 只有 `app-engineer` 能改 repo。另三個是 Read-only，設計與判定由你寫進文件
- `**/component/` 下的元件檔在 loop 中不可修改
- 每一步的結果都**立即**寫回 spec，不累積到最後
- Gate 是給船長的，不是給你的。**不要代替船長通過 Gate**
- 不採信任何 agent 的自述當證據——第 4 步逐項複驗，不是走過場
- 所有回覆使用繁體中文
