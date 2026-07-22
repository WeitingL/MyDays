package com.weiting.mydays.data.habit

data class Habit(
    val id: String,
    val name: String,
    val type: HabitType,
    val createdAt: Long,
    val updatedAt: Long
)

fun HabitEntity.toDomain(): Habit = Habit(
    id = id,
    name = name,
    type = type,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    name = name,
    type = type,
    createdAt = createdAt,
    updatedAt = updatedAt
)
