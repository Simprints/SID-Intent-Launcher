package com.simprints.intentlauncher.ui.intent

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent
import com.simprints.libsimprints.contracts.SimprintsRequest
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class IntentViewState(
    val projectId: String = "",
    val userId: String = "",
    val moduleId: String = "",
    val metadata: String = "",
    val guid: String = "",
    val sessionId: String = "",
    val result: String = "",
    val responseJson: String = "",
    val responseIntentId: String? = null,
    val lastIntentCall: IntentCall? = null,
    @IgnoredOnParcel
    val showWrongMetadataAlert: MutableState<Boolean> = mutableStateOf(false),
    @IgnoredOnParcel
    val showIntent: StateEventWithContent<SimprintsRequest> = consumed(),
    @IgnoredOnParcel
    val events: List<ResponseEvent> = emptyList(),
) : Parcelable
