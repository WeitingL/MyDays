package com.weiting.mydays.preview.experiments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * 時間軸變體
 *
 * 提供 4 種不同的空間利用方案：
 * - Version A: 緊湊卡片（減少 padding 和間距）
 * - Version B: 列表式（無卡片背景）
 * - Version C: 緊湊群組（同類型記錄合併顯示）
 * - Version D: 時間軸左側（類似 GitHub timeline）
 */

private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))

private val ContentColor = Color(0xFF2E2A45)

// 記錄資料
data class TimelineRecord(
    val type: RecordType,
    val title: String,
    val time: String,
    val detail: String? = null,
    val amount: String? = null
)

enum class RecordType(val icon: ImageVector, val color: Color, val label: String) {
    HABIT(Icons.Default.CheckCircle, Color(0xFF4CAF50), "習慣"),
    EXPENSE(Icons.Default.AttachMoney, Color(0xFFFF9800), "記帳"),
    DIARY(Icons.Default.Edit, Color(0xFF2196F3), "日記")
}

/**
 * Version A - 緊湊卡片
 *
 * 特點：
 * - padding 從 16dp → 8dp（省 50%）
 * - 卡片間距從 12dp → 4dp（省 67%）
 * - icon 從 40dp → 24dp（省 40%）
 * - 字級從 15sp → 12sp（更緊湊）
 * - 整體高度約為原版的 60%
 *
 * 適合：想保留 Liquid Glass 風格但更緊湊
 */
@Composable
fun TimelineCompactCard(
    record: TimelineRecord,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .then(GlassCardModifier)
            .clickable { }
            .padding(8.dp),  // 原本 16dp
        horizontalArrangement = Arrangement.spacedBy(8.dp),  // 原本 12dp
        verticalAlignment = Alignment.Top
    ) {
        // 左側：類型 icon（更小）
        Box(
            modifier = Modifier
                .size(24.dp)  // 原本 40dp
                .clip(CircleShape)
                .background(record.type.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = record.type.icon,
                contentDescription = null,
                tint = record.type.color,
                modifier = Modifier.size(16.dp)  // 原本 24dp
            )
        }

        // 中間：內容
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)  // 原本 4dp
        ) {
            Text(
                text = record.title,
                color = ContentColor,
                fontSize = 12.sp,  // 原本 15sp
                fontWeight = FontWeight.Medium
            )

            if (record.detail != null) {
                Text(
                    text = record.detail,
                    color = ContentColor.copy(alpha = 0.6f),
                    fontSize = 10.sp,  // 原本 13sp
                    maxLines = 1,  // 原本 2
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 9.sp  // 原本 12sp
            )
        }

        // 右側：金額
        if (record.amount != null) {
            Text(
                text = record.amount,
                color = if (record.amount.startsWith("-")) Color(0xFFE53935)
                else Color(0xFF4CAF50),
                fontSize = 14.sp,  // 原本 17sp
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Version B - 列表式（無卡片）
 *
 * 特點：
 * - 取消 Liquid Glass 卡片背景
 * - 純列表，用分隔線區分
 * - 只保留核心資訊（時間 + 標題 + 金額/狀態）
 * - 垂直高度約為原版的 50%
 *
 * 適合：極簡風格，最省空間
 */
@Composable
fun TimelineListItem(
    record: TimelineRecord,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側：小圓點 icon
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(record.type.color)
            )

            // 時間（固定寬度）
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 10.sp,
                modifier = Modifier.width(42.dp)
            )

            // 標題
            Text(
                text = record.title,
                color = ContentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 金額或類型標籤
            if (record.amount != null) {
                Text(
                    text = record.amount,
                    color = if (record.amount.startsWith("-")) Color(0xFFE53935)
                    else Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = record.type.label,
                    color = record.type.color.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
        }

        // 分隔線
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 22.dp)
                .background(ContentColor.copy(alpha = 0.1f))
        )
    }
}

/**
 * Version C - 緊湊群組
 *
 * 特點：
 * - 同類型記錄合併顯示（「今天 3 筆記帳」）
 * - 預設收合，點擊展開細節
 * - 節省大量空間（收合時約為原版的 30%）
 * - 展開時與原版相同
 *
 * 適合：記錄很多時，避免列表過長
 */
