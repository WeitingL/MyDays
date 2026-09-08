package com.weiting.mydays.preview.experiments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
 * FlowScreen 緊湊組合版本
 *
 * 整合最佳的日期進度方塊 + 時間軸變體，提供 3 種組合：
 * 1. 最緊湊組合 - 極簡摘要 + 列表式（最省空間）
 * 2. 平衡組合 - 緊湊網格 + 緊湊卡片（保留視覺化但緊湊）
 * 3. 推薦組合 - 橫條圖 + 時間軸左側（視覺清晰且省空間）
 */

private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))

private val ContentColor = Color(0xFF2E2A45)

// Filter 選項
enum class FilterType(val displayName: String) {
    ALL("全部"),
    DIARY("日記"),
    EXPENSE("記帳"),
    HABIT("習慣")
}

// 記錄類型
enum class RecordTypeEnum(val icon: ImageVector, val color: Color, val label: String) {
    HABIT(Icons.Default.CheckCircle, Color(0xFF4CAF50), "習慣"),
    EXPENSE(Icons.Default.AttachMoney, Color(0xFFFF9800), "記帳"),
    DIARY(Icons.Default.Edit, Color(0xFF2196F3), "日記")
}

data class Record(
    val type: RecordTypeEnum,
    val title: String,
    val time: String,
    val date: String,
    val detail: String? = null,
    val amount: String? = null
)

/**
 * Filter tabs（共用元件）
 */
@Composable
private fun FilterTabs(
    selectedFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterType.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        fontSize = 13.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color.White.copy(alpha = 0.8f),
                    containerColor = Color.White.copy(alpha = 0.4f),
                    selectedLabelColor = ContentColor,
                    labelColor = ContentColor.copy(alpha = 0.7f)
                )
            )
        }
    }
}

/**
 * 日期分隔線（共用元件）
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
                .height(1.dp)
                .background(ContentColor.copy(alpha = 0.2f))
        )
        Text(
            text = date,
            color = ContentColor.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(ContentColor.copy(alpha = 0.2f))
        )
    }
}

// ==================== 組合 1: 最緊湊組合 ====================

/**
 * 組合 1 - 最緊湊組合
 *
 * = 極簡摘要（Version C）+ 列表式時間軸（Version B）
 *
 * 特點：
 * - 日期進度只顯示統計數字（最省空間）
 * - 時間軸用純列表（無卡片背景）
 * - 整體垂直空間約為原版的 35-40%
 * - 最大化螢幕利用
 *
 * 適合：螢幕空間極度有限，優先顯示更多記錄
 */
@Composable
fun FlowScreenMostCompact(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. 極簡摘要（日期進度）
        item {
            MinimalSummary(weekCount = 5, monthCount = 18)
        }

        // 2. Filter tabs
        item {
            FilterTabs(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }

        // 3. 列表式時間軸
        val recordsByDate = mockRecords().groupBy { it.date }
        recordsByDate.forEach { (date, records) ->
            item {
                DateDivider(date = date)
            }
            items(records) { record ->
                CompactListItem(record = record)
            }
        }
    }
}

@Composable
private fun MinimalSummary(weekCount: Int, monthCount: Int) {
    Row(
        modifier = GlassCardModifier.padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(28.dp)
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "本週有記錄 $weekCount 天 · 本月 $monthCount 天",
                color = ContentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "連續記錄 3 天",
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${(weekCount.toFloat() / 7 * 100).toInt()}%",
                color = Color(0xFF4CAF50),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "本週",
                color = ContentColor.copy(alpha = 0.4f),
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun CompactListItem(record: Record) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(record.type.color)
            )
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 10.sp,
                modifier = Modifier.width(42.dp)
            )
            Text(
                text = record.title,
                color = ContentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 22.dp)
                .background(ContentColor.copy(alpha = 0.1f))
        )
    }
}

// ==================== 組合 2: 平衡組合 ====================

/**
 * 組合 2 - 平衡組合
 *
 * = 緊湊網格（Version A）+ 緊湊卡片（Version A）
 *
 * 特點：
 * - 保留日期進度網格視覺化（但更小）
 * - 保留 Liquid Glass 卡片風格（但更緊湊）
 * - 整體垂直空間約為原版的 50-60%
 * - 視覺 vs 空間的平衡點
 *
 * 適合：想保留 Liquid Glass 風格但更緊湊
 */
@Composable
fun FlowScreenBalanced(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. 緊湊網格（日期進度）
        item {
            CompactGrid(activityMap = mockActivityMap())
        }

        // 2. Filter tabs
        item {
            FilterTabs(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }

        // 3. 緊湊卡片時間軸
        val recordsByDate = mockRecords().groupBy { it.date }
        recordsByDate.forEach { (date, records) ->
            item {
                DateDivider(date = date)
            }
            items(records) { record ->
                CompactCard(record = record)
            }
        }
    }
}

