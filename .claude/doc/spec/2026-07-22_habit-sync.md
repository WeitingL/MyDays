# Habit 雲端同步（Room + Firestore）

> 所屬 Feature：`doc/feature/2026-07-22_habit.md`
> 狀態：草稿
> Jira：___

---

## Overview

> 由我填寫。

定義習慣資料在 Room（本地）與 Firestore（雲端）之間的雙層同步機制，離線優先（Phase 2）。

範圍包含：Firestore 資料結構（對應 Room entity）、離線優先讀寫策略、背景同步與衝突處理。
不包含：習慣 CRUD 與打卡邏輯（見 Phase 1 specs）、使用者以外的資料分享。

---

## Requirements

> 由我填寫。列出具體的功能需求，以使用者可觀察的行為描述。
> （以下為 Agent 依 Overview + feature 文件草擬，待使用者增刪確認）

1. 使用者登入後，習慣與打卡紀錄自動同步到雲端；於另一裝置登入同一帳號可看到相同資料。
2. 離線時仍可正常新增 / 編輯 / 刪除習慣、打卡 / 破戒；恢復連線後自動補同步到雲端。
3. 本地為讀取來源（離線優先），UI 不因等待網路而卡頓或空白。
4. 多裝置或離線重連造成的衝突以一致規則解決（見 Open Q1），不遺失使用者最新的有效操作。
5. 刪除習慣時，雲端對應的 Habit 與其所有 CheckIn 一併移除。
6. （待決，Open Q4）登入前於本地建立的資料，登入後如何與雲端帳號資料整合。

---

## Framework

> 由 Agent 填寫。對照現有 codebase / 架構。

- **現有基礎**：
  - Firestore 依賴已在 `libs.versions.toml` / `app/build.gradle.kts` 宣告，但**程式碼尚未使用任何 Firestore API**（首次導入實作）。
  - Auth 已提供登入的 `FirebaseUser`（`AuthRepository.currentUser`），其 `uid` 作為雲端資料的 owner key。
  - Room 為本地單一真實來源：`habits`（HabitEntity）、`check_ins`（CheckInEntity）兩表；`HabitEntity` 已含 `updatedAt`（衝突解決預留）。
  - Koin DI（`di/AppModule.kt`）已就位，可注入 remote data source / sync 協調者。
- **落點 package**：`data/habit/` 新增遠端層與同步協調（remote data source + sync）；`HabitRepositoryImpl` 現況只包 DAO，需在其上/其內接上雲端 push / pull。
- **同步模型（依 Overview「離線優先、雙層」）**：Room 為 local source of truth，Firestore 為雲端鏡像；讀取一律走 Room Flow，寫入先落 Room 再 push 雲端，pull 時將雲端變更 upsert 回 Room（見 Open Q3 實作路線）。
- **Firestore 結構（見 Open Q2）**：以 `uid` 隔離——`users/{uid}`（文件，含帳號層級 `lastUpdate` 高水位）、`users/{uid}/habits/{habitId}`、`users/{uid}/checkIns/{checkInId}`，欄位對應 Room entity。
- **刪除採 soft delete（Q6=B）**：記錄加 `deletedAt` tombstone，永不實刪；保留歷史 CheckIn 所屬習慣，並讓刪除能無歧義同步。
- **觸發機制**：App 啟動 / 登入時 pull；本地寫入即時 push；背景以 WorkManager 補漏（Open Q5 需新增 WorkManager 依賴）。
- **衝突處理**：Habit 以 `updatedAt`（last-write-wins）比較；CheckIn 因 `habitId + epochDay` 唯一且語意為「存在即事件」，衝突為冪等（存在/不存在），較單純。
- **環境**：minSdk 35 / Kotlin 2.2 / Koin 執行期 DI（不需 KSP）。

---

## Open Questions

> 由 Agent 填寫。每項附方案與建議供選擇。

