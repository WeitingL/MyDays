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

1. ___

---

## Framework

> 由 Agent 填寫。對照現有 codebase / 架構。

- ___

---

## Open Questions

> 由 Agent 填寫。每項附方案與建議供選擇。

1. **衝突解決策略**
   - 方案 A：last-write-wins，以伺服器時間為準
   - 方案 B：以裝置本地時間為準
   - 建議：**方案 A**。伺服器時間可避免裝置時鐘不同步造成的錯亂。
   - 決議：

2. **同步觸發時機**
   - 方案 A：開啟 App + 打卡當下即時推送
   - 方案 B：定期背景同步（WorkManager）
   - 建議：兩者並用 — 即時推送保證體感，背景同步補漏。
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

- Firestore 已導入並套用（`app/build.gradle.kts`）；本 spec 直接沿用。
