package com.simprints.intentlauncher.domain

import android.content.Intent
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class IntentCall(
    val id: String = "",
    val timestamp: String = "",
    val action: String = "",
    val extra: Map<String, String> = emptyMap(),
    val fields: IntentFields = IntentFields(),
    val result: IntentResult? = null,
) : Parcelable {

    constructor(timestamp: Date, intent: Intent, fields: IntentFields) : this(
        timestamp = timestamp.toString(),
        action = intent.action.orEmpty(),
        fields = fields,
        extra = intent.extras?.let { extras ->
            // Assuming all provided values are strings, cos they are
            extras.keySet().associateWith { extras.getString(it).orEmpty() }
        } ?: emptyMap(),
    )
}
