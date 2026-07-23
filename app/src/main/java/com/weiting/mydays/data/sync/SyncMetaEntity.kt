package com.weiting.mydays.data.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

// 單列表：固定 id = 0，只存一份帳號同步高水位
@Entity(tableName = "sync_meta")
data class SyncMetaEntity(
    @PrimaryKey val id: Int = 0,
    val lastSyncedAt: Long?
)
