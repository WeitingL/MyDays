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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
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

/**
 * 日期進度方塊變體
 *
 * 提供 4 種不同的空間利用方案：
 * - Version A: 緊湊網格（更小方塊 + 更小間距）
 * - Version B: 橫條圖（改用長條圖代替網格）
 * - Version C: 極簡摘要（只顯示統計數字）
 * - Version D: 迷你月曆（類似 calendar widget）
 */

private val GlassCardModifier = Modifier
    .fillMaxWidth()
    .clip(RoundedCornerShape(20.dp))
    .background(Color.White.copy(alpha = 0.55f))
    .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))

private val ContentColor = Color(0xFF2E2A45)

// 活動級別顏色
private fun getActivityColor(level: Int): Color = when {
    level == 0 -> Color(0xFFEEEEEE)
    level <= 2 -> Color(0xFFAED581)
    level <= 4 -> Color(0xFF66BB6A)
    else -> Color(0xFF2E7D32)
}

/**
 * Version A - 緊湊網格
 *
 * 特點：
 * - 方塊從 36dp → 8dp（省 78% 空間）
 * - 間距從 6dp → 2dp（省 67% 空間）
 * - 只顯示最近 30 天（6×5 網格）
 * - 不顯示日期數字，只用顏色
 * - 整體高度約為原版的 40%
 *
 * 適合：希望保留視覺化但節省空間
 */
@Composable
fun DateProgressCompactGrid(
    activityMap: Map<Int, Int> = mockActivityMap(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.then(GlassCardModifier).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 標題 + 統計（小字）
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

        // 緊湊網格（6×5 = 30 天）
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

        // 圖例（極小）
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "少",
                color = ContentColor.copy(alpha = 0.4f),
                fontSize = 8.sp
            )
            Spacer(modifier = Modifier.width(3.dp))
            for (i in 0..3) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(getActivityColor(i * 2))
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Text(
                text = "多",
                color = ContentColor.copy(alpha = 0.4f),
                fontSize = 8.sp
            )
        }
    }
}

/**
 * Version B - 橫條圖
 *
 * 特點：
 * - 改用水平長條圖（每天一條）
 * - 長度代表活動量
 * - 只顯示最近 7 天
 * - 垂直空間約為原版的 50%
 * - 更容易看出趨勢
 *
 * 適合：更關注趨勢而非完整月曆
 */
@Composable
fun DateProgressBarChart(
    recentDays: List<Pair<String, Int>> = mockRecentDays(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.then(GlassCardModifier).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 標題
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

        // 長條圖列表
        recentDays.forEach { (day, count) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 日期標籤
                Text(
                    text = day,
                    color = ContentColor.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    modifier = Modifier.width(32.dp)
                )

                // 長條
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(12.dp)
                ) {
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

                // 數字
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

/**
 * Version C - 極簡摘要
 *
 * 特點：
 * - 只顯示統計數字（本週/本月有記錄天數）
 * - 不顯示網格或圖表
 * - 用 1-2 行文字 + icon
 * - 垂直空間約為原版的 20%
 * - 最省空間
 *
 * 適合：螢幕空間極度有限，只需要知道大致活動量
 */
@Composable
fun DateProgressMinimalSummary(
    weekCount: Int = 5,
    monthCount: Int = 18,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .then(GlassCardModifier)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(28.dp)
        )

        // 統計文字
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

        // 百分比圓環（簡化版：只用文字）
        Column(
            horizontalAlignment = Alignment.End
        ) {
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

/**
 * Version D - 迷你月曆
 *
 * 特點：
 * - 類似 calendar widget 的緊湊月曆
 * - 更小的日期數字（8sp）
 * - 有記錄的日期加圓點標記
 * - 取消彩色背景，只留標記
 * - 垂直空間約為原版的 60%
 *
 * 適合：想保留完整月曆但更緊湊
 */
@Composable
fun DateProgressMiniCalendar(
    activityMap: Map<Int, Int> = mockActivityMap(),
    currentMonth: String = "8月",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.then(GlassCardModifier).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 標題
        Text(
            text = "$currentMonth 活動",
            color = ContentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        // 星期標籤
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                Text(
                    text = day,
                    color = ContentColor.copy(alpha = 0.4f),
                    fontSize = 8.sp,
                    modifier = Modifier.width(20.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // 日期網格（5 週）
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            for (row in 0 until 5) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until 7) {
                        val day = row * 7 + col + 1
                        if (day <= 31) {
                            val hasActivity = activityMap.containsKey(day)
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(1.dp)
                                ) {
                                    Text(
                                        text = day.toString(),
                                        color = if (hasActivity) ContentColor
                                        else ContentColor.copy(alpha = 0.3f),
                                        fontSize = 8.sp,
                                        fontWeight = if (hasActivity) FontWeight.Bold
                                        else FontWeight.Normal
                                    )
                                    if (hasActivity) {
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    getActivityColor(activityMap[day] ?: 0)
                                                )
                                        )
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

// Mock data
private fun mockActivityMap() = mapOf(
    1 to 3, 2 to 1, 3 to 2, 5 to 4, 6 to 2, 7 to 3, 8 to 1,
    10 to 5, 11 to 2, 12 to 3, 13 to 4, 14 to 2, 15 to 6,
    18 to 3, 20 to 2, 22 to 4, 25 to 1, 28 to 3, 30 to 2
)

private fun mockRecentDays() = listOf(
    "8/9" to 2,
    "8/10" to 5,
    "8/11" to 2,
    "8/12" to 3,
    "8/13" to 4,
    "8/14" to 2,
    "8/15" to 6
)

// Previews
@Preview(name = "Date Progress - Version A: Compact Grid", widthDp = 360)
@Composable
private fun DateProgressCompactGridPreview() {
    Box(
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
        DateProgressCompactGrid()
    }
}

@Preview(name = "Date Progress - Version B: Bar Chart", widthDp = 360)
@Composable
private fun DateProgressBarChartPreview() {
    Box(
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
        DateProgressBarChart()
    }
}

@Preview(name = "Date Progress - Version C: Minimal Summary", widthDp = 360)
@Composable
private fun DateProgressMinimalSummaryPreview() {
    Box(
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
        DateProgressMinimalSummary()
    }
}

@Preview(name = "Date Progress - Version D: Mini Calendar", widthDp = 360)
@Composable
private fun DateProgressMiniCalendarPreview() {
    Box(
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
        DateProgressMiniCalendar()
    }
}
