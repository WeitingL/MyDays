# Feature Framework 設計文件

> 建立日期：2026-08-15  
> 作者：PMM  
> 目標：建立可複用的 Feature 框架，以 Habit 為範例

---

## 1. 概述

### 目標

建立一套標準化的 Feature 框架，讓每個新功能（habit、expense、diary、todo）都能快速套用相同架構，保持一致性、易擴展、低耦合。

### 設計原則

1. **可複用（Reusable）**：新增 feature 時，只需套用模式，不用重新設計
2. **一致性（Consistency）**：所有 feature 的 UI、資料流、檔案結構保持一致
3. **解耦（Decoupled）**：Home、Flow、Feature Page 三個進入點低耦合，可獨立開發
4. **易擴展（Extensible）**：未來加新功能（統計、搜尋）時，架構能支援

### 三個進入點

每個 feature 包含：

1. **Home Widget** — 在首頁顯示摘要 + 快速操作
2. **Flow Record** — 在 Flow 頁面顯示時間軸記錄（簡單清單）
3. **Feature Page** — 功能中心進去的完整頁面（從 Setting > Features 進入）

### 核心架構

```
┌─────────────────────────────────────────┐
│  Home Screen                            │  提供入口
│  ├─ HabitHomeWidget                     │  ← 顯示摘要
│  ├─ ExpenseHomeWidget                   │
│  └─ DiaryHomeWidget                     │
└─────────────────────────────────────────┘
                ↓
┌─────────────────────────────────────────┐
│  Flow Screen                            │  時間軸記錄
│  ├─ HabitFlowRecord                     │  ← 顯示歷史
│  ├─ ExpenseFlowRecord                   │
│  └─ DiaryFlowRecord                     │
└─────────────────────────────────────────┘
                ↓
┌─────────────────────────────────────────┐
│  Features Screen                        │  功能列表
│  └─ [Feature]Screen                     │  ← 完整功能
│      ├─ HabitScreen                     │
│      ├─ ExpenseScreen                   │
│      └─ DiaryScreen                     │
└─────────────────────────────────────────┘
```

---

## 2. 架構設計

### 2.1 Feature 標準結構

每個 feature 包含以下 4 個層次：

```
ui/[feature]/
├── widget/
│   ├── [Feature]HomeWidget.kt          # Home 頁面的 widget
│   └── [Feature]HomeWidgetContract.kt  # Widget 的 state + actions
├── record/
│   ├── [Feature]FlowRecord.kt          # Flow 頁面的記錄項
│   └── [Feature]FlowRecordContract.kt  # Record 的 data class
├── screen/
│   ├── [Feature]Screen.kt              # 功能主頁
│   └── [Feature]Display.kt             # UI 輔助函式（如格式化）
└── [Feature]ViewModel.kt               # ViewModel
```

### 2.2 資料流向

```
┌─────────────────────────────────────────┐
│  ViewModel                              │
│  ├─ widgetState: StateFlow             │ → Home Widget
│  ├─ flowRecords: StateFlow             │ → Flow Record
│  └─ items: StateFlow                   │ → Feature Screen
└─────────────────────────────────────────┘
                ↓
┌─────────────────────────────────────────┐
│  Repository                             │
│  ├─ observe[Feature]WithDetails()      │ → 提供資料流
│  └─ CRUD 操作                           │
└─────────────────────────────────────────┘
                ↓
┌─────────────────────────────────────────┐
│  Data Source                            │
│  ├─ Room (local)                        │
│  └─ Firebase (remote)                   │
└─────────────────────────────────────────┘
```

**特點**：
- ViewModel 提供 3 個 StateFlow，分別對應 3 個進入點
- Home、Flow、Feature Page 各自訂閱不同的 StateFlow
- ViewModel 是唯一的資料入口，UI 層不直接存取 Repository

---

## 3. Widget System 設計

### 3.1 Widget 規範

每個 feature 的 Home Widget 遵循以下規範：

#### Contract 定義

```kotlin
// ui/[feature]/widget/[Feature]HomeWidgetContract.kt

/**
 * [Feature] Home Widget State
 * 
 * 定義 widget 需要的資料
 */
data class [Feature]HomeWidgetState(
    val summary: String,              // 摘要資訊（如「3/5 完成」）
    val recentItems: List<[Feature]Item>,  // 最近的項目（3-5 個）
    val quickActions: List<QuickAction>?   // 快速操作（可選）
)

/**
 * [Feature] Home Widget Actions
 * 
 * 定義 widget 的互動
 */
interface [Feature]HomeWidgetActions {
    fun onViewAll()                   // 查看全部（跳轉到 Feature Screen）
    fun onQuickAction(action: Action) // 快速操作（如快速打卡、記帳）
}
```

#### UI 規範

- **尺寸**：`fillMaxWidth()`，高度自適應（最大不超過 200.dp）
- **形狀**：`RoundedCornerShape(20.dp)`
- **背景**：`Color.White.copy(alpha = 0.55f)` + 邊框 `Color.White.copy(alpha = 0.7f)`
- **內邊距**：`padding(16.dp)`
- **配色**：使用 `SettingContentColor`（統一的內容色）
- **字體**：
  - 標題：16.sp，SemiBold
  - 摘要：13.sp，alpha 0.6f
  - 內容：14.sp，Medium

