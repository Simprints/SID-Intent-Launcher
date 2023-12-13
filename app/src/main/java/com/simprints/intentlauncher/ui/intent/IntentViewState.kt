package com.simprints.intentlauncher.ui.intent

import android.content.Intent
import com.simprints.intentlauncher.model.domain.SimprintsIntent
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed
import java.util.Date

data class IntentViewState(
    val projectId: String = "",
    val userId: String = "",
    val moduleId: String = "",
    val guid: String = "",
    val sessionId: String = "",

    val result: String = "",

    val lastIntentSentTime: Date? = null,
    val lastSentIntent: SimprintsIntent? = null,

    val showIntent: StateEventWithContent<Intent> = consumed(),
)
