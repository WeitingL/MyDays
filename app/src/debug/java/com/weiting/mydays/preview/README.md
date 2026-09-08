# UI Design Preview

這個目錄給 UI designer agent 工作使用。

## 用途

- 提供 Compose `@Preview` 元件給船長 review
- 所有檔案只在 debug build 編譯，不會進 release APK

## 檔案組織

```
preview/
├── README.md              # 本說明
├── wireframes/            # 線框圖 preview
├── samples/              # 設計樣本 preview
└── experiments/          # 實驗性設計
```

## 使用方式

1. UI designer 在這裡寫 Compose Preview
2. 使用 `@Preview` annotation
3. 在 Android Studio 可直接預覽
4. 不影響正式 code

## 範例

```kotlin
package com.weiting.mydays.preview.samples

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.weiting.mydays.ui.component.FeatureCard

@Preview(name = "Feature Card - Dark Mode")
@Composable
fun FeatureCardDarkPreview() {
    // Preview implementation
}
```
