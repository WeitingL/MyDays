package com.weiting.mydays.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.ZoneId

class HabitReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    // 不精確鬧鐘（Q2=B）：免精確鬧鐘權限，容忍數分鐘誤差；one-shot，觸發後由 receiver 重排隔日
    fun schedule(habitId: String, name: String, minuteOfDay: Int) {
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            nextTriggerMillis(minuteOfDay),
            pendingIntent(habitId, name)
        )
    }

    fun cancel(habitId: String) {
        alarmManager.cancel(pendingIntent(habitId, null))
    }

    private fun pendingIntent(habitId: String, name: String?): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_HABIT_ID, habitId)
            name?.let { putExtra(EXTRA_HABIT_NAME, it) }
        }
        // PendingIntent 以 requestCode + filterEquals（不含 extras）辨識，故 cancel 傳同 id 即可對上
        return PendingIntent.getBroadcast(
            context,
            habitId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    // 今天該時刻若已過 → 排明天
    private fun nextTriggerMillis(minuteOfDay: Int): Long {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(minuteOfDay / 60, minuteOfDay % 60)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    companion object {
        const val EXTRA_HABIT_ID = "habit_id"
        const val EXTRA_HABIT_NAME = "habit_name"
    }
}
