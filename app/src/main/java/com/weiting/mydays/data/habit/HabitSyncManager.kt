package com.weiting.mydays.data.habit

import com.weiting.mydays.data.auth.AuthRepository
import com.weiting.mydays.data.sync.SyncMetaDao
import com.weiting.mydays.data.sync.SyncMetaEntity
import com.weiting.mydays.notification.HabitReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HabitSyncManager(
    private val remote: HabitRemoteDataSource,
    private val habitDao: HabitDao,
    private val checkInDao: CheckInDao,
    private val syncMetaDao: SyncMetaDao,
    private val authRepository: AuthRepository,
    private val reminderScheduler: HabitReminderScheduler,
    private val scope: CoroutineScope
) {
    /** 本地寫入後即時觸發（fire-and-forget）；離線失敗則旗標留給 Worker 補傳。 */
    fun schedulePush() {
        scope.launch { runCatching { pushPending() } }
    }

    /** 登入 / App 啟動觸發一次完整同步。 */
    fun scheduleSync() {
        scope.launch { runCatching { sync() } }
    }

    suspend fun sync() {
        pushPending()
        pull()
        // pull 後（可能含跨裝置同步來的提醒設定）在本機重排 alarm
        habitDao.getActiveWithReminder().forEach { habit ->
            habit.reminderMinuteOfDay?.let { reminderScheduler.schedule(habit.id, habit.name, it) }
        }
    }

    suspend fun pushPending() {
        val uid = authRepository.currentUser?.uid ?: return
        val habits = habitDao.getPending()
        val checkIns = checkInDao.getPending()
        if (habits.isEmpty() && checkIns.isEmpty()) return
        remote.push(uid, habits, checkIns)
        habitDao.clearPending(habits.map { it.id })
        checkInDao.clearPending(checkIns.map { it.id })
    }

    suspend fun pull() {
        val uid = authRepository.currentUser?.uid ?: return
        val cloudLastUpdate = remote.fetchLastUpdate(uid) ?: return
        val localSyncedAt = syncMetaDao.getLastSyncedAt()
        // 閘門：雲端沒有比上次讀到的更新 → 略過全量拉取
        if (localSyncedAt != null && cloudLastUpdate <= localSyncedAt) return

        remote.fetchHabits(uid).forEach { cloud ->
            val local = habitDao.getById(cloud.id)
            if (local == null || cloud.updatedAt > local.updatedAt) {
                habitDao.upsert(cloud)
            }
        }
        remote.fetchCheckIns(uid).forEach { cloud ->
            val local = checkInDao.getById(cloud.id)
            if (local == null || cloud.updatedAt > local.updatedAt) {
                checkInDao.upsert(cloud)
            }
        }
        syncMetaDao.set(SyncMetaEntity(lastSyncedAt = cloudLastUpdate))
    }
}