#### 資料來源

從 ViewModel 的 `widgetState: StateFlow<[Feature]HomeWidgetState>` 取得。

### 3.2 Habit Widget 範例

```kotlin
// ui/habit/widget/HabitHomeWidgetContract.kt

data class HabitHomeWidgetState(
    val todayCompleted: Int,          // 今日完成數
    val todayTotal: Int,              // 今日總數
    val recentHabits: List<HabitItem> // 最近 3 個習慣
)

interface HabitHomeWidgetActions {
    fun onViewAll()                   // 查看全部習慣
    fun onQuickCheck(habitId: String) // 快速打卡
    fun onCreateHabit()               // 新增習慣
}
```

```kotlin
// ui/habit/widget/HabitHomeWidget.kt

@Composable
fun HabitHomeWidget(
    state: HabitHomeWidgetState,
    actions: HabitHomeWidgetActions,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Header: 標題 + 摘要 + 操作按鈕
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("今日習慣", color = SettingContentColor, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text("${state.todayCompleted}/${state.todayTotal} 完成", color = SettingContentColor.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            Row {
                IconButton(onClick = actions::onCreateHabit) { Icon(Icons.Default.Add) }
                TextButton(text = "全部", onClick = actions::onViewAll)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Content: 最近習慣列表（或進度環、卡片網格）
        state.recentHabits.forEach { habit ->
            HabitCompactRow(habit = habit, onCheck = { actions.onQuickCheck(habit.id) })
        }
    }
}
```

### 3.3 套用到其他 Feature

**Expense（記帳）範例**：

```kotlin
// ui/expense/widget/ExpenseHomeWidgetContract.kt

data class ExpenseHomeWidgetState(
    val todayTotal: Int,              // 今日支出總額
    val monthTotal: Int,              // 本月支出總額
    val recentTransactions: List<ExpenseItem> // 最近 3 筆交易
)

interface ExpenseHomeWidgetActions {
    fun onViewAll()
    fun onQuickAdd()                  // 快速記帳
}
```

**Diary（日記）範例**：

```kotlin
// ui/diary/widget/DiaryHomeWidgetContract.kt

data class DiaryHomeWidgetState(
    val todayWritten: Boolean,        // 今天是否已寫
    val totalDays: Int,               // 累積天數
    val recentEntry: DiaryEntry?      // 最近一篇
)

interface DiaryHomeWidgetActions {
    fun onViewAll()
    fun onWrite()                     // 寫日記
}
```

---

## 4. Flow Record System 設計

### 4.1 Flow Record 規範

每個 feature 在 Flow 頁面的記錄遵循以下規範：

#### Contract 定義

```kotlin
// ui/[feature]/record/[Feature]FlowRecordContract.kt

/**
 * [Feature] Flow Record
 * 
 * 定義時間軸記錄的資料結構
 */
data class [Feature]FlowRecord(
    val id: String,
    val timestamp: Long,              // Unix timestamp
    val type: RecordType,             // Habit / Expense / Diary / Todo
    val title: String,                // 標題（如「完成晨間運動」）
    val detail: String? = null,       // 詳細資訊（如「分類: 飲食」）
    val amount: String? = null,       // 金額（記帳專用，如「-$120」）
    val icon: ImageVector              // 類型 icon
)
```

#### UI 規範

- **形狀**：`RoundedCornerShape(20.dp)`
- **背景**：`Color.White.copy(alpha = 0.55f)`
- **佈局**：
  - 左側：圓形 icon（40.dp，背景色 = type.color.copy(alpha = 0.2f)）
  - 中間：標題 + 詳細 + 時間
  - 右側：金額（記帳專用）

#### 資料來源

從 ViewModel 的 `flowRecords: StateFlow<List<[Feature]FlowRecord>>` 取得。

### 4.2 統一的 FlowRecord Interface

為了讓 Flow 頁面能統一顯示所有 feature 的記錄，定義一個共用 interface：

```kotlin
// ui/flow/FlowRecordContract.kt

/**
 * 統一的時間軸記錄 interface
 * 
 * 所有 feature 的 FlowRecord 都實作這個 interface
 */
interface FlowRecordItem {
    val id: String
    val timestamp: Long
    val type: RecordType          // Habit / Expense / Diary / Todo
    val title: String
    val detail: String?
    val amount: String?
    val icon: ImageVector
}

enum class RecordType(val color: Color, val label: String) {
    Habit(Color(0xFF4CAF50), "習慣"),
    Expense(Color(0xFFFF9800), "記帳"),
    Diary(Color(0xFF2196F3), "日記"),
    Todo(Color(0xFF9C27B0), "待辦")
}
```

### 4.3 Habit FlowRecord 範例