1. **離線優先的實作路線（最關鍵）**
   - 方案 A：**自建同步層**——Room 為 source of truth，自己寫 push（本地→雲）/ pull（雲→本地 upsert）邏輯，Firestore 只當遠端儲存
   - 方案 B：**倚賴 Firestore SDK 內建離線持久化**（`setPersistenceEnabled`）當作本地層，弱化或移除 Room
   - 方案 C：混合——讀寫走 Room，雲端用 Firestore SDK 的離線佇列自動送出，pull 靠 snapshot listener
   - 建議：**方案 A**。已投資 Room 為 source of truth，streak 計算也吃 Room Flow；自建同步可控、與現有架構一致，Firestore 只作鏡像。B 會推翻既有 Room 設計。
   - 決議：A

2. **Firestore 資料結構**
   - 方案 A：per-user 子集合——`users/{uid}/habits/{id}`、`users/{uid}/checkIns/{id}`
   - 方案 B：頂層集合 + `ownerId` 欄位——`habits/{id}`（含 `ownerId`）、`checkIns/{id}`
   - 建議：**方案 A**。天然以 `uid` 隔離、安全規則最單純（`request.auth.uid == uid`），符合單一使用者自有資料。
   - 決議：A

3. **衝突解決策略**
   - 方案 A：last-write-wins，以 `updatedAt`（伺服器時間 `serverTimestamp`）為準
   - 方案 B：以裝置本地時間為準
   - 建議：**方案 A**。伺服器時間避免裝置時鐘不同步造成錯亂；CheckIn 為冪等事件（存在/不存在）衝突更單純。
   - 決議：**A（per-record 逐筆 LWW）**。記錄 `updatedAt` MVP 用裝置時間、同域比較；帳號 `lastUpdate` 用 serverTimestamp 僅作變更偵測（見 Q7 與 Functional Design「設計取捨」）。

4. **登入前本地資料的整合**
   - 方案 A：登入後將本地既有資料全部上傳並綁定該 `uid`（合併）
   - 方案 B：登入後以雲端為準，捨棄/覆蓋本地未綁定資料
   - 方案 C：MVP 不處理——假設打卡功能一律於登入後使用（目前 App 進入點已需登入）
   - 建議：**方案 C**。現有導覽 `login → main`，使用者本就先登入才用功能，登入前本地資料情境不存在，MVP 先不處理。
   - 決議：**C**——不處理匿名 / 跨帳號合併；但登入首次同步時若 `users/{uid}` 文件不存在則建立（見 Q7），既有本地資料以正常 push 上傳綁定該 uid。

5. **同步觸發時機**
   - 方案 A：開啟 App / 登入時 pull + 本地寫入當下即時 push
   - 方案 B：定期背景同步（WorkManager）
   - 建議：**兩者並用**——即時 push / 啟動 pull 保證體感，WorkManager 背景補漏（需新增 WorkManager 依賴）。
   - 決議：兩者並用

6. **刪除的同步語意**
   - 方案 A：hard delete——本地刪除同時刪雲端文件
   - 方案 B：soft delete（tombstone）——標記 `deleted` 欄位，同步後再實刪
   - 建議：**方案 A**。單一使用者、資料量小；hard delete 最簡潔，暫不需 tombstone 處理跨裝置刪除競態（可列未來）。
   - 決議：**B（soft delete / tombstone）**。一石二鳥：(1) 讓刪除變成可同步的「狀態」，消除「local 有 / cloud 無」的方向歧義；(2) 保留 Habit 記錄，讓歷史 CheckIn 即使習慣停用仍有主可循。以 `deletedAt: Long?`（null=存在）當旗標兼時間戳；不做永久抹除後門與 tombstone 清理（列未來）。

