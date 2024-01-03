package com.simprints.intentlauncher.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Preset(
    val id: String = "",
    val name: String = "",
    val fields: IntentFields = IntentFields(),
) : Parcelable
