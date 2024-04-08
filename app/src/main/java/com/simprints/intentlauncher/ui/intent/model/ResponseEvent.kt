package com.simprints.intentlauncher.ui.intent.model


data class ResponseEvent(
    val id: String,
    val type: String,
    val scopeId: String,
    val projectId: String,
    val createdAtMs: Long,
    val payload: Map<String, Any>
)