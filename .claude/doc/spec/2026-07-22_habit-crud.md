# Habit CRUD

> 所屬 Feature：`doc/feature/2026-07-22_habit.md`
> 狀態：草稿
> Jira：___

---

## Overview

> 由我填寫。

定義習慣的建立 / 編輯 / 刪除，以及底層資料模型（Phase 1）。

範圍包含：CRUD、習慣類型（好習慣養成 / 壞習慣戒除）、每日單一內容、Habit 資料模型（Room entity）。
不包含：打卡與 streak（見 `habit-checkin-streak`）、雲端同步（見 `habit-sync`）。

---

## Requirements

> 由我填寫。列出具體的功能需求，以使用者可觀察的行為描述。
> （以下為 Agent 依 Overview + feature 文件草擬，待使用者增刪確認）

1. 使用者可以新增習慣，輸入習慣名稱。
2. 使用者可以指定習慣類型：養成好習慣 / 戒除壞習慣。
3. 習慣為每日執行、單一內容，不需頻率設定。
4. 使用者可以編輯既有習慣的名稱與類型。
5. 使用者可以刪除習慣。
6. 使用者可以在列表看到自己建立的所有習慣，顯示名稱與類型。
7. 習慣資料在重啟 App 後仍保留（本地持久化）。

---

## Framework

> 由 Agent 填寫。對照現有 codebase / 架構。

- **落點 package**：資料層 `data/habit/`（對照 `data/auth/AuthRepository.kt`）；UI 層 `ui/habit/`（對照 `ui/todo/`）。
- **Repository / ViewModel 模式**：repository **改為介面 + 實作分離**；本 spec 引入 **Koin** 作為專案級 DI（Q3 = C），Habit 與既有 Auth 的 repository / ViewModel 皆由 Koin 注入（見 Functional Design「附帶基礎建設」）。
- **UI 進入點已存在**：`ui/features/FeaturesScreen.kt` 已有停用的「習慣養成」入口（`Icons.Default.Loop`, `enabled = false`）。只需補 `onClick` 並在 `FeaturesNavHost` 新增 route，與 Todo 的 `ROUTE_FEATURES_TODO` 同模式。
- **子頁 scaffold**：沿用 `SubScreenScaffold`（Todo 使用），維持 Liquid Glass 風格一致（符合 feature 的 Non-Goal：不改風格）。
- **持久化現況**：專案目前**無 Room**（版本目錄未含），但 **Firestore 已導入並套用**（`app/build.gradle.kts`）。本 spec 需新增 Room 依賴（runtime + compiler，Kotlin 2.2 走 KSP）。
- **列表狀態**：Todo 目前用 `mutableStateMapOf` 純 mock、無 ViewModel。Habit crud 需真資料，自建 `HabitViewModel` + `HabitRepository`。
- **環境**：minSdk 35 / compileSdk 37 / Kotlin 2.2.10 / Java 11。

---

## Open Questions

> 由 Agent 填寫。每項附方案與建議供選擇。

1. **crud MVP 是否一次接上 Room，還是先 UI + in-memory**
   - 方案 A：直接落 Room（本 spec 目標即資料模型，一次到位）
   - 方案 B：先 UI + in-memory 假資料（如 Todo），Room 留待後續
   - 建議：**方案 A**。crud 的核心價值是持久化，且 Room entity 是後續 `habit-sync` 的基礎。
   - 決議：A

2. **好 / 壞習慣型別如何表示**
   - 方案 A：Habit 上一個欄位 `type: HabitType`（enum：BUILD / QUIT）
   - 方案 B：兩張分開的 entity
   - 建議：**方案 A**。多數欄位共用，僅打卡 / streak 語意不同，單一 entity + type 欄最簡潔。
   - 決議：A

3. **無 DI 下 ViewModel 如何取得 Room-backed repository**
   - 背景：`AuthViewModel` 用建構子預設參數 `= AuthRepository()` 可行，因 `AuthRepository` 不需 Context；但 `HabitRepositoryImpl` 需 Room DAO（建 DB 需 Context），**無法無參數預設建構**。
   - 方案 A：Compose `viewModel(factory = ...)` + `ViewModelProvider.Factory` 手動注入 `HabitRepository` 介面
   - 方案 B：改用 `AndroidViewModel`，以 `application` context 取 `MyDaysDatabase` singleton，於 ViewModel 內建立 repository
   - 方案 C：引入 **Koin** 作為專案級 DI，統一管理 DB / DAO / repository / ViewModel 注入
   - 決議：**C（引入 Koin）**。專案級 DI，Habit 與既有 Auth 皆走 Koin 注入；`AuthRepository` 一併改為 interface + Impl。Koin 基礎建設與 Auth 遷移**併入本 spec 一起處理**（不另開 spec）。

