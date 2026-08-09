# Habit 打卡與 Streak 計算

> 所屬 Feature：`doc/feature/2026-07-22_habit.md`
> 狀態：已實作，待驗證
> Jira：___
>
> ⚠️ **2026-08-08 用語修訂**：本文的「好習慣 / 壞習慣」已改稱「每天都做 / 克制不做」，
> 「破戒」改稱「發生了一次」。`buildStreak` / `quitStreak` **計算邏輯完全不變**，
> 只有顯示文案改變。見 `doc/feature/2026-07-22_habit.md` Phase 4。

---

## Overview

> 由我填寫。

定義每日打卡互動，以及依習慣類型（好 / 壞）計算 streak 的規則（Phase 1）。

範圍包含：打卡互動（單純打勾）、好習慣 streak（連續完成天數）、壞習慣 streak（距上次「破戒」的天數，無紀錄 = 成功，自建立日起算）。
不包含：完成率 / 日曆熱力圖等其他統計、雲端同步。

---

## Requirements

> 由我填寫。列出具體的功能需求，以使用者可觀察的行為描述。
> （以下為 Agent 依 Overview + feature 文件草擬，待使用者增刪確認）

1. 使用者可對「好習慣」標記今日完成，亦可取消今日完成。
2. 使用者可對「壞習慣」記錄一次「破戒」（發生於今日）。
3. 好習慣顯示目前連續完成天數；漏一整天未完成則歸零。
4. 壞習慣顯示距上次破戒的天數；無破戒紀錄時自建立日起算；記錄破戒即歸零。
5. 打卡 / 破戒紀錄持久化，重啟 App 後 streak 仍正確。
6. 僅能對「今日」打卡 / 記錄破戒，不支援補登（MVP）。
7. （待決，Open Q4）習慣顯示歷史最佳連續天數。

---

## Framework

> 由 Agent 填寫。對照現有 codebase / 架構。

- **沿用 habit-crud 基礎建設**：`MyDaysDatabase`（Room）、Koin DI、`HabitRepository` 介面 + Impl、`HabitViewModel`、`HabitScreen` 皆已存在，本 spec 在其上擴充。
- **新增資料表**：`CheckInEntity`（打卡 / 破戒事件），DB version 1 → 2（見 Open Q5 遷移策略）。
- **落點 package**：`data/habit/`（entity / dao / mapping）、`ui/habit/`（ViewModel 擴充、Screen 打卡互動）。
- **DI**：`AppModule` 新增 `CheckInDao` 提供；`HabitRepositoryImpl` 建構子多收 `CheckInDao`（Koin `get()` 自動注入）。
- **streak 計算位置**：於 domain / repository 層計算（見 Open Q3），不落 denormalized 欄位。
- **日期處理**：`java.time.LocalDate`（minSdk 35，API 完整可用）；儲存以 `epochDay`（Long）為 key，"今日" = `LocalDate.now()`（裝置本地時區）。
- **UI 現況**：`HabitScreen` 清單列目前 trailing 為刪除 icon；本 spec 需在列上呈現 streak 與打卡控制，trailing 配置需調整（見 Open Q6）。

---

## Open Questions

> 由 Agent 填寫。每項附方案與建議供選擇。

1. **是否允許補打卡（回補前幾天）**
   - 方案 A：不允許，只能打卡當日
   - 方案 B：允許回補，streak 需重算
   - 建議：**方案 A** 作為 MVP（對應 feature Ambiguity 2），降低 streak 計算複雜度；補打卡列未來。
   - 決議：A

2. **打卡 / 破戒事件的資料模型**
   - 方案 A：單一 `CheckInEntity` 表（`habitId` + `epochDay`，一天一列），語意依習慣類型解讀：BUILD 有列 = 當日完成、QUIT 有列 = 當日破戒
   - 方案 B：兩張分開的表（completions / relapses）
   - 建議：**方案 A**。兩者本質都是「某習慣在某天發生一次事件」，單表 + `habitId+epochDay` 唯一索引最簡潔，且與後續 `habit-sync` 對齊。
   - 決議：A

3. **streak 計算：即時計算 vs 儲存欄位**
   - 方案 A：由打卡紀錄即時計算（讀取時算），Habit 不存 streak 欄位
   - 方案 B：在 Habit 上存 `currentStreak` / `bestStreak`，每次打卡更新
   - 建議：**方案 A**。資料量小、永遠正確、無 drift；效能足夠。
   - 決議：A

4. **是否納入歷史最佳連續天數（bestStreak）**
   - 方案 A：納入（BUILD = 最長連續完成天數；QUIT = 破戒之間最長間隔），即時計算成本低
   - 方案 B：MVP 不做，僅顯示目前 streak
   - 建議：**方案 A**（對應 feature Ambiguity 1 傾向保留最佳紀錄，正向激勵、成本低）。
   - 決議：**B（MVP 不做，僅顯示目前 streak）**

5. **DB 遷移策略（version 1 → 2，新增 CheckIn 表）**
   - 方案 A：手寫 `Migration(1, 2)` 執行 `CREATE TABLE`
   - 方案 B：`fallbackToDestructiveMigration()`（升級時清庫重建）
   - 建議：**方案 B**。專案早期、無正式使用者、資料可拋；正式上線前再改回手寫 migration。
   - 決議：B

6. **打卡控制與刪除在列上的擺放**
   - 方案 A：列 trailing 改放「打卡控制」（BUILD = 今日完成 toggle、QUIT =「記錄破戒」鈕），刪除移入編輯 dialog（於 `GlassDialog` 加刪除鈕）
   - 方案 B：列上同時塞 streak + 打卡 + 刪除三者
   - 建議：**方案 A**。trailing 空間有限，打卡是主要動作；刪除為低頻操作移入編輯較清爽。
   - 決議：**B（streak + 打卡 + 刪除三者同列，刪除維持列上 trailing）**

