package com.simprints.intentlauncher.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class IntentPresetEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: String,

    val name: String,
    val projectId: String,
    val moduleId: String,
    val userId: String,
    @ColumnInfo(defaultValue = "")
    val metadata: String,
)
