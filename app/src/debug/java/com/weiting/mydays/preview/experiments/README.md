# FlowScreen 緊湊版本實驗

已建立多個版本的 FlowScreen 變體，提供更緊湊的空間利用方案。

## 檔案結構

```
preview/experiments/
├── FlowDateProgressVariants.kt        # 4 個日期進度方塊變體
├── FlowTimelineVariants.kt            # 4 個時間軸變體
└── FlowCompactCombinations.kt         # 3 個完整頁面組合
```

---

## 📊 日期進度方塊變體

**檔案**: `FlowDateProgressVariants.kt`

### Version A - 緊湊網格
- **Preview 名稱**: `Date Progress - Version A: Compact Grid`
- **特點**:
  - 方塊從 36dp → 8dp（省 78% 空間）
  - 間距從 6dp → 2dp
  - 只顯示最近 30 天（6×5 網格）
  - 不顯示日期數字，只用顏色
- **空間利用**: 約為原版的 40%
- **適合**: 希望保留視覺化但節省空間

### Version B - 橫條圖
- **Preview 名稱**: `Date Progress - Version B: Bar Chart`
- **特點**:
  - 改用水平長條圖（每天一條）
  - 長度代表活動量
  - 只顯示最近 7 天
  - 更容易看出趨勢
- **空間利用**: 約為原版的 50%
- **適合**: 更關注趨勢而非完整月曆

### Version C - 極簡摘要
- **Preview 名稱**: `Date Progress - Version C: Minimal Summary`
- **特點**:
  - 只顯示統計數字（本週/本月有記錄天數）
  - 不顯示網格或圖表
  - 用 1-2 行文字 + icon
- **空間利用**: 約為原版的 20%（最省空間）
- **適合**: 螢幕空間極度有限

### Version D - 迷你月曆
- **Preview 名稱**: `Date Progress - Version D: Mini Calendar`
- **特點**:
  - 類似 calendar widget 的緊湊月曆
  - 更小的日期數字（8sp）
  - 有記錄的日期加圓點標記
  - 取消彩色背景，只留標記
- **空間利用**: 約為原版的 60%
- **適合**: 想保留完整月曆但更緊湊

---

## 📝 時間軸變體

**檔案**: `FlowTimelineVariants.kt`

### Version A - 緊湊卡片
- **Preview 名稱**: `Timeline - Version A: Compact Cards`
- **特點**:
  - padding 從 16dp → 8dp（省 50%）
  - 卡片間距從 12dp → 4dp
  - icon 從 40dp → 24dp
  - 字級從 15sp → 12sp
- **空間利用**: 約為原版的 60%
- **適合**: 想保留 Liquid Glass 風格但更緊湊

### Version B - 列表式（無卡片）
- **Preview 名稱**: `Timeline - Version B: List Style`
- **特點**:
  - 取消 Liquid Glass 卡片背景
  - 純列表，用分隔線區分
  - 只保留核心資訊（時間 + 標題 + 金額/狀態）
- **空間利用**: 約為原版的 50%
- **適合**: 極簡風格，最省空間

### Version C - 緊湊群組
- **Preview 名稱**: `Timeline - Version C: Compact Groups`
- **特點**:
  - 同類型記錄合併顯示（「今天 3 筆記帳」）
  - 預設收合，點擊展開細節
  - 節省大量空間
- **空間利用**: 收合時約為原版的 30%
- **適合**: 記錄很多時，避免列表過長

### Version D - 時間軸左側
- **Preview 名稱**: `Timeline - Version D: Left Side Timeline`
- **特點**:
  - 時間線在左側（類似 Slack、GitHub timeline）
  - 時間點用小圓點 + 垂直線標記
  - 內容在右側，更窄的卡片
- **空間利用**: 約為原版的 70%
- **適合**: 喜歡經典 timeline 風格

---

## 🎯 完整頁面組合

