package com.simprints.intentlauncher.ui.events

import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.tools.extractEventsFromJson
import com.simprints.intentlauncher.ui.base.BaseMviViewModel
import com.simprints.intentlauncher.ui.events.model.EventDialogType
import com.simprints.intentlauncher.ui.events.model.EventSortingOption
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ResponseEventsScreenViewModel.Factory::class)
class ResponseEventsScreenViewModel @AssistedInject constructor(
    @Assisted private val intentId: String,
    @Assisted val initialSoftInputMode: Int,
    private val intentCallRepository: IntentCallRepository,
    private val gson: Gson,
) : BaseMviViewModel<ResponseEventsUiState, ResponseEventsUiAction, ResponseEventsUiEffect>(
        initialState = ResponseEventsUiState(
            events = emptyList(),
            totalEvents = 0,
            query = "",
            sorting = EventSortingOption.DateAsc,
        ),
    ) {
    @AssistedFactory
    interface Factory {
        fun create(
            intentId: String,
            initialSoftInputMode: Int,
        ): ResponseEventsScreenViewModel
    }

    init {
        loadEvents()
    }

    private var allEvents: List<ResponseEvent> = emptyList()

    override suspend fun handleAction(action: ResponseEventsUiAction) {
        when (action) {
            is ResponseEventsUiAction.SearchQueryChanged -> {
                updateFilteredState(
                    query = action.query,
                    sorting = state.value.sorting,
                )
            }

            is ResponseEventsUiAction.SortingChanged -> {
                updateFilteredState(
                    query = state.value.query,
                    sorting = action.sorting,
                    activeDialog = null,
                )
            }

            ResponseEventsUiAction.SortOptionsClicked -> {
                updateState {
                    it.copy(
                        activeDialog = EventDialogType.SortingOptions(EventSortingOption.entries),
                    )
                }
            }

            is ResponseEventsUiAction.EventOptionsClicked -> {
                updateState {
                    it.copy(activeDialog = EventDialogType.EventOptions(action.event))
                }
            }

            ResponseEventsUiAction.DialogDismissed -> {
                updateState { it.copy(activeDialog = null) }
            }

            is ResponseEventsUiAction.CopyIdClicked -> {
                emitEffect(
                    ResponseEventsUiEffect.CopyToClipboard(
                        label = "eventId",
                        value = action.id,
                    ),
                )
                emitEffect(ResponseEventsUiEffect.ShowToast("${action.id} copied to clipboard"))
            }

            is ResponseEventsUiAction.CopyEventJsonClicked -> {
                emitEffect(
                    ResponseEventsUiEffect.CopyToClipboard(
                        label = "eventJson",
                        value = gson.toJson(action.event),
                    ),
                )
                emitEffect(ResponseEventsUiEffect.ShowToast("Event JSON copied to clipboard"))
            }

            is ResponseEventsUiAction.CopyPayloadJsonClicked -> {
                emitEffect(
                    ResponseEventsUiEffect.CopyToClipboard(
                        label = "payloadJson",
                        value = gson.toJson(action.event.payload),
                    ),
                )
                emitEffect(ResponseEventsUiEffect.ShowToast("Payload JSON copied to clipboard"))
            }
        }
    }

    private fun loadEvents() = viewModelScope.launch {
        allEvents = intentCallRepository
            .getIntentCall(intentId)
            ?.result
            ?.json
            .extractEventsFromJson(gson)
        updateFilteredState(
            query = state.value.query,
            sorting = state.value.sorting,
        )
    }

    private fun updateFilteredState(
        query: String,
        sorting: EventSortingOption,
        activeDialog: EventDialogType? = state.value.activeDialog,
    ) {
        val filteredEvents = allEvents.filter { event ->
            if (query.isEmpty()) {
                true
            } else {
                event.id.contains(query, ignoreCase = true) ||
                    event.type.contains(query, ignoreCase = true)
            }
        }
        val sortedEvents = when (sorting) {
            EventSortingOption.DateAsc -> filteredEvents.sortedBy { it.createdAtMs }
            EventSortingOption.AsReceivedInResponse -> filteredEvents
        }
        updateState {
            it.copy(
                query = query,
                sorting = sorting,
                activeDialog = activeDialog,
                totalEvents = allEvents.size,
                events = sortedEvents,
            )
        }
    }
}
