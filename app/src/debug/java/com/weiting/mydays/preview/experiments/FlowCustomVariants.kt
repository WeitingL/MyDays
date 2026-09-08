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
 * FlowScreen 自訂變體
 *
 * 基於船長選定的基礎組合：
 * - 上方：Compact Grid（8dp 小方塊網格）
 * - 下方：List Style（無卡片背景純列表）
 *
 * 提供 5 個變體，每個改動 1-2 個細節：
 * - Version 1: Enhanced Hierarchy（加強視覺層次）
 * - Version 2: Minimal B&W（極簡黑白）
 * - Version 3: Interactive Feedback（加強互動反饋）
 * - Version 4: Dense Layout（資訊密度優化）
 * - Version 5: Flexible Layout（彈性佈局，可選）
 */

private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))

private val ContentColor = Color(0xFF2E2A45)

// Filter 選項（檔案內部專用）
private enum class CustomFilterType(val displayName: String) {
    ALL("全部"),
    DIARY("日記"),
    EXPENSE("記帳"),
    HABIT("習慣")
}

// 記錄類型（檔案內部專用）
private enum class CustomRecordType(val icon: ImageVector, val color: Color, val label: String) {
    HABIT(Icons.Default.CheckCircle, Color(0xFF4CAF50), "習慣"),
    EXPENSE(Icons.Default.AttachMoney, Color(0xFFFF9800), "記帳"),
    DIARY(Icons.Default.Edit, Color(0xFF2196F3), "日記")
}

private data class CustomRecord(
    val type: CustomRecordType,
    val title: String,
    val time: String,
    val date: String,
    val detail: String? = null,
    val amount: String? = null
)

// 共用元件：日期分隔線
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

// 共用工具
private fun getActivityColor(level: Int): Color = when {
    level == 0 -> Color(0xFFEEEEEE)
    level <= 2 -> Color(0xFFAED581)
    level <= 4 -> Color(0xFF66BB6A)
    else -> Color(0xFF2E7D32)
}

// ==================== Version 1: Enhanced Hierarchy ====================

/**
 * Version 1 - 加強視覺層次
 *
 * 改動：
 * 1. Filter tabs 加上 Liquid Glass 半透明背景（更明顯的區域劃分）
 * 2. 列表項之間用淡色分隔線（保持 List Style，但增加微妙層次）
 *
 * 特點：
 * - 保持整體極簡風格
 * - 用微妙的視覺元素增加層次感
 * - 不破壞 List Style 的簡潔性
 */
@Composable
fun FlowCustomVersion1(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf(CustomFilterType.ALL) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Compact Grid（保持原樣）
        item {
            CompactGrid(activityMap = mockActivityMap())
        }

        // 2. Filter tabs with Glass background（改動 1）
        item {
            Box(
                modifier = GlassCardModifier.padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomFilterType.entries.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(text = filter.displayName, fontSize = 13.sp)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.White.copy(alpha = 0.9f),
                                containerColor = Color.White.copy(alpha = 0.3f),
                                selectedLabelColor = ContentColor,
                                labelColor = ContentColor.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }

        // 3. List with subtle dividers（改動 2）
        val recordsByDate = mockRecords().groupBy { it.date }
        recordsByDate.forEach { (date, records) ->
            item {
                DateDivider(date = date)
            }
            items(records) { record ->
                ListItemWithDivider(record = record)
            }
        }
    }
}

@Composable
private fun ListItemWithDivider(record: CustomRecord) {
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
        // 淡色分隔線（非常微妙）
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 22.dp)
                .background(ContentColor.copy(alpha = 0.08f))
        )
    }
}

// ==================== Version 2: Minimal B&W ====================

/**
 * Version 2 - 極簡黑白
 *
 * 改動：
 * 1. Compact Grid 改用灰階單色（取消彩色）
 * 2. 列表項移除 icon 圓點，只用文字顏色標記類型
 * 3. 減少顏色使用，只在金額上用色
 *
 * 特點：
 * - 極度簡潔的黑白風格
 * - 減少視覺干擾
 * - 資訊層次更清晰
 */
@Composable
fun FlowCustomVersion2(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf(CustomFilterType.ALL) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Compact Grid in Grayscale（改動 1）
        item {
            CompactGridGrayscale(activityMap = mockActivityMap())
        }

        // 2. Filter tabs（保持原樣）
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomFilterType.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(text = filter.displayName, fontSize = 13.sp)
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

        // 3. Minimal List（改動 2 & 3）
        val recordsByDate = mockRecords().groupBy { it.date }
        recordsByDate.forEach { (date, records) ->
            item {
                DateDivider(date = date)
            }
            items(records) { record ->
                MinimalListItem(record = record)
            }
        }
    }
}

