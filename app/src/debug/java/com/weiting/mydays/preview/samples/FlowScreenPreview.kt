package com.weiting.mydays.preview.samples

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
import androidx.compose.foundation.lazy.LazyRow
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
 * FlowScreen UI Spec
 *
 * 完整的 Flow 頁面 UI：
 * 1. 上方：日期進度方塊（GitHub 風格）
 * 2. 中間：Filter tabs（全部/日記/記帳/習慣）
 * 3. 下方：時間軸列表
 */

private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))

private val ContentColor = Color(0xFF2E2A45)

/**
 * FlowScreen 完整頁面
 */
@Composable
fun FlowScreen(
    state: FlowScreenState,
    actions: FlowScreenActions,
    modifier: Modifier = Modifier
) {
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 標題
            item {
                Text(
                    text = "時間軸",
                    color = ContentColor,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 日期進度方塊（GitHub 風格）
            item {
                ActivityCalendar(
                    activityMap = state.activityMap,
                    currentMonth = state.currentMonth,
                    onDateClicked = actions::onDateCellClicked
                )
            }

            // Filter tabs
            item {
                FilterTabs(
                    selectedFilter = state.selectedFilter,
                    onFilterSelected = actions::onFilterSelected
                )
            }

            // 記錄列表（依日期分組）
            val recordsByDate = state.records.groupBy { it.date }
            recordsByDate.forEach { (date, records) ->
                // 日期分隔線
                item {
                    DateDivider(date)
                }

                // 該日期的記錄
                items(records) { record ->
                    RecordCard(
                        record = record,
                        onClick = { actions.onRecordClicked(record.id) }
                    )
                }
            }
        }
    }
}

/**
 * 日期進度方塊（GitHub contribution graph 風格）
 */
@Composable
private fun ActivityCalendar(
    activityMap: Map<Int, Int>,
    currentMonth: String,
    onDateClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.then(GlassCardModifier).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$currentMonth 活動",
            color = ContentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // 簡化版：只顯示最近 14 天
        val days = (1..15).toList()

        // 分成 2 行
        val rows = days.chunked(8)

        rows.forEach { rowDays ->
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(rowDays) { day ->
                    val activityLevel = activityMap[day] ?: 0
                    ActivityCell(
                        day = day,
                        activityLevel = activityLevel,
                        onClick = { onDateClicked(day) }
                    )
                }
            }
        }
    }
}

/**
 * 活動方塊（單個日期）
 */
@Composable
private fun ActivityCell(
    day: Int,
    activityLevel: Int,
    onClick: () -> Unit
) {
    val color = when {
        activityLevel == 0 -> Color(0xFFEEEEEE)
        activityLevel <= 2 -> Color(0xFFAED581)
        activityLevel <= 4 -> Color(0xFF66BB6A)
        else -> Color(0xFF2E7D32)
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            color = if (activityLevel == 0) ContentColor.copy(alpha = 0.4f) else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Filter tabs
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
                        fontSize = 14.sp
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
                .height(1.dp)
                .background(ContentColor.copy(alpha = 0.2f))
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
                .height(1.dp)
                .background(ContentColor.copy(alpha = 0.2f))
        )
    }
}

/**
 * 記錄卡片
 */
@Composable
private fun RecordCard(
    record: Record,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .then(GlassCardModifier)
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 左側：類型 icon
        RecordTypeIcon(record.type)

        // 中間：內容
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RecordContent(record.type)

            // 時間
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        // 右側：金額（記帳專用）
        if (record.type is RecordType.Expense) {
            val amount = record.type.amount
            Text(
                text = if (amount < 0) "-$${-amount}" else "+$${amount}",
                color = if (amount < 0) Color(0xFFE53935) else Color(0xFF4CAF50),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 記錄類型 icon
 */
@Composable
private fun RecordTypeIcon(type: RecordType) {
    val (icon, color) = when (type) {
        is RecordType.Habit -> Icons.Default.CheckCircle to Color(0xFF4CAF50)
        is RecordType.Expense -> Icons.Default.AttachMoney to Color(0xFFFF9800)
        is RecordType.Diary -> Icons.Default.Edit to Color(0xFF2196F3)
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * 記錄內容（依類型顯示）
 */
@Composable
private fun RecordContent(type: RecordType) {
    when (type) {
        is RecordType.Habit -> {
            Text(
                text = type.name,
                color = ContentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        is RecordType.Expense -> {
            Text(
                text = type.category,
                color = ContentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        is RecordType.Diary -> {
            Text(
                text = "日記",
                color = ContentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = type.preview,
                color = ContentColor.copy(alpha = 0.6f),
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Preview
 */
@Preview(name = "Flow Screen", showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun FlowScreenPreview() {
    val state = FlowScreenMockData.createMockState()
    val actions = object : FlowScreenActions {
        override fun onFilterSelected(filter: FilterType) {}
        override fun onRecordClicked(recordId: String) {}
        override fun onDateCellClicked(date: Int) {}
    }

    FlowScreen(state = state, actions = actions)
}