```kotlin
// ui/habit/record/HabitFlowRecordContract.kt

data class HabitFlowRecord(
    override val id: String,
    override val timestamp: Long,
    override val type: RecordType = RecordType.Habit,
    override val title: String,          // 如「完成晨間運動」
    override val detail: String? = null, // 如「連續 7 天」
    override val amount: String? = null,
    override val icon: ImageVector = Icons.Default.CheckCircle,
    val habitId: String,
    val habitName: String,
    val action: HabitAction              // CheckIn / Undo / RecordOccurrence
) : FlowRecordItem

enum class HabitAction(val label: String) {
    CheckIn("完成打卡"),
    Undo("取消打卡"),
    RecordOccurrence("記錄發生")
}
```

```kotlin
// ui/habit/record/HabitFlowRecord.kt

@Composable
fun HabitFlowRecordCard(
    record: HabitFlowRecord,
    modifier: Modifier = Modifier
) {
    FlowRecordCardLayout(
        icon = {
            Icon(
                imageVector = record.icon,
                contentDescription = record.habitName,
                tint = RecordType.Habit.color,
                modifier = Modifier.size(24.dp)
            )
        },
        title = record.title,
        detail = record.detail,
        time = formatTimestamp(record.timestamp),
        amount = null,
        modifier = modifier
    )
}

// 共用的 FlowRecordCardLayout（所有 feature 都用這個）
@Composable
private fun FlowRecordCardLayout(
    icon: @Composable () -> Unit,
    title: String,
    detail: String?,
    time: String,
    amount: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left: Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(RecordType.Habit.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        
        // Center: Content
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = SettingContentColor, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            if (detail != null) Text(detail, color = SettingContentColor.copy(alpha = 0.6f), fontSize = 13.sp)
            Text(time, color = SettingContentColor.copy(alpha = 0.5f), fontSize = 12.sp)
        }
        
        // Right: Amount (optional)
        if (amount != null) {
            Text(amount, color = if (amount.startsWith("-")) Color(0xFFE53935) else Color(0xFF4CAF50), fontSize = 17.sp, fontWeight = FontWeight.Bold)
        }
    }
}
```

### 4.4 套用到其他 Feature

**Expense FlowRecord**：

```kotlin
data class ExpenseFlowRecord(
    override val id: String,
    override val timestamp: Long,
    override val type: RecordType = RecordType.Expense,
    override val title: String,          // 如「午餐」
    override val detail: String? = null, // 如「分類: 飲食」
    override val amount: String,         // 如「-$120」
    override val icon: ImageVector = Icons.Default.AttachMoney,
    val expenseId: String,
    val category: String
) : FlowRecordItem
```

**Diary FlowRecord**：

```kotlin
data class DiaryFlowRecord(
    override val id: String,
    override val timestamp: Long,
    override val type: RecordType = RecordType.Diary,
    override val title: String,          // 如「今天的心情」
    override val detail: String? = null, // 日記預覽（前 50 字）
    override val amount: String? = null,
    override val icon: ImageVector = Icons.Default.Edit,
    val diaryId: String
) : FlowRecordItem
```

---

## 5. Feature Page Pattern 設計

### 5.1 Feature Page 規範

每個 feature 的主頁面遵循以下結構：

#### 導航路徑

```
Setting > Features > [Feature]
```

#### Screen 結構

```kotlin
@Composable
fun [Feature]Screen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: [Feature]ViewModel = koinViewModel()
) {
    val items by viewModel.items.collectAsState()
    var editorTarget by remember { mutableStateOf<[Feature]EditorState?>(null) }
    
    SubScreenScaffold(
        title = "[Feature 名稱]",
        onBack = onBack,
        modifier = modifier
    ) {
        // 1. 新增按鈕
        SettingGroup {
            SettingItem(
                icon = Icons.Default.Add,
                title = "新增 [Feature]",
                onClick = { editorTarget = [Feature]EditorState() }
            )
        }
        
        // 2. 項目列表
        if (items.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            SettingSectionHeader("我的 [Feature]")
            SettingGroup {
                items.forEachIndexed { index, item ->
                    [Feature]Row(
                        item = item,
                        onEdit = { editorTarget = [Feature]EditorState(item) },
                        onDelete = { viewModel.delete(item.id) }
                    )
                    if (index != items.lastIndex) SettingDivider()
                }
            }
        }
    }
    
    // 3. 編輯 dialog
    editorTarget?.let { state ->
        [Feature]EditorDialog(
            state = state,
            onDismiss = { editorTarget = null },
            onConfirm = { /* ... */ }
        )
    }
}
```

### 5.2 Habit Screen 範例

當前的 `HabitScreen.kt` 已經很接近標準模式：

