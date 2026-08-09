# 習慣兩種模式的正向呈現

> **這份文件回答**：這一次交付什麼？完成長什麼樣？
>
> 所屬 Feature：`doc/feature/2026-07-22_habit.md`（Phase 4）
> 狀態：**已完成**（2026-08-08 定案並進入 build loop，2026-08-09 Gate 2 驗收通過）
> Jira：___

---

## Overview

> 由我填寫。這份 spec 要定義的範圍是什麼？
> （大副依 feature Phase 4 草擬，船長已確認 2026-08-08）

把習慣的兩種模式從「好習慣 / 壞習慣」改為兩邊都正向的呈現。
兩種模式的區分保留，移除單側的負面包裝。只改呈現，不動資料結構、
streak 計算、同步與提醒機制。

---

## Requirements

> 由我填寫，大副（spec-partner）協助降高度改寫。
> ⚠️ 以下每一句都可回溯到 `doc/feature/2026-07-22_habit.md` 的已結案決議，
> 大副未新增任何需求意圖。追溯欄位供船長稽核。

| # | 需求 | 來源 |
|---|---|---|
| 1 | 建立習慣時要選一種模式，兩個選項顯示為「每天都做」與「克制不做」 | Ambiguity 3 決議 |
| 2 | 習慣的天數顯示為「連續 N 天做了」（每天都做）或「連續 N 天沒做」（克制不做）；N 為 0 時改用不含數字的句子（見文案定義） | Ambiguity 3 決議、Open Q1 |
| 3 | App 任何地方都不出現「好習慣 / 壞習慣」的分類名稱、「破戒」字樣，也不使用表達禁止或失敗語意的圖示 | Phase 4 |
| 3b | 原始碼識別字也不得帶「破戒 / relapse」語意（🧭 Gate 1 裁決 1B，2026-08-08）。**但被序列化的識別字例外**——見約束 | Gate 1 |
| 4 | 兩種模式的天數以同等的正向分量呈現，不因模式不同而有正負差異 | Design Review「語氣分工」 |
| 5 | 當連續天數**達到** 7、30、100 天、以及之後每滿 100 天時出現文字稱讚；其餘天數不出現文字稱讚 | Ambiguity 4 決議 C、Open Q4 |
| 6 | 記錄動作的按鈕文字只登記事實，不評價 | Design Review「語氣分工」 |
| 7 | 使用者不需要說明就能看懂每個數字在算什麼，以及按下記錄按鈕會記下什麼 | 船長對問 3 的回答（2026-08-08） |
| 8 | 點擊記錄按鈕的**當下**要有立即可見的視覺反饋，讓使用者知道點擊成功了 | 🧭 Gate 2 退回（2026-08-09） |

**不可協商的約束**：

- 兩種模式的區分**必須保留**。它們是不同的數字、不同的語意，不可合併為一種
- `HabitType` 的 `BUILD` / `QUIT` 值**絕對不改**。它被序列化進資料：
  `HabitRemoteDataSource.kt:40` 寫 `type.name`、`:59` 讀 `HabitType.valueOf(...)`，
  Room 亦透過 `HabitConverters` 存字串。改名等於既有雲端資料讀不回來
- **需求 3b 的界線**（🧭 Gate 1 裁決）：
  - **要改** —— 純方法名 / 變數名，如 `recordRelapse`。新名的語意是
    「登記事情發生了一次」，不含價值判斷
  - **不改** —— 被序列化的 `HabitType.BUILD` / `QUIT`；
    以及鏡像該 enum 的 `buildStreak` / `quitStreak`
    （enum 留著，函式跟著 enum 走才一致；只改函式名反而更混亂）
- 需求 5 的觸發條件是「連續天數達到門檻」，**不是**「使用者按了按鈕」
  （`CONVENTIONS.md` #2：達成不可綁死在 UI 動作上。未來健身模組寫入的達成
  也必須能觸發里程碑稱讚）
- **可理解性優先於美感**：船長對視覺手法無偏好，但「清楚呈現」與
  「使用者輕鬆理解怎麼互動」是不可協商的。若某個視覺方案漂亮但需要說明才懂，
  該方案不合格（見需求 7）

**交付範圍**：需求 1–4、6–7（改寫用語）與需求 5（里程碑稱讚）**同一次交付**，
不拆成兩份 spec——稱讚依附在天數的呈現上，分兩次會把同一塊 UI 動兩遍。
（2026-08-08 船長授權大副決定）

### 文案定義

> Open Q1 決議 B、Q3 決議 A、Q4 決議 B 的產物。文案是使用者看得到的內容，
> 屬 Requirements，不是留白。

**天數為 0 時的顯示**（取代「連續 0 天…」）

| 模式 | 顯示 |
|---|---|
| 每天都做 | 今天開始 |
| 克制不做 | 從今天重新開始 |

**里程碑稱讚**

| 天數 | 文案 |
|---|---|
| 7 | 連續 7 天了，很棒！ |
| 30 | 30 天了。這已經變成習慣了。 |
| 100 | 100 天。真的很了不起。 |
| 200、300、…（每 100 天） | N 天了。一直都在。 |

**約束：文案必須對兩種模式都成立。**
同一句話會出現在「連續 N 天做了」與「連續 N 天沒做」兩種卡片上，
因此句子只能稱讚**天數本身**，不可提到「做」這個動作——
否則「克制不做」那側會讀不通。日後要改文案也受此限制。

---

## 非目標

> 由大副從對話中萃取、我確認。明確寫出這份 spec **刻意不做**什麼。

**不動既有實作**（本次只改呈現）：

- 不動 streak 計算 —— `buildStreak` / `quitStreak` 的邏輯完全不變
- 不動資料結構 —— `HabitType` 的 enum 值不改，`CheckIn` 不加欄位（含「來源」欄位）
- 不動 Room ↔ Firestore 同步
- 不動提醒機制本身

