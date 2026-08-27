package com.simprints.intentlauncher.ui.events

import com.simprints.intentlauncher.ui.base.UiAction
import com.simprints.intentlauncher.ui.base.UiEffect
import com.simprints.intentlauncher.ui.base.UiState
import com.simprints.intentlauncher.ui.events.model.EventDialogType
import com.simprints.intentlauncher.ui.events.model.EventSortingOption
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent

data class ResponseEventsUiState(
    val events: List<ResponseEvent>,
    val totalEvents: Int,
    val query: String,
    val sorting: EventSortingOption,
    val activeDialog: EventDialogType? = null,
) : UiState

sealed interface ResponseEventsUiAction : UiAction {
    data object LoadEvents : ResponseEventsUiAction
    data class SearchQueryChanged(val query: String) : ResponseEventsUiAction
    data class SortingChanged(val sorting: EventSortingOption) : ResponseEventsUiAction
    data object SortOptionsClicked : ResponseEventsUiAction
    data class EventOptionsClicked(val event: ResponseEvent) : ResponseEventsUiAction
    data object DialogDismissed : ResponseEventsUiAction
    data class CopyIdClicked(val id: String) : ResponseEventsUiAction
    data class CopyEventJsonClicked(val event: ResponseEvent) : ResponseEventsUiAction
    data class CopyPayloadJsonClicked(val event: ResponseEvent) : ResponseEventsUiAction
}

sealed interface ResponseEventsUiEffect : UiEffect {
    data class CopyToClipboard(
        val label: String,
        val value: String,
    ) : ResponseEventsUiEffect

    data class ShowToast(
        val message: String,
    ) : ResponseEventsUiEffect
}
