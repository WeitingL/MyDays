# 習慣養成功能重構提案

> 建立日期：2026-08-15  
> 作者：PMM  
> 目標：引入 Clean Architecture，建立 Domain Layer

---

## 執行摘要

### 為什麼要重構？

當前習慣功能存在 4 個核心問題：

1. **業務邏輯塞在 Repository**（110 行 HabitRepositoryImpl）
   - Streak 計算、打卡判斷、同步觸發、提醒排程全在 data layer
   - 違反單一職責原則
   
2. **缺少 Domain Layer**
   - 無 UseCase，業務流程散落各處
   - 難以複用邏輯（Widget/Notification 無法複用打卡功能）
   
3. **同步機制脆弱**
   - Fire-and-forget push，失敗無重試
   - 無衝突解決（LWW 可能遺失變更）
   
4. **UI 功能單薄**
   - 只有列表 + 打卡
   - 無歷史檢視、無統計圖表

### 重構目標

- ✅ 建立清晰的三層架構（Presentation → Domain → Data）
- ✅ 業務邏輯集中在 UseCase，易測試
- ✅ Repository 只做資料存取（< 50 行/個）
- ✅ 支援跨層複用（Widget、Notification 可複用 UseCase）
- ✅ 核心 UseCase 單元測試覆蓋率 > 80%

### 預估工時

**總計**：14-20 小時（分 5 個 Phase 執行）

### 風險評估

| 風險 | 機率 | 影響 | 緩解措施 |
|---|---|---|---|
| 遷移過程功能 broken | 中 | 高 | 每個 Phase 完成後手動測試 |
| 工時超出預期 | 高 | 中 | 分階段執行，Phase 1-2 完成後重新評估 |
| 測試覆蓋率不足 | 中 | 中 | Phase 4 專門補測試，強制檢查覆蓋率 |
| 新舊代碼並存期間混亂 | 低 | 中 | 清楚標記舊代碼（@Deprecated），Phase 5 統一清理 |

---

## 當前架構問題詳述

### 問題 1：Repository 包含業務邏輯

**HabitRepositoryImpl（110 行）做了太多事**：

```kotlin
// 問題範例 1：Streak 計算（業務邏輯）在 Repository
fun observeHabitsWithStreak(): Flow<List<HabitWithStreak>> {
    return combine(habitDao.observeAll(), checkInDao.observeAll()) { habits, checkIns ->
        habits.map { habitEntity ->
            val habit = habitEntity.toHabit()
            val streak = calculateStreak(habit, checkIns) // ← 業務邏輯
            HabitWithStreak(habit, streak, ...)
        }
    }
}
```

**為什麼這是問題**：
- Streak 計算是核心業務邏輯，不應在 data layer
- 無法單獨測試 streak 計算（必須帶著 Room、Flow 一起測）
- Widget 想要顯示 streak，必須依賴整個 Repository

---

### 問題 2：缺少 Domain Layer

**實際案例：Widget 想複用打卡邏輯**

當前架構：
```
Widget → 直接呼叫 Repository.recordCheckIn()
       → 但 Widget 不該依賴 data layer
```

理想架構：
```
Widget → RecordCheckInUseCase → Repository
       → UseCase 封裝業務規則，可被任何 UI 複用
```

**導致的困難**：
- Widget、Notification Action、Screen 各自重複實作打卡邏輯
- 業務規則變更時（例如「連續 7 天後獎勵」）要改 N 個地方

---

### 問題 3：同步機制與 Repository 耦合

```kotlin
// HabitRepositoryImpl
suspend fun add(habit: Habit) {
    habitDao.insert(...)
    syncManager.schedulePush() // ← 同步邏輯綁在 Repository
}
```

**為什麼這是問題**：
- Repository 職責過多（資料存取 + 同步協調）
- 同步失敗無重試機制（fire-and-forget）
- 難以抽換同步策略（例如改用 WorkManager）

---

### 問題 4：UI 功能難以擴展

**想加「歷史日曆檢視」需要**：
1. 在 Repository 加 `getCheckInsByMonth()` → 又是業務查詢
2. 在 ViewModel 加計算邏輯 → 業務邏輯又散落到 presentation layer
3. 無法測試（UI 層難寫單元測試）

---

## 目標架構設計

### 三層架構