---

## Functional Design

> 由 Agent 填寫。資料模型、狀態流轉、UI 互動、邊界條件。
> 前提：Q1 = A（直接落 Room）、Q2 = A（單一 entity + `type` 欄）、Q3 = C（引入 Koin），已決議。

### 資料模型（Habit entity）

table `habits`（僅定義層欄位；CheckIn / streak / bestStreak 屬 `habit-checkin-streak`，本 spec 不含）：

| 欄位 | 型別 | 說明 |
|---|---|---|
| `id` | String (UUID) | 主鍵（見 data-model Open Q1，建議 UUID String） |
| `name` | String | 習慣名稱 |
| `type` | HabitType（BUILD / QUIT） | Room 以 String 存，透過 `@TypeConverter` 或 enum name |
| `createdAt` | Long (epoch millis) | 建立時間；壞習慣 streak 起算點 |
| `updatedAt` | Long | 供後續 `habit-sync` 衝突解決 |

### 分層（對照 `data/auth` 與 `AuthRepository` 寫法）

- **HabitEntity**：Room `@Entity`；`HabitType` enum 同檔或獨立檔
- **HabitDao**：`insert` / `update` / `delete` + `observeAll(): Flow<List<HabitEntity>>`
- **MyDaysDatabase**：首個 `RoomDatabase`，singleton（後續 CheckIn 表共用）
- **HabitRepository（interface）**：定義 CRUD 契約，保留平台轉換 / 測試替身空間
- **HabitRepositoryImpl**：Room 實作，包 DAO
- **HabitViewModel**：`StateFlow<List<Habit>>` + `addHabit` / `updateHabit` / `deleteHabit`；建構子收 `HabitRepository` 介面，由 Koin 注入（Compose 端 `koinViewModel()`）

### 附帶基礎建設：引入 Koin + Auth 遷移（Q3 = C）

本 spec 首次引入 DI，順帶把既有 Auth 一併遷移，統一注入模型：

- **Koin 導入**：加 `koin-android` / `koin-androidx-compose`（執行期 DI，**不需 plugin / KSP**）；新增 `MyDaysApp : Application`，於 `onCreate` 跑 `startKoin { androidContext(...); modules(appModule) }`；manifest 補 `android:name`。
- **AppModule**（`di/AppModule.kt`）：集中提供 `MyDaysDatabase` → `HabitDao` → `HabitRepositoryImpl`（as `HabitRepository`）→ `HabitViewModel`，並含 Auth 綁定。
- **Auth 遷移**：`AuthRepository` 拆為 interface + `AuthRepositoryImpl`（現有實作搬入）；`AuthViewModel` 移除 `= AuthRepository()` 預設參數改由 Koin 注入；`MainActivity` 的 `viewModel()` 改 `koinViewModel()`。

### 狀態流轉

- **新增**：輸入 name + 選 type → `insert`（id = UUID、createdAt = updatedAt = now）
- **編輯**：改 name / type → `update`（updatedAt = now）
- **刪除**：依 id `delete`
- **列表**：DAO `Flow` → ViewModel `StateFlow` → UI 即時反映

### UI 互動

- **HabitScreen**：沿用 `SubScreenScaffold`（Todo 同款，維持 Liquid Glass），清單列出 name + type 標示
- **新增 / 編輯**：輸入 name、選 type（好習慣 BUILD / 壞習慣 QUIT）
- **刪除**：list item 操作（滑動或長按，UI 細節實作時定）

### 邊界條件

- 依全域偏好，不主動加入未要求的名稱驗證 / error handling；空名稱等 UX 規則待需求明確再補。

---

## Coding Scope

> 由 Agent 填寫。預計新增 / 修改的檔案與範圍。
> 前提同 Functional Design（Q1 = A、Q2 = A、Q3 = C，已決議）。

