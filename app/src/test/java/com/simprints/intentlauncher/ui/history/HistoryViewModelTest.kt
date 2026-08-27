package com.simprints.intentlauncher.ui.history

import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = mockk<IntentCallRepository>()

    @Test
    fun `history state updates from repository flow when started`() = runTest {
        val callsFlow = MutableStateFlow(emptyList<IntentCall>())
        every { repository.getAllIntentCalls() } returns callsFlow

        val vm = HistoryViewModel(repository)
        advanceUntilIdle()
        assertEquals(0, vm.state.value.items.size)

        callsFlow.value = listOf(IntentCall(id = "one"), IntentCall(id = "two"))
        advanceUntilIdle()

        assertEquals(2, vm.state.value.items.size)
        assertEquals(
            "one",
            vm.state.value.items[0]
                .id,
        )
        assertEquals(
            "two",
            vm.state.value.items[1]
                .id,
        )
    }

    @Test
    fun `history collection keeps observing repository updates after start`() = runTest {
        every { repository.getAllIntentCalls() } returns flow {
            emit(listOf(IntentCall(id = "one")))
            emit(listOf(IntentCall(id = "two")))
        }

        val vm = HistoryViewModel(repository)
        advanceUntilIdle()

        assertEquals(1, vm.state.value.items.size)
        assertEquals(
            "two",
            vm.state.value.items[0]
                .id,
        )
    }
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