@Composable
fun TimelineGroupCard(
    type: RecordType,
    records: List<TimelineRecord>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .then(GlassCardModifier)
            .clickable { expanded = !expanded }
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 群組標題
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(type.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    tint = type.color,
                    modifier = Modifier.size(18.dp)
                )
            }

            // 標題
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${type.label} · 共 ${records.size} 筆",
                    color = ContentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                if (!expanded && records.isNotEmpty()) {
                    Text(
                        text = "最新: ${records.first().time}",
                        color = ContentColor.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                }
            }

            // 展開/收合按鈕
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess
                else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "收合" else "展開",
                tint = ContentColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }

        // 展開後的記錄列表
        if (expanded) {
            records.forEach { record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 38.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = record.time,
                        color = ContentColor.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        modifier = Modifier.width(36.dp)
                    )
                    Text(
                        text = record.title,
                        color = ContentColor,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (record.amount != null) {
                        Text(
                            text = record.amount,
                            color = if (record.amount.startsWith("-")) Color(0xFFE53935)
                            else Color(0xFF4CAF50),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Version D - 時間軸左側
 *
 * 特點：
 * - 時間線在左側（類似 Slack、GitHub timeline）
 * - 時間點用小圓點 + 垂直線標記
 * - 內容在右側，更窄的卡片
 * - 視覺更緊湊（約為原版的 70%）
 *
 * 適合：喜歡經典 timeline 風格
 */
@Composable
fun TimelineLeftSide(
    record: TimelineRecord,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 左側：時間軸
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(50.dp)
        ) {
            // 時間文字
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 9.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 圓點
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(record.type.color)
                    .border(2.dp, Color.White, CircleShape)
            )

            // 垂直線（如果不是最後一個）
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(ContentColor.copy(alpha = 0.15f))
                )
            }
        }

        // 右側：內容卡片（更窄）
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.5f))
                .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .clickable { }
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon（小）
            Icon(
                imageVector = record.type.icon,
                contentDescription = null,
                tint = record.type.color,
                modifier = Modifier.size(16.dp)
            )

            // 內容
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = record.title,
                    color = ContentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (record.detail != null) {
                    Text(
                        text = record.detail,
                        color = ContentColor.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 金額
            if (record.amount != null) {
                Text(
                    text = record.amount,
                    color = if (record.amount.startsWith("-")) Color(0xFFE53935)
                    else Color(0xFF4CAF50),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Mock data
private fun mockRecords() = listOf(
    TimelineRecord(
        type = RecordType.HABIT,
        title = "完成晨間運動",
        time = "08:30"
    ),
    TimelineRecord(
        type = RecordType.EXPENSE,
        title = "早餐",
        time = "09:12",
        detail = "分類: 飲食",
        amount = "-$120"
    ),
    TimelineRecord(
        type = RecordType.DIARY,
        title = "今天的心情",
        time = "21:40",
        detail = "今天去了很遠的地方，雖然很累但心情好..."
    )
)

private fun mockGroupRecords() = listOf(
    TimelineRecord(
        type = RecordType.EXPENSE,
        title = "早餐",
        time = "09:12",
        amount = "-$120"
    ),
    TimelineRecord(
        type = RecordType.EXPENSE,
        title = "午餐",
        time = "12:30",
        amount = "-$180"
    ),
    TimelineRecord(
        type = RecordType.EXPENSE,
        title = "咖啡",
        time = "15:45",
        amount = "-$65"
    )
)

// Previews
@Preview(name = "Timeline - Version A: Compact Cards", widthDp = 360)
@Composable
private fun TimelineCompactCardsPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFE0F7FA),
                        Color(0xFFB3E5FC)
                    )
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        mockRecords().forEach { record ->
            TimelineCompactCard(record = record)
        }
    }
}

@Preview(name = "Timeline - Version B: List Style", widthDp = 360)
@Composable
private fun TimelineListStylePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFE0F7FA),
                        Color(0xFFB3E5FC)
                    )
                )
            )
            .padding(20.dp)
    ) {
        mockRecords().forEach { record ->
            TimelineListItem(record = record)
        }
    }
}

@Preview(name = "Timeline - Version C: Compact Groups", widthDp = 360)
@Composable
private fun TimelineCompactGroupsPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFE0F7FA),
                        Color(0xFFB3E5FC)
                    )
                )
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimelineGroupCard(
            type = RecordType.EXPENSE,
            records = mockGroupRecords()
        )
        TimelineGroupCard(
            type = RecordType.HABIT,
            records = listOf(mockRecords()[0])
        )
    }
}

@Preview(name = "Timeline - Version D: Left Side Timeline", widthDp = 360)
@Composable
private fun TimelineLeftSidePreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFE0F7FA),
                        Color(0xFFB3E5FC)
                    )
                )
            )
            .padding(20.dp)
    ) {
        mockRecords().forEachIndexed { index, record ->
            TimelineLeftSide(
                record = record,
                isLast = index == mockRecords().lastIndex
            )
        }
    }
}