```kotlin
@Composable
fun HabitScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HabitViewModel = koinViewModel()
) {
    val habits by viewModel.habits.collectAsState()
    var editorTarget by remember { mutableStateOf<HabitEditorState?>(null) }

    SubScreenScaffold(title = "習慣養成", onBack = onBack, modifier = modifier) {
        // 1. 新增按鈕
        SettingGroup {
            SettingItem(icon = Icons.Default.Add, title = "新增習慣", onClick = { editorTarget = HabitEditorState() })
        }

        // 2. 習慣列表
        if (habits.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            SettingSectionHeader("我的習慣")
            SettingGroup {
                habits.forEachIndexed { index, item ->
                    HabitRow(
                        item = item,
                        onEdit = { editorTarget = HabitEditorState(item.habit) },
                        onToggleBuild = { viewModel.toggleTodayCheckIn(item.habit.id, item.completedToday) },
                        onRecordQuit = { viewModel.recordOccurrence(item.habit.id) },
                        onDelete = { viewModel.deleteHabit(item.habit.id) }
                    )
                    if (index != habits.lastIndex) SettingDivider()
                }
            }
        }
    }

    // 3. 編輯 dialog
    editorTarget?.let { state ->
        HabitEditorDialog(
            state = state,
            onDismiss = { editorTarget = null },
            onConfirm = { name, type, reminderMinuteOfDay ->
                val existing = state.habit
                if (existing == null) {
                    viewModel.addHabit(name, type, reminderMinuteOfDay)
                } else {
                    viewModel.updateHabit(existing.copy(name = name, type = type, reminderMinuteOfDay = reminderMinuteOfDay))
                }
                editorTarget = null
            }
        )
    }
}
```

### 5.3 套用到其他 Feature

**Expense Screen**：

```kotlin
@Composable
fun ExpenseScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseViewModel = koinViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    var editorTarget by remember { mutableStateOf<ExpenseEditorState?>(null) }

    SubScreenScaffold(title = "記帳", onBack = onBack, modifier = modifier) {
        SettingGroup {
            SettingItem(icon = Icons.Default.Add, title = "新增支出", onClick = { editorTarget = ExpenseEditorState() })
        }

        if (expenses.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            SettingSectionHeader("本月記錄")
            SettingGroup {
                expenses.forEachIndexed { index, item ->
                    ExpenseRow(
                        item = item,
                        onEdit = { editorTarget = ExpenseEditorState(item) },
                        onDelete = { viewModel.delete(item.id) }
                    )
                    if (index != expenses.lastIndex) SettingDivider()
                }
            }
        }
    }

    editorTarget?.let { state ->
        ExpenseEditorDialog(/* ... */)
    }
}
```

**共同模式**：
1. 新增按鈕在頂部
2. 列表放在中間，用 `SettingGroup` 包裹
3. 編輯 dialog 在最下方
4. 使用 `SubScreenScaffold` 統一頂部 bar

---

## 6. ViewModel Pattern 設計

### 6.1 ViewModel 規範

每個 feature 的 ViewModel 提供以下 3 個 StateFlow：

```kotlin
class [Feature]ViewModel(
    private val repository: [Feature]Repository
) : ViewModel() {

    // 1. 給 Home Widget 用
    val widgetState: StateFlow<[Feature]HomeWidgetState> = 
        repository.observe[Feature]WithDetails()
            .map { items -> 
                [Feature]HomeWidgetState(
                    summary = calculateSummary(items),
                    recentItems = items.take(3)
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), [Feature]HomeWidgetState.Empty)

    // 2. 給 Flow 用
    val flowRecords: StateFlow<List<[Feature]FlowRecord>> = 
        repository.observe[Feature]Records()
            .map { records -> records.map { it.toFlowRecord() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // 3. 給 Feature Screen 用
    val items: StateFlow<List<[Feature]Item>> = 
        repository.observe[Feature]WithDetails()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // CRUD 操作
    fun create(...) { viewModelScope.launch { repository.add(...) } }
    fun update(...) { viewModelScope.launch { repository.update(...) } }
    fun delete(id: String) { viewModelScope.launch { repository.delete(id) } }
}
```

### 6.2 Habit ViewModel 範例

當前的 `HabitViewModel.kt` 只有 `habits`，需要擴充：

```kotlin
class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    // 1. 給 Home Widget 用
    val widgetState: StateFlow<HabitHomeWidgetState> = 
        repository.observeHabitsWithStreak()
            .map { habits ->
                HabitHomeWidgetState(
                    todayCompleted = habits.count { it.completedToday },
                    todayTotal = habits.size,
                    recentHabits = habits.take(3).map { it.toHabitItem() }
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitHomeWidgetState.Empty)

    // 2. 給 Flow 用
    val flowRecords: StateFlow<List<HabitFlowRecord>> = 
        repository.observeCheckInRecords()
            .map { records -> records.map { it.toFlowRecord() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // 3. 給 Habit Screen 用（已存在）
    val habits: StateFlow<List<HabitWithStreak>> = 
        repository.observeHabitsWithStreak()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // CRUD 操作（已存在）
    fun addHabit(name: String, type: HabitType, reminderMinuteOfDay: Int?) {
        viewModelScope.launch { repository.add(name, type, reminderMinuteOfDay) }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch { repository.update(habit) }
    }

    fun deleteHabit(id: String) {
        viewModelScope.launch { repository.delete(id) }
    }

    fun toggleTodayCheckIn(habitId: String, completedToday: Boolean) {
        viewModelScope.launch {
            if (completedToday) repository.undoTodayCheckIn(habitId)
            else repository.checkInToday(habitId)
        }
    }

    fun recordOccurrence(habitId: String) {
        viewModelScope.launch { repository.recordOccurrence(habitId) }
    }
}

// 輔助擴充函式
private fun HabitWithStreak.toHabitItem(): HabitItem {
    return HabitItem(
        id = habit.id,
        name = habit.name,
        type = habit.type,
        streak = streak,
        completedToday = completedToday,
        reminderEnabled = habit.reminderMinuteOfDay != null,
        reminderTime = habit.reminderMinuteOfDay?.let { formatTime(it) }
    )
}
```

