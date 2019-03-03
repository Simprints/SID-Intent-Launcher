package com.simprints.simprintsidtester.model.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IntentArgument(var key: String, var value: String) : Parcelable