**檔案**: `FlowCompactCombinations.kt`

### 組合 1 - 最緊湊組合
- **Preview 名稱**: `Combination 1: Most Compact (Minimal + List)`
- **組成**: 極簡摘要（Version C）+ 列表式時間軸（Version B）
- **空間利用**: 約為原版的 35-40%
- **特點**:
  - 最大化螢幕利用
  - 優先顯示更多記錄
- **適合**: 螢幕空間極度有限

### 組合 2 - 平衡組合
- **Preview 名稱**: `Combination 2: Balanced (Compact Grid + Compact Cards)`
- **組成**: 緊湊網格（Version A）+ 緊湊卡片（Version A）
- **空間利用**: 約為原版的 50-60%
- **特點**:
  - 保留日期進度網格視覺化（但更小）
  - 保留 Liquid Glass 卡片風格（但更緊湊）
  - 視覺 vs 空間的平衡點
- **適合**: 想保留 Liquid Glass 風格但更緊湊

### 組合 3 - 推薦組合 ⭐
- **Preview 名稱**: `Combination 3: Recommended (Bar Chart + Left Timeline)`
- **組成**: 橫條圖（Version B）+ 時間軸左側（Version D）
- **空間利用**: 約為原版的 55-65%
- **特點**:
  - 橫條圖更容易看出活動趨勢
  - 左側時間軸視覺層次清晰
  - 視覺設計最佳平衡
- **適合**: 推薦作為預設方案（視覺清晰 + 省空間）

---

## 如何預覽

1. 在 Android Studio 開啟任一檔案
2. 點擊右上角的 "Split" 或 "Design" 分頁
3. 會看到所有 `@Preview` 標註的版本
4. 可以同時比較多個版本

---

## 空間節省比較表

| 版本 | 空間利用（vs 原版） | 視覺化程度 | 推薦指數 |
|------|-------------------|-----------|---------|
| **日期進度方塊** |
| Version A - 緊湊網格 | 40% | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| Version B - 橫條圖 | 50% | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Version C - 極簡摘要 | 20% | ⭐ | ⭐⭐⭐ |
| Version D - 迷你月曆 | 60% | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **時間軸** |
| Version A - 緊湊卡片 | 60% | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| Version B - 列表式 | 50% | ⭐⭐ | ⭐⭐⭐ |
| Version C - 緊湊群組 | 30% (收合) | ⭐⭐ | ⭐⭐⭐⭐ |
| Version D - 左側時間軸 | 70% | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **完整組合** |
| 組合 1 - 最緊湊 | 35-40% | ⭐⭐ | ⭐⭐⭐ |
| 組合 2 - 平衡 | 50-60% | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 組合 3 - 推薦 | 55-65% | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 設計師建議

根據視覺設計與空間利用的平衡，推薦：

**🎯 最佳選擇**: **組合 3 - 推薦組合**
- 橫條圖（日期進度）+ 左側時間軸
- 視覺層次清晰，容易閱讀
- 省下約 40% 空間
- Liquid Glass 風格保持一致

**如果需要更多空間**: 選擇 **組合 1 - 最緊湊組合**
- 極簡摘要 + 列表式
- 省下約 60% 空間
- 犧牲部分視覺化

**如果喜歡原版風格**: 選擇 **組合 2 - 平衡組合**
- 緊湊網格 + 緊湊卡片
- 保留 Liquid Glass 質感
- 省下約 40-50% 空間

---

## 技術細節

- 所有版本都使用 Liquid Glass 設計系統
- 所有版本都使用相同的配色方案
- 使用 mock data，方便預覽
- 所有元件都可獨立使用
- 編譯通過，可直接整合到 app

---

## 下一步

1. 在 Android Studio 預覽各個版本
2. 選擇喜歡的組合
3. 將選定版本整合到正式的 FlowScreen
4. 可以混搭不同元件（例如：橫條圖 + 緊湊卡片）