**明確移出，交給別處**：

| 不做的事 | 去哪 |
|---|---|
| 「克制不做」模式的提醒策略調整（提醒「你已 12 天沒做」易反效果） | 後續修 `habit-reminder` |
| 其他模組（如健身）寫入達成 | feature「對外接點」，無 Phase |
| App 主動鼓勵使用者 | 主頁面「今天摘要」feature，尚未開檔 |
| 間隔限制（每 N 天才能一次的上限、倒數、阻擋） | 已於 feature 層取消，不做 |
| 歷史最佳連續天數（`bestStreak`） | 已於 feature Ambiguity 1 決議不做 |
| 補打卡（回補前幾天） | 已於 feature Ambiguity 2 決議不做 |
| 「今天已記錄」的**持久狀態**（chip 依今日是否記錄過而改變外觀 / 文字） | 新 spec（🧭 Gate 2 裁決 2026-08-09） |
| 「克制不做」的記錄**撤銷**（誤按可還原今天的記錄） | 新 spec（同上） |

**需求 8 的界線**（🧭 Gate 2，2026-08-09）：本次只做**按壓當下**的即時反饋。
上表兩項（持久狀態、撤銷）是船長明確認定的**新增 spec 範圍**，不在本次。
QUIT 的 `completedToday` 在 `HabitRepositoryImpl.kt:34` 目前硬寫 `false`，
**本次不改** —— 它是「持久狀態」那條的前置，屬新 spec。

---

## 驗收情境

> 由大副撰寫、我確認。以「完成後我實際操作」的敘事描述。只寫主線。
> ✅ 船長已確認（2026-08-08，三段皆無修正）

**情境一 — 瀏覽與記錄**

早上 7:10 打開習慣頁，兩張卡片。「每天運動」寫著「連續 6 天做了」，
「克制亂花錢」寫著「連續 23 天沒做」。兩個數字一樣醒目，沒有一張卡片
帶紅色警告或禁止符號，我看不出來哪一個是「壞的那種」。

點「每天運動」的打勾 → 變成「連續 7 天做了」，同時出現「連續 7 天了，很棒！」。
隔天再點，變成「連續 8 天做了」，這次沒有稱讚。

中午我亂買了東西，點「克制亂花錢」的記錄按鈕 —— 按鈕上寫的不是「破戒」，
我按下去之前就知道它會記下「今天發生了」。按完，卡片不是寫「連續 0 天沒做」，
而是「從今天重新開始」。畫面沒有任何一個字或圖示在責備我。

**情境二 — 建立**

點新增習慣，輸入「早睡」。模式有兩個選項：「每天都做」與「克制不做」，
沒有出現「好習慣」或「壞習慣」。選「每天都做」儲存後，清單多一張
「早睡」的卡片，上面寫著「今天開始」。

**情境三 — 今天還沒做**

隔天早上 7:00 打開，「每天運動」還沒打勾，卡片仍然寫「連續 8 天做了」——
數字沒有因為今天還沒做就掉。我從打勾本身的狀態就看得出來今天還沒完成，
不需要另一行字提醒我。

---

## 留白清單

> 由大副維護。我提到但屬於實作手段的決定，移到這裡，交給 data-architect 提案。

| # | 待決事項 | 需滿足的條件 | 提案（data-architect 回填 2026-08-08） |
|---|---|---|---|
| 1 | 「正向呈現」的具體視覺手法（顏色、圖示、排版） | 兩種模式的天數看起來同等是成就；沒有任一側帶有禁止、警告或失敗的視覺語意；**不需要說明就看得懂**（船長對美感無偏好，但可理解性是硬條件） | 兩模式共用同一個習慣列 composable 與同一組字級／字重／顏色常數；天數句升為列內最醒目文字；leading icon 兩模式**同一個**（`Icons.Default.LocalFireDepartment`，移除 `Block`）；不使用紅色系與 `SettingDestructiveColor`；BUILD 今日狀態改用 outline↔filled |
| 2 | 里程碑稱讚出現的位置與存續時間 | 達到 7 / 30 / 100 天時看得到；不干擾日常瀏覽；不需要使用者手動關閉 | 習慣列內的第三行文字，**出現條件 = 目前天數正好是里程碑值**（純函式 `Int → String?`）。不是 Snackbar、不是 Dialog、不是橫幅 |
| 3 | 「克制不做」模式的記錄按鈕如何讓人一眼看懂「按下去是登記事情發生了」 | 不用「破戒」等譴責字眼，也不能讓人誤解為「我克制成功了」；沿用 Liquid Glass，不另設計 | `GlassChoiceChip(text = "今天發生了", selected = false)`，放在與 BUILD 打勾**相同的 slot 位置** |

### 各項提案為什麼滿足條件

**#1** —— 「同等是成就」是**結構保證**的：模式只影響句尾兩個字，字級／字重／顏色／位置
全部共用同一組常數，不靠人工比對；且兩側都從 13sp / alpha 0.6 提升為列內最大文字。
「無負面語意」：唯一帶禁止語意的 `Icons.Default.Block` 被移除，且兩模式用**同一個**圖示 ——
同一個圖示不可能有一側帶負面語意。「不需說明就看得懂」：數字的意義完全由**句子**承載
（「連續 6 天做了」／「連續 23 天沒做」），**不靠顏色或圖示編碼**，所以不需要圖例；
圖示不承載任何判讀責任 —— 這也是船長若不喜歡火焰、換掉不影響任何 AC 的原因。

