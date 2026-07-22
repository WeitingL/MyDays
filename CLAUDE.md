# MyDays — Project Context

## Overview

個人日記 / 記帳 / 習慣追蹤的 Android 應用程式，採用 Liquid Glass 視覺風格。

## Tech Stack

| Layer | Library |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Auth | Firebase Authentication（Google Sign-In） |
| Navigation | Compose Navigation |
| DI | Koin（Auth 與 Habit 已遷移） |

## Architecture

MVVM + Clean Architecture（尚在早期建構中）

```
app/
└── src/main/java/com/weiting/mydays/
    ├── MainActivity.kt          # Entry point，頂層 NavHost（login / main）
    ├── data/
    │   └── auth/
    │       └── AuthRepository.kt
    ├── ui/
    │   ├── auth/                # 登入頁 + AuthViewModel
    │   ├── component/           # 共用 UI 元件
    │   ├── features/            # 功能頁（含子頁導航 FeaturesNavHost）
    │   ├── home/
    │   ├── main/                # MainScreen：4-tab 根容器
    │   ├── profile/
    │   ├── records/
    │   ├── theme/
    │   └── todo/
    └── ...
```

## Navigation Structure

```
MyDaysNavHost
├── login  → LoginScreen
└── main   → MainScreen（4 tabs）
    ├── 0: HomeScreen
    ├── 1: RecordsScreen
    ├── 2: FeaturesNavHost
    │   ├── features（FeaturesScreen）
    │   └── features/todo（TodoScreen）
    └── 3: ProfileScreen
```

## Key UI Components

- **MainScaffold** — 包住整體 scaffold，管理 blur-transition 背景切換與 nav bar 顯示控制
- **NavigationBar** — Liquid Glass 底部導覽列，依 tab 切換配色主題
- **AppBar** — 玻璃質感頂部 bar（`SubScreenScaffold` 用）
- **Background** — 依 tab index 渲染對應漸層背景
- **FeatureCard** — 功能入口卡片
- **SettingItem** — Profile 頁設定列表 row

## Build & Run

```bash
./gradlew :app:assembleDebug
```

```bash
/Users/weiting/Library/Android/sdk/platform-tools/adb install app/build/outputs/apk/debug/app-debug.apk
```

## Design Docs

討論記錄與設計文件存放於 `.claude/doc/`。