```
┌─────────────────────────────────────┐
│   Presentation Layer                │
│   (UI + ViewModel)                  │
│   - HabitScreen                     │
│   - HabitViewModel                  │
└─────────────┬───────────────────────┘
              │ 呼叫 UseCase
              ↓
┌─────────────────────────────────────┐
│   Domain Layer                      │
│   (UseCase + Domain Models)         │
│   - RecordCheckInUseCase            │
│   - CalculateStreakUseCase          │
│   - 純 Kotlin，無 Android 依賴       │
└─────────────┬───────────────────────┘
              │ 呼叫 Repository Interface
              ↓
┌─────────────────────────────────────┐
│   Data Layer                        │
│   (Repository Impl + Data Source)   │
│   - HabitRepositoryImpl             │
│   - Room, Firebase                  │
└─────────────────────────────────────┘
```

### 依賴規則（Dependency Rule）

- **Presentation 依賴 Domain**（呼叫 UseCase）
- **Data 依賴 Domain**（實作 Repository Interface）
- **Domain 不依賴任何層**（純 Kotlin，可獨立測試）

### 完整檔案結構

```
app/src/main/java/com/weiting/mydays/
├── domain/
│   ├── model/
│   │   ├── Habit.kt              # Domain model（純 Kotlin）
│   │   └── CheckIn.kt
│   ├── repository/
│   │   ├── HabitRepository.kt    # Interface
│   │   └── CheckInRepository.kt
│   └── usecase/
│       ├── habit/
│       │   ├── CreateHabitUseCase.kt
│       │   ├── UpdateHabitUseCase.kt
│       │   ├── DeleteHabitUseCase.kt
│       │   └── GetHabitsWithStreakUseCase.kt
│       ├── checkin/
│       │   ├── RecordCheckInUseCase.kt
│       │   ├── UndoCheckInUseCase.kt
│       │   └── RecordOccurrenceUseCase.kt
│       └── sync/
│           ├── SyncHabitsUseCase.kt
│           └── PushPendingChangesUseCase.kt
├── data/
│   └── habit/
│       ├── local/
│       │   ├── HabitEntity.kt         # Room entity
│       │   └── HabitDao.kt
│       ├── remote/
│       │   └── HabitRemoteDataSource.kt
│       ├── repository/
│       │   └── HabitRepositoryImpl.kt  # 實作 domain interface
│       └── mapper/
│           └── HabitMapper.kt          # Entity ↔ Domain model
└── ui/
    └── habit/
        ├── HabitScreen.kt
        └── HabitViewModel.kt
```

---

## UseCase 範例

### RecordCheckInUseCase

