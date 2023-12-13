package com.simprints.intentlauncher.model.domain

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class SimprintsIntent(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var action: String = "",
    var extra: MutableList<IntentArgument> = mutableListOf(),
) : Parcelable {

    constructor(intent: Intent) : this(
        name = intent.action.orEmpty(),
        action = intent.action.orEmpty(),
        extra = intent.extras?.let { extras ->
            extras.keySet()
                .mapNotNull { key ->
                    // Assuming all provided values are strings, cos they are
                    extras.getString(key)?.let { IntentArgument(key, it) }
                }
                .toMutableList()
        } ?: mutableListOf(),
    )
}

fun SimprintsIntent.toIntent(): Intent =
    Intent(action).apply {
        this.putExtras(
            Bundle().apply {
                extra.map {
                    putString(it.key, it.value)
                }
            }
        )
    }