@Composable
private fun CompactGridGrayscale(activityMap: Map<Int, Int>) {
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
                        // 灰階：無活動 -> 淺灰，有活動 -> 深灰
                        val grayColor = when {
                            level == 0 -> Color(0xFFEEEEEE)
                            level <= 2 -> Color(0xFFBDBDBD)
                            level <= 4 -> Color(0xFF757575)
                            else -> Color(0xFF424242)
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(grayColor)
                                .clickable { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MinimalListItem(record: CustomRecord) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 移除 icon 圓點，改用文字
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 10.sp,
                modifier = Modifier.width(42.dp)
            )
            Text(
                text = record.type.label,
                color = ContentColor.copy(alpha = 0.4f),
                fontSize = 9.sp,
                modifier = Modifier.width(32.dp)
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
            // 只有金額用色
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 22.dp)
                .background(ContentColor.copy(alpha = 0.1f))
        )
    }
}

// ==================== Version 3: Interactive Feedback ====================

/**
 * Version 3 - 加強互動反饋
 *
 * 改動：
 * 1. Compact Grid 的方塊加上 border 暗示可點擊
 * 2. 列表項加上微妙的背景色（hover/press 狀態的視覺提示）
 *
 * 特點：
 * - 加強互動提示
 * - 讓使用者知道哪些元素可點擊
 * - 保持整體簡潔風格
 *
 * 註：靜態 preview 無法顯示實際的 hover/press 效果，
 * 但可以看到視覺提示（border、背景色）
 */
@Composable
fun FlowCustomVersion3(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf(CustomFilterType.ALL) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Interactive Compact Grid（改動 1）
        item {
            InteractiveCompactGrid(activityMap = mockActivityMap())
        }

        // 2. Filter tabs（保持原樣）
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomFilterType.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(text = filter.displayName, fontSize = 13.sp)
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

        // 3. Interactive List（改動 2）
        val recordsByDate = mockRecords().groupBy { it.date }
        recordsByDate.forEach { (date, records) ->
            item {
                DateDivider(date = date)
            }
            items(records) { record ->
                InteractiveListItem(record = record)
            }
        }
    }
}

@Composable
private fun InteractiveCompactGrid(activityMap: Map<Int, Int>) {
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
                text = "8月活動（點擊查看當天記錄）",
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
                        // 加上 border 暗示可點擊
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(getActivityColor(level))
                                .border(
                                    width = 0.5.dp,
                                    color = if (level > 0) Color.White.copy(alpha = 0.6f)
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(2.dp)
                                )
                                .clickable { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InteractiveListItem(record: CustomRecord) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // 加上淡色背景暗示可點擊
                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .clickable { }
                .padding(vertical = 6.dp, horizontal = 8.dp),
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
        Spacer(modifier = Modifier.height(2.dp))
    }
}

// ==================== Version 4: Dense Layout ====================

/**
 * Version 4 - 資訊密度優化
 *
 * 改動：
 * 1. Compact Grid 加上數字標記（方塊內顯示當天記錄數）
 * 2. 列表項改用單行緊湊佈局（時間、類型、內容全在同一行）
 *
 * 特點：
 * - 最大化資訊密度
 * - 一眼看到更多資訊
 * - 適合資訊量大的使用者
 */
@Composable
fun FlowCustomVersion4(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf(CustomFilterType.ALL) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Compact Grid with numbers（改動 1）
        item {
            CompactGridWithNumbers(activityMap = mockActivityMap())
        }

        // 2. Filter tabs（保持原樣）
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomFilterType.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(text = filter.displayName, fontSize = 13.sp)
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

        // 3. Dense single-line list（改動 2）
        val recordsByDate = mockRecords().groupBy { it.date }
        recordsByDate.forEach { (date, records) ->
            item {
                DateDivider(date = date)
            }
            items(records) { record ->
                DenseListItem(record = record)
            }
        }
    }
}

@Composable
private fun CompactGridWithNumbers(activityMap: Map<Int, Int>) {
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
                        // 方塊內顯示數字
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(getActivityColor(level))
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            if (level > 0) {
                                Text(
                                    text = level.toString(),
                                    color = Color.White,
                                    fontSize = 5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DenseListItem(record: CustomRecord) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 4.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 時間
            Text(
                text = record.time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 9.sp,
                modifier = Modifier.width(36.dp)
            )
            // 類型 icon（更小）
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(record.type.color)
            )
            // 類型標籤
            Text(
                text = record.type.label,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 9.sp,
                modifier = Modifier.width(28.dp)
            )
            // 內容
            Text(
                text = record.title,
                color = ContentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // 金額/狀態
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(start = 22.dp)
                .background(ContentColor.copy(alpha = 0.1f))
        )
    }
}

// ==================== Version 5: Flexible Layout ====================

