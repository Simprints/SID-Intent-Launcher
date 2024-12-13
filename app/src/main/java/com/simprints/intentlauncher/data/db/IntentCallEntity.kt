package com.simprints.intentlauncher.data.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class IntentCallEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: String,
    val intentAction: String,
    val intentExtras: String,
    @Embedded(prefix = "field_")
    val fields: IntentFieldsEntity,
    @Embedded(prefix = "result_")
    val result: IntentResultEntity?,
)

data class IntentFieldsEntity(
    val projectId: String,
    val moduleId: String,
    val userId: String,
    @ColumnInfo(defaultValue = "")
    val metadata: String,
)

data class IntentResultEntity(
    val code: Int,
    val json: String,
)
