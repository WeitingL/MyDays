# Agent Response Schema

所有 sub-agents（PMM、Engineer、QA）的回應必須遵循此標準化 JSON 格式。

## 為什麼需要標準化？

1. **Orchestrator 可解析** - 機器可讀，自動判斷下一步
2. **一致性** - 所有 agents 輸出格式統一
3. **可追溯** - 結構化記錄便於後續分析
4. **可驗證** - 檢查回應是否完整

---

## 通用欄位（所有 agents）

```json
{
  "agent": "PMM" | "Engineer" | "QA",
  "status": "completed" | "failed" | "blocked" | "need_decision" | "need_clarification",
  "summary": "一句話總結（50 字內）",
  "next_suggested": "dispatch_engineer" | "run_qa" | "need_fix" | "need_guidance" | "approve" | "need_captain_decision"
}
```

### 欄位說明

#### agent
**類型**: string  
**必填**: ✅  
**說明**: Agent 身份識別

**可能值**:
- `"PMM"` - Product Manager + Tech Lead
- `"Engineer"` - Senior Android Engineer
- `"QA"` - QA Engineer

#### status
**類型**: string  
**必填**: ✅  
**說明**: 任務執行狀態

**可能值**:
- `"completed"` - 任務完成
- `"failed"` - 任務失敗（無法完成）
- `"blocked"` - 被阻擋（需要外部輸入）
- `"need_decision"` - 需要決策（多個方案）
- `"need_clarification"` - 需要澄清（任務不明確）
- `"passed"` - QA 專用：測試通過
- `"passed_with_warnings"` - QA 專用：測試通過但有警告

#### summary
**類型**: string  
**必填**: ✅  
**說明**: 一句話總結任務結果  
**限制**: 50 字內

**範例**:
- ✅ `"深色模式可透過 Material3 DynamicTheme 實作，需修改 3 個檔案"`
- ✅ `"統計功能實作完成，有 1 個測試失敗待修正"`
- ❌ `"我分析了很多東西，然後發現..."`（太冗長）

#### next_suggested
**類型**: string  
**必填**: ✅  
**說明**: 建議 Orchestrator 下一步動作

**可能值**:
- `"dispatch_engineer"` - 調度 Engineer
- `"run_qa"` - 調度 QA
- `"need_fix"` - 需要修復（重新調度當前或上一個 agent）
- `"need_guidance"` - 需要更多指引
- `"approve"` - 建議批准
- `"need_captain_decision"` - 需要船長決策

---

## PMM Agent 專屬欄位

```json
{
  "agent": "PMM",
  "status": "completed",
  "summary": "...",
  "findings": {
    "current_architecture": "...",
    "recommended_approach": "...",
    "alternative_approaches": ["..."],
    "files_to_create": ["..."],
    "files_to_modify": ["..."],
    "dependencies_needed": ["..."],
    "effort_estimate": "...",
    "risks": ["..."]
  },
  "decision_needed": null | {
    "question": "...",
    "options": [
      {
        "name": "A",
        "description": "...",
        "pros": ["..."],
        "cons": ["..."]
      }
    ],
    "recommendation": "..."
  },
  "next_suggested": "..."
}
```

### findings
**類型**: object  
**必填**: ✅  
**說明**: 分析結果

#### findings.current_architecture
**類型**: string  
**必填**: ✅  
**說明**: 現有架構描述（簡潔說明相關的現有實作）

#### findings.recommended_approach
**類型**: string  
**必填**: ✅  
**說明**: 建議的實作方案（一段話說明核心思路）

#### findings.alternative_approaches
**類型**: string[]  
**必填**: ❌  
**說明**: 替代方案（若有）

#### findings.files_to_create
**類型**: string[]  
**必填**: ✅  
**說明**: 需要新建的檔案（相對於 repo 根目錄的路徑）

#### findings.files_to_modify
**類型**: string[]  
**必填**: ✅  
**說明**: 需要修改的檔案

