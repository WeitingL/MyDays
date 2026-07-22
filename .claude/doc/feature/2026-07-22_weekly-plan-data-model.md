# 一週計劃 — 資料模型

> 所屬 Feature：`doc/feature/2026-07-22_weekly-plan.md`
> 狀態：草稿（待 review）
> Jira：___

---

## 目的

定義「一週計劃」的資料結構。與「習慣養成」的資料模型分離（見 `doc/feature/2026-07-22_habit-data-model.md`），因為兩者本質不同：習慣是單一內容連續型，計劃是依星期安排的多內容。

---

## 核心概念（初步）

| 實體 | 角色 | 說明 |
|---|---|---|
| **Plan** | 計劃（容器） | 一個系列計劃，如「運動計劃」 |
| **PlanItem** | 每日內容 | 綁定星期幾的執行內容；休息日 = 該星期無 item |
| **CheckIn** | 紀錄（打卡） | 某天某項內容的打卡 |

關聯（初步）：`Plan 1 ──< N PlanItem`（每個 item 綁 `dayOfWeek`）、`PlanItem 1 ──< N CheckIn`

> 打卡最小單位為「PlanItem」而非「Plan」，因為一天可能有多項內容，且需分別打勾——這與習慣養成「一個習慣一天一筆」不同。

---

## Entities & Fields（待討論）

### Plan

| 欄位 | 型別 | 說明 |
|---|---|---|
| `id` | String (UUID) | 主鍵 |
| `name` | String | 計劃名稱 |
| `createdAt` | Long | 建立時間 |

### PlanItem

| 欄位 | 型別 | 說明 |
|---|---|---|
| `id` | String (UUID) | 主鍵 |
| `planId` | String (FK) | 所屬 Plan |
| `dayOfWeek` | DayOfWeek | 綁定星期幾 |
| `content` | String | 執行內容（重訓 / 有氧…） |

### CheckIn

| 欄位 | 型別 | 說明 |
|---|---|---|
| `id` | String (UUID) | 主鍵 |
| `planItemId` | String (FK) | 打的是哪一項 |
| `date` | LocalDate | 日期 |
| `done` | Boolean | 完成與否 |

---

## Open Questions

1. 一格單一內容 vs 多內容 → 影響 PlanItem 與 `dayOfWeek` 的關係（一對一還是一對多）。 答：
2. 是否需要完成率 / 週 streak → 影響是否需額外統計欄位或即時計算。 答：
3. CheckIn 顆粒度以 PlanItem 為單位是否正確？ 答：

---

## 影響的 Spec

> 待 feature review 後識別。

- [ ] ___
