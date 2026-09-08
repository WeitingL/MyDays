# 習慣功能 UI Preview

完整的習慣養成功能 UI 設計預覽系統，提供多種設計變體供選擇。

## 檔案結構

```
habit/
├── HabitFlowContract.kt              # Interface 定義與 Mock Data
├── HomeHabitWidgetVariants.kt        # Home 頁習慣 widget（3 個變體）
├── HabitListVariants.kt              # 習慣列表頁（3 個變體）
├── HabitFormVariants.kt              # 建立/編輯表單（3 個變體）
└── HabitCompleteFlow.kt              # 完整流程組合（3 個流程）
```

## 頁面流程

1. **Home Widget** → 建立習慣 → 表單
2. **Home Widget** → 查看全部 → 習慣列表
3. **習慣列表** → 點擊習慣 → 編輯表單
4. **表單** → 儲存/刪除 → 返回

## 設計變體

### Home Habit Widget（3 個版本）

- **Version A - Card Grid**: 卡片網格，顯示前 3 個習慣
- **Version B - Progress Ring**: 圓環進度 + 列表
- **Version C - Compact List**: 緊湊列表 + 進度條

### Habit List（3 個版本）

- **Version A - Card List**: Liquid Glass 卡片列表
- **Version B - Grouped List**: 按狀態分組（已完成/進行中）
- **Version C - Minimal List**: 極簡列表，無卡片背景

### Habit Form（3 個版本）

- **Version A - Dialog**: 浮動 dialog 風格
- **Version B - Full Screen**: 全螢幕表單，更多空間
- **Version C - Bottom Sheet**: 底部彈出表單

## 完整流程 Preview（3 個組合）

### 1. Dialog Forms Flow
- Home Widget: Compact List
- Habit List: Card Style
- Forms: Dialog Style

### 2. Full Screen Forms Flow
- Home Widget: Progress Ring
- Habit List: Grouped Style
- Forms: Full Screen Style

### 3. Mixed Styles Flow
- Home Widget: Card Grid
- Habit List: Minimal Style
- Forms: Bottom Sheet Style

## 如何使用

### 在 Android Studio 中預覽

1. 開啟任一檔案（例如 `HabitCompleteFlow.kt`）
2. 找到 `@Preview` 的 Composable function
3. 點擊左側的預覽圖示
4. 在 Preview 面板中查看 UI

### 查看所有變體

每個檔案都包含多個 `@Preview`：

```kotlin
// HomeHabitWidgetVariants.kt
@Preview HomeHabitWidgetCardGridPreview()
@Preview HomeHabitWidgetProgressRingPreview()
@Preview HomeHabitWidgetCompactListPreview()

// HabitListVariants.kt
@Preview HabitListCardStylePreview()
@Preview HabitListGroupedStylePreview()
@Preview HabitListMinimalStylePreview()

// HabitFormVariants.kt
@Preview HabitFormDialogCreatePreview()
@Preview HabitFormDialogEditPreview()
@Preview HabitFormFullScreenCreatePreview()
@Preview HabitFormFullScreenEditPreview()
@Preview HabitFormBottomSheetCreatePreview()
@Preview HabitFormBottomSheetEditPreview()

// HabitCompleteFlow.kt
@Preview HabitCompleteFlowDialogPreview()
@Preview HabitCompleteFlowFullScreenPreview()
@Preview HabitFlowMixedStylesPreview()
```

### 測試完整流程

開啟 `HabitCompleteFlow.kt`，三個完整流程 preview 都可以：
- 點擊按鈕導航到不同頁面
- 建立/編輯/刪除習慣
- 打卡完成習慣
- 切換習慣類型（養成/戒除）

## Mock Data

所有 preview 都使用 `HabitMockData` 產生測試資料：

```kotlin
// 5 個假習慣
- 晨間運動（BUILD, 7 天, 已完成）
- 閱讀 30 分鐘（BUILD, 3 天, 未完成）
- 戒糖飲（QUIT, 14 天, 已完成）
- 冥想 10 分鐘（BUILD, 30 天, 已完成）
- 戒宵夜（QUIT, 5 天, 未完成）
```

## 技術細節

### 使用的元件

- `GlassDialog` - Liquid Glass 風格 dialog
- `GlassTextField` - 玻璃質感輸入框
- `GlassChoiceChip` - 選擇 chip
- `SettingGroup`, `SettingItem` - 設定列表元件
- `PreviewNavigator` - Preview navigation 系統

### Preview Navigation

使用 `PreviewNavigator` 實現頁面導航：

```kotlin
PreviewNavigator(initialScreen = "home", showDebugBar = true) { 
    currentScreen, navigateTo, onBack ->
    when {
        currentScreen == "home" -> HomeScreen(...)
        currentScreen == "habit_list" -> ListScreen(...)
        currentScreen.startsWith("habit_edit/") -> EditScreen(...)
    }
}
```

## 轉換到正式 App

這些 preview 元件都遵循 Contract 定義，可以直接搬到正式 app：

1. 複製想要的變體到 `ui/habit/`
2. 接上 ViewModel 的 state 和 actions
3. 移除 mock data，使用真實資料

範例：

```kotlin
// Preview
HomeHabitWidgetCardGrid(
    state = HabitMockData.createHomeWidgetState(),
    actions = object : HomeHabitWidgetActions { ... }
)

// Production
HomeHabitWidgetCardGrid(
    state = viewModel.widgetState.collectAsState().value,
    actions = viewModel
)
```

## 建議的選擇

依據不同情境，建議的組合：

### 簡潔風格
- Home Widget: Compact List
- Habit List: Minimal Style
- Forms: Bottom Sheet

### 資訊豐富
- Home Widget: Progress Ring
- Habit List: Card Style
- Forms: Dialog

### 全功能
- Home Widget: Card Grid
- Habit List: Grouped Style
- Forms: Full Screen