**#2** —— 「達標時看得到」：門檻當天一打開卡片就在天數句正下方，不需展開／捲動／剛好在場。
「不干擾」：只多一行文字，不彈出、不遮擋控制、不搶焦點；非里程碑的日子完全不佔位。
「不需手動關閉」：存續條件就是天數本身，天數離開里程碑值即自動消失 ——
沒有關閉鈕、沒有計時器、沒有已讀狀態。
**已排除**：Snackbar / Toast 需當下有人在收集才看得到，直接寫入 CheckIn 時會漏，違反 AC；
需手動關閉的橫幅直接違反第三個條件。

**#3**（船長點名最易做歪的一項）—— 「不用譴責字眼」：全句只描述事件發生，無評價。
「不能誤解為『我克制成功了』」的憑據：本模式的習慣名稱幾乎必然是「克制 X」形式
（spec 自己的例子是「克制亂花錢」），這使任何含「做」的按鈕文字都有第二種讀法 ——
「今天做了」「記一次」可以掛回「克制」這個動詞而被讀成「我今天克制了」，正是要避免的誤解。
**「發生」的主語只能是事件**：中文裡「克制發生了」不成立，所以「今天發生了」在語法上
**只能**指向被計數的那件事（亂花錢），沒有第二種讀法。這是不選「今天做了」的唯一理由。
其次它與同卡的天數句互為對照：「連續 23 天沒做」→ 按下「今天發生了」→「從今天重新開始」，
因果全在同一張卡上可見，按下前就能預期結果（需求 7）。用詞直接取自 spec 情境一的敘述
（「我按下去之前就知道它會記下『今天發生了』」），不是另造的說法。
**因此此處必須是文字而非圖示**：能直覺表達「這件事發生了」的圖示都是禁止／警告類，
已被需求 3 排除。**已排除**：「記錄」是動作名不是事實；「+1」看不出記的是什麼。

---

## Open Questions

> 由大副填寫。每項附 2 個以上方案並給建議。

1. **天數為 0 時顯示什麼**
   新建習慣、以及「克制不做」剛記錄完，都會落到 0。「連續 0 天沒做」讀起來會卡。
   - 方案 A：照實顯示「連續 0 天做了」／「連續 0 天沒做」
   - 方案 B：0 時換句子，如「還沒開始」／「今天剛記錄，重新開始」
   - 方案 C：0 時不顯示天數，只顯示習慣名稱
   - 建議：**方案 B**。需求 7 要求不用說明就看懂，A 會讓人停頓一秒；
     C 則讓卡片在最需要鼓勵的時刻變空白。
   - 決議：**方案 B**（2026-08-08）。文案見上方「文案定義」。

2. **「每天都做」今天還沒打勾時，天數怎麼呈現**
   既有 `buildStreak` 是「從今天或昨天往回算」，所以昨天做了、今天還沒做，
   仍會顯示「連續 6 天做了」。使用者可能誤以為今天已經算進去。
   - 方案 A：數字照既有邏輯不變，用打勾的視覺狀態表達「今天還沒」
   - 方案 B：文字補一句，如「連續 6 天做了 · 今天還沒」
   - 建議：**方案 A**。計算邏輯不動是非目標；「今天做了沒」用打勾狀態表達最直接，
     具體手法歸留白 #1。
   - 決議：**方案 A**（2026-08-08）。數字沿用既有邏輯，不加補述文字。

3. **稱讚文案由誰寫**
   - 方案 A：大副在 spec 定義固定文案
   - 方案 B：列進留白清單，交給下游提案
   - 方案 C：船長自己寫三句（7 / 30 / 100 各一句）
   - 建議：**方案 C**。這是你要聽的話，不是我該替你寫的話。
     三個里程碑三句，成本很低，但由別人代寫會變成罐頭文案。
   - 決議：**方案 A**（2026-08-08，船長選擇，與建議不同）。
     大副已寫入「文案定義」，船長保留隨時改字的權利；
     改字時須守住「文案對兩種模式都成立」的約束。

4. **超過 100 天之後還有里程碑嗎**
   - 方案 A：100 天之後不再出現稱讚
   - 方案 B：100 天之後每 100 天一次（200 / 300 …）
   - 建議：**方案 B**。成本幾乎為零，而且真正撐過 100 天的習慣最不該失去回饋。
   - 決議：**方案 B**（2026-08-08）。100 天之後每滿 100 天一次，文案見「文案定義」。

---

## Acceptance Criteria

> 由大副草擬、我確認。pm-reviewer 的 Gate 以此為判準。

**用語**
- [ ] 建立習慣的模式選擇顯示「每天都做」與「克制不做」
- [ ] 「每天都做」的卡片顯示「連續 N 天做了」；「克制不做」顯示「連續 N 天沒做」
- [ ] 全 App 原始碼中搜不到「好習慣」「壞習慣」「破戒」任一字串
- [ ] 記錄按鈕的文字是中性的登記語，不含評價
- [ ] 原始碼中不存在含 `relapse` 語意的識別字（需求 3b）
- [ ] `HabitType.BUILD` / `QUIT` 兩個 enum 值未改名，`buildStreak` / `quitStreak` 亦未改名

**呈現**
- [ ] 兩種模式的天數視覺分量相同，無一側使用禁止／警告／失敗語意的圖示
- [ ] 天數為 0 時顯示「今天開始」／「從今天重新開始」，不出現「連續 0 天」
- [ ] 「每天都做」今天尚未打勾時，天數不因此改變（Open Q2 決議 A）

**里程碑**
- [ ] 連續天數達 7 / 30 / 100 天、及之後每滿 100 天時出現文字稱讚，其餘天數不出現
- [ ] 四句文案與「文案定義」一致，且對兩種模式都讀得通
- [ ] 稱讚由「天數達到門檻」觸發，非由按鈕點擊觸發
      —— 驗證方式：不經 UI 直接寫入一筆 CheckIn 使天數跨過門檻，稱讚仍會出現

