package com.simprints.intentlauncher.model.domain

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class SimprintsIntent(
    var id: String = UUID.randomUUID().toString(),
    var name: String = "",
    var action: String = "",
    var extra: MutableList<IntentArgument> = mutableListOf()
) : Parcelable

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