### 6.3 套用到其他 Feature

**Expense ViewModel**：

```kotlin
class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val widgetState: StateFlow<ExpenseHomeWidgetState> = 
        repository.observeExpenses()
            .map { expenses ->
                ExpenseHomeWidgetState(
                    todayTotal = expenses.filter { it.isToday() }.sumOf { it.amount },
                    monthTotal = expenses.filter { it.isThisMonth() }.sumOf { it.amount },
                    recentTransactions = expenses.take(3)
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExpenseHomeWidgetState.Empty)

    val flowRecords: StateFlow<List<ExpenseFlowRecord>> = 
        repository.observeExpenses()
            .map { expenses -> expenses.map { it.toFlowRecord() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val expenses: StateFlow<List<Expense>> = 
        repository.observeExpenses()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun create(...) { /* ... */ }
    fun update(...) { /* ... */ }
    fun delete(id: String) { /* ... */ }
}
```

---

## 7. 命名規範

### 7.1 檔案命名

| 類型 | 命名格式 | 範例 |
|---|---|---|
| Widget | `[Feature]HomeWidget.kt` | `HabitHomeWidget.kt` |
| Widget Contract | `[Feature]HomeWidgetContract.kt` | `HabitHomeWidgetContract.kt` |
| Flow Record | `[Feature]FlowRecord.kt` | `HabitFlowRecord.kt` |
| Flow Record Contract | `[Feature]FlowRecordContract.kt` | `HabitFlowRecordContract.kt` |
| Screen | `[Feature]Screen.kt` | `HabitScreen.kt` |
| Display | `[Feature]Display.kt` | `HabitDisplay.kt` |
| ViewModel | `[Feature]ViewModel.kt` | `HabitViewModel.kt` |

### 7.2 Data Class 命名

| 類型 | 命名格式 | 範例 |
|---|---|---|
| Widget State | `[Feature]HomeWidgetState` | `HabitHomeWidgetState` |
| Widget Actions | `[Feature]HomeWidgetActions` | `HabitHomeWidgetActions` |
| Flow Record | `[Feature]FlowRecord` | `HabitFlowRecord` |
| Item | `[Feature]Item` | `HabitItem` |
| Editor State | `[Feature]EditorState` | `HabitEditorState` |

### 7.3 Function 命名

| 類型 | 命名格式 | 範例 |
|---|---|---|
| Widget Composable | `[Feature]HomeWidget()` | `HabitHomeWidget()` |
| Flow Record Composable | `[Feature]FlowRecordCard()` | `HabitFlowRecordCard()` |
| Screen Composable | `[Feature]Screen()` | `HabitScreen()` |
| Row Composable | `[Feature]Row()` | `HabitRow()` |
| Editor Dialog | `[Feature]EditorDialog()` | `HabitEditorDialog()` |

---

## 8. Habit 實作範例

### 8.1 完整檔案清單

```
app/src/main/java/com/weiting/mydays/
├── ui/
│   ├── habit/
│   │   ├── widget/
│   │   │   ├── HabitHomeWidget.kt           # Home widget UI
│   │   │   └── HabitHomeWidgetContract.kt   # State + Actions
│   │   ├── record/
│   │   │   ├── HabitFlowRecord.kt           # Flow record UI
│   │   │   └── HabitFlowRecordContract.kt   # Data class
│   │   ├── screen/
│   │   │   ├── HabitScreen.kt               # 功能主頁（已存在）
│   │   │   └── HabitDisplay.kt              # 格式化函式（已存在）
│   │   └── HabitViewModel.kt                # ViewModel（需擴充）
│   ├── home/
│   │   └── HomeScreen.kt                    # 排版所有 widgets
│   └── flow/
│       └── FlowScreen.kt                    # 排版所有 records（已存在）
└── data/
    └── habit/
        ├── Habit.kt                         # Domain model（已存在）
        ├── HabitRepository.kt               # Interface（已存在）
        ├── HabitRepositoryImpl.kt           # 實作（已存在）
        └── HabitWithStreak.kt               # Domain model（已存在）
```

### 8.2 檔案職責

| 檔案 | 職責 | 依賴 |
|---|---|---|
| `HabitHomeWidget.kt` | 渲染 Home widget UI | `HabitHomeWidgetState`, `HabitHomeWidgetActions` |
| `HabitHomeWidgetContract.kt` | 定義 widget state + actions | `HabitItem` |
| `HabitFlowRecord.kt` | 渲染 Flow record UI | `HabitFlowRecord` |
| `HabitFlowRecordContract.kt` | 定義 flow record 資料結構 | `FlowRecordItem` |
| `HabitScreen.kt` | 功能主頁 UI | `HabitViewModel` |
| `HabitDisplay.kt` | 格式化函式（streak 文字、里程碑） | `HabitType` |
| `HabitViewModel.kt` | 提供 3 個 StateFlow | `HabitRepository` |
| `HomeScreen.kt` | 排版所有 feature widgets | 所有 `[Feature]HomeWidget` |
| `FlowScreen.kt` | 排版所有 feature records | 所有 `[Feature]FlowRecord` |

