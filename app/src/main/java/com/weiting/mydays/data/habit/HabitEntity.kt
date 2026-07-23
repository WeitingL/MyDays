package com.weiting.mydays.data.habit

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class HabitType { BUILD, QUIT }

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: HabitType,
    val createdAt: Long,
    val updatedAt: Long,
    val reminderMinuteOfDay: Int? = null,
    val deletedAt: Long? = null,
    val pendingSync: Boolean = false
)

class HabitConverters {
    @TypeConverter
    fun fromHabitType(type: HabitType): String = type.name

    @TypeConverter
    fun toHabitType(value: String): HabitType = HabitType.valueOf(value)
}
