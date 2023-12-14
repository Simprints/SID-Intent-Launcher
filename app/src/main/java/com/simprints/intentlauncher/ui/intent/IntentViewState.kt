package com.simprints.intentlauncher.ui.intent

import android.content.Intent
import android.os.Parcelable
import com.simprints.intentlauncher.domain.IntentCall
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntentViewState(
    val projectId: String = "",
    val userId: String = "",
    val moduleId: String = "",
    val guid: String = "",
    val sessionId: String = "",
    val result: String = "",

    val lastIntentCall: IntentCall? = null,

    @IgnoredOnParcel
    val showIntent: StateEventWithContent<Intent> = consumed(),
) : Parcelable