**未動到的東西（回歸）**
- [ ] `buildStreak` / `quitStreak` 的計算結果與改動前一致
- [ ] `HabitType` enum 值、`CheckIn` 欄位、Room ↔ Firestore 同步邏輯皆未改動
- [ ] 既有的雲端習慣資料在改動後正常顯示，無需遷移

**點擊反饋（需求 8）**
- [ ] 按下「今天發生了」的當下，按鈕本身有可見的視覺變化
- [ ] 該反饋不依賴 `streak` 是否改變 —— 在 `streak` 已為 0（新建習慣、
      或今天已記錄過）而天數句不會變的情況下，仍看得到點擊成功
- [ ] 未加入「今天已記錄」的持久狀態，`completedToday` 在 QUIT 仍為 `false`（已移出）

**可理解性（需求 7）**
- [ ] 船長不看 spec 直接開 App，能正確說出兩張卡片的數字各在算什麼，
      以及按下記錄按鈕會記下什麼

---

<!-- ══════════ 以下由 Loop 階段回填；Spec 定稿時應為空 ══════════ -->

## Framework

> 由 data-architect 填寫（2026-08-08 Round 1）。

**package 沿用 `data/habit/` 與 `ui/habit/`，不新增 package。本次改動全部落在顯示層。**

### 完全不動（對應「未動到的東西」回歸 AC）

- `data/habit/HabitWithStreak.kt` — `buildStreak`（14–23）、`quitStreak`（26–29）的
  **計算邏輯**與 `HabitWithStreak` data class 皆不改，
  **不加 `milestone` 之類欄位**（文案不進 data 層）
  - ⚠️ **2026-08-08 修正**（🧭 裁決 A，Round 1 pm-reviewer 抓出）：
    原文寫「皆不改」與 AC「原始碼中不存在含 `relapse` 語意的識別字」互斥 ——
    `quitStreak` 的參數名是 `relapseDays`。界線更正為：
    **計算邏輯與函式名不改，參數名 / 區域變數 / 註解的 `relapse` 語意要改**
- `data/habit/` 其餘全部：`HabitEntity`、`CheckInEntity`、`MyDaysDatabase`（version 維持 **4**）、
  `HabitDao`、`CheckInDao`、`HabitRepository(Impl)`、`HabitSyncManager`、`HabitRemoteDataSource`、`Habit`
- `di/AppModule.kt`
- `ui/habit/HabitViewModel.kt` — `toggleTodayCheckIn` / `recordRelapse` / `addHabit` 簽章不變
- `notification/` 四個檔 —— 含 `NotificationHelper.kt:38` 的「該完成「$name」了」。
  這句對「克制不做」讀不通，但 spec 已將其移出（→ 後續修 `habit-reminder`），本次不碰
- `ui/component/SettingItem.kt`、`GlassDialog.kt` — **不加參數、不改樣式**。
  共用元件不為單一 caller 開洞
  - ⚠️ **2026-08-09 修正**（🧭 Gate 2 退回，需求 8）：`GlassChoiceChip`
    （`GlassDialog.kt:170`）**要加按壓反饋**。這不違反上述原則 ——
    **不加參數**，且兩個 caller（`HabitScreen.kt:126` 與 `:242`）一致受益，
    不是為單一 caller 開洞。原文的「不改樣式」在此更正為
    「不改 `selected` / 未選的既有配色，僅補按壓態」
- `FeaturesScreen.kt:29` 與 `HabitScreen.kt:69` 的「習慣養成」不改 ——
  不屬需求 3 禁用的三個字串，是 feature 本身的名稱

### 要動的

- `ui/habit/HabitScreen.kt` — 拔掉 `HabitType.label()`（50–53）與
  `HabitWithStreak.subtitle()`（55–58）、`Icons.Default.Block`（85）、
  `GlassChoiceChip(text = "破戒")`（106）；習慣列改用新版面；編輯 dialog 的模式 chip 改讀新標籤
- **新增顯示層轉換**（建議 `ui/habit/HabitDisplay.kt`）：模式標籤、天數句、里程碑稱讚三個純函式。
  硬約束：① 不得放進 `data/habit/`；② 里程碑判定必須是**不含 Compose / Android 依賴的純函式**，
  輸入只有 `Int`
- **新增習慣列版面**（建議 `ui/habit/HabitCard.kt`，或由 app-engineer 決定併入 `HabitScreen.kt`）：
  組合既有 `SettingGroup` / `SettingDivider` / `GlassChoiceChip`，不新增視覺語彙。
  不沿用 `SettingItem` 的理由：其 subtitle 硬寫 13sp / alpha 0.6（`SettingItem.kt:141`），
  天數放進去不可能「醒目」，而為此改共用元件會波及 Profile 等既有 caller

### 與 CONVENTIONS #2 的關係（成本為零）

天數已是 `checkInDao.observeAll()` → `combine` → `streak` 的推導結果
（`HabitRepositoryImpl.kt:15-38`）。稱讚只要是 `streak` 的純函式，
任何來源寫入 CheckIn（UI、同步 pull、未來健身模組、直接寫 DB）都會走同一條 Flow 讓稱讚出現。
**不抽 interface、不設事件總線、不加來源欄位。**

---

## Functional Design

> 由 data-architect 填寫（2026-08-08 Round 1）。

### 資料模型

**無任何資料模型改動。** 本次所需的一切都能從既有的
`HabitWithStreak(habit, streak, completedToday)` 推導。

| 未來欄位三段 | 內容 |
|---|---|
| 現在就建 | **無**。不新增任何欄位、表或 DB version |
| 現在就加但 UI 先不用 | **無** |
| 只記錄不實作 | ① CheckIn 的「來源」欄位；② 「此里程碑已顯示過」的持久化狀態；③ `bestStreak`。三者日後加都便宜：Room 走 `fallbackToDestructiveMigration(dropAllTables = true)`（`AppModule.kt:29`），Firestore 缺欄位視為預設值 |