7. **帳號層級 `lastUpdate` 的定位（變更偵測 vs 衝突裁判）**
   - 原始構想：在 `users/{uid}` 放一個 `lastUpdate`，同步時比時間前後決定「用雲端或本地為主」。
   - 釐清：帳號層級單一時間戳**只能當「變更偵測」**（雲端有沒有變 → 要不要 pull），**不能當「誰贏」的裁判**——若拿它整包決定方向，會把兩台裝置各改不同筆的資料互相蓋掉（coarse-grained LWW 掉資料）。
   - 決議：**採用，但職責分層**——
     - `users/{uid}.lastUpdate`（serverTimestamp）＝ high-water mark，配合本地 `lastSyncedAt` 判斷要不要同步。
     - 衝突解決下放到**每一筆記錄各自的 `updatedAt`**（逐筆 LWW）。
   - 附帶：本 spec 需一併建立 `users/{uid}` 使用者空間（Firestore 目前尚無）。

---

## Functional Design

> 由 Agent 填寫。前提（已決議）：Q1=A 自建同步層、Q2=A per-user 子集合、Q3=A per-record LWW、Q4=C（不合併、登入建 user doc）、Q5 即時 + WorkManager、Q6=B soft delete/tombstone、Q7 lastUpdate 當變更偵測。

### 核心觀念：同步的兩個獨立職責（別混用）

1. **變更偵測（要不要同步）** → 帳號層級一個 `lastUpdate`（high-water mark）。
2. **衝突解決（哪一筆聽誰的）** → 逐筆比各自的 `updatedAt`（LWW）。

（見 Open Q7：帳號層級時間戳只能當偵測器，拿它整包決定方向會掉資料。）

### Firestore 資料結構（Q2=A）

- `users/{uid}`（文件）：`{ lastUpdate: serverTimestamp }` — 帳號層級變更高水位。
- `users/{uid}/habits/{habitId}`：對應 HabitEntity 全欄位（含 `updatedAt`、`deletedAt`）。
- `users/{uid}/checkIns/{checkInId}`：對應 CheckInEntity 全欄位（含 `updatedAt`、`deletedAt`）。

### 本地資料模型調整

- **HabitEntity** 加：`deletedAt: Long?`（tombstone，null=存在）、`pendingSync: Boolean`（outbox 旗標：本地改過、未確認上傳）。
- **CheckInEntity** 加：`updatedAt: Long`、`deletedAt: Long?`、`pendingSync: Boolean`。
- **新增 `sync_meta`**（Room 單列表，或 DataStore）：`lastSyncedAt: Long?`（上次 pull 讀到的雲端 `lastUpdate` server 值）。
- DB version 2 → 3（沿用 `fallbackToDestructiveMigration`，開發期）。

### 三個時鐘 / 游標（釐清時間軸，避免跨域比較）

| 名稱 | 存在哪 | 時鐘域 | 職責 |
|---|---|---|---|
| 記錄 `updatedAt` | 每筆 habit/checkin（雲＋本地同值） | 裝置寫入時 epoch millis | 逐筆 LWW 比大小 |
| `pendingSync` | 每筆本地記錄 | — | 標記「本地改過、還沒確認上傳」 |
| `lastUpdate` | `users/{uid}` | Firestore serverTimestamp | 偵測「雲端有沒有變」 |
| `lastSyncedAt` | 本地 `sync_meta` | 上次讀到的 server 值 | 與 `lastUpdate` 比 → 要不要 pull |

> 每個比較都在**同一時鐘域內**進行（updatedAt↔updatedAt 皆裝置時間；lastUpdate↔lastSyncedAt 皆 server 時間），不跨域比。

### 讀取查詢分流（Q6=B 的必然結果）

- **活躍清單**（管理頁、今日打卡、streak 計算）：`WHERE deletedAt IS NULL`。
- **歷史紀錄**（未來紀錄頁）：可含 `deletedAt` 不為 null 的習慣，顯示已停用習慣的舊名。

### Push（本地 → 雲）

