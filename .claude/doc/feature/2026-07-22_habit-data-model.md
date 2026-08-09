# 習慣養成 — 資料模型

> 所屬 Feature：`doc/feature/2026-07-22_habit.md`
> 狀態：討論中（2026-08-08 override：同步 Phase 4 用語，回填三題決議）
> Jira：___

---

## 目的

定義「習慣養成」（連續型習慣）的核心資料結構，作為以下 spec 的共同基礎：

- `doc/spec/2026-07-22_habit-crud.md` — Habit 定義與 CRUD
- `doc/spec/2026-07-22_habit-checkin-streak.md` — CheckIn 紀錄與 streak

> 範圍：單一內容、每日執行的習慣。依星期不同內容的資料模型見 `doc/feature/2026-07-22_weekly-plan-data-model.md`。

---

## 核心概念

兩個實體，對應「建立 → 紀錄」：

| 實體 | 角色 | 說明 |
|---|---|---|
| **Habit** | 建立（定義） | 習慣的名稱、模式 |
| **CheckIn** | 紀錄（打卡） | 某一天的紀錄，一個習慣一天一筆 |

**CheckIn 的語意依模式而異**（同一張表，同一種資料）：

| 模式 | 一筆 CheckIn 代表 | 顯示 |
|---|---|---|
| 每天都做（`BUILD`） | 那天做了 | 連續 N 天做了 |
| 克制不做（`QUIT`） | 那天發生了 | 連續 N 天沒做 |

關聯：`Habit 1 ──< N CheckIn`

因為習慣為單一內容，打卡最小單位就是「習慣本身」，不需要 variant / 子項目（那些屬「一週計劃」與未來的保養複合 routine）。

---

## Entities & Fields（2026-08-08 已對齊實作）

### Habit（`HabitEntity`）

| 欄位 | 型別 | 說明 | 狀態 |
|---|---|---|---|
| `id` | String (UUID) | 主鍵；UUID 以利 Firestore 同步 | ✅ 已實作 |
| `name` | String | 習慣名稱 | ✅ 已實作 |
| `type` | HabitType（BUILD / QUIT） | 每天都做 / 克制不做。**enum 值不改**，只改顯示用語 | ✅ 已實作 |
| `createdAt` | Long (epoch) | 建立時間；`QUIT` 尚無紀錄時的 streak 起算點 | ✅ 已實作 |
| `updatedAt` | Long | `habit-sync` 衝突解決用（updatedAt-wins） | ✅ 已實作 |
| `reminderMinuteOfDay` | Int? | 每日提醒時間；null = 未設 | ✅ 已實作（Phase 3） |
| `deletedAt` | Long? | 軟刪除 tombstone | ✅ 已實作（Phase 2） |
| `pendingSync` | Boolean | 待上傳標記 | ✅ 已實作（Phase 2） |
| ~~`bestStreak`~~ | — | **不採用**，見 Open Q3 | ❌ |

### CheckIn（`CheckInEntity`）

| 欄位 | 型別 | 說明 | 狀態 |
|---|---|---|---|
| `id` | String (UUID) | 主鍵 | ✅ 已實作 |
| `habitId` | String (FK) | 所屬 Habit，`onDelete = CASCADE` | ✅ 已實作 |
| `epochDay` | Long | 日期；`(habitId, epochDay)` unique index | ✅ 已實作 |
| `createdAt` | Long | 建立時間 | ✅ 已實作 |
| `updatedAt` | Long | 同步衝突解決用 | ✅ 已實作 |
| `deletedAt` | Long? | 軟刪除 tombstone | ✅ 已實作 |
| `pendingSync` | Boolean | 待上傳標記 | ✅ 已實作 |
| ~~`done`~~ | — | **不採用**：一筆紀錄存在 = 那天發生了，見 Open Q2 | ❌ |

> `LocalDate` 未落地為欄位型別，實際存 `epochDay: Long`（裝置本地時區換算）。

**未來欄位（記錄，不實作）**：CheckIn 的「來源」欄位——用於區分手動打卡與其他模組
（如健身）回報的達成。屬 `doc/feature/2026-07-22_habit.md`「對外接點」，
現在**不加**；延後成本低（Room destructive migration、Firestore 缺欄位視為預設值）。

---

## Open Questions

1. **id 型別：UUID String vs 自增 Long？**
   - 建議：UUID String，避免 Firestore 同步時 id 衝突。
   - 決議：**UUID String**（2026-08-08 回填，實作已如此）

2. **`QUIT` 的紀錄語意如何與 `done` 協調？**
   - 決議：**不設 `done` 欄位**（2026-08-08 回填，實作已如此）。
     一筆 CheckIn 存在即代表「那天發生了」，語意由 `Habit.type` 決定：
     `BUILD` → 那天做了；`QUIT` → 那天發生了。無紀錄的日子不需要任何列。
   - 好處：兩種模式共用同一張表與同一組同步邏輯，不需要區分寫入路徑。

3. **是否需要 `bestStreak` 欄位？**（對應 feature Ambiguity 1）
   - 決議：**不需要**（2026-08-08 回填，實作已如此）。streak 由 CheckIn 即時計算，
     不落地為欄位。feature Ambiguity 1 建議的是保留（方案 B），實作採了方案 A，
     當時未記錄；2026-08-08 review 補記並維持 A。

---

## 影響的 Spec

- [x] `habit-crud` — 依此定 Habit 的 CRUD 與 Room entity
- [x] `habit-checkin-streak` — 依此定 CheckIn 與 streak
- [ ] `habit-counting-mode`（Phase 4）— **不動本文件的資料結構**，只改顯示用語
