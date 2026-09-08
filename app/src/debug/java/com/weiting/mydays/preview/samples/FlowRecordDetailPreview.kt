package com.weiting.mydays.preview.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weiting.mydays.preview.navigation.PreviewNavigator

/**
 * FlowRecordDetail UI Spec
 *
 * 記錄詳細頁面（示範 preview navigation）：
 * - 顯示記錄完整內容
 * - 有返回按鈕
 * - 展示 PreviewNavigator 的用法
 */

private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))

private val ContentColor = Color(0xFF2E2A45)

/**
 * 記錄詳細頁面
 */
@Composable
fun RecordDetailScreen(
    recordId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 根據 recordId 找到對應的 record（這裡用 mock data）
    val record = FlowScreenMockData.createMockState().records.find { it.id == recordId }

    if (record == null) {
        // 找不到記錄
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFE0F7FA)),
            contentAlignment = Alignment.Center
        ) {
            Text("找不到記錄 #$recordId", color = ContentColor)
        }
        return
    }

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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 返回按鈕
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = ContentColor
                )
            }

            // 標題
            Text(
                text = "記錄詳情",
                color = ContentColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            // 詳細內容卡片
            RecordDetailCard(record)
        }
    }
}

/**
 * 記錄詳細內容卡片
 */
@Composable
private fun RecordDetailCard(record: Record) {
    Column(
        modifier = GlassCardModifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Icon + 類型
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val (icon, color, typeName) = when (record.type) {
                is RecordType.Habit -> Triple(Icons.Default.CheckCircle, Color(0xFF4CAF50), "習慣")
                is RecordType.Expense -> Triple(Icons.Default.AttachMoney, Color(0xFFFF9800), "記帳")
                is RecordType.Diary -> Triple(Icons.Default.Edit, Color(0xFF2196F3), "日記")
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column {
                Text(
                    text = typeName,
                    color = ContentColor.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = record.date,
                    color = ContentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 分隔線
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ContentColor.copy(alpha = 0.1f))
        )

        // 詳細資訊
        RecordDetailContent(record)

        // 時間
        DetailRow(label = "時間", value = record.time)
    }
}

/**
 * 記錄詳細內容（依類型顯示）
 */
@Composable
private fun RecordDetailContent(record: Record) {
    when (val type = record.type) {
        is RecordType.Habit -> {
            DetailRow(label = "習慣", value = type.name)
            DetailRow(
                label = "狀態",
                value = if (type.completed) "已完成" else "未完成"
            )
        }
        is RecordType.Expense -> {
            DetailRow(label = "分類", value = type.category)
            DetailRow(
                label = "金額",
                value = if (type.amount < 0) "-$${-type.amount}" else "+$${type.amount}"
            )
        }
        is RecordType.Diary -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "內容",
                    color = ContentColor.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Text(
                    text = type.preview,
                    color = ContentColor,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

/**
 * 詳細資訊行
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = ContentColor.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = ContentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Preview - 展示完整的 navigation 流程
 */
@Preview(name = "Flow with Navigation", showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun FlowScreenWithNavigationPreview() {
    PreviewNavigator(initialScreen = "flow") { currentScreen, navigateTo, onBack ->
        when {
            currentScreen == "flow" -> {
                val state = FlowScreenMockData.createMockState()
                val actions = object : FlowScreenActions {
                    override fun onFilterSelected(filter: FilterType) {}
                    override fun onRecordClicked(recordId: String) {
                        navigateTo("detail/$recordId")
                    }
                    override fun onDateCellClicked(date: Int) {}
                }
                FlowScreen(state = state, actions = actions)
            }
            currentScreen.startsWith("detail/") -> {
                val recordId = currentScreen.removePrefix("detail/")
                RecordDetailScreen(
                    recordId = recordId,
                    onBack = onBack
                )
            }
        }
    }
}

/**
 * Preview - 只看詳細頁面
 */
@Preview(name = "Record Detail", showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun RecordDetailScreenPreview() {
    RecordDetailScreen(
        recordId = "4",  // 日記
        onBack = {}
    )
}
