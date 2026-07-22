# Habit CRUD & 頻率設定

> 所屬 Feature：`doc/feature/2026-07-22_habit.md`
> 狀態：草稿
> Jira：___

---

## Overview

> 由我填寫。

定義習慣的建立 / 編輯 / 刪除，以及頻率設定與底層資料模型（Phase 1）。

範圍包含：CRUD、習慣類型（好習慣養成 / 壞習慣戒除）、頻率四模式（每日 / 每 N 天 / 每月指定日 / 每週指定星期）、Habit 資料模型（Room entity）。
不包含：打卡與 streak（見 `habit-checkin-streak`）、雲端同步（見 `habit-sync`）、輪替型習慣（見 `habit-rotation-component`）。

---

## Requirements

> 由我填寫。列出具體的功能需求，以使用者可觀察的行為描述。

1. ___

---

## Framework

> 由 Agent 填寫。對照現有 codebase / 架構。

- ___

---

## Open Questions

> 由 Agent 填寫。每項附方案與建議供選擇。

1. **頻率模式的資料如何統一表示**
   - 方案 A：單一 enum（FrequencyType）+ 參數欄（interval、weekdays、dayOfMonth）
   - 方案 B：sealed class 多型結構，每種頻率一個型別
   - 建議：**方案 B**。Kotlin sealed class 對「每種頻率帶不同參數」表達更自然，且 when 分支可窮舉。
   - 決議：

---

## Functional Design

> 由 Agent 填寫。資料模型、狀態流轉、UI 互動、邊界條件。

- ___

---

## Coding Scope

> 由 Agent 填寫。預計新增 / 修改的檔案與範圍。

**新增：**
- `___` — ___

**修改：**
- `___` — ___

---

## Acceptance Criteria

> 定義「完成」的標準。

- [ ] ___

---

## Notes

> 實作注意事項、已知限制。

- ___
