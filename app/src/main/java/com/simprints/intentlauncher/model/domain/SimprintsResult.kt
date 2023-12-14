package com.simprints.intentlauncher.model.domain

import java.util.*

data class SimprintsResult(
    val id: String = UUID.randomUUID().toString(),
    val dateTimeSent: String,
    val intentSent: String,
    val resultReceived: String
)