/**
 * Version 5 - 彈性佈局（Optional）
 *
 * 改動：
 * 1. Compact Grid 可展開/收合（預設收合只顯示統計摘要）
 * 2. 列表項加上「展開顯示更多」提示（有 detail 的項目）
 *
 * 特點：
 * - 使用者可控制顯示內容
 * - 預設最省空間
 * - 需要時可展開查看詳情
 *
 * 註：這個版本加入互動狀態，適合實際 app 使用
 */
@Composable
fun FlowCustomVersion5(modifier: Modifier = Modifier) {
    var selectedFilter by remember { mutableStateOf(CustomFilterType.ALL) }
    var gridExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Collapsible Compact Grid（改動 1）
        item {
            CollapsibleCompactGrid(
                activityMap = mockActivityMap(),
                expanded = gridExpanded,
                onExpandToggle = { gridExpanded = !gridExpanded }
            )
        }

        // 2. Filter tabs（保持原樣）
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomFilterType.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(text = filter.displayName, fontSize = 13.sp)
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

        // 3. Expandable List（改動 2）
        val recordsByDate = mockRecords().groupBy { it.date }
        recordsByDate.forEach { (date, records) ->
            item {
                DateDivider(date = date)
            }
            items(records) { record ->
                ExpandableListItem(record = record)
            }
        }
    }
}

@Composable
private fun CollapsibleCompactGrid(
    activityMap: Map<Int, Int>,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Column(
        modifier = GlassCardModifier
            .clickable { onExpandToggle() }
            .padding(12.dp),
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
                text = if (expanded) "收合 ▲" else "${activityMap.size} 天 · 展開 ▼",
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 10.sp
            )
        }

        if (expanded) {
            // 展開時顯示完整網格
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
                            )
                        }
                    }
                }
            }
        } else {
            // 收合時只顯示統計
            Text(
                text = "本月記錄 ${activityMap.values.sum()} 筆 · 平均每天 ${activityMap.values.sum() / activityMap.size} 筆",
                color = ContentColor.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ExpandableListItem(record: CustomRecord) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (record.detail != null) expanded = !expanded }
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    color = ContentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // 展開時顯示詳情
                if (expanded && record.detail != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = record.detail,
                        color = ContentColor.copy(alpha = 0.6f),
                        fontSize = 10.sp
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
            } else {
                Text(
                    text = record.type.label,
                    color = record.type.color.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
            }
            // 有詳情的顯示展開提示
            if (record.detail != null) {
                Text(
                    text = if (expanded) "▲" else "▼",
                    color = ContentColor.copy(alpha = 0.3f),
                    fontSize = 8.sp
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

// ==================== 共用元件 ====================

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

// ==================== Mock Data ====================

private fun mockActivityMap() = mapOf(
    1 to 3, 2 to 1, 3 to 2, 5 to 4, 6 to 2, 7 to 3, 8 to 1,
    10 to 5, 11 to 2, 12 to 3, 13 to 4, 14 to 2, 15 to 6,
    18 to 3, 20 to 2, 22 to 4, 25 to 1, 28 to 3, 30 to 2
)

private fun mockRecords() = listOf(
    CustomRecord(
        type = CustomRecordType.HABIT,
        title = "完成晨間運動",
        time = "08:30",
        date = "今天"
    ),
    CustomRecord(
        type = CustomRecordType.EXPENSE,
        title = "早餐",
        time = "09:12",
        date = "今天",
        detail = "分類: 飲食 · 咖啡廳",
        amount = "-$120"
    ),
    CustomRecord(
        type = CustomRecordType.HABIT,
        title = "閱讀 30 分鐘",
        time = "21:00",
        date = "今天"
    ),
    CustomRecord(
        type = CustomRecordType.DIARY,
        title = "今天的心情",
        time = "22:30",
        date = "昨天",
        detail = "今天天氣很好，去了公園散步，心情也跟著好起來..."
    ),
    CustomRecord(
        type = CustomRecordType.EXPENSE,
        title = "晚餐",
        time = "18:45",
        date = "昨天",
        detail = "分類: 飲食 · 義式餐廳",
        amount = "-$250"
    )
)

// ==================== Previews ====================

@Preview(
    name = "Flow Custom - Version 1: Enhanced Hierarchy",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun FlowCustomVersion1Preview() {
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
        FlowCustomVersion1()
    }
}

@Preview(
    name = "Flow Custom - Version 2: Minimal B&W",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun FlowCustomVersion2Preview() {
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
        FlowCustomVersion2()
    }
}

@Preview(
    name = "Flow Custom - Version 3: Interactive Feedback",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun FlowCustomVersion3Preview() {
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
        FlowCustomVersion3()
    }
}

@Preview(
    name = "Flow Custom - Version 4: Dense Layout",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun FlowCustomVersion4Preview() {
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
        FlowCustomVersion4()
    }
}

@Preview(
    name = "Flow Custom - Version 5: Flexible Layout",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Composable
private fun FlowCustomVersion5Preview() {
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
        FlowCustomVersion5()
    }
}
