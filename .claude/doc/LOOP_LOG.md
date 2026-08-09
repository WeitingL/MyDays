# LOOP LOG

跨 spec 的 loop 病歷。記錄**各角色的行為模式**——哪個角色在哪種情況下會出錯，
以及 orchestrator 該怎麼盯。

**這裡只留模式，不留逐次細節。** 單次派工的原始回報與複驗結果留在各 spec 的 `Build Log`。
一件事發生一次是事件，發生兩次才寫進這裡。

**為什麼有這份文件**：每份 spec 的 `Build Log` 只有那份 spec 看得到。
沒有這裡，下一輪 orchestrator 得重新發現一次同樣的坑；而且 agent 的完整回報只有
orchestrator 看得到，船長拿到的是壓縮過的摘要——這份文件是他看見原始行為模式的管道。

**維護方式**：每輪 loop 收尾時由 orchestrator 更新；開跑前必讀一次。

---

## `app-engineer`（sonnet）

**模式：對「我的改動實際造成什麼視覺／字面效果」的自述不可靠。**

兩次都是編譯乾淨、看起來合理、但與程式碼不符：

| 日期 | spec | 它說 | 事實 |
|---|---|---|---|
| 2026-08-09 | `habit-counting-mode` | 「無偏離、無缺口」 | 里程碑文案用了半角標點（設計明文要求不得改標點），未回報 |
| 2026-08-09 | `habit-counting-mode` | 「整顆按鈕（含背景與文字）的 alpha 從 1.0 降至 0.6」 | `.graphicsLayer()` 排在 `.background()` 之後，只有 `Text` 會淡；另外自行加了 `indication = null` 關掉 ripple，未回報 |

三次派工出現兩次不實自述。

**orchestrator 對策**：第 4 步逐項複驗，不接受任何自述當證據。

- 文案 → 逐字比對，含標點全半角（`hexdump` 確認）
- 範圍 → `git diff --stat` 比對 `Coding Scope`
- 視覺效果 → 讀 modifier chain 的實際順序
- 「無偏離、無缺口」 → **這句話本身最需要驗**

已升級為 `build.md` 第 4 步的複驗表。

---

## `data-architect`（opus）

**模式：宣告「這個檔案不改」時，不會回頭對照 AC 的禁止清單。**

2026-08-09 `habit-counting-mode`：`Framework` 寫 `HabitWithStreak.kt:26-29` 完全不改，
但 AC 禁止 `relapse` 識別字，而 `relapseDays` 就在那幾行。兩者互斥，
實作者照設計做必然違反 AC、照 AC 做必然違反設計——**他無論怎麼選都是錯的**。

**orchestrator 對策**：把設計寫進 spec 之前，對每個「不改」宣告 grep 一次 AC 的禁止字串。

已升級為 `CONVENTIONS.md` #4。

---

## `pm-reviewer`（opus）

**不是缺陷，是工具限制。** 它沒有 Bash，拿不到 `git diff`。

2026-08-09：它拿到的 git status 快照已經過期，只能用檔案 mtime 反推哪些檔被改過，
並在回報中明講這件事。它的自我揭露是對的行為，缺的是輸入。

**orchestrator 對策**：第 5 步派工時，把 `git diff` 的實際內容一併給它。

已寫入 `build.md` 第 5 步。

---

## `orchestrator`（我）

**模式：船長裁決之後，會傾向省略重派流程、自己改文件。**

2026-08-09 `habit-counting-mode` 兩次：設計缺陷本該退回 `data-architect`，
但船長的裁決已經把設計自由度收乾（只剩一種改法），我就自己改了 `Framework`。
兩次都有向船長揭露，他沒有反對。

**這仍然是偏離。** 記在這裡是因為「結果對」不等於「流程對」——
不記錄的偏離會變成默默改掉的規則。

另一件：Round 1 的 pre-flight 記了「`app/` 下無未提交改動」，
但後來發現 `google-services.json` 有變更。**pre-flight 要照實記，不要寫成乾淨。**

**對策**：偏離要寫進 `Build Log`，不只是口頭揭露。已寫入 `build.md` 的 Build Log 格式段。