1. 任何本地寫入（新增/編輯/soft delete habit、打卡/破戒/undo checkin）：先落 Room，該筆 `updatedAt = now`、`pendingSync = true`。
2. 若在線 → 立即 push（Q5 即時）。push 用 **batch** 原子提交：`set` 對應 habit/checkin 文件（整筆欄位）＋ `set users/{uid}.lastUpdate = serverTimestamp()`。
3. push 成功 → 該筆 `pendingSync = false`。
4. 若離線 / 失敗 → `pendingSync` 維持 true，由 **WorkManager** 背景重試（Q5 補漏）：撈所有 `pendingSync = true` 記錄重送。

### Pull（雲 → 本地，全量 reconcile）

1. **閘門**：讀 `users/{uid}.lastUpdate`；若 `<= lastSyncedAt` → 雲端無新變更，直接結束（省讀取）。
2. 雲端較新 → 拉 `users/{uid}/habits` 與 `/checkIns` 全部文件，逐筆 reconcile：
   - 雲有、本地無 → insert（`pendingSync = false`）。
   - 兩邊都有 → **LWW**：`cloud.updatedAt > local.updatedAt` 才用雲端覆蓋；否則保留本地。
   - 雲端該筆 `deletedAt` 有值（tombstone）→ 本地套用 soft delete。
3. reconcile 完成 → `lastSyncedAt = 剛讀到的 cloud.lastUpdate`。
- 觸發時機（Q5）：App 啟動 / 登入時、WorkManager 週期。

### 刪除流程（Q6=B）

- **刪習慣**：`UPDATE habits SET deletedAt = now, updatedAt = now, pendingSync = true`（不 `DELETE`）；其 CheckIn 不動（保留歷史）。
- **undo 打卡**：改為 soft delete 該 checkin（設 `deletedAt`）而非實刪，才能同步「取消」這個動作。
- **重新打卡同一天**：若該日 checkin 已存在（含 tombstone）→ 復活（`deletedAt = null, updatedAt = now, pendingSync = true`），而非 insert 新列（維持 `habitId + epochDay` 唯一）。

### 登入 / 使用者空間（Q4=C + Q7）

- 導覽 `login → main`，功能一律登入後使用；不處理匿名 / 跨帳號資料合併。
- 首次同步時若 `users/{uid}` 不存在 → 建立（初始化 lastUpdate）；既有本地資料透過正常 push（`pendingSync`）上傳綁定該 uid。

### 邊界條件 / 設計取捨（誠實標注）

- **記錄 LWW 用裝置時間**：MVP 為簡化，記錄 `updatedAt` 用裝置 epoch millis，兩端同域比較即可。裝置時鐘偏移只在「同一筆被兩台裝置離線同時改」時才有風險（單一使用者極罕見）。硬化方向：改 serverTimestamp / version vector（列未來）。
- **CheckIn 衝突幾乎不存在**：append-only 且 `habitId + epochDay` 唯一冪等，兩台同日打卡結果一致。
- WorkManager 僅作同步補漏；背景排程與通知權限等非本 spec 打卡邏輯範圍。
- 依全域偏好，不主動加入未要求的 error handling / validation。

---

## Coding Scope

> 由 Agent 填寫。前提同 Functional Design。對照現有 `data/habit/` 檔案。

**新增：**
- `data/habit/HabitRemoteDataSource.kt` — Firestore 讀寫：`users/{uid}` 文件（`lastUpdate`）、`habits` / `checkIns` 子集合的 set/get；batch 上傳＋bump `lastUpdate`。
- `data/habit/HabitSyncManager.kt` — 同步協調：push（撈 `pendingSync` 上傳）、pull（`lastUpdate` 閘門 → 全量 reconcile 逐筆 LWW → 套用 tombstone → 更新 `lastSyncedAt`）；供即時觸發與 Worker 呼叫。
- `data/sync/SyncMetaEntity.kt` — Room 單列表 `sync_meta`（`lastSyncedAt: Long?`）。
- `data/sync/SyncMetaDao.kt` — 讀 / 寫 `lastSyncedAt`。
- `data/sync/SyncWorker.kt` — `CoroutineWorker`，週期呼叫 `HabitSyncManager`（Q5 背景補漏）。

