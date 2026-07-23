package com.weiting.mydays.data.habit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Insert
    suspend fun insert(habit: HabitEntity)

    @Update
    suspend fun update(habit: HabitEntity)

    // reconcile 用：以雲端整筆覆蓋本地（存在即更新，否則插入）
    @Upsert
    suspend fun upsert(habit: HabitEntity)

    @Query("UPDATE habits SET deletedAt = :now, updatedAt = :now, pendingSync = 1 WHERE id = :id")
    suspend fun softDelete(id: String, now: Long)

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: String): HabitEntity?

    // 活躍清單（管理頁 / streak）：不含已刪
    @Query("SELECT * FROM habits WHERE deletedAt IS NULL ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<HabitEntity>>

    // 同步用：撈待上傳 / 清旗標
    @Query("SELECT * FROM habits WHERE pendingSync = 1")
    suspend fun getPending(): List<HabitEntity>

    @Query("UPDATE habits SET pendingSync = 0 WHERE id IN (:ids)")
    suspend fun clearPending(ids: List<String>)
}
