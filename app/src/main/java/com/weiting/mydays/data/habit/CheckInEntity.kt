package com.weiting.mydays.data.habit

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "check_ins",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId", "epochDay"], unique = true)]
)
data class CheckInEntity(
    @PrimaryKey val id: String,
    val habitId: String,
    val epochDay: Long,
    val createdAt: Long
)
