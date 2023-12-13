package com.simprints.intentlauncher.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntentFields(
    val projectId: String = "",
    val moduleId: String = "",
    val userId: String = "",
) : Parcelable