@Composable
private fun CompactGrid(activityMap: Map<Int, Int>) {
    Column(
        modifier = GlassCardModifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "8月活動",
                color = ContentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${activityMap.size} 天有記錄",
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            for (row in 0 until 5) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (col in 0 until 6) {
                        val day = row * 6 + col + 1
                        val level = activityMap[day] ?: 0
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(getActivityColor(level))
                                .clickable { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactCard(record: Record) {
    Row(
        modifier = GlassCardModifier
            .clickable { }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(record.type.color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = record.type.icon,
                contentDescription = null,
                tint = record.type.color,
                modifier = Modifier.size(16.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = record.title,
                color = ContentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            if (record.detail != null) {
                Text(
                    text = record.detail,
                    color = ContentColor.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 9.sp
            )
        }
        if (record.amount != null) {
            Text(
                text = record.amount,
                color = if (record.amount.startsWith("-")) Color(0xFFE53935)
                else Color(0xFF4CAF50),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==================== 組合 3: 推薦組合 ====================

/**
 * 組合 3 - 推薦組合（Designer's Pick）
 *
 * = 橫條圖（Version B）+ 時間軸左側（Version D）
 *
 * 特點：
 * - 橫條圖更容易看出活動趨勢
 * - 左側時間軸視覺層次清晰
 * - 整體垂直空間約為原版的 55-65%
 * - 視覺設計最佳平衡
 *
 * 適合：推薦作為預設方案（視覺清晰 + 省空間）
 */
@Composable
fun FlowScreenRecommended(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. 橫條圖（日期進度）
        item {
            BarChart(recentDays = mockRecentDays())
        }

        // 2. Filter tabs
        item {
            FilterTabs(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }

        // 3. 左側時間軸
        val recordsByDate = mockRecords().groupBy { it.date }
        recordsByDate.forEach { (date, records) ->
            item {
                DateDivider(date = date)
            }
            items(records.size) { index ->
                LeftSideTimeline(
                    record = records[index],
                    isLast = index == records.lastIndex && date == recordsByDate.keys.last()
                )
            }
        }
    }
}

@Composable
private fun BarChart(recentDays: List<Pair<String, Int>>) {
    Column(
        modifier = GlassCardModifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最近 7 天活動",
                color = ContentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "共 ${recentDays.sumOf { it.second }} 筆記錄",
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }
        recentDays.forEach { (day, count) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = day,
                    color = ContentColor.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    modifier = Modifier.width(32.dp)
                )
                Box(modifier = Modifier.weight(1f).height(12.dp)) {
                    val maxCount = recentDays.maxOf { it.second }
                    val fraction = if (maxCount > 0) count.toFloat() / maxCount else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction)
                            .height(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(getActivityColor(count))
                    )
                }
                Text(
                    text = count.toString(),
                    color = ContentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LeftSideTimeline(record: Record, isLast: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(50.dp)
        ) {
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 9.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(record.type.color)
                    .border(2.dp, Color.White, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(60.dp)
                        .background(ContentColor.copy(alpha = 0.15f))
                )
            }
        }
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
            Icon(
                imageVector = record.type.icon,
                contentDescription = null,
                tint = record.type.color,
                modifier = Modifier.size(16.dp)
            )
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

// 共用工具函數
private fun getActivityColor(level: Int): Color = when {
    level == 0 -> Color(0xFFEEEEEE)
    level <= 2 -> Color(0xFFAED581)
    level <= 4 -> Color(0xFF66BB6A)
    else -> Color(0xFF2E7D32)
}

private fun mockActivityMap() = mapOf(
    1 to 3, 2 to 1, 3 to 2, 5 to 4, 6 to 2, 7 to 3, 8 to 1,
    10 to 5, 11 to 2, 12 to 3, 13 to 4, 14 to 2, 15 to 6,
    18 to 3, 20 to 2, 22 to 4, 25 to 1, 28 to 3, 30 to 2
)

private fun mockRecentDays() = listOf(
    "8/9" to 2, "8/10" to 5, "8/11" to 2, "8/12" to 3,
    "8/13" to 4, "8/14" to 2, "8/15" to 6
)

private fun mockRecords() = listOf(
    Record(
        type = RecordTypeEnum.HABIT,
        title = "完成晨間運動",
        time = "08:30",
        date = "今天"
    ),
    Record(
        type = RecordTypeEnum.EXPENSE,
        title = "早餐",
        time = "09:12",
        date = "今天",
        detail = "分類: 飲食",
        amount = "-$120"
    ),
    Record(
        type = RecordTypeEnum.HABIT,
        title = "閱讀 30 分鐘",
        time = "21:00",
        date = "今天"
    ),
    Record(
        type = RecordTypeEnum.DIARY,
        title = "今天的心情",
        time = "22:30",
        date = "昨天",
        detail = "今天天氣很好，心情也不錯..."
    ),
    Record(
        type = RecordTypeEnum.EXPENSE,
        title = "晚餐",
        time = "18:45",
        date = "昨天",
        detail = "分類: 飲食",
        amount = "-$250"
    )
)

// Previews
@Preview(
    name = "Combination 1: Most Compact (Minimal + List)",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun FlowMostCompactPreview() {
    Box(
        modifier = Modifier
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
        FlowScreenMostCompact()
    }
}

@Preview(
    name = "Combination 2: Balanced (Compact Grid + Compact Cards)",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun FlowBalancedPreview() {
    Box(
        modifier = Modifier
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
        FlowScreenBalanced()
    }
}

@Preview(
    name = "Combination 3: Recommended (Bar Chart + Left Timeline)",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun FlowRecommendedPreview() {
    Box(
        modifier = Modifier
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
        FlowScreenRecommended()
    }
}
