package com.simprints.intentlauncher.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntentFields(
    val projectId: String = "",
    val moduleId: String = "",
    val userId: String = "",
    val metadata: String = "",
) : Parcelable
