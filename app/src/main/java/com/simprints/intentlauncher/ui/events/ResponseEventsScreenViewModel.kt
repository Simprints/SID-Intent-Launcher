package com.simprints.intentlauncher.ui.events

import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.tools.extractEventsFromJson
import com.simprints.intentlauncher.ui.events.model.EventSortingOption
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

@HiltViewModel(assistedFactory = ResponseEventsScreenViewModel.Factory::class)
class ResponseEventsScreenViewModel @AssistedInject constructor(
    @Assisted private val intentId: String,
    @Assisted val initialSoftInputMode: Int,
    private val intentCallRepository: IntentCallRepository,
    private val gson: Gson,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(
            intentId: String,
            initialSoftInputMode: Int,
        ): ResponseEventsScreenViewModel
    }

    val initialViewState =
        ResponseEventsViewState(
            events = emptyList(),
            totalEvents = 0,
            query = "",
            sorting = EventSortingOption.DateAsc,
        )
    private val allEventsFlow: MutableStateFlow<List<ResponseEvent>?> = MutableStateFlow(null)
    private val searchQueryFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    private val sortFlow: MutableStateFlow<EventSortingOption> =
        MutableStateFlow(initialViewState.sorting)
    val state = combine(allEventsFlow, sortFlow, searchQueryFlow) { allEvents, sorting, query ->
        val events = allEvents ?: intentCallRepository
            .getIntentCall(intentId)
            ?.result
            ?.json
            .extractEventsFromJson(gson)
            .also { allEventsFlow.value = it }
        val filteredEvents = events.filter { event ->
            if (query.isNullOrEmpty()) {
                true
            } else {
                event.id.contains(query, ignoreCase = true) ||
                    event.type.contains(
                        query,
                        ignoreCase = true,
                    )
            }
        }
        val sortedEvents = when (sorting) {
            EventSortingOption.DateAsc -> filteredEvents.sortedBy { it.createdAtMs }
            EventSortingOption.AsReceivedInResponse -> filteredEvents
        }
        return@combine ResponseEventsViewState(
            events = sortedEvents,
            totalEvents = events.size,
            query = query.orEmpty(),
            sorting = sorting,
        )
    }

    fun setSorting(sorting: EventSortingOption) {
        sortFlow.value = sorting
    }

    fun setSearchQuery(query: String?) {
        searchQueryFlow.value = query
    }

    fun serializeEventToJson(event: ResponseEvent): String? = try {
        gson.toJson(event)
    } catch (e: Exception) {
        null
    }

    fun serializePayloadToJson(event: ResponseEvent): String? = try {
        gson.toJson(event.payload)
    } catch (e: Exception) {
        null
    }
}
