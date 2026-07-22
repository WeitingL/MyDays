# 習慣養成 — 資料模型

> 所屬 Feature：`doc/feature/2026-07-22_habit.md`
> 狀態：討論中
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
| **Habit** | 建立（定義） | 習慣的名稱、類型 |
| **CheckIn** | 紀錄（打卡） | 某一天的打卡，一個習慣一天一筆 |

關聯：`Habit 1 ──< N CheckIn`

因為習慣為單一內容，打卡最小單位就是「習慣本身」，不需要 variant / 子項目（那些屬「一週計劃」與未來的保養複合 routine）。

---

## Entities & Fields（草稿）

### Habit

| 欄位 | 型別 | 說明 | 狀態 |
|---|---|---|---|
| `id` | String (UUID) | 主鍵；UUID 以利 Firestore 同步 | 待確認 id 型別 |
| `name` | String | 習慣名稱 | ✅ |
| `type` | HabitType（BUILD / QUIT） | 養成 / 戒除 | ✅ |
| `createdAt` | Long (epoch) | 建立時間；壞習慣 streak 起算點 | ✅ |
| `updatedAt` | Long | 下游 `habit-sync` 衝突解決用 | 建議先放 |
| `bestStreak` | Int | 歷史最佳連續天數（視 Ambiguity 1 決議） | 待確認 |

### CheckIn

| 欄位 | 型別 | 說明 | 狀態 |
|---|---|---|---|
| `id` | String (UUID) | 主鍵 | 待確認 |
| `habitId` | String (FK) | 所屬 Habit | ✅ |
| `date` | LocalDate | 打卡日期 | ✅ |
| `done` | Boolean | 好習慣：是否完成；壞習慣：見 Open Q | 待確認 |
| `createdAt` | Long | 打卡時間 | ✅ |

---

## Open Questions

1. **id 型別：UUID String vs 自增 Long？**
   - 建議：UUID String，避免 Firestore 同步時 id 衝突。
   - 決議：

2. **壞習慣（QUIT）的紀錄語意如何與 `done` 協調？**
   - feature 已決議：無紀錄 = 成功，只記「破戒」。
   - 待確認：QUIT 下的 CheckIn 代表「破戒」（done=false），還是另設欄位。
   - 決議：

3. **是否需要 `bestStreak` 欄位？**（對應 feature Ambiguity 1）
   - 若採「保留歷史最佳」則需要；可存 Habit 上或即時計算。
   - 決議：

---

## 影響的 Spec

- [ ] `habit-crud` — 依此定 Habit 的 CRUD 與 Room entity
- [ ] `habit-checkin-streak` — 依此定 CheckIn 與 streak
