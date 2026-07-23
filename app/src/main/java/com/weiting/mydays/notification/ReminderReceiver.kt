package com.weiting.mydays.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.weiting.mydays.data.habit.HabitDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val habitDao: HabitDao by inject()
    private val scheduler: HabitReminderScheduler by inject()
    private val notificationHelper: NotificationHelper by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra(HabitReminderScheduler.EXTRA_HABIT_ID) ?: return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val habit = habitDao.getById(habitId)
                // 已刪 / 停用 / 不存在 → 不發、不重排（自我清理殘留 alarm，涵蓋跨裝置刪除）
                val minute = habit?.reminderMinuteOfDay
                if (habit != null && habit.deletedAt == null && minute != null) {
                    notificationHelper.notify(habit.id, habit.name)
                    scheduler.schedule(habit.id, habit.name, minute)
                }
            } finally {
                pending.finish()
            }
        }
    }
}