### 8.3 資料流向圖

```
┌─────────────────────────────────────────┐
│  HomeScreen                             │
│  ├─ HabitHomeWidget                     │
│  │  ↑                                   │
│  │  └─ widgetState: StateFlow           │
│  │                                       │
│  ├─ ExpenseHomeWidget                   │
│  └─ DiaryHomeWidget                     │
└─────────────────────────────────────────┘
                ↓
┌─────────────────────────────────────────┐
│  HabitViewModel                         │
│  ├─ widgetState: StateFlow              │ ← observeHabitsWithStreak()
│  ├─ flowRecords: StateFlow              │ ← observeCheckInRecords()
│  └─ habits: StateFlow                   │ ← observeHabitsWithStreak()
└─────────────────────────────────────────┘
                ↓
┌─────────────────────────────────────────┐
│  HabitRepository                        │
│  ├─ observeHabitsWithStreak(): Flow    │
│  └─ observeCheckInRecords(): Flow      │
└─────────────────────────────────────────┘
                ↓
┌─────────────────────────────────────────┐
│  Room / Firebase                        │
└─────────────────────────────────────────┘
```

### 8.4 UI 規範

#### Home Widget

- **變體**：支援 3 種 UI 變體（Card Grid / Progress Ring / Compact List）
- **尺寸**：`fillMaxWidth()`，高度自適應（約 150-180.dp）
- **內容**：
  - Header：標題（今日習慣）+ 摘要（3/5 完成）+ 新增/全部按鈕
  - Body：最近 3 個習慣 + 快速打卡按鈕

#### Flow Record

- **尺寸**：`fillMaxWidth()`，高度自適應（約 80-100.dp）
- **佈局**：
  - 左側：圓形 icon（Habit = CheckCircle，綠色）
  - 中間：標題（完成晨間運動）+ 詳細（連續 7 天）+ 時間（今天 08:30）
  - 右側：無金額

#### Feature Screen

- **佈局**：
  - 頂部：新增按鈕（SettingItem）
  - 中間：習慣列表（SettingGroup + HabitRow）
  - Row 內容：icon + 名稱 + streak + 打卡按鈕 + 刪除按鈕

---

## 9. 擴展到 Expense

### 9.1 檔案清單（套用命名規範）

```
ui/expense/
├── widget/
│   ├── ExpenseHomeWidget.kt
│   └── ExpenseHomeWidgetContract.kt
├── record/
│   ├── ExpenseFlowRecord.kt
│   └── ExpenseFlowRecordContract.kt
├── screen/
│   ├── ExpenseScreen.kt
│   └── ExpenseDisplay.kt
└── ExpenseViewModel.kt
```

### 9.2 需要建立的內容

#### Widget State

```kotlin
// ui/expense/widget/ExpenseHomeWidgetContract.kt

data class ExpenseHomeWidgetState(
    val todayTotal: Int,              // 今日支出總額
    val monthTotal: Int,              // 本月支出總額
    val recentTransactions: List<ExpenseItem> // 最近 3 筆
)

interface ExpenseHomeWidgetActions {
    fun onViewAll()
    fun onQuickAdd()                  // 快速記帳
}

data class ExpenseItem(
    val id: String,
    val title: String,                // 如「午餐」
    val category: String,             // 如「飲食」
    val amount: Int,                  // -120
    val timestamp: Long
)
```

#### Flow Record

```kotlin
// ui/expense/record/ExpenseFlowRecordContract.kt

data class ExpenseFlowRecord(
    override val id: String,
    override val timestamp: Long,
    override val type: RecordType = RecordType.Expense,
    override val title: String,          // 如「午餐」
    override val detail: String? = "分類: $category",
    override val amount: String,         // 如「-$120」
    override val icon: ImageVector = Icons.Default.AttachMoney,
    val expenseId: String,
    val category: String
) : FlowRecordItem
```

#### Screen

```kotlin
// ui/expense/screen/ExpenseScreen.kt

@Composable
fun ExpenseScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpenseViewModel = koinViewModel()
) {
    val expenses by viewModel.expenses.collectAsState()
    var editorTarget by remember { mutableStateOf<ExpenseEditorState?>(null) }

    SubScreenScaffold(title = "記帳", onBack = onBack, modifier = modifier) {
        SettingGroup {
            SettingItem(icon = Icons.Default.Add, title = "新增支出", onClick = { editorTarget = ExpenseEditorState() })
        }

        if (expenses.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            SettingSectionHeader("本月記錄")
            SettingGroup {
                expenses.forEachIndexed { index, item ->
                    ExpenseRow(item = item, onEdit = { editorTarget = ExpenseEditorState(item) }, onDelete = { viewModel.delete(item.id) })
                    if (index != expenses.lastIndex) SettingDivider()
                }
            }
        }
    }

    editorTarget?.let { state -> ExpenseEditorDialog(/* ... */) }
}
```

