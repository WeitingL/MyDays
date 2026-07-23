# Habit 提醒通知

> 所屬 Feature：`doc/feature/2026-07-22_habit.md`
> 狀態：草稿
> Jira：___

---

## Overview

> 由我填寫。

定義每個習慣的提醒時間設定與推播排程機制（Phase 3）。

範圍包含：每個習慣可設定每日提醒時間、通知權限處理。
不包含：打卡與 streak 邏輯、壞習慣提醒策略（見 Open Questions，可能預設關閉）。

---

## Requirements

> 由我填寫。列出具體的功能需求，以使用者可觀察的行為描述。
> （以下為 Agent 依 Overview + feature 文件草擬，待使用者增刪確認）

1. 每個習慣可設定一個「每日提醒時間」（時:分），也可關閉提醒。
2. 到達設定時間時發出系統通知，內容提示該習慣名稱；點擊通知可開啟 App。
3. Android 13+ 首次需要時請求 `POST_NOTIFICATIONS` 權限；未授權則不發通知（不 crash）。
4. 壞習慣（QUIT）預設關閉提醒（避免誘導破戒），使用者可手動開啟。
5. 裝置重開機後，既有提醒仍能於每日設定時間繼續觸發。
6. 提醒設定隨習慣資料保存，並透過既有同步機制帶到雲端 / 其他裝置。

---

## Framework

> 由 Agent 填寫。對照現有 codebase / 架構。

- **現有基礎**：
  - `HabitEntity`（Room + Firestore 已同步；push/pull 帶整筆欄位）——提醒設定若加為欄位即可搭上既有同步，無需另建表 / 另寫同步。
  - `HabitScreen` 的 `HabitEditorDialog`（現有 name + type 輸入）為提醒設定 UI 落點。
  - `HabitViewModel` / `HabitRepository(Impl)` 為寫入路徑；`add` / `update` / `delete` 需一併觸發排程。
  - Koin DI（`di/AppModule.kt`）可注入排程器 / 通知工具。
  - **`AndroidManifest.xml` 目前無任何權限與 receiver 宣告**（首次導入通知需補齊）。
- **落點 package**：
  - 提醒設定資料：加在 `data/habit/HabitEntity.kt`（單一每日提醒 → 一個欄位）。
  - 排程與通知基礎建設：新增 `notification/` package（scheduler、receiver、通知工具）。
- **排程模型**：AlarmManager（本地、按時觸發）→ 觸發時由 `BroadcastReceiver` 發通知並重排隔日；開機由 `BootReceiver` 重建所有 alarm。
- **與同步的協同（重要邊界）**：提醒設定值走雲端同步，但 **alarm 是裝置本地狀態**——雲端只存「幾點提醒」，各裝置各自排程。故 pull 到新設定 / App 啟動時，需對有提醒的習慣重新排程（類似開機重排）。
- **環境**：minSdk 35（Android 15）；`POST_NOTIFICATIONS`（13+）、精確鬧鐘權限（視 Q2）、`RECEIVE_BOOT_COMPLETED` 需宣告。

---

## Open Questions

> 由 Agent 填寫。每項附方案與建議供選擇。

1. **壞習慣是否預設關閉提醒**
   - 方案 A：壞習慣預設關閉提醒（Feature 討論指出提醒可能誘導破戒）
   - 方案 B：一律開放，由使用者自行決定
   - 建議：**方案 A**。預設關閉、允許手動開啟，兼顧安全與彈性。
   - 決議：A

2. **排程機制選型（含精確度 / 權限取捨）**
   - 方案 A：**AlarmManager `setExactAndAllowWhileIdle`（精確）** — 準點，但 Android 12+ 需 `SCHEDULE_EXACT_ALARM`（使用者可撤銷、Play 政策對用途有限制）。
   - 方案 B：**AlarmManager `setAndAllowWhileIdle`（不精確，允許 Doze 中觸發）** — 免特殊權限，系統可能延後數分鐘～視窗批次觸發。
   - 方案 C：WorkManager 週期任務 — 最低 15 分鐘粒度且不保證準點，較不適合「指定時刻」提醒。
   - 建議：**方案 B**。習慣提醒容忍數分鐘誤差，換取零權限摩擦、實作單純；日後要「分秒不差」再升級 A。（原骨架建議 A，此處補上權限成本後改推 B，待你定奪）
   - 決議：B

3. **提醒資料模型：欄位 on HabitEntity vs 獨立表**
   - 方案 A：`HabitEntity` 加 `reminderMinuteOfDay: Int?`（null=關閉，0..1439=每日該分鐘） — 單一每日提醒最省，直接搭既有同步。
   - 方案 B：獨立 `reminders` 表（一對多） — 為「一天多次提醒 / 依星期不同」預留，但目前需求為單一每日。
   - 建議：**方案 A**。符合目前「單一每日提醒」需求；多次提醒屬未來，屆時再遷移。
   - 決議：A

