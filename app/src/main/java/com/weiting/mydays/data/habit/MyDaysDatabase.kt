package com.weiting.mydays.data.habit

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HabitEntity::class, CheckInEntity::class], version = 2, exportSchema = false)
@TypeConverters(HabitConverters::class)
abstract class MyDaysDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun checkInDao(): CheckInDao
}
