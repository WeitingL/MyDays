package com.weiting.mydays.data.habit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Insert
    suspend fun insert(checkIn: CheckInEntity)

    // 復活（同日重新打卡）與 reconcile 用
    @Upsert
    suspend fun upsert(checkIn: CheckInEntity)

    @Query("UPDATE check_ins SET deletedAt = :now, updatedAt = :now, pendingSync = 1 WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun softDeleteByHabitAndDay(habitId: String, epochDay: Long, now: Long)

    // 取某日該習慣的打卡（含 tombstone），供判斷新增 vs 復活
    @Query("SELECT * FROM check_ins WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun getByHabitAndDay(habitId: String, epochDay: Long): CheckInEntity?

    @Query("SELECT * FROM check_ins WHERE id = :id")
    suspend fun getById(id: String): CheckInEntity?

    // 活躍打卡（streak 計算）：不含已刪
    @Query("SELECT * FROM check_ins WHERE deletedAt IS NULL")
    fun observeAll(): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM check_ins WHERE pendingSync = 1")
    suspend fun getPending(): List<CheckInEntity>

    @Query("UPDATE check_ins SET pendingSync = 0 WHERE id IN (:ids)")
    suspend fun clearPending(ids: List<String>)
}
