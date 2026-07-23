package com.weiting.mydays

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.weiting.mydays.data.sync.SyncWorker
import com.weiting.mydays.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class MyDaysApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyDaysApp)
            modules(appModule)
        }
        scheduleSync()
    }

    private fun scheduleSync() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "habit_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