#### findings.dependencies_needed
**類型**: string[]  
**必填**: ✅  
**說明**: 需要新增的依賴（通常應為空，有值時會觸發 Orchestrator 請示）

#### findings.effort_estimate
**類型**: string  
**必填**: ✅  
**說明**: 工作量估算（要明確，如 "2-3 hours"）

#### findings.risks
**類型**: string[]  
**必填**: ✅  
**說明**: 風險列表（空陣列表示無風險）

### decision_needed
**類型**: object | null  
**必填**: ✅  
**說明**: 需要決策時使用，否則為 `null`

#### decision_needed.question
**類型**: string  
**必填**: ✅（當 decision_needed 不為 null）  
**說明**: 需要決策的問題

#### decision_needed.options
**類型**: object[]  
**必填**: ✅（當 decision_needed 不為 null）  
**說明**: 可選方案列表

**Option object**:
```json
{
  "name": "A",
  "description": "方案描述",
  "pros": ["優點 1", "優點 2"],
  "cons": ["缺點 1", "缺點 2"]
}
```

#### decision_needed.recommendation
**類型**: string  
**必填**: ✅（當 decision_needed 不為 null）  
**說明**: PMM 的建議（說明建議哪個方案及原因）

---

## Engineer Agent 專屬欄位

```json
{
  "agent": "Engineer",
  "status": "completed",
  "summary": "...",
  "implementation": {
    "files_created": ["..."],
    "files_modified": ["..."],
    "key_changes": ["..."],
    "tests_added": ["..."],
    "tests_status": "..."
  },
  "issues_found": [
    {
      "type": "...",
      "description": "...",
      "file": "...",
      "attempted_fix": "..."
    }
  ],
  "next_suggested": "..."
}
```

### implementation
**類型**: object  
**必填**: ✅  
**說明**: 實作內容

#### implementation.files_created
**類型**: string[]  
**必填**: ✅  
**說明**: 新建的檔案列表

#### implementation.files_modified
**類型**: string[]  
**必填**: ✅  
**說明**: 修改的檔案列表

#### implementation.key_changes
**類型**: string[]  
**必填**: ✅  
**說明**: 關鍵改動列表（每項簡述檔案名 + 改動內容）

**範例**:
```json
"key_changes": [
  "Theme.kt: 加入 darkTheme 參數支援深色模式",
  "SettingsScreen.kt: 加入 ThemeToggle 元件",
  "UserPreferences.kt: 新增 isDarkMode Flow"
]
```

#### implementation.tests_added
**類型**: string[]  
**必填**: ✅  
**說明**: 新增的測試檔案

#### implementation.tests_status
**類型**: string  
**必填**: ✅  
**說明**: 測試執行狀態

**格式**: `"{passed} pass, {failed} fail"`  
**範例**: `"12 pass, 0 fail"`

### issues_found
**類型**: object[]  
**必填**: ✅  
**說明**: 遇到的問題列表（無問題則為空陣列 `[]`）

**Issue object**:
```json
{
  "type": "test_failure" | "compile_error" | "lint_warning" | "spec_unclear" | "design_suggestion",
  "description": "問題詳細描述",
  "file": "相關檔案路徑（若知道）",
  "attempted_fix": "嘗試的修復方法（若有）或 null"
}
```

---

## QA Agent 專屬欄位

```json
{
  "agent": "QA",
  "status": "passed",
  "summary": "...",
  "test_results": {
    "total_cases": 10,
    "passed": 8,
    "failed": 2,
    "skipped": 0
  },
  "bugs_found": [
    {
      "severity": "...",
      "title": "...",
      "description": "...",
      "steps_to_reproduce": ["..."],
      "expected": "...",
      "actual": "...",
      "file": "..."
    }
  ],
  "warnings": [
    {
      "type": "...",
      "description": "..."
    }
  ],
  "regression_check": {
    "tested_flows": ["..."],
    "issues_found": ["..."]
  },
  "next_suggested": "..."
}
```

### test_results
**類型**: object  
**必填**: ✅  
**說明**: 測試結果統計

