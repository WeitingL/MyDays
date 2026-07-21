package com.weiting.mydays.ui.todo

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weiting.mydays.ui.component.SettingContentColor
import com.weiting.mydays.ui.component.SettingDivider
import com.weiting.mydays.ui.component.SettingGroup
import com.weiting.mydays.ui.component.SettingItem
import com.weiting.mydays.ui.component.SettingSectionHeader
import com.weiting.mydays.ui.component.SubScreenScaffold

private data class MockTodoItem(val id: Int, val title: String)

private val mockTodoSections: List<Pair<String, List<MockTodoItem>>> = run {
    var nextId = 0
    listOf(
        "今天" to listOf(
            "買早餐咖啡", "回覆主管信件", "整理今天的會議記錄",
            "健身房 30 分鐘", "繳交水電費", "晚上煮飯採買食材"
        ),
        "明天" to listOf(
            "牙醫回診", "車子加油", "跟朋友約晚餐",
            "準備週報投影片", "還書給圖書館"
        ),
        "本週稍後" to listOf(
            "整理衣物季節換季", "更新履歷", "報名週末的登山活動",
            "打電話給爸媽", "清理電腦下載資料夾", "續訂訂閱服務", "洗車"
        )
    ).map { (section, titles) ->
        section to titles.map { title -> MockTodoItem(id = nextId++, title = title) }
    }
}

@Composable
fun TodoScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val checkedIds = remember { mutableStateMapOf<Int, Boolean>() }

    SubScreenScaffold(title = "待辦事項", onBack = onBack, modifier = modifier) {
        mockTodoSections.forEachIndexed { sectionIndex, (section, items) ->
            SettingSectionHeader(section)
            SettingGroup {
                items.forEachIndexed { itemIndex, item ->
                    val checked = checkedIds[item.id] == true
                    SettingItem(
                        icon = Icons.Default.CheckCircle,
                        title = item.title,
                        iconTint = if (checked) SettingContentColor else SettingContentColor.copy(alpha = 0.25f),
                        titleColor = if (checked) SettingContentColor.copy(alpha = 0.4f) else SettingContentColor,
                        onClick = { checkedIds[item.id] = !checked }
                    )
                    if (itemIndex != items.lastIndex) {
                        SettingDivider()
                    }
                }
            }
            if (sectionIndex != mockTodoSections.lastIndex) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