4. **通知權限（POST_NOTIFICATIONS）請求時機**
   - 方案 A：使用者「開啟某習慣的第一個提醒」時才請求 — 有情境、轉化率高。
   - 方案 B：進入習慣頁 / App 啟動即請求 — 早但可能無脈絡被拒。
   - 建議：**方案 A**。在使用者表達提醒意圖的當下請求最合理。
   - 決議：A

5. **重開機後重排（Requirement 5 的實作）**
   - 方案 A：宣告 `RECEIVE_BOOT_COMPLETED` + `BootReceiver`，開機讀所有有提醒的習慣重排 alarm。
   - 方案 B：不處理，開機後提醒失效直到使用者再次進 App。
   - 建議：**方案 A**。Requirement 5 明確要求，AlarmManager 重開機會清空，必須重排。
   - 決議：A

6. **刪除習慣需一併移除提醒**（使用者補充）
   - 決議：採用。分兩層處理——(1) 本地刪除當下 `scheduler.cancel(habitId)` 撤未來 alarm（Q7=B：已顯示通知不特別撤下）；(2) `ReminderReceiver` 觸發時回查 Room，習慣已刪 / 停用 → 不發、不重排（自我清理，涵蓋跨裝置刪除的殘留 alarm）。

7. **刪除時是否撤下「已顯示」的通知**
   - 方案 A：一併 `NotificationManager.cancel(notificationId)` 撤下欄上通知。
   - 方案 B：只取消未來 alarm，不管已顯示的（極少見時間點，MVP 不處理）。
   - 建議：**方案 A**。多一行、體驗完整。
   - 決議：B

---

## Functional Design

> 由 Agent 填寫。前提（未決議者以建議方案設計並註明）：Q1=A 壞習慣預設關閉、Q2=B 不精確鬧鐘、Q3=A 欄位 on HabitEntity、Q4=A 開啟時請求、Q5=A 開機重排。

### 資料模型

- **HabitEntity** 加 `reminderMinuteOfDay: Int?`（null=關閉；0..1439=每日該分鐘，本地時區）。
  - `Habit` domain + mapper 同步加欄位。
  - 透過既有 push/pull 自動同步（雲端只存設定值）。
  - DB version 3 → 4（沿用 `fallbackToDestructiveMigration`，開發期）。

### 排程（Q2=B）

- **HabitReminderScheduler**（包 AlarmManager）：
  - `schedule(habitId, name, minuteOfDay)`：算下一個觸發時刻（今天該分鐘已過 → 排明天），`setAndAllowWhileIdle(RTC_WAKEUP, triggerAt, pendingIntent)`；`PendingIntent` 帶 `habitId` / `name`，`requestCode = habitId.hashCode()`。
  - `cancel(habitId)`：撤銷對應 alarm。
  - alarm 為 one-shot，觸發後由 receiver 重排隔日（等效每日重複，且能吸收 Doze）。
- **ReminderReceiver**（`BroadcastReceiver`）：收到 alarm → **回查 Room 該習慣目前狀態**：
  - 存在且 `deletedAt IS NULL` 且 `reminderMinuteOfDay != null` → 發通知 → 重排隔天同一時刻。
  - 否則（已刪 / 停用 / 不存在）→ **不發、不重排**（alarm 自我清理，涵蓋跨裝置刪除 / 停用同步後在本機殘留的過期 alarm）。
- **BootReceiver**（`RECEIVE_BOOT_COMPLETED`）：開機 → 讀 **active 且有提醒**的習慣（`deletedAt IS NULL AND reminderMinuteOfDay IS NOT NULL`）→ 逐一重排。

### 通知

- **NotificationHelper**：首次建立通知 channel；發送通知（標題=習慣名稱、內容=提醒文案）；tap → `MainActivity`（`PendingIntent`）。

### 權限（Q4=A）

- Android 13+：使用者在編輯 dialog **開啟提醒開關**時，以 `rememberLauncherForActivityResult` 請求 `POST_NOTIFICATIONS`；拒絕則不排程 / 不發（不 crash，符合全域偏好不加多餘 fallback，僅做必要的權限 gate）。

### UI（HabitEditorDialog）

- 現有 name + type 之下，新增「每日提醒」區塊：
  - 開關（Switch）：關 → `reminderMinuteOfDay = null`；開 → 顯示時間選擇。
  - 時間選擇：Material3 `TimePicker`（或時/分輸入），存為 `minuteOfDay`。
  - **QUIT 類型預設開關為關（Q1=A）**，使用者可手動開。
- `onConfirm` 多回傳 `reminderMinuteOfDay: Int?`。

### 寫入路徑

