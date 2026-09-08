package com.weiting.mydays.ui.habit.record

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Habit Flow Record Data
 *
 * 定義時間軸記錄的資料結構
 */
data class HabitFlowRecordData(
    val id: String,
    val timestamp: Long,
    val habitName: String,
    val action: HabitAction,
    val detail: String? = null
)

/**
 * Habit Action Type
 */
enum class HabitAction(val label: String, val icon: ImageVector) {
    CheckIn("完成打卡", Icons.Default.CheckCircle),
    Undo("取消打卡", Icons.Default.CheckCircle),
    RecordOccurrence("記錄發生", Icons.Default.CheckCircle)
}
