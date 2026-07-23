package com.weiting.mydays.data.sync

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SyncMetaDao {
    @Query("SELECT lastSyncedAt FROM sync_meta WHERE id = 0")
    suspend fun getLastSyncedAt(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun set(meta: SyncMetaEntity)
}
