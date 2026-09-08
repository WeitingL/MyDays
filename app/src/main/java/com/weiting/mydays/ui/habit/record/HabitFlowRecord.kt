package com.weiting.mydays.ui.habit.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weiting.mydays.ui.component.SettingContentColor
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Habit Flow Record Card
 *
 * 在 Flow 時間軸中顯示習慣記錄
 */
@Composable
fun HabitFlowRecordCard(
    record: HabitFlowRecordData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.55f))
            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left: Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF4CAF50).copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = record.action.icon,
                contentDescription = record.action.label,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
        }

        // Center: Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${record.action.label}：${record.habitName}",
                color = SettingContentColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (record.detail != null) {
                Text(
                    text = record.detail,
                    color = SettingContentColor.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
            Text(
                text = formatTimestamp(record.timestamp),
                color = SettingContentColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Format timestamp to readable string
 */
private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val formatter = DateTimeFormatter.ofPattern("今天 HH:mm")
        .withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