### 三個純函式

**① 模式標籤**（建立 / 編輯 dialog 的 chip）

| `HabitType` | 顯示 |
|---|---|
| `BUILD` | 每天都做 |
| `QUIT` | 克制不做 |

`HabitType.entries` 現行順序即 `BUILD, QUIT`，「每天都做」自然在左，不需額外排序。

**② 天數句**（輸入 `type` + `streak`）

| type | streak | 顯示 |
|---|---|---|
| `BUILD` | 0 | 今天開始 |
| `BUILD` | ≥1 | 連續 {streak} 天做了 |
| `QUIT` | 0 | 從今天重新開始 |
| `QUIT` | ≥1 | 連續 {streak} 天沒做 |

0 的替代句**以模式為唯一鍵**，不分「新建」與「剛記錄」——
Open Q1 已明載兩種情境共用同一句。

**③ 里程碑稱讚**（輸入只有 `streak: Int`，輸出 `String?`）

| 條件（依序判定） | 文案 |
|---|---|
| `streak == 7` | 連續 7 天了，很棒！ |
| `streak == 30` | 30 天了。這已經變成習慣了。 |
| `streak == 100` | 100 天。真的很了不起。 |
| `streak >= 200 && streak % 100 == 0` | {streak} 天了。一直都在。 |
| 其餘（**含 0**） | 無 |

- 文案**逐字**取自「文案定義」，含標點（7 用驚嘆號、30 與 100 用句號）。不得順手統一標點
- 判定順序不可調換：100 有專屬文案，必須在通用的每 100 天規則之前
- **`streak == 0` 必須明確排除**：`0 % 100 == 0`，寫錯會讓每個剛建立 / 剛記錄的習慣
  都跳出「0 天了。一直都在。」
- 稱讚**與 `type` 無關**，兩種模式共用同一函式與同一組文案 →
  直接滿足「文案對兩種模式都成立」的約束

### 版面（習慣列）

沿用 `SettingGroup` 卡片 + `SettingDivider` 分隔的既有結構，列內三段：

1. **leading icon** — 兩種模式**共用同一個圖示**（提案 `Icons.Default.LocalFireDepartment`；
   `material-icons-extended` 已在依賴中）。`Icons.Default.Block` 移除
2. **文字欄** — 第一行習慣名稱（維持 16sp / `SettingContentColor`）；
   第二行天數句，字級大於名稱、**不降透明度**（現行 13sp / alpha 0.6 → 約 20sp / SemiBold / 全不透明）；
   第三行稱讚，僅在 ③ 有輸出時存在（比天數句小、不使用強調色）
3. **trailing 控制欄** — 記錄控制 + 既有刪除 `IconButton`
   （`Icons.Default.Delete`, alpha 0.5，不動；刪除是管理操作，不帶模式語意）

| type | 記錄控制 | 狀態表達 |
|---|---|---|
| `BUILD` | 勾的 `IconButton`（沿用 `toggleTodayCheckIn`） | 已完成 = filled `CheckCircle`；未完成 = outline 版本。改用 outline↔filled 取代現行 alpha 0.25↔1.0，讓「今天還沒」更明確（對應情境三：不加補述文字） |
| `QUIT` | `GlassChoiceChip(text = "今天發生了", selected = false)`（沿用 `recordRelapse`） | 無切換狀態；按下後天數句變成「從今天重新開始」即是回饋 |

兩種控制形狀不同是 spec 情境本身的描述（情境一：BUILD「打勾」、QUIT「記錄按鈕」），
與需求 4 無衝突 —— 需求 4 管的是**天數呈現**的分量，而天數句在兩模式共用同一個 composable、
同一組字級與顏色常數，模式只影響句尾兩個字。

**視覺語意的排除**：習慣列不出現紅色系；`SettingItem.kt:48` 的 `SettingDestructiveColor`
不在習慣列使用；無警告三角、無禁止符號、無「一側灰一側亮」的對比。

### 狀態流轉

```
CheckIn 表變動（任何來源）
  └→ checkInDao.observeAll() 發射
      └→ HabitRepositoryImpl.combine 重算 streak（既有邏輯，不動）
          └→ HabitWithStreak
              ├→ 天數句（②）
              └→ 稱讚（③）
```

稱讚與天數句在同一次重組中一起更新，沒有第二條路徑、沒有事件、沒有需要記住的狀態。
**AC「不經 UI 直接寫入一筆 CheckIn 使天數跨過門檻，稱讚仍會出現」由這條結構保證**：
UI 端不存在「剛剛按了按鈕」這個變數。

### 邊界條件

1. **BUILD 今日未打勾時稱讚會跨到隔天**：`buildStreak` 的 anchor 在今天未完成時退到昨天，
   所以達到 7 的隔天早上 streak 仍是 7，稱讚仍在；打勾變 8 後消失。
   這是 Open Q2 決議 A 的直接推論，不是 bug
2. **QUIT 可能在無任何操作的日子達到里程碑**：`quitStreak = today − anchor` 會隨日期自增。
   稱讚由天數推導天然覆蓋；若綁在點擊上，QUIT 這側**永遠不會觸發** ——
   CONVENTIONS #2 在本次交付內就已生效，不只是為了未來的健身模組
3. **兩模式的稱讚存續時間天生不等長**：QUIT 恰好一個日曆天，BUILD 會延到下次打卡。
   源自兩種 streak 定義（不可動），不影響需求 4（管視覺分量，非時長）
