package com.weiting.mydays.preview.wireframes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Flow (時間軸) Wireframe
 *
 * 顯示所有紀錄的時間軸（類似社交 app 的 feed）：
 * - HabitRecord: 習慣打卡（icon + 習慣名稱 + 時間）
 * - ExpenseRecord: 記帳記錄（金額 + 分類 + 時間）
 * - DiaryRecord: 日記（前 2 行預覽 + 時間）
 *
 * 使用 Liquid Glass 風格：
 * - 每條紀錄是獨立的半透明卡片
 * - 時間標記清楚顯示（今天 14:30、昨天 09:15...）
 * - 不同類型有視覺區分（icon、顏色）
 */

private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
    .padding(16.dp)

private val ContentColor = Color(0xFF2E2A45)

/**
 * 紀錄類型枚舉
 */
enum class RecordType(val color: Color, val icon: ImageVector) {
    Habit(Color(0xFF4CAF50), Icons.Default.CheckCircle),
    Expense(Color(0xFFFF9800), Icons.Default.AttachMoney),
    Diary(Color(0xFF2196F3), Icons.Default.Edit)
}

/**
 * 時間軸紀錄 data class
 */
data class FlowRecord(
    val type: RecordType,
    val title: String,
    val time: String,
    val detail: String? = null,
    val amount: String? = null
)

/**
 * 單條時間軸紀錄卡片
 */
@Composable
private fun FlowRecordCard(record: FlowRecord, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.then(GlassCardModifier),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 左側：類型 icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(record.type.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = record.type.icon,
                contentDescription = record.type.name,
                tint = record.type.color,
                modifier = Modifier.size(24.dp)
            )
        }

        // 中間：內容
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 標題
            Text(
                text = record.title,
                color = ContentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // 詳細資訊（記帳分類、日記預覽）
            if (record.detail != null) {
                Text(
                    text = record.detail,
                    color = ContentColor.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 時間
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        // 右側：金額（記帳專用）
        if (record.amount != null) {
            Text(
                text = record.amount,
                color = if (record.amount.startsWith("-")) Color(0xFFE53935) else Color(0xFF4CAF50),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 日期分隔線
 */
@Composable
private fun DateDivider(date: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .background(ContentColor.copy(alpha = 0.2f))
                .padding(vertical = 0.5.dp)
        )
        Text(
            text = date,
            color = ContentColor.copy(alpha = 0.6f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .background(ContentColor.copy(alpha = 0.2f))
                .padding(vertical = 0.5.dp)
        )
    }
}

/**
 * Flow (時間軸) 完整畫面
 * 包含背景漸層 + 可捲動的紀錄列表
 */
@Composable
fun FlowTimelineWireframe(modifier: Modifier = Modifier) {
    // 假資料
    val records = listOf(
        // 今天
        FlowRecord(
            type = RecordType.Habit,
            title = "完成晨間運動",
            time = "今天 08:30"
        ),
        FlowRecord(
            type = RecordType.Expense,
            title = "午餐",
            time = "今天 12:15",
            detail = "分類: 飲食",
            amount = "-$120"
        ),
        FlowRecord(
            type = RecordType.Habit,
            title = "完成閱讀 30 分鐘",
            time = "今天 21:00"
        ),

        // 昨天
        FlowRecord(
            type = RecordType.Diary,
            title = "今天的心情",
            time = "昨天 22:30",
            detail = "今天天氣很好，心情也不錯。下午去了公園散步，看到很多人在運動..."
        ),
        FlowRecord(
            type = RecordType.Habit,
            title = "完成冥想 10 分鐘",
            time = "昨天 20:00"
        ),
        FlowRecord(
            type = RecordType.Expense,
            title = "晚餐",
            time = "昨天 18:45",
            detail = "分類: 飲食",
            amount = "-$250"
        ),
        FlowRecord(
            type = RecordType.Expense,
            title = "捷運",
            time = "昨天 09:00",
            detail = "分類: 交通",
            amount = "-$20"
        ),

        // 前天
        FlowRecord(
            type = RecordType.Habit,
            title = "完成晨間運動",
            time = "前天 08:00"
        ),
        FlowRecord(
            type = RecordType.Diary,
            title = "週末計畫",
            time = "前天 23:00",
            detail = "這個週末想去爬山，要準備一下裝備和食物。天氣預報說會是好天氣..."
        ),
        FlowRecord(
            type = RecordType.Expense,
            title = "買書",
            time = "前天 15:30",
            detail = "分類: 娛樂",
            amount = "-$380"
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFE0F7FA),
                        Color(0xFFB3E5FC),
                        Color(0xFF81D4FA)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 頂部標題
            Text(
                text = "時間軸",
                color = ContentColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 今天
            DateDivider("今天")
            records.filter { it.time.startsWith("今天") }.forEach { record ->
                FlowRecordCard(record)
            }

            // 昨天
            DateDivider("昨天")
            records.filter { it.time.startsWith("昨天") }.forEach { record ->
                FlowRecordCard(record)
            }

            // 前天
            DateDivider("8月13日 (三)")
            records.filter { it.time.startsWith("前天") }.forEach { record ->
                FlowRecordCard(record)
            }
        }
    }
}

@Preview(name = "Flow Timeline", showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun FlowTimelineWireframePreview() {
    FlowTimelineWireframe()
}
