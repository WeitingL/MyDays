package com.weiting.mydays.data.habit

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.weiting.mydays.data.sync.SyncMetaDao
import com.weiting.mydays.data.sync.SyncMetaEntity

@Database(
    entities = [HabitEntity::class, CheckInEntity::class, SyncMetaEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(HabitConverters::class)
abstract class MyDaysDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun checkInDao(): CheckInDao
    abstract fun syncMetaDao(): SyncMetaDao
}