#### test_results.total_cases
**類型**: number  
**必填**: ✅  
**說明**: 測試案例總數

#### test_results.passed
**類型**: number  
**必填**: ✅  
**說明**: 通過數量

#### test_results.failed
**類型**: number  
**必填**: ✅  
**說明**: 失敗數量

#### test_results.skipped
**類型**: number  
**必填**: ✅  
**說明**: 跳過數量

### bugs_found
**類型**: object[]  
**必填**: ✅  
**說明**: 發現的 bug 列表（無 bug 則為空陣列）

**Bug object**:
```json
{
  "severity": "critical" | "major" | "minor",
  "title": "Bug 標題（簡短描述）",
  "description": "詳細描述",
  "steps_to_reproduce": ["步驟 1", "步驟 2", "步驟 3"],
  "expected": "預期行為",
  "actual": "實際行為",
  "file": "可能的問題檔案（若知道）或 null"
}
```

### warnings
**類型**: object[]  
**必填**: ✅  
**說明**: 警告列表（非阻擋性問題，空陣列表示無警告）

**Warning object**:
```json
{
  "type": "performance" | "usability" | "style" | "accessibility",
  "description": "警告描述"
}
```

### regression_check
**類型**: object  
**必填**: ✅  
**說明**: 回歸測試結果

#### regression_check.tested_flows
**類型**: string[]  
**必填**: ✅  
**說明**: 測試的流程列表

#### regression_check.issues_found
**類型**: string[]  
**必填**: ✅  
**說明**: 發現的回歸問題（空陣列表示無回歸問題）

---

## 完整範例

### PMM 範例（成功）
```json
{
  "agent": "PMM",
  "status": "completed",
  "summary": "深色模式可透過 Material3 DynamicTheme 實作，需修改 3 個檔案，無風險",
  "findings": {
    "current_architecture": "已有 Theme.kt 使用 MaterialTheme，專案已集成 Material3",
    "recommended_approach": "擴充 Theme.kt 支援 darkTheme 參數，在 SettingsScreen 加入切換開關，使用 DataStore 儲存使用者偏好",
    "alternative_approaches": ["使用 AppCompatDelegate（不推薦，專案已全面使用 Compose）"],
    "files_to_create": ["app/src/main/java/com/weiting/mydays/ui/component/ThemeToggle.kt"],
    "files_to_modify": [
      "app/src/main/java/com/weiting/mydays/ui/theme/Theme.kt",
      "app/src/main/java/com/weiting/mydays/ui/settings/SettingsScreen.kt",
      "app/src/main/java/com/weiting/mydays/data/preferences/UserPreferences.kt"
    ],
    "dependencies_needed": [],
    "effort_estimate": "2-3 hours",
    "risks": ["需要測試所有畫面在深色模式下的顯示"]
  },
  "decision_needed": null,
  "next_suggested": "dispatch_engineer"
}
```

### Engineer 範例（有問題）
```json
{
  "agent": "Engineer",
  "status": "completed",
  "summary": "深色模式實作完成，但有 1 個測試失敗待修正",
  "implementation": {
    "files_created": ["app/src/main/java/com/weiting/mydays/ui/component/ThemeToggle.kt"],
    "files_modified": [
      "app/src/main/java/com/weiting/mydays/ui/theme/Theme.kt",
      "app/src/main/java/com/weiting/mydays/ui/settings/SettingsScreen.kt",
      "app/src/main/java/com/weiting/mydays/data/preferences/UserPreferences.kt"
    ],
    "key_changes": [
      "Theme.kt: 加入 darkTheme 參數，根據 isSystemInDarkTheme() + 偏好決定主題",
      "SettingsScreen.kt: 加入 ThemeToggle 元件並連接 ViewModel",
      "UserPreferences.kt: 新增 isDarkMode Flow 及 toggleDarkMode() 方法",
      "ThemeToggle.kt: 可複用的主題切換元件，支援預覽"
    ],
    "tests_added": [
      "app/src/test/java/com/weiting/mydays/ui/component/ThemeToggleTest.kt",
      "app/src/test/java/com/weiting/mydays/data/preferences/UserPreferencesTest.kt"
    ],
    "tests_status": "11 pass, 1 fail"
  },
  "issues_found": [
    {
      "type": "test_failure",
      "description": "UserPreferencesTest.`toggle dark mode should update flow` 失敗，預期 true 但得到 false",
      "file": "app/src/test/java/com/weiting/mydays/data/preferences/UserPreferencesTest.kt:23",
      "attempted_fix": "檢查後發現是 TestScope 的 runTest 沒有正確收集 Flow，需要加 advanceUntilIdle()"
    }
  ],
  "next_suggested": "need_fix"
}
```