### 9.3 哪些部分是「複製 + 改名」

| 部分 | 操作 |
|---|---|
| 檔案結構 | 複製 `ui/habit/` → `ui/expense/`，重新命名 |
| Widget Contract | 複製 `HabitHomeWidgetContract.kt`，改 data class 名稱和欄位 |
| Widget UI | 複製 `HabitHomeWidget.kt`，改 Composable 名稱和內容 |
| Flow Record Contract | 複製 `HabitFlowRecordContract.kt`，實作 `FlowRecordItem` |
| Flow Record UI | **不用複製**，用共用的 `FlowRecordCardLayout` |
| Screen | 複製 `HabitScreen.kt`，改 title、ViewModel、Row |
| ViewModel | 複製 `HabitViewModel.kt`，改 Repository、State 映射 |

### 9.4 哪些需要客製化

| 部分 | 客製化內容 |
|---|---|
| Widget State | 欄位不同（todayTotal、monthTotal vs todayCompleted、todayTotal） |
| Flow Record | 多了 `amount` 欄位（右側顯示金額） |
| Row UI | 不用打卡按鈕，改成顯示金額 + 分類 |
| Editor Dialog | 輸入欄位不同（金額、分類 vs 名稱、模式） |

---

## 10. 未來擴展

### 10.1 Phase 2 功能（預留架構）

#### 統計功能

每個 feature 都有統計頁，路徑：`Features > [Feature] > Statistics`

**預留架構**：

```kotlin
// ui/[feature]/statistics/[Feature]StatisticsScreen.kt

@Composable
fun [Feature]StatisticsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: [Feature]ViewModel = koinViewModel()
) {
    val statistics by viewModel.statistics.collectAsState()
    
    SubScreenScaffold(title = "[Feature] 統計", onBack = onBack, modifier = modifier) {
        // 圖表、趨勢、里程碑
    }
}
```

**ViewModel 擴充**：

```kotlin
// 新增 StateFlow
val statistics: StateFlow<[Feature]Statistics> = 
    repository.observe[Feature]Statistics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), [Feature]Statistics.Empty)
```

#### 搜尋功能

Flow 頁面加搜尋框，過濾所有 feature 的記錄。

**預留架構**：

```kotlin
// ui/flow/FlowViewModel.kt

class FlowViewModel(
    private val habitViewModel: HabitViewModel,
    private val expenseViewModel: ExpenseViewModel,
    private val diaryViewModel: DiaryViewModel
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    val filteredRecords: StateFlow<List<FlowRecordItem>> = 
        combine(
            habitViewModel.flowRecords,
            expenseViewModel.flowRecords,
            diaryViewModel.flowRecords,
            searchQuery
        ) { habit, expense, diary, query ->
            (habit + expense + diary)
                .filter { it.title.contains(query, ignoreCase = true) }
                .sortedByDescending { it.timestamp }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
```

#### Widget 客製化

Home 頁面可以開關 widget、調整排序。

**預留架構**：

```kotlin
// data/preferences/WidgetPreferences.kt

data class WidgetPreferences(
    val enabledWidgets: Set<FeatureType>,  // [Habit, Expense, Diary]
    val widgetOrder: List<FeatureType>     // [Habit, Diary, Expense]
)

// ui/home/HomeScreen.kt

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val preferences by viewModel.widgetPreferences.collectAsState()
    
    LazyColumn {
        preferences.widgetOrder.forEach { feature ->
            if (preferences.enabledWidgets.contains(feature)) {
                item {
                    when (feature) {
                        FeatureType.Habit -> HabitHomeWidget(/* ... */)
                        FeatureType.Expense -> ExpenseHomeWidget(/* ... */)
                        FeatureType.Diary -> DiaryHomeWidget(/* ... */)
                    }
                }
            }
        }
    }
}
```

#### Feature 之間的關聯

例如：記帳關聯到習慣（「今天因為運動買了運動飲料」）。

**預留架構**：

```kotlin
// domain/model/FeatureLink.kt

data class FeatureLink(
    val id: String,
    val sourceType: FeatureType,
    val sourceId: String,
    val targetType: FeatureType,
    val targetId: String,
    val relation: LinkRelation
)

enum class LinkRelation {
    RelatedTo,      // 相關
    TriggeredBy,    // 觸發
    PartOf          // 屬於
}

// 範例：記帳關聯到習慣
FeatureLink(
    id = "...",
    sourceType = FeatureType.Expense,
    sourceId = "expense-001",
    targetType = FeatureType.Habit,
    targetId = "habit-001",
    relation = LinkRelation.TriggeredBy
)
```

### 10.2 架構如何支援這些擴展

| 擴展功能 | 當前架構支援 | 需要新增 |
|---|---|---|
| **統計頁** | ViewModel 已分離，可直接加 `statistics: StateFlow` | 統計計算邏輯（UseCase） |
| **搜尋** | FlowRecordItem interface 統一，可合併過濾 | FlowViewModel + 搜尋 UI |
| **Widget 客製化** | Home 已是獨立排版，可動態調整順序 | WidgetPreferences + 設定 UI |
| **Feature 關聯** | 每個 feature 有獨立 ID，可建立關聯 | FeatureLink model + 關聯 UI |

