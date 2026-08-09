package com.weiting.mydays.ui.habit

import com.weiting.mydays.data.habit.HabitType

/** 建立 / 編輯 dialog 的模式標籤 */
fun HabitType.modeLabel(): String = when (this) {
    HabitType.BUILD -> "每天都做"
    HabitType.QUIT -> "克制不做"
}

/** 天數顯示句 */
fun habitStreakText(type: HabitType, streak: Int): String = when {
    type == HabitType.BUILD && streak == 0 -> "今天開始"
    type == HabitType.BUILD && streak >= 1 -> "連續 $streak 天做了"
    type == HabitType.QUIT && streak == 0 -> "從今天重新開始"
    else -> "連續 $streak 天沒做"
}

/** 里程碑稱讚（輸入只有 streak，輸出 String?） */
fun milestoneText(streak: Int): String? = when {
    streak == 7 -> "連續 7 天了，很棒！"
    streak == 30 -> "30 天了。這已經變成習慣了。"
    streak == 100 -> "100 天。真的很了不起。"
    streak >= 200 && streak % 100 == 0 -> "$streak 天了。一直都在。"
    else -> null
}
