package com.simprints.intentlauncher.ui.custom

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.google.gson.Gson
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentResult
import com.simprints.intentlauncher.domain.IntentResult.Companion.INVALID_CUSTOM_INTENT
import com.simprints.intentlauncher.domain.IntentResultParser
import io.mockk.coEvery
import io.mockk.coVerify
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
class CustomIntentViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle = SavedStateHandle()
    private val projectDataCache = mockk<ProjectDataCache>()
    private val intentCallRepository = mockk<IntentCallRepository>()
    private val intentResultParser = IntentResultParser(Gson())

    @Test
    fun `action and extras actions update state`() = runTest {
        val vm = createViewModel()

        vm.onAction(CustomIntentUiAction.ActionChanged("my.action"))
        vm.onAction(CustomIntentUiAction.ExtrasChanged("a=1\nb=2"))
        advanceUntilIdle()

        assertEquals("my.action", vm.state.value.action)
        assertEquals("a=1\nb=2", vm.state.value.extras)
    }

    @Test
    fun `load cached fields hydrates state`() = runTest {
        coEvery { projectDataCache.getCustomAction() } returns "cached.action"
        coEvery { projectDataCache.getCustomExtras() } returns "k=v"

        val vm = createViewModel()
        vm.onAction(CustomIntentUiAction.LoadCachedFields)
        advanceUntilIdle()

        assertEquals("cached.action", vm.state.value.action)
        assertEquals("k=v", vm.state.value.extras)
    }

    @Test
    fun `launch clicked caches input`() = runTest {
        coEvery { projectDataCache.saveCustomIntent(any(), any()) } returns mockk<Preferences>()
        val vm = createViewModel()
        vm.onAction(CustomIntentUiAction.ActionChanged("com.test.ACTION"))
        vm.onAction(CustomIntentUiAction.ExtrasChanged("k=v\nfoo=bar"))
        vm.onAction(CustomIntentUiAction.LaunchClicked)
        advanceUntilIdle()

        coVerify(exactly = 1) { projectDataCache.saveCustomIntent("com.test.ACTION", "k=v\nfoo=bar") }
    }

    @Test
    fun `non invalid result persists pending call and updates result`() = runTest {
        coEvery { projectDataCache.saveCustomIntent(any(), any()) } returns mockk<Preferences>()
        coEvery { intentCallRepository.save(any(), any()) } answers { firstArg<IntentCall>() }

        val vm = createViewModel()
        vm.onAction(CustomIntentUiAction.ActionChanged("com.test.ACTION"))
        vm.onAction(CustomIntentUiAction.LaunchClicked)
        advanceUntilIdle()

        vm.onAction(CustomIntentUiAction.IntentResultReceived(200 to null))
        advanceUntilIdle()

        assertTrue(vm.state.value.result.contains("200"))
        assertTrue(vm.state.value.result.contains("null"))
        coVerify(exactly = 1) { intentCallRepository.save(any(), any()) }
    }

    @Test
    fun `invalid custom intent does not persist call`() = runTest {
        val vm = createViewModel()
        vm.onAction(CustomIntentUiAction.IntentResultReceived(INVALID_CUSTOM_INTENT to null))
        advanceUntilIdle()

        coVerify(exactly = 0) { intentCallRepository.save(any(), any()) }
    }

    @Test
    fun `clear fields resets state`() = runTest {
        coEvery { projectDataCache.clearCustomIntent() } returns mockk<Preferences>()
        val vm = createViewModel()
        vm.onAction(CustomIntentUiAction.ActionChanged("my.action"))
        vm.onAction(CustomIntentUiAction.ExtrasChanged("a=1"))
        advanceUntilIdle()

        vm.onAction(CustomIntentUiAction.ClearFieldsClicked)
        advanceUntilIdle()

        assertEquals("", vm.state.value.action)
        assertEquals("", vm.state.value.extras)
        assertEquals("", vm.state.value.result)
    }

    private fun createViewModel(handle: SavedStateHandle = savedStateHandle) = CustomIntentViewModel(
        savedStateHandle = handle,
        projectDataCache = projectDataCache,
        intentCallRepository = intentCallRepository,
        intentResultParser = intentResultParser,
    )
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