4. **同日重複按 QUIT 記錄鍵是幂等的**：`recordCheckIn`（`HabitRepositoryImpl.kt:90-109`）
   復活 / 更新同一列，streak 維持 0，稱讚維持不出現
5. **天數重算時機沿用既有**：`combine` 在 DAO 發射時取當下 `localToday()`；
   `stateIn(WhileSubscribed(5_000))` 使離開頁面 5 秒後重新進入即重新收集。
   App 整夜停在習慣頁不離開時不會自行跳日 —— 既有行為，非目標禁止改動計算與資料流，本次不處理
6. **字串 grep 的範圍是 `app/src`**：目前只有 4 個命中，全在 `HabitScreen.kt`（51、52、57、106），
   改動後歸零。`.claude/doc/` 下的歷史 spec 仍保留舊用語（已加註記），
   **不在清除範圍**，不要順手改掉決策脈絡

---

## Coding Scope

> 由 app-engineer 填寫（2026-08-08 Round 1）。

**新增：**
- `ui/habit/HabitDisplay.kt` — 三個純函式：`modeLabel()`、`habitStreakText()`、`milestoneText()`

**修改：**
- `ui/habit/HabitScreen.kt` — 移除 `HabitType.label()` 與 `HabitWithStreak.subtitle()`；
  移除 `Icons.Default.Block` 與 `Icons.Default.TrendingUp`；新增 `HabitRow` composable
  （火焰圖示、三行文字、outline↔filled 打勾 ／「今天發生了」chip）；
  編輯 dialog 的模式 chip 改用 `modeLabel()`
- `ui/habit/HabitViewModel.kt` — `recordRelapse` → `recordOccurrence`
- `data/habit/HabitRepository.kt` — 介面簽章 `recordRelapse` → `recordOccurrence`
- `data/habit/HabitRepositoryImpl.kt` — 實作 `recordRelapse` → `recordOccurrence`

**識別字改名**：`recordRelapse` → **`recordOccurrence`**（需求 3b）。
`HabitType.BUILD` / `QUIT` 與 `buildStreak` / `quitStreak` 未動。

**app-engineer 自述（Round 1）**：無缺口、無偏離、無範圍外發現
（**經 pm-reviewer 查核，此自述不成立** —— 見 `Build Log`）。

### 退回修正（Round 1 第二次派工，2026-08-08）

**修改：**
- `ui/habit/HabitDisplay.kt:21` — 里程碑 7 天文案標點改回全形（`，` `！`）
- `data/habit/HabitWithStreak.kt:25-27` — 參數 `relapseDays` → `occurrenceDays`，
  註解 `relapse` → `occurrence`。**函式名與計算三行未動**

**app-engineer 自述**：無缺口、無偏離；已自查 `app/src` 下 `relapse|Relapse` 為 0 筆，
另三句全形句號（U+3002）未被改動；`HabitRepositoryImpl.kt:33` 為位置參數呼叫，不需改動。

---

## Build Log

> 由 orchestrator 維護。符號：⛔ 退回 ／ 🧭 船長裁決 ／ ✅ 通過 ／ 📐 升級通則

### Round 1 — 2026-08-08

- 預檢：狀態已定案、三段待回填區為空（首跑）、`app/` 下無未提交改動
- 1️⃣ `data-architect` 交件：`Framework` / `Functional Design` / 留白清單三項提案已回填。
  結論是**零資料模型改動、零新增欄位、零 DB version 變更**，全部落在顯示層
- 🧭 回報兩個缺口，轉 Gate 1 由船長裁決：
  1. 需求 3 的「App 任何地方」是否含**原始碼識別字**（`recordRelapse` 的 relapse = 破戒）
  2. 里程碑當天沒開 App，隔天是否要補顯示稱讚
- 🧭 **Gate 1 裁決（2026-08-08）**
  - 缺口 1 → **1B，識別字要改**。理由：怕之後語意誤解。
    但 orchestrator 查出 `HabitType` 的 enum 值被序列化進 Room 與 Firestore
    （`HabitRemoteDataSource.kt:40`、`:59`），改名會讓既有雲端資料 `valueOf` 失敗 →
    界線定為「純方法名改、被序列化的 enum 不改」，寫入需求 3b 與約束區，並加兩條 AC
  - 缺口 2 → **A，不補顯示**。船長原本以為天數會漏，經釐清後確認
    「數字不會漏，只有稱讚有一天的窗口」，接受此行為。
    曾評估 B1（加欄位）與 B2（改徽章）皆未採用，理由記於 `Notes`
  - `buildStreak` / `quitStreak` 不改名（orchestrator 判斷：enum 留著，
    函式跟著 enum 走才一致），已告知船長
- ✅ Gate 1 通過，設計未退回。3️⃣ 派工 `app-engineer`
- 3️⃣ `app-engineer` 交件：`Coding Scope` 已回填（1 新增 + 4 修改）。
  自述無缺口、無偏離、無順手發現
  - orchestrator 註記：它多移除了 `Icons.Default.TrendingUp`，
    該識別字未出現在 `Framework` 的「要動的」清單中 → 已列為 pm-reviewer 的查核點
- 4️⃣ 編譯由 orchestrator 自行執行：`BUILD SUCCESSFUL`，
  38 tasks 全部 up-to-date（＝交件後檔案未再變動，此結果對應交付的程式碼）