```kotlin
class RecordCheckInUseCase(
    private val checkInRepository: CheckInRepository,
    private val habitRepository: HabitRepository,
    private val pushPendingChangesUseCase: PushPendingChangesUseCase
) {
    suspend operator fun invoke(habitId: String, date: LocalDate): Result<Unit> {
        return try {
            // 1. 檢查今天是否已打卡
            val existingCheckIn = checkInRepository.getByHabitAndDate(habitId, date)
            if (existingCheckIn != null) {
                return Result.failure(AlreadyCheckedInException())
            }
            
            // 2. 記錄打卡
            val checkIn = CheckIn(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                date = date,
                createdAt = Clock.System.now()
            )
            checkInRepository.insert(checkIn)
            
            // 3. 觸發同步（fire-and-forget）
            pushPendingChangesUseCase()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**特點**：
- 業務邏輯清楚（檢查 → 記錄 → 同步）
- 可獨立測試（mock repository）
- 可被任何 UI 複用（Screen / Widget / Notification）

---

## 遷移策略

### 原則

- ✅ 逐步遷移，不做 Big Bang
- ✅ 保持功能運作（每個 Phase 完成後可交付）
- ✅ 先建新架構，再遷移舊邏輯
- ✅ 舊代碼標記 @Deprecated，Phase 5 統一清理

---

### Phase 1：建立 Domain Layer 骨架

**目標**：建立 domain/ 資料夾結構，定義 interface 和第一個 UseCase

**工時**：3-4 小時

**要做的事**：

1. **建立 Domain Model**（2 個檔案）
   - `domain/model/Habit.kt`
   - `domain/model/CheckIn.kt`
   
2. **建立 Repository Interface**（2 個檔案）
   - `domain/repository/HabitRepository.kt`
   - `domain/repository/CheckInRepository.kt`
   
3. **建立第一個 UseCase**（1 個檔案）
   - `domain/usecase/habit/GetHabitsWithStreakUseCase.kt`
   
4. **建立 Mapper**（1 個檔案）
   - `data/habit/mapper/HabitMapper.kt`（Entity ↔ Domain model）

**驗證方式**：
- 編譯通過
- Domain layer 無 Android / Room / Firebase 依賴

**風險**：低（只加新檔案，不動舊代碼）

---

### Phase 2：遷移 Repository

**目標**：將 RepositoryImpl 的業務邏輯搬到 UseCase

**工時**：4-5 小時

**要做的事**：

建立 11 個 UseCase：

**Habit CRUD**（4 個）：
- CreateHabitUseCase
- UpdateHabitUseCase
- DeleteHabitUseCase
- GetHabitByIdUseCase

**CheckIn**（3 個）：
- RecordCheckInUseCase
- UndoCheckInUseCase
- RecordOccurrenceUseCase

**Streak**（1 個）：
- CalculateStreakUseCase

**Sync**（2 個）：
- SyncHabitsUseCase
- PushPendingChangesUseCase

**Reminder**（2 個）：
- ScheduleReminderUseCase
- CancelReminderUseCase

**同時**：
- 實作 `HabitRepositoryImpl` 和 `CheckInRepositoryImpl`（實作 domain interface）
- 保留舊的 `data/habit/HabitRepositoryImpl.kt`（標記 @Deprecated）

**驗證方式**：
- 每個 UseCase 編譯通過
- Repository 只做資料存取（< 50 行/個）

**風險**：中（業務邏輯搬移可能有遺漏）

---

### Phase 3：遷移 ViewModel

**目標**：HabitViewModel 改用 UseCase

**工時**：2-3 小時

**要做的事**：

修改 `HabitViewModel`：
- 注入 UseCase（不再注入 Repository）
- 所有操作改呼叫 UseCase
- 移除 ViewModel 內的業務邏輯

**範例**：
```kotlin
// 舊
class HabitViewModel(
    private val repository: HabitRepository
) {
    fun toggleCheckIn(habitId: String) {
        viewModelScope.launch {
            repository.recordCheckIn(habitId)  // ← 直接呼叫 Repository
        }
    }
}

