package com.simprints.intentlauncher.ui.events

import com.simprints.intentlauncher.ui.events.model.EventSortingOption
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent

data class ResponseEventsViewState(
    val events: List<ResponseEvent>,
    val totalEvents: Int,
    val query: String,
    val sorting: EventSortingOption
)