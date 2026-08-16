package com.weiting.mydays.ui.flow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weiting.mydays.ui.component.BaseScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Liquid Glass 卡片樣式
private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
    .padding(16.dp)

private val ContentColor = Color(0xFF2E2A45)

// 記錄類型
enum class RecordType(val color: Color, val icon: ImageVector, val label: String) {
    Habit(Color(0xFF4CAF50), Icons.Default.CheckCircle, "習慣"),
    Expense(Color(0xFFFF9800), Icons.Default.AttachMoney, "記帳"),
    Diary(Color(0xFF2196F3), Icons.Default.Edit, "日記")
}

// Filter 選項
enum class FilterOption(val label: String) {
    All("全部"),
    Diary("日記"),
    Expense("記帳"),
    Habit("習慣")
}

// 時間軸記錄
data class FlowRecord(
    val type: RecordType,
    val title: String,
    val time: String,
    val detail: String? = null,
    val amount: String? = null
)

// 日期分組
data class DateGroup(
    val dateLabel: String,
    val records: List<FlowRecord>
)

/**
 * 上方：日期進度方塊（GitHub contribution graph 風格）
 */
@Composable
private fun ActivityGrid(modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    val currentMonth = today.monthValue
    val currentYear = today.year

    // 計算當月天數
    val daysInMonth = today.lengthOfMonth()

    // Mock data: 隨機標記某些日子有活動
    val activeDays = setOf(1, 3, 5, 8, 10, 12, 14, 15, 18, 20, 22, 25, 28, 30)

    Column(
        modifier = modifier.then(GlassCardModifier),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 標題與統計
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentYear}年 ${currentMonth}月",
                color = ContentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "最近幾天 ${activeDays.size}",
                color = ContentColor.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }

        // 方塊網格（7 columns for 7 days of week）
        val rows = (daysInMonth + 6) / 7 // 向上取整
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            for (row in 0 until rows) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (col in 0 until 7) {
                        val day = row * 7 + col + 1
                        if (day <= daysInMonth) {
                            val isActive = activeDays.contains(day)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isActive) ContentColor.copy(alpha = 0.7f)
                                        else ContentColor.copy(alpha = 0.15f)
                                    )
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 中間：Filter Tab
 */
@Composable
private fun FilterTabs(
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterOption.entries.forEach { option ->
            val isSelected = selectedFilter == option
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.7f)
                        else Color.White.copy(alpha = 0.4f)
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) Color.White.copy(alpha = 0.9f)
                        else Color.White.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onFilterSelected(option) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = option.label,
                    fontSize = 14.sp,
                    color = if (isSelected) ContentColor else ContentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * 下方：時間軸單條記錄卡片
 */
@Composable
private fun TimelineRecordCard(record: FlowRecord, modifier: Modifier = Modifier) {
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
                contentDescription = record.type.label,
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            // 詳細資訊（記帳分類、日記預覽）
            if (record.detail != null) {
                Text(
                    text = record.detail,
                    color = ContentColor.copy(alpha = 0.6f),
                    fontSize = 13.sp,
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
                fontSize = 17.sp,
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
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(ContentColor.copy(alpha = 0.25f))
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
                .background(ContentColor.copy(alpha = 0.25f))
        )
    }
}

/**
 * FlowScreen 主畫面
 */
@Composable
fun FlowScreen(
    modifier: Modifier = Modifier,
    habitViewModel: com.weiting.mydays.ui.habit.HabitViewModel = org.koin.androidx.compose.koinViewModel()
) {
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filterOption = FilterOption.entries[selectedFilter]
    val habitRecords by habitViewModel.flowRecords.collectAsState()

    // Mock data (未來會移除，改用真實資料)
    val allRecords = listOf(
        DateGroup(
            dateLabel = "今天",
            records = listOf(
                FlowRecord(
                    type = RecordType.Habit,
                    title = "完成晨間運動",
                    time = "今天 08:30"
                ),
                FlowRecord(
                    type = RecordType.Expense,
                    title = "早餐",
                    time = "今天 09:12",
                    detail = "分類: 飲食",
                    amount = "-$120"
                ),
                FlowRecord(
                    type = RecordType.Habit,
                    title = "習慣 2/3 完成",
                    time = "今天 12:00",
                    detail = "運動、讀書"
                )
            )
        ),
        DateGroup(
            dateLabel = "昨天",
            records = listOf(
                FlowRecord(
                    type = RecordType.Diary,
                    title = "今天的心情",
                    time = "昨天 21:40",
                    detail = "今天去了很遠的地方，雖然很累但心情好。下午去了公園散步..."
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
                )
            )
        ),
        DateGroup(
            dateLabel = "8月8日 (四)",
            records = listOf(
                FlowRecord(
                    type = RecordType.Habit,
                    title = "完成晨間運動",
                    time = "8月8日 08:00"
                ),
                FlowRecord(
                    type = RecordType.Expense,
                    title = "買書",
                    time = "8月8日 15:30",
                    detail = "分類: 娛樂",
                    amount = "-$380"
                )
            )
        )
    )

    // 根據 filter 篩選記錄
    val filteredGroups = if (filterOption == FilterOption.All) {
        allRecords
    } else {
        allRecords.map { group ->
            group.copy(
                records = group.records.filter { record ->
                    when (filterOption) {
                        FilterOption.Diary -> record.type == RecordType.Diary
                        FilterOption.Expense -> record.type == RecordType.Expense
                        FilterOption.Habit -> record.type == RecordType.Habit
                        else -> true
                    }
                }
            )
        }.filter { it.records.isNotEmpty() }
    }

    BaseScreen(
        title = "河流",
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. 日期進度方塊
            item {
                ActivityGrid()
            }

            // 2. Filter Tabs
            item {
                FilterTabs(
                    selectedFilter = filterOption,
                    onFilterSelected = { selectedFilter = it.ordinal }
                )
            }

            // 3. 時間軸列表
            filteredGroups.forEach { group ->
                item {
                    DateDivider(date = group.dateLabel)
                }
                items(group.records) { record ->
                    TimelineRecordCard(record = record)
                }
            }
        }
    }
}

@Preview(name = "Flow Screen", showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun FlowScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(
                        Color(0xFFE0F7FA),
                        Color(0xFFB3E5FC),
                        Color(0xFF81D4FA)
                    )
                )
            )
    ) {
        FlowScreen()
    }
}
