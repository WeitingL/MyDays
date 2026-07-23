package com.weiting.mydays.data.habit

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(checkIn: CheckInEntity)

    @Query("DELETE FROM check_ins WHERE habitId = :habitId AND epochDay = :epochDay")
    suspend fun deleteByHabitAndDay(habitId: String, epochDay: Long)

    @Query("SELECT * FROM check_ins")
    fun observeAll(): Flow<List<CheckInEntity>>
}
