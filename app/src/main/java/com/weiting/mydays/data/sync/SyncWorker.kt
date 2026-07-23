package com.weiting.mydays.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.weiting.mydays.data.habit.HabitSyncManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val syncManager: HabitSyncManager by inject()

    override suspend fun doWork(): Result =
        runCatching { syncManager.sync() }
            .fold(onSuccess = { Result.success() }, onFailure = { Result.retry() })
}