- 5️⃣ `pm-reviewer` 交件：**⛔ 退回**。19 條 AC 中 17 ✅ / 2 ❌，
  1 條（可理解性）標明只能人工驗收、不代簽
  - ✅ 已查證通過的重點：`milestoneText()` 的 `streak == 0` 陷阱已避開；
    判定順序（100 在每 100 天之前）正確；稱讚輸入只有 `Int` 且呼叫點不依賴任何
    「剛剛按了按鈕」的 state（畫面唯一 `remember` 是 `editorTarget`）→ CONVENTIONS #2 成立；
    `recordOccurrence` 四處齊備、`recordRelapse` 全專案 0 筆；
    `HabitType` / `buildStreak` / `quitStreak` / `CheckIn` 欄位 / DB version 4 /
    `notification/` / 共用元件皆未動
  - `Icons.Default.TrendingUp` 的移除**判定不構成違反** ——
    留白 #1 已定「兩模式共用同一個圖示」，拿掉 BUILD 側原圖示是該提案的必然結果；
    `Framework` 只點名 `Block` 屬列舉不完整，非禁止
  - ⛔ **缺陷 1（實作缺陷）**：`HabitDisplay.kt:21` 里程碑 7 天文案標點被半形化
    （`,` `!` 應為全形 `，` `！`）。orchestrator 以 hexdump 複驗：`2c` / `21`，確認成立。
    Functional Design 已明文「不得順手統一標點」，故責任在實作
  - ⛔ **缺陷 2（歸屬待裁決）**：`HabitWithStreak.kt:25-27` 殘留 `relapseDays`
    參數與 `relapse` 註解，違反 AC「原始碼中不存在含 `relapse` 語意的識別字」；
    但 `Framework` 又明文寫該檔 26–29 行「完全不動」→ 設計自我矛盾。
    pm-reviewer 傾向設計缺陷，並指出若船長本意是「整個 `quitStreak` 簽章都豁免」，
    則屬 spec 表述缺陷。**此項只有船長能定 → 🧭**
  - app-engineer 自述「無偏離、無缺口」兩項皆不成立（標點偏離未報、
    `Framework` 與 AC 3b 的衝突未報）
- 🧭 **裁決（2026-08-08）：缺陷 2 選 A** —— `relapseDays` 與註解要改，
  歸屬確認為設計缺陷。界線同 Gate 1 的 1B：語意要改、序列化與函式名不改
  - ⚠️ **orchestrator 跳步揭露**：按流程設計缺陷應退回第 1 步重派 `data-architect`，
    本次未重派。理由：船長已裁定做法、新名亦由 pm-reviewer 給出，
    `data-architect` 無剩餘設計自由度。`Framework` 的更正由 orchestrator 直接寫入，
    已向船長明示此為跳步
- ⛔ 兩個缺陷併為同一次派工，回第 3 步
- 3️⃣ `app-engineer` 交件（第二次）：2 檔修改，見 `Coding Scope` 的「退回修正」
- 4️⃣ orchestrator 自行複驗（不採信自述）：
  - `BUILD SUCCESSFUL`
  - `HabitDisplay.kt:21` hexdump = `ef bc 8c` / `ef bc 81` → 全形，正確
  - `grep -rn "relapse\|Relapse" app/src` → **0 筆**
  - `quitStreak` 的 `maxOrNull() ?: createdDay` / `today - anchor` /
    `coerceAtLeast(0)` 三行逐字未動，函式名保留
  - `git diff --stat`：`app/src` 下僅 5 檔（+ 未追蹤的 `HabitDisplay.kt`），無外溢
- ⚠️ **範圍外異動，非 loop 造成**：`app/google-services.json` 多出一組 OAuth client
  （`client_type: 1`，`certificate_hash: b27b1dd4…`）。此為 Firebase console 產物，
  兩個 read-only agent 無此能力、app-engineer 亦無理由觸碰。
  但這與本輪預檢記的「`app/` 下無未提交改動」矛盾 ——
  可能是預檢看漏或船長於他處重抓。已告知船長，**不列入本次交付**
- 5️⃣ `pm-reviewer` 複審：**✅ 通過**。15 條可由 code 判定的 AC 全 ✅、無非目標違反、
  無範圍外溢；第 16 條（可理解性）依角色定義不代簽，留給 Gate 2
  - 兩個缺陷皆確認修正：標點以**字面全形 pattern** grep 命中四句、
    反向 grep 半形殘留 0 筆；`relapse` 對整個 `app/`（不限 `.kt`）grep 0 筆
  - 回歸重點確認：`quitStreak` 計算三行逐字相符、型別簽章未變、
    `buildStreak` 未受波及、caller `HabitRepositoryImpl.kt:33` 為位置參數呼叫，
    全專案無其他 caller（含測試）
  - 它在無 Bash 的限制下自行改用 `Glob` mtime 排序反推異動檔案，
    結論與 orchestrator 的 `git diff --stat` 獨立吻合（同 6 檔）
- ➡️ 進 🧭 Gate 2，等船長驗收
- ⛔ **Gate 2 退回（2026-08-09）**：船長實測「今天發生了」按鈕，
  **點擊後畫面無任何反饋**
  - orchestrator 診斷：功能有生效，是看不出來。根因兩層 ——
    ① `HabitScreen.kt:126` 的 `selected = false` 是硬寫常數，chip 外觀恆定；
    ② 唯一的反饋來源是天數句，但 `streak` 已為 0 時（新建習慣 `createdDay = today`，
    或今天已記錄過的幂等情形）天數句不會變 → 零反饋。
    `GlassChoiceChip` 本體雖有預設 ripple，但畫在 `Color.White.copy(alpha = 0.5f)`
    加玻璃背景上實際不可見
  - 對照：另一個 caller `HabitScreen.kt:242`（dialog 模式選擇）點擊後 `selected`
    轉 true 而變色，所以**只有這一顆按鈕**沒有反饋
  - 歸屬：**設計缺陷**。`Functional Design` 寫「按下後天數句變成『從今天重新開始』
    即是回饋」，但同一份文件的邊界條件 4 已寫明「同日重複按是幂等的，streak 維持 0」——
    設計自己寫下了推翻該反饋論證的前提而未察。
    附帶 **spec 缺陷**：原 AC 無任何一條要求「記錄動作要有可感知反饋」，
    故 pm-reviewer 判通過並無失職，此洞在其判準射程之外
  - 🧭 **船長裁決（2026-08-09）**：本次只補**按壓當下**的即時反饋（新增需求 8 與 3 條 AC）。
    orchestrator 曾提出的「持久狀態」與「撤銷」兩案，船長認定屬**新增 spec**，移出本次
  - orchestrator 揭露：本次仍未重派 `data-architect`（同前次跳步理由，
    範圍已由船長界定至單一元件的按壓態）
