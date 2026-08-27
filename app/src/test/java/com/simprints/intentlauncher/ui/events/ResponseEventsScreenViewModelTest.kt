package com.simprints.intentlauncher.ui.events

import app.cash.turbine.test
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentResult
import com.simprints.intentlauncher.ui.events.model.EventDialogType
import com.simprints.intentlauncher.ui.events.model.EventSortingOption
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class ResponseEventsScreenViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val intentCallRepository = mockk<IntentCallRepository>()
    private val gson: Gson = GsonBuilder()
        .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
        .create()

    @Test
    fun `load events populates and applies default sorting`() = runTest {
        coEvery { intentCallRepository.getIntentCall("intent-id") } returns IntentCall(
            id = "intent-id",
            result = IntentResult(json = eventsJson()),
        )
        val vm = createViewModel()

        vm.onAction(ResponseEventsUiAction.LoadEvents)
        advanceUntilIdle()

        assertEquals(2, vm.state.value.totalEvents)
        assertEquals(2, vm.state.value.events.size)
        assertEquals("id-2", vm.state.value.events[0].id)
        assertEquals("id-1", vm.state.value.events[1].id)
    }

    @Test
    fun `search query filters event list`() = runTest {
        coEvery { intentCallRepository.getIntentCall("intent-id") } returns IntentCall(
            id = "intent-id",
            result = IntentResult(json = eventsJson()),
        )
        val vm = createViewModel()
        vm.onAction(ResponseEventsUiAction.LoadEvents)
        advanceUntilIdle()

        vm.onAction(ResponseEventsUiAction.SearchQueryChanged("id-1"))
        advanceUntilIdle()

        assertEquals(1, vm.state.value.events.size)
        assertEquals("id-1", vm.state.value.events.first().id)
    }

    @Test
    fun `sorting changed to as received preserves response order`() = runTest {
        coEvery { intentCallRepository.getIntentCall("intent-id") } returns IntentCall(
            id = "intent-id",
            result = IntentResult(json = eventsJson()),
        )
        val vm = createViewModel()
        vm.onAction(ResponseEventsUiAction.LoadEvents)
        advanceUntilIdle()

        vm.onAction(ResponseEventsUiAction.SortingChanged(EventSortingOption.AsReceivedInResponse))
        advanceUntilIdle()

        assertEquals("id-1", vm.state.value.events[0].id)
        assertEquals("id-2", vm.state.value.events[1].id)
    }

    @Test
    fun `dialog actions update active dialog state`() = runTest {
        val vm = createViewModel()
        val event = ResponseEvent(
            id = "id-1",
            type = "TYPE",
            scopeId = "scope",
            projectId = "project",
            createdAtMs = 1L,
            payload = emptyMap(),
        )

        vm.onAction(ResponseEventsUiAction.SortOptionsClicked)
        advanceUntilIdle()
        assertTrue(vm.state.value.activeDialog is EventDialogType.SortingOptions)

        vm.onAction(ResponseEventsUiAction.EventOptionsClicked(event))
        advanceUntilIdle()
        assertTrue(vm.state.value.activeDialog is EventDialogType.EventOptions)

        vm.onAction(ResponseEventsUiAction.DialogDismissed)
        advanceUntilIdle()
        assertEquals(null, vm.state.value.activeDialog)
    }

    @Test
    fun `copy id emits clipboard and toast effects`() = runTest {
        val vm = createViewModel()

        vm.effects.test {
            vm.onAction(ResponseEventsUiAction.CopyIdClicked("id-1"))
            advanceUntilIdle()

            assertEquals(
                ResponseEventsUiEffect.CopyToClipboard("eventId", "id-1"),
                awaitItem(),
            )
            assertEquals(
                ResponseEventsUiEffect.ShowToast("id-1 copied to clipboard"),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = ResponseEventsScreenViewModel(
        intentId = "intent-id",
        initialSoftInputMode = 0,
        intentCallRepository = intentCallRepository,
        gson = gson,
    )

    private fun eventsJson(): String = """
        {
          "events":"{\"events\":[{\"id\":\"id-1\",\"type\":\"ONE_TO_MANY_MATCH\",\"scopeId\":\"scope-1\",\"projectId\":\"project-1\",\"payload\":{\"createdAt\":{\"ms\":5}}},{\"id\":\"id-2\",\"type\":\"ONE_TO_MANY_MATCH\",\"scopeId\":\"scope-2\",\"projectId\":\"project-2\",\"payload\":{\"createdAt\":{\"ms\":1}}}]}"
        }
    """.trimIndent()
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
