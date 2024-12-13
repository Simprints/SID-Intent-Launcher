package com.simprints.intentlauncher.ui.custom

import android.content.Intent
import android.os.Parcelable
import com.simprints.intentlauncher.domain.IntentCall
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomIntentViewState(
    val action: String = "",
    val extras: String = "",
    val result: String = "",
    val lastIntentCall: IntentCall? = null,
    @IgnoredOnParcel
    val showIntent: StateEventWithContent<Intent> = consumed(),
) : Parcelable
