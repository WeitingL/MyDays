package com.weiting.mydays.data.habit

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class HabitRemoteDataSource(
    private val firestore: FirebaseFirestore
) {
    private fun userDoc(uid: String): DocumentReference =
        firestore.collection("users").document(uid)

    /** batch 原子上傳：整筆 habit/checkIn + bump 帳號 lastUpdate（serverTimestamp）。 */
    suspend fun push(uid: String, habits: List<HabitEntity>, checkIns: List<CheckInEntity>) {
        val batch = firestore.batch()
        val user = userDoc(uid)
        habits.forEach { batch.set(user.collection("habits").document(it.id), it.toMap()) }
        checkIns.forEach { batch.set(user.collection("checkIns").document(it.id), it.toMap()) }
        batch.set(user, mapOf("lastUpdate" to FieldValue.serverTimestamp()), SetOptions.merge())
        batch.commit().await()
    }

    /** 讀帳號層級高水位；使用者空間尚未建立時為 null。 */
    suspend fun fetchLastUpdate(uid: String): Long? =
        userDoc(uid).get().await().getTimestamp("lastUpdate")?.toDate()?.time

    suspend fun fetchHabits(uid: String): List<HabitEntity> =
        userDoc(uid).collection("habits").get().await().documents.map { it.toHabitEntity() }

    suspend fun fetchCheckIns(uid: String): List<CheckInEntity> =
        userDoc(uid).collection("checkIns").get().await().documents.map { it.toCheckInEntity() }
}

private fun HabitEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "name" to name,
    "type" to type.name,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt,
    "deletedAt" to deletedAt
)

private fun CheckInEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id,
    "habitId" to habitId,
    "epochDay" to epochDay,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt,
    "deletedAt" to deletedAt
)

private fun DocumentSnapshot.toHabitEntity(): HabitEntity = HabitEntity(
    id = getString("id")!!,
    name = getString("name")!!,
    type = HabitType.valueOf(getString("type")!!),
    createdAt = getLong("createdAt")!!,
    updatedAt = getLong("updatedAt")!!,
    deletedAt = getLong("deletedAt"),
    pendingSync = false
)

private fun DocumentSnapshot.toCheckInEntity(): CheckInEntity = CheckInEntity(
    id = getString("id")!!,
    habitId = getString("habitId")!!,
    epochDay = getLong("epochDay")!!,
    createdAt = getLong("createdAt")!!,
    updatedAt = getLong("updatedAt")!!,
    deletedAt = getLong("deletedAt"),
    pendingSync = false
)