- `HabitViewModel.addHabit` / `updateHabit`：多帶 `reminderMinuteOfDay`。
- `HabitRepositoryImpl`：
  - add / update：寫 Room（含 `updatedAt`/`pendingSync`，走既有同步）後，依 `reminderMinuteOfDay` 呼叫 `scheduler.schedule(...)` 或 `cancel(...)`。
  - delete（soft delete）：`scheduler.cancel(habitId)` 撤未來 alarm（Q7=B：已顯示通知不特別處理）。

### 同步協同重排（Framework 邊界）

- alarm 為裝置本地：**App 啟動 / 登入 pull 之後**，對 **active 且有提醒**的習慣重排一次（`deletedAt IS NULL AND reminderMinuteOfDay IS NOT NULL`，等同 boot 重排邏輯），確保跨裝置同步來的設定在本機生效。
- 跨裝置刪除的殘留 alarm 由 `ReminderReceiver` 觸發時回查 Room 自我清理（見「排程」），不需在 SyncManager 內處理取消，避免提醒關注點污染同步層。

### 邊界條件 / 取捨

- Q2=B 提醒容忍數分鐘誤差；要精確再升級 `setExactAndAllowWhileIdle` + `SCHEDULE_EXACT_ALARM`（列未來）。
- 不做貪睡（snooze）、不做多次提醒 / 依星期（屬未來 / 一週計劃 feature）。
- 依全域偏好，不主動加入未要求的 error handling / validation，僅保留權限 gate 這類必要判斷。

---

## Coding Scope

> 由 Agent 填寫。前提同 Functional Design。對照現有檔案結構。

**新增：**
- `notification/HabitReminderScheduler.kt` — AlarmManager 包裝：`schedule` / `cancel` / 算下一觸發時刻。
- `notification/ReminderReceiver.kt` — 收 alarm → 回查 Room 狀態 → 發通知並重排隔日；已刪 / 停用則不發不重排（Koin 注入 HabitDao / Scheduler / NotificationHelper）。
- `notification/BootReceiver.kt` — 開機重排 active 且有提醒的習慣。
- `notification/NotificationHelper.kt` — channel 建立 + 發送通知（tap 進 App）。

**修改：**
- `data/habit/HabitEntity.kt` — 加 `reminderMinuteOfDay: Int?`。
- `data/habit/Habit.kt` — domain + `toDomain()` / `toEntity()` 對應新欄位。
- `data/habit/MyDaysDatabase.kt` — version 3 → 4。
- `data/habit/HabitRemoteDataSource.kt` — habit map 讀寫加 `reminderMinuteOfDay`（隨同步帶走）。
- `data/habit/HabitDao.kt` — 加「active 且有提醒」查詢（`WHERE deletedAt IS NULL AND reminderMinuteOfDay IS NOT NULL`）供開機 / 啟動重排；ReminderReceiver 回查用 `getById`（已存在）。
- `data/habit/HabitRepository.kt` / `HabitRepositoryImpl.kt` — `add` / `update` 帶 `reminderMinuteOfDay`；寫入後 `schedule` / `cancel`；delete → `cancel`。
- `ui/habit/HabitViewModel.kt` — `addHabit` / `updateHabit` 帶提醒參數。
- `ui/habit/HabitScreen.kt` — `HabitEditorDialog` 加提醒開關 + 時間選擇；`POST_NOTIFICATIONS` 權限請求。
- `di/AppModule.kt` — 提供 `HabitReminderScheduler`、`NotificationHelper`；`HabitRepositoryImpl` 建構子多收 scheduler。
- `MainActivity.kt` / App 啟動 — pull 後對有提醒的習慣重排。
- `AndroidManifest.xml` — 宣告 `POST_NOTIFICATIONS`、`RECEIVE_BOOT_COMPLETED`（Q2=B 免精確鬧鐘權限）；註冊 `ReminderReceiver`、`BootReceiver`。

---

## Acceptance Criteria

> 定義「完成」的標準。

- [ ] 習慣編輯可開 / 關每日提醒並設定時間；QUIT 預設關閉
- [ ] 到設定時間發出通知，點擊可進 App
- [ ] Android 13+ 開啟提醒時請求通知權限；拒絕不 crash、不發通知
- [ ] 提醒設定隨習慣同步到雲端，另一裝置 pull 後在該機重排生效
- [ ] 裝置重開機後提醒仍會於設定時間觸發（且已刪習慣不會被重排）
- [ ] 刪除習慣後其提醒不再觸發
- [ ] 一裝置刪除 / 停用習慣，另一裝置同步後不再收到該習慣提醒（殘留 alarm 觸發時自我清理）
- [ ] 關閉提醒後 alarm 被撤銷、不再通知

---

## Notes

> 實作注意事項、已知限制。

- Android 13+ 需請求 `POST_NOTIFICATIONS` 權限。
- 提醒時間以裝置本地時區的 `minuteOfDay` 表示；跨時區行為未特別處理（列未來）。