**新增（Habit）：**
- `data/habit/HabitEntity.kt` — Room `@Entity` + `HabitType` enum（BUILD / QUIT）
- `data/habit/HabitDao.kt` — CRUD + `observeAll(): Flow`
- `data/habit/MyDaysDatabase.kt` — 首個 `RoomDatabase`，singleton
- `data/habit/HabitRepository.kt` — CRUD 介面契約
- `data/habit/HabitRepositoryImpl.kt` — Room 實作（對照 `data/auth/AuthRepository.kt`，改為介面 + 實作分離）
- `ui/habit/HabitViewModel.kt` — `StateFlow` + CRUD（收 `HabitRepository` 介面，Koin 注入）
- `ui/habit/HabitScreen.kt` — 清單 + 新增 / 編輯 / 刪除，`SubScreenScaffold`（對照 `ui/todo/TodoScreen.kt`）

**新增（Koin 基礎建設 + Auth 遷移）：**
- `MyDaysApp.kt`（`com.weiting.mydays`）— `Application` + `startKoin`
- `di/AppModule.kt` — Koin module（DB / DAO / repository / viewModel 綁定；含 Auth）
- `data/auth/AuthRepositoryImpl.kt` — 既有 `AuthRepository` 實作內容搬入

**修改：**
- `ui/features/FeaturesScreen.kt` — 「習慣養成」entry 設 `enabled = true` + `onClick`，新增 `onOpenHabit` 參數（第 30 行）
- `ui/features/FeaturesNavHost.kt` — 新增 `ROUTE_FEATURES_HABIT` route + `composable(HabitScreen)`，傳入 `onOpenHabit`
- `data/auth/AuthRepository.kt` — 改為 interface（`currentUser` / `signInWithGoogle` / `signOut`）
- `ui/auth/AuthViewModel.kt` — 移除 `= AuthRepository()` 預設參數，改注入 `AuthRepository` 介面
- `MainActivity.kt` — `authViewModel: AuthViewModel = viewModel()` → `koinViewModel()`（第 40 行）
- `app/src/main/AndroidManifest.xml` — `<application>` 補 `android:name=".MyDaysApp"`
- `gradle/libs.versions.toml` — 新增 Room（`room-runtime` / `room-ktx` / `room-compiler`）+ KSP plugin；新增 Koin（`koin-android` / `koin-androidx-compose`）
- `app/build.gradle.kts` — 套用 KSP plugin、加 Room 依賴與 `ksp(room-compiler)`；加 Koin 依賴
- 專案根 `build.gradle.kts` — KSP plugin 以 `apply false` 宣告

---

## Acceptance Criteria

> 定義「完成」的標準。

- [ ] 可新增習慣（name + type），重啟 App 後仍存在（Room 持久化）
- [ ] 可編輯既有習慣的 name / type
- [ ] 可刪除習慣
- [ ] 習慣清單即時反映新增 / 編輯 / 刪除（DAO Flow → StateFlow）
- [ ] 「習慣養成」入口從停用改為可點，進入 `HabitScreen`
- [ ] `HabitRepository` 為介面、`HabitViewModel` 依賴介面，可替換測試替身
- [ ] App 啟動 `startKoin` 成功、無 crash
- [ ] `AuthRepository` 為介面、`AuthViewModel` 由 Koin 注入（`koinViewModel()`），登入 / 登出行為不變

---

## Notes

> 實作注意事項、已知限制。

- 本 spec 首次引入 Room：使用 KSP（Kotlin 2.2，不用 kapt）；`MyDaysDatabase` 為後續 `habit-checkin-streak`（CheckIn 表）與 `habit-sync` 的共用基礎，命名不綁單一功能。
- 本 spec 同時首次引入 **Koin（專案級 DI）並遷移 Auth**：範圍超出「Habit CRUD」本身，屬前置基礎建設，一併於此完成（依討論不另開 spec）。Koin 為執行期 DI、**不需 KSP**，與 Room 的 KSP 相互獨立。
- Repository 介面化（`HabitRepository` / `AuthRepository`）為本專案首例，刻意偏離現有無介面寫法（測試 / 平台轉換）。
- 專案 `CLAUDE.md` 的 DI 欄位需同步從「無」更新為「Koin」。
- 依全域偏好，不主動加入未要求的驗證 / error handling。