- ⛔ 回第 3 步，派工 `app-engineer`（僅需求 8）
- 3️⃣ `app-engineer` 交件：`GlassChoiceChip` 加 `MutableInteractionSource` +
  `collectIsPressedAsState` + `animateFloatAsState`（alpha 1f → 0.6f），
  自述「整顆按鈕含背景與文字一起變淡」
- ⛔ **orchestrator 攔下，自述與程式碼不符**：`.graphicsLayer(alpha = pressAlpha)`
  被排在 `.background()` 與 `.border()` **之後**。Compose 的 modifier chain 外層先繪製
  再呼叫 `drawContent()`，故背景與邊框在 layer 之外畫完、不受 alpha 影響，
  實際只有 `Text` 變淡。它照抄 `HabitRow` 的順序，但 `HabitRow` 無 `background`
  修飾子，整列內容都落在 layer 內 —— 條件不同。
  且它同時加了 `indication = null`，關掉原本的預設 ripple，淨效果可能更弱。
  歸屬 **實作缺陷**，退回：`graphicsLayer` 移到 `.clip()` 之前
- 3️⃣ `app-engineer` 交件（修正）：`.graphicsLayer(alpha = pressAlpha)` 已移至
  chain 首位（緊接傳入的 `modifier`），背景 / 邊框 / 文字三者皆落在 layer 內
- 4️⃣ orchestrator 複驗：`BUILD SUCCESSFUL`；modifier 順序確認為
  `graphicsLayer → clip → background → border → clickable → padding`；
  `app/src` 異動 6 檔（新增 `GlassDialog.kt`，其餘為前幾輪既有）；
  `HabitRepositoryImpl.kt:34` 的 `completedToday = false` 未動（非目標守住）；
  `ui/habit/` 下無新增 Snackbar / Toast / undo 呼叫
- ➡️ 重回 🧭 Gate 2
- ✅ **Gate 2 通過（2026-08-09）**：船長實測後確認「還不錯」。
  人工驗收的兩條 AC（需求 7 可理解性、需求 8 的視覺強度）由船長本人判定通過
- 5️⃣ `pm-reviewer` 增量複審：**✅ 通過**。19 條可由 code 判定的 AC 全 ✅、
  無非目標違反、無範圍外溢（本輪唯一異動檔為 `GlassDialog.kt`，4 行新增 + 1 行 modifier）
  - 它**自行核對 modifier 順序**（orchestrator 指名的查核點）：確認
    `graphicsLayer` 位於 chain 首位，`background`（:187）／`border`（:188）／
    `Text`（:193）三者的繪製皆在 layer 內部 → 上一版「只有文字變淡」的缺陷已消除
  - AC 8-2 是這組重點，判定 ✅：`pressAlpha` 的唯一輸入是 `isPressed`，
    `GlassChoiceChip` 的四個參數中**沒有任何管道**能讀到 `streak` /
    `completedToday` / repository → 反饋與資料層結構性隔離
  - 另一個 caller `HabitScreen.kt:242` 未被破壞：`pressAlpha` 是暫態乘法因子，
    press 釋放回 1f，`selected` 的持久變色照舊
  - 不代簽兩項（需求 7 可理解性、需求 8 的視覺強度），均為人工驗收而非缺陷。
    它補了一項事實供 Gate 2 參考：alpha 0.6f 的按壓手法已是專案既有慣例
    （`SettingItem.kt:110,122`、`FeatureCard.kt:47,63`、`AppBar.kt:56,86`）
- ✅ **Round 1 結案**。共 3 次派工 `app-engineer`、2 次 `pm-reviewer` 複審、
  2 次 Gate 2 驗收，未觸及 3 輪上限
  - 交付：`ui/habit/HabitDisplay.kt`（新增）、`HabitScreen.kt`、`HabitViewModel.kt`、
    `HabitRepository.kt`、`HabitRepositoryImpl.kt`、`HabitWithStreak.kt`、
    `ui/component/GlassDialog.kt`
  - 零資料模型改動：無新增欄位、DB version 維持 4、序列化格式未變、無遷移需求

---

## Notes

> 實作注意事項、已知限制。

**已知限制 — 里程碑稱讚有一天的窗口**（🧭 Gate 1 裁決 2A，2026-08-08）

天數本身是從日期換算的（`quitStreak = 今天 − 上次記錄那天`），**每天自己長，不會漏**。
但稱讚的出現條件是「天數**正好等於**門檻」，所以里程碑當天沒開 App，
隔天數字已經跨過去，那句話就不會出現。

**不補顯示。** 補顯示必須持久化「已顯示到哪個里程碑」＝加欄位，
直接撞非目標「不動資料結構」。

船長已理解並選擇維持此行為。曾評估的替代方案：

| | 方案 | 為何未採用 |
|---|---|---|
| B1 | 加欄位記錄已顯示的里程碑，事後補一次 | 要打開「不動資料結構」這條非目標，且欄位須進 Firestore 同步 |
| B2 | 改成徽章：跨過門檻後常駐顯示，直到跨過下一個門檻 | 零成本且不會錯過，但稱讚會幾乎天天在畫面上，且「連續 7 天了」掛在「連續 23 天沒做」旁讀不通，文案得跟著改 |
