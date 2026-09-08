package com.weiting.mydays.preview.wireframes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Home Dashboard Wireframe
 *
 * 控制台頁面，顯示客製化 widget：
 * - QuickStatsCard: 快速統計（今日習慣達成率、本月記帳總額）
 * - ShortcutCard: 快速入口（新增習慣、新增記帳）
 * - TimelinePreview: 最近 5 筆紀錄預覽
 *
 * 使用 Liquid Glass 風格：
 * - 半透明白色背景（alpha 0.55f）
 * - 白色邊框（alpha 0.7f）
 * - 漸層背景（淺粉色 Sunrise 主題）
 * - 圓角 20dp
 */

// 定義玻璃卡片的基礎樣式
private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
    .padding(20.dp)

private val ContentColor = Color(0xFF2E2A45)

/**
 * 快速統計卡片
 * 顯示今日習慣達成率、本月記帳總額等數據
 */
@Composable
private fun QuickStatsCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.then(GlassCardModifier),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 標題
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = "Stats",
                tint = ContentColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "今日概況",
                color = ContentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 統計項目
        StatItem(
            label = "習慣達成率",
            value = "75%",
            subtitle = "6/8 完成"
        )
        StatItem(
            label = "今日記帳",
            value = "$450",
            subtitle = "3 筆記錄"
        )
    }
}

@Composable
private fun StatItem(label: String, value: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                color = ContentColor.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
            Text(
                text = subtitle,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
        Text(
            text = value,
            color = ContentColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 快速入口卡片
 * 提供新增習慣、新增記帳的快速入口
 */
@Composable
private fun ShortcutCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.then(GlassCardModifier),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "快速動作",
            color = ContentColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShortcutButton(
                icon = Icons.Default.CheckCircle,
                label = "新增習慣",
                modifier = Modifier.weight(1f)
            )
            ShortcutButton(
                icon = Icons.Default.Add,
                label = "新增記帳",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ShortcutButton(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.3f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ContentColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            color = ContentColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * 時間軸預覽卡片
 * 顯示最近 5 筆紀錄的摘要
 */
@Composable
private fun TimelinePreviewCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.then(GlassCardModifier),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 標題
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "最近動態",
                color = ContentColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "查看全部",
                color = ContentColor.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }

        // 紀錄列表
        TimelinePreviewItem(
            type = "習慣",
            title = "完成晨間運動",
            time = "08:30"
        )
        TimelinePreviewItem(
            type = "記帳",
            title = "午餐 - 便當",
            time = "12:15",
            amount = "-$80"
        )
        TimelinePreviewItem(
            type = "日記",
            title = "今天天氣真好...",
            time = "昨天 21:00"
        )
        TimelinePreviewItem(
            type = "習慣",
            title = "完成閱讀 30 分鐘",
            time = "昨天 20:00"
        )
        TimelinePreviewItem(
            type = "記帳",
            title = "交通 - 捷運",
            time = "昨天 09:00",
            amount = "-$20"
        )
    }
}

@Composable
private fun TimelinePreviewItem(
    type: String,
    title: String,
    time: String,
    amount: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 類型標籤
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    when (type) {
                        "習慣" -> Color(0xFF4CAF50)
                        "記帳" -> Color(0xFFFF9800)
                        "日記" -> Color(0xFF2196F3)
                        else -> ContentColor
                    }
                )
        )

        // 內容
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ContentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = time,
                color = ContentColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        // 金額（記帳專用）
        if (amount != null) {
            Text(
                text = amount,
                color = if (amount.startsWith("-")) Color(0xFFE53935) else Color(0xFF4CAF50),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Home Dashboard 完整畫面
 * 包含背景漸層 + 可捲動的 widget 列表
 */
@Composable
fun HomeDashboardWireframe(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFFE5EC),
                        Color(0xFFFFD6E8),
                        Color(0xFFFFF0C2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 頂部標題
            Text(
                text = "我的一天",
                color = ContentColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Widget 卡片
            QuickStatsCard()
            ShortcutCard()
            TimelinePreviewCard()
        }
    }
}

@Preview(name = "Home Dashboard", showBackground = true, widthDp = 360, heightDp = 740)
@Composable
private fun HomeDashboardWireframePreview() {
    HomeDashboardWireframe()
}
