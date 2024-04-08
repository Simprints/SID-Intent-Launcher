package com.simprints.intentlauncher.ui.events.model

import com.simprints.intentlauncher.ui.intent.model.ResponseEvent

sealed class EventDialogType {
    data class SortingOptions(val sortingOptions: List<EventSortingOption>) : EventDialogType()
    data class EventOptions(val event: ResponseEvent) : EventDialogType()
}