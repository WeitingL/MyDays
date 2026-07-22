# Habit 提醒通知

> 所屬 Feature：`doc/feature/2026-07-22_habit.md`
> 狀態：草稿
> Jira：___

---

## Overview

> 由我填寫。

定義每個習慣的提醒時間設定與推播排程機制（Phase 3）。

範圍包含：每個習慣可設定提醒時間、依頻率型別排程（每日 / 每週指定星期 / 每月指定日）、通知權限處理。
不包含：打卡與 streak 邏輯、壞習慣提醒策略（見 Open Questions，可能預設關閉）。

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

1. **壞習慣是否預設關閉提醒**
   - 方案 A：壞習慣預設關閉提醒（Feature 討論指出提醒可能誘導破戒）
   - 方案 B：一律開放，由使用者自行決定
   - 建議：**方案 A**。預設關閉、允許使用者手動開啟，兼顧安全與彈性。
   - 決議：

2. **排程機制選型**
   - 方案 A：WorkManager
   - 方案 B：AlarmManager（setExactAndAllowWhileIdle）
   - 建議：提醒需準時觸發，**方案 B** 較適合精確時間；WorkManager 不保證準點。
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

- Android 13+ 需請求 POST_NOTIFICATIONS 權限。
