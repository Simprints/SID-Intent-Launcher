package com.simprints.intentlauncher.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class IntentCallEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: String,

    val intentAction: String,
    val intentExtras: String,

    val resultCode: Int,
    val resultReceived: String,
)