**關鍵設計**：
- ViewModel 提供多個 StateFlow，方便擴充新的資料流
- FlowRecordItem interface 統一，方便合併、過濾、搜尋
- Home 和 Flow 都是獨立排版，不綁死特定 feature
- 每個 feature 有獨立 ViewModel，可隨時加新功能（統計、匯出）

---

## 11. 總結

### 11.1 核心模式

**3 個進入點 = 3 個 StateFlow**：

```
HomeWidget    ← widgetState: StateFlow<[Feature]HomeWidgetState>
FlowRecord    ← flowRecords: StateFlow<List<[Feature]FlowRecord>>
FeatureScreen ← items: StateFlow<List<[Feature]Item>>
```

**統一的命名規範**：

```
ui/[feature]/
├── widget/[Feature]HomeWidget.kt
├── record/[Feature]FlowRecord.kt
├── screen/[Feature]Screen.kt
└── [Feature]ViewModel.kt
```

**共用的 UI 元件**：

- `FlowRecordCardLayout`（所有 feature 用同一個）
- `SubScreenScaffold`（所有 feature screen 用同一個）
- `SettingGroup`、`SettingItem`（統一的列表樣式）

### 11.2 套用流程

**新增一個 feature（以 Todo 為例）**：

1. **複製檔案結構**：
   ```bash
   cp -r ui/habit ui/todo
   ```

2. **重新命名檔案**：
   ```
   HabitHomeWidget.kt → TodoHomeWidget.kt
   HabitViewModel.kt → TodoViewModel.kt
   ...
   ```

3. **修改 State 定義**：
   ```kotlin
   data class TodoHomeWidgetState(
       val todayCompleted: Int,
       val todayTotal: Int,
       val recentTodos: List<TodoItem>
   )
   ```

4. **實作 Repository**：
   ```kotlin
   interface TodoRepository {
       fun observeTodos(): Flow<List<Todo>>
       suspend fun add(...): Unit
       suspend fun update(...): Unit
       suspend fun delete(id: String): Unit
   }
   ```

5. **註冊 Koin**：
   ```kotlin
   single { TodoRepository(...) }
   viewModel { TodoViewModel(get()) }
   ```

6. **加入 Home**：
   ```kotlin
   // ui/home/HomeScreen.kt
   TodoHomeWidget(
       state = todoViewModel.widgetState.collectAsState(),
       actions = object : TodoHomeWidgetActions { /* ... */ }
   )
   ```

7. **加入 Flow**：
   ```kotlin
   // ui/flow/FlowScreen.kt
   items(todoViewModel.flowRecords.collectAsState()) { record ->
       TodoFlowRecordCard(record = record)
   }
   ```

8. **加入導航**：
   ```kotlin
   // ui/features/FeaturesScreen.kt
   SettingItem(
       icon = Icons.Default.CheckBox,
       title = "待辦事項",
       onClick = { navController.navigate("features/todo") }
   )
   ```

**預估時間**：
- 複製 + 重新命名：30 分鐘
- 修改 State 定義：1 小時
- 實作 Repository：2-3 小時
- 接線（Home、Flow、導航）：1 小時
- **總計**：4.5-5.5 小時

### 11.3 成功指標

- ✅ 所有 feature 的檔案結構一致
- ✅ 命名規範統一，易於辨識
- ✅ Home、Flow、Feature Page 三個進入點低耦合
- ✅ 新增 feature 時間 < 6 小時
- ✅ UI 風格統一（Liquid Glass）

---

## 12. 下一步建議

### Phase 1：重構 Habit（套用框架）

**目標**：將現有 habit 重構為標準 feature 框架

**要做的事**：
1. 建立 `ui/habit/widget/`
2. 建立 `ui/habit/record/`
3. HabitViewModel 擴充 3 個 StateFlow
4. Home 和 Flow 接線

**工時**：3-4 小時

**驗證**：
- HabitHomeWidget 在 Home 顯示
- HabitFlowRecord 在 Flow 顯示
- 功能正常運作（打卡、刪除）

---

### Phase 2：實作 Expense（驗證框架）

**目標**：套用框架實作第二個 feature

**要做的事**：
1. 複製 habit 結構 → expense
2. 修改 State 定義
3. 實作 Repository
4. 接線（Home、Flow、導航）

**工時**：5-6 小時

**驗證**：
- ExpenseHomeWidget 在 Home 顯示
- ExpenseFlowRecord 在 Flow 顯示
- 功能正常運作（新增、刪除）

---

### Phase 3：文件化 + 模板

**目標**：建立 feature template，加速後續開發

**要做的事**：
1. 建立 `ui/_template/` 資料夾
2. 提供完整的 template 檔案（含註解）
3. 寫 README：如何套用 template

**工時**：2-3 小時

**驗證**：
- 套用 template 建立 Todo feature < 5 小時

---

**提案完成**
