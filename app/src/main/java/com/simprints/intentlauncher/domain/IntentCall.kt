package com.simprints.intentlauncher.domain

import android.content.Intent
import android.os.Parcelable
import com.simprints.intentlauncher.tools.toIsoString
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class IntentCall(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String = "",
    val action: String = "",
    val extra: Map<String, String> = emptyMap(),
    val fields: IntentFields = IntentFields(),
    val result: IntentResult? = null,
) : Parcelable {
    constructor(timestamp: Date, intent: Intent) : this(timestamp, intent, IntentFields())

    constructor(timestamp: Date, intent: Intent, fields: IntentFields) : this(
        timestamp = timestamp.toIsoString(),
        action = intent.action.orEmpty(),
        fields = fields,
        extra = intent.extras?.let { extras ->
            // Assuming all provided values are strings, cos they are
            extras.keySet().associateWith { extras.getString(it).orEmpty() }
        } ?: emptyMap(),
    )
}