// 新
class HabitViewModel(
    private val recordCheckInUseCase: RecordCheckInUseCase,
    private val undoCheckInUseCase: UndoCheckInUseCase
) {
    fun toggleCheckIn(habitId: String) {
        viewModelScope.launch {
            recordCheckInUseCase(habitId, LocalDate.now())  // ← 呼叫 UseCase
        }
    }
}
```

**驗證方式**：
- 編譯通過
- 手動測試核心流程（新增、打卡、刪除）

**風險**：低（UI 不動，只改 ViewModel 內部）

---

### Phase 4：補測試

**目標**：核心 UseCase 單元測試覆蓋率 > 80%

**工時**：4-6 小時

**要測試的 UseCase**（優先度排序）：

**P0 - Critical**（必測）：
- RecordCheckInUseCase
- CalculateStreakUseCase
- GetHabitsWithStreakUseCase

**P1 - Important**（建議測）：
- UndoCheckInUseCase
- RecordOccurrenceUseCase
- CreateHabitUseCase

**P2 - Nice to have**：
- 其他 CRUD UseCase

**驗證方式**：
- `./gradlew testDebugUnitTest`
- 覆蓋率報告 > 80%

**風險**：低（不影響功能）

---

### Phase 5：清理舊代碼

**目標**：移除 @Deprecated 的舊 Repository

**工時**：1-2 小時

**要做的事**：

1. 刪除 `data/habit/HabitRepositoryImpl.kt`（舊版）
2. 更新 Koin module（移除舊 Repository 註冊）
3. 全域搜尋 `@Deprecated`，確認無遺漏

**驗證方式**：
- 編譯通過
- 手動測試核心流程

**風險**：低（Phase 3 已確認新架構可用）

---

## 預估工時表

| Phase | 任務 | 工時 | 累積 |
|---|---|---|---|
| **Phase 1** | 建立 Domain Model（2 檔案） | 0.5 hr | 0.5 hr |
| | 建立 Repository Interface（2 檔案） | 0.5 hr | 1 hr |
| | 建立第一個 UseCase（1 檔案） | 1 hr | 2 hr |
| | 建立 Mapper（1 檔案） | 0.5 hr | 2.5 hr |
| | 驗證與調整 | 0.5-1 hr | **3-4 hr** |
| **Phase 2** | 建立 11 個 UseCase | 3-4 hr | 6-8 hr |
| | 實作新 RepositoryImpl | 0.5-1 hr | 6.5-9 hr |
| | 驗證與調整 | 0.5 hr | **7-9 hr** |
| **Phase 3** | 遷移 HabitViewModel | 1.5-2 hr | 8.5-11 hr |
| | 手動測試 | 0.5-1 hr | **9-12 hr** |
| **Phase 4** | 寫 P0 測試（3 個） | 2-3 hr | 11-15 hr |
| | 寫 P1 測試（3 個） | 1.5-2 hr | 12.5-17 hr |
| | 驗證覆蓋率 | 0.5-1 hr | **13-18 hr** |
| **Phase 5** | 刪除舊代碼 | 0.5-1 hr | 13.5-19 hr |
| | 更新 DI | 0.5-1 hr | **14-20 hr** |

**總計**：14-20 小時

---

## 成功指標

### 架構指標

- ✅ Domain Layer 建立，包含 15 個 UseCase
- ✅ Repository 只做資料存取（< 50 行/個）
- ✅ Domain Layer 無 Android / Room / Firebase 依賴

### 功能指標

- ✅ 核心功能正常運作（新增、打卡、刪除、同步、提醒）
- ✅ 手動測試通過（至少 10 個測試案例）

### 測試指標

- ✅ P0 UseCase 覆蓋率 100%
- ✅ 所有 UseCase 平均覆蓋率 > 80%

### 代碼品質指標

- ✅ 無重複業務邏輯（Repository 和 ViewModel 都不含業務邏輯）
- ✅ 依賴方向正確（Presentation → Domain ← Data）

---

## 附錄：UseCase 清單

| UseCase | 職責 | 依賴 Repository |
|---|---|---|
| **Habit CRUD** |
| CreateHabitUseCase | 建立習慣 + 排程提醒 | HabitRepository |
| UpdateHabitUseCase | 更新習慣 + 重排提醒 | HabitRepository |
| DeleteHabitUseCase | 刪除習慣 + 取消提醒 | HabitRepository, CheckInRepository |
| GetHabitByIdUseCase | 取得單一習慣 | HabitRepository |
| GetHabitsWithStreakUseCase | 取得習慣列表 + 計算 streak | HabitRepository, CheckInRepository |
| **CheckIn** |
| RecordCheckInUseCase | 記錄打卡 + 觸發同步 | CheckInRepository |
| UndoCheckInUseCase | 取消打卡 + 觸發同步 | CheckInRepository |
| RecordOccurrenceUseCase | 記錄發生（QUIT 模式） | CheckInRepository |
| **Streak** |
| CalculateStreakUseCase | 計算連續天數 | CheckInRepository |
| **Sync** |
| SyncHabitsUseCase | 完整同步（push + pull） | HabitRepository, CheckInRepository |
| PushPendingChangesUseCase | 上傳待同步資料 | HabitRepository, CheckInRepository |
| **Reminder** |
| ScheduleReminderUseCase | 排程提醒 | HabitRepository |
| CancelReminderUseCase | 取消提醒 | HabitRepository |

---

## 下一步建議

1. ✅ 船長審核提案
2. ✅ 確認 Phase 1 開始日期
3. ✅ Phase 1 完成後 checkpoint（確認方向正確）
4. ✅ Phase 2-3 連續執行
5. ✅ Phase 3 完成後 checkpoint（確認功能正常）
6. ✅ Phase 4 補測試
7. ✅ Phase 5 清理
8. ✅ 最終驗收
9. ✅ 合併到 main branch

**建議執行方式**：
- Phase 1-2 在 worktree 執行（隔離風險）
- Phase 3 後合併到 main（確認可用）
- Phase 4-5 在 main 直接執行（低風險）

---

**提案結束**