---

## Functional Design

> 由 Agent 填寫。前提（已決議）：Q1=A（不補登）、Q2=A（單一 CheckIn 表）、Q3=A（即時計算）、Q4=B（不做 bestStreak）、Q5=B（清庫重建）、Q6=B（三者同列）。

### 資料模型（CheckInEntity）

table `check_ins`（唯一索引 `habitId` + `epochDay`）：

| 欄位 | 型別 | 說明 |
|---|---|---|
| `id` | String (UUID) | 主鍵 |
| `habitId` | String | 對應 `habits.id` |
| `epochDay` | Long | 事件日期（`LocalDate.toEpochDay()`）；BUILD = 完成日、QUIT = 破戒日 |
| `createdAt` | Long | 事件寫入時間（供後續 sync） |

### Streak 演算法

以裝置本地 `today = LocalDate.now()`、`createdDay = createdAt → LocalDate`。

- **BUILD（連續完成天數）**：
  - 取該習慣所有 `epochDay` 集合。
  - 從「今日或昨日」作為錨點往回數連續天數：若今日已完成，錨點 = 今日；否則錨點 = 昨日（給當天尚未打卡的寬限）。
  - 自錨點起，逐日往前只要集合含該日就 +1，遇到缺漏即停止。錨點當日也不在集合則 streak = 0。
- **QUIT（距上次破戒天數）**：
  - `anchor = 最近一次破戒日` ?: `createdDay`。
  - `streak = today.toEpochDay - anchor.toEpochDay`（今日破戒 → 0）。

（Q4=B：不計算 / 不顯示 bestStreak。）

### UI 模型與狀態流轉

- domain 新增 `HabitWithStreak(habit, streak, completedToday)`（`completedToday` 僅 BUILD 有意義）。
- `HabitRepository` 以 `combine(habitsFlow, checkInsFlow)` 產出 `Flow<List<HabitWithStreak>>`，即時反映。
- **BUILD 打卡**：toggle 今日 → 無今日列則 `insert`（epochDay = today）、有則 `deleteByHabitAndDay`。
- **QUIT 破戒**：`insert` 今日一列（若今日已存在則不重複）。
- streak 隨紀錄變動由 Flow 重算，UI 自動更新。

### UI 互動（HabitScreen 列，Q6=B 三者同列）

- 列主體：習慣名稱；subtitle 呈現 type + 目前 streak（BUILD「養成好習慣 · 連續 N 天」、QUIT「戒除壞習慣 · 已 N 天未破戒」）。
- trailing（Row）：打卡控制 + 刪除 icon。
  - BUILD → 今日完成 toggle（`CheckCircle` 實心 / 淡色）。
  - QUIT → 「破戒」小鈕。
- 點列主體 → 開編輯 `GlassDialog`（沿用 habit-crud，不變）。

### 邊界條件

- 僅今日可操作（Q1=A）；不做補登 UI。
- 依全域偏好，不主動加入未要求的驗證 / error handling。

---

## Coding Scope

> 由 Agent 填寫。前提同 Functional Design。

**新增：**
- `data/habit/CheckInEntity.kt` — Room `@Entity`（`check_ins`，唯一索引 habitId+epochDay）
- `data/habit/CheckInDao.kt` — `insert` / `deleteByHabitAndDay` / `observeAll`（或依 habit 觀察）
- `data/habit/HabitWithStreak.kt` — UI domain 模型 + streak 計算函式（BUILD / QUIT 兩套）

**修改：**
- `data/habit/MyDaysDatabase.kt` — entities 加 `CheckInEntity`、version 1→2、`abstract fun checkInDao()`
- `data/habit/HabitRepository.kt` — 介面加 `observeHabitsWithStreak()`、`checkInToday(habitId)` / `undoTodayCheckIn(habitId)`（BUILD）、`recordRelapse(habitId)`（QUIT）
- `data/habit/HabitRepositoryImpl.kt` — 建構子加 `CheckInDao`；實作上述方法與 `combine` 計算
- `di/AppModule.kt` — 提供 `CheckInDao`；Room builder 加 `fallbackToDestructiveMigration(true)`；`HabitRepositoryImpl` 綁定多帶一個 `get()`
- `ui/habit/HabitViewModel.kt` — 改觀察 `observeHabitsWithStreak()`；加 `checkInToday` / `undoTodayCheckIn` / `recordRelapse`
- `ui/habit/HabitScreen.kt` — 列 subtitle 顯示 streak，trailing 加打卡控制（保留刪除 icon）

---

## Acceptance Criteria

> 定義「完成」的標準。

- [ ] 好習慣今日打卡後 streak +1，取消今日打卡後對應回退
- [ ] 好習慣漏一整天後 streak 歸零
- [ ] 壞習慣預設自建立日累積乾淨天數；記錄破戒後歸零、隔日起重新累積
- [ ] streak 於重啟 App 後仍正確（由紀錄即時計算）
- [ ] 打卡 / 破戒 / 刪除操作後清單即時更新（Flow）
- [ ] 僅能操作今日，無補登入口

---

## Notes

> 實作注意事項、已知限制。

- 好習慣與壞習慣為兩套 streak 演算法，需分開實作。
- `fallbackToDestructiveMigration()` 為開發期權宜；正式上線前改為手寫 `Migration`。
- streak 即時計算，資料量大時可再評估改存欄位（Q3 方案 B）。