### QA 範例（發現 bug）
```json
{
  "agent": "QA",
  "status": "failed",
  "summary": "發現 1 個 major bug：切換主題時 NavigationBar 顏色閃爍",
  "test_results": {
    "total_cases": 8,
    "passed": 7,
    "failed": 1,
    "skipped": 0
  },
  "bugs_found": [
    {
      "severity": "major",
      "title": "切換主題時 NavigationBar 顏色閃爍",
      "description": "從淺色切換到深色主題時，NavigationBar 會先變白再變黑，產生明顯閃爍",
      "steps_to_reproduce": [
        "開啟 App（淺色模式）",
        "進入設定頁",
        "點擊深色模式開關",
        "觀察底部 NavigationBar"
      ],
      "expected": "NavigationBar 平滑過渡到深色",
      "actual": "NavigationBar 先閃白色再變黑色",
      "file": "可能是 Theme.kt 或 MainScreen.kt 的 NavigationBar 配色問題"
    }
  ],
  "warnings": [
    {
      "type": "usability",
      "description": "建議在切換主題時加入短暫的 crossfade 動畫，提升體驗"
    }
  ],
  "regression_check": {
    "tested_flows": [
      "主頁 → 設定頁 → 個人頁（各頁面正確顯示主題）",
      "開啟 App → 切換主題 → 關閉 App → 重開（偏好保留）"
    ],
    "issues_found": []
  },
  "next_suggested": "need_fix"
}
```

---

## 驗證規則

Orchestrator 會驗證回應是否符合以下規則：

### 必要欄位檢查
- ✅ 所有標記為「必填」的欄位都存在
- ✅ 欄位類型正確（string、number、array、object）

### 邏輯一致性檢查
- ✅ `status === "completed"` 時，不應有 `decision_needed`
- ✅ `status === "need_decision"` 時，必須有 `decision_needed`
- ✅ `test_results.total_cases === passed + failed + skipped`
- ✅ `bugs_found.length > 0` 時，`status` 不應為 `"passed"`

### 內容品質檢查
- ✅ `summary` 長度在 50 字內
- ✅ 路徑字串不包含空格或特殊字元
- ✅ `tests_status` 符合格式 `"{n} pass, {m} fail"`

---

## 使用指南

### 對 Sub-Agents
在你的回應中，**必須**：
1. 使用 JSON 格式回傳
2. 包含所有必填欄位
3. 確保邏輯一致性
4. 提供清晰、簡潔的內容

**錯誤示範**：
```json
{
  "agent": "PMM",
  "status": "completed",
  "summary": "我分析了很多東西，發現這個功能可以用很多方式實作，比如說...",
  // ❌ summary 太長
  // ❌ 缺少 findings
  // ❌ 缺少 next_suggested
}
```

**正確示範**：
```json
{
  "agent": "PMM",
  "status": "completed",
  "summary": "深色模式可用 Material3 實作，需修改 3 檔案",
  "findings": { ... },
  "decision_needed": null,
  "next_suggested": "dispatch_engineer"
}
```

### 對 Orchestrator
收到 agent 回應後，檢查：
1. JSON 格式是否正確
2. 必要欄位是否完整
3. 邏輯是否一致
4. 根據 `next_suggested` 決定下一步

---

## 版本

**Current Version**: 1.0  
**Last Updated**: 2026-08-14