**修改：**
- `data/habit/HabitEntity.kt` — 加 `deletedAt: Long?`、`pendingSync: Boolean`。
- `data/habit/CheckInEntity.kt` — 加 `updatedAt: Long`、`deletedAt: Long?`、`pendingSync: Boolean`。
- `data/habit/Habit.kt` — domain 與 `toEntity()` / `toDomain()` 對應新欄位（`pendingSync` 屬同步基礎建設、不進 domain）。
- `data/habit/HabitDao.kt` — 活躍查詢加 `WHERE deletedAt IS NULL`；新增 upsert、soft delete、`getPending()`、reconcile 用的全量取得（含已刪）。
- `data/habit/CheckInDao.kt` — `deleteByHabitAndDay` 改 soft delete；加「復活」upsert；`observeAll()` 改只回活躍（`deletedAt IS NULL`）；加 pending / reconcile 全量查詢。
- `data/habit/HabitRepositoryImpl.kt` — 寫入設 `updatedAt` / `pendingSync`；delete → soft delete；undo → soft delete；`checkInToday` → 復活或 insert；寫入後透過 `HabitSyncManager` 觸發即時 push。
- `data/habit/HabitRepository.kt` — 視需要補手動同步 / 初始 pull 介面（如 `suspend fun sync()`）。
- `data/habit/HabitWithStreak.kt`（streak 計算）— 確保只吃活躍 checkin（`deletedAt IS NULL`），沿用 `combine`。
- `data/habit/MyDaysDatabase.kt` — entities 加 `SyncMetaEntity`、version 2→3、`abstract fun syncMetaDao()`。
- `di/AppModule.kt` — 提供 `FirebaseFirestore`、`HabitRemoteDataSource`、`HabitSyncManager`、`SyncMetaDao`；`HabitRepositoryImpl` 建構子多收同步相依；Worker 工廠綁定。
- `MyDaysApp.kt` — 設定 Koin WorkManager factory；排程週期性 `SyncWorker`（Q5）。
- `MainActivity.kt` / 登入流程 — 登入 / App 啟動時觸發一次 pull。
- `gradle/libs.versions.toml` — 新增 WorkManager（`androidx.work:work-runtime-ktx`）＋ `koin-androidx-workmanager`。
- `app/build.gradle.kts` — 套用上述依賴（Firestore 已宣告，直接沿用）。

---

## Acceptance Criteria

> 定義「完成」的標準。

- [ ] 登入後本地既有習慣 / 打卡自動上傳到 `users/{uid}` 對應子集合
- [ ] 另一裝置登入同帳號，App 啟動後可 pull 到相同的習慣與打卡
- [ ] 離線新增 / 編輯 / 打卡 / 破戒可正常操作；恢復連線後（即時或 WorkManager）自動補傳，`pendingSync` 歸零
- [ ] 讀取一律走本地 Room，離線 UI 不空白 / 不卡
- [ ] 同一筆習慣被兩端更新時，以 `updatedAt` 較新者為準（逐筆 LWW），未衝突的其他筆不遺失
- [ ] 刪除習慣為 soft delete：活躍清單不再顯示，但該習慣記錄與其歷史 CheckIn 仍保留可供歷史查詢
- [ ] 刪除 / undo 打卡能同步到另一裝置（tombstone 傳遞），不被全量 reconcile 復活
- [ ] `users/{uid}.lastUpdate` 未變時，pull 走閘門直接略過（不做全量拉取）
- [ ] streak 計算只計入活躍（未刪）打卡

---

## Notes

> 實作注意事項、已知限制。

- Firestore 已導入並套用（`app/build.gradle.kts`）；本 spec 直接沿用。
