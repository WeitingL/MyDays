package com.weiting.mydays.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.weiting.mydays.MainActivity
import com.weiting.mydays.R

class NotificationHelper(private val context: Context) {

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "習慣提醒",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    // 未授權 POST_NOTIFICATIONS 時 NotificationManagerCompat 會靜默略過（不 crash、不顯示）
    fun notify(habitId: String, name: String) {
        val intent = Intent(context, MainActivity::class.java)
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val contentIntent = PendingIntent.getActivity(
            context,
            habitId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(name)
            .setContentText("該完成「$name」了")
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()
        NotificationManagerCompat.from(context).notify(habitId.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ID = "habit_reminder"
    }
}
