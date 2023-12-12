package com.simprints.intentlauncher.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntentArgument(var key: String, var value: String) : Parcelable