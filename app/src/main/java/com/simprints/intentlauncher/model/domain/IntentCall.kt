package com.simprints.intentlauncher.model.domain

import android.content.Intent
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class IntentCall(
    val timestamp: String = "",
    val action: String = "",
    val extra: Map<String, String> = emptyMap(),
    val resultCode: Int? = null,
    val resultReceived: String? = null,
    val resultSessionId: String? = null,
) : Parcelable {

    constructor(timestamp: Date, intent: Intent) : this(
        timestamp = timestamp.toString(),
        action = intent.action.orEmpty(),
        extra = intent.extras?.let { extras ->
            // Assuming all provided values are strings, cos they are
            extras.keySet().associateWith { extras.getString(it).orEmpty() }
        } ?: emptyMap(),
    )
}
