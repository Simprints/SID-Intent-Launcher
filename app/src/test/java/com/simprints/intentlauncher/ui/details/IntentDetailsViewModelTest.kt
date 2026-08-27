package com.simprints.intentlauncher.ui.details

import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.domain.IntentPresetRepository
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
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class IntentDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val projectDataCache = mockk<ProjectDataCache>()
    private val intentRepository = mockk<IntentCallRepository>()
    private val presetRepository = mockk<IntentPresetRepository>()

    @Test
    fun `load action updates state with intent details`() = runTest {
        val intentCall = IntentCall(id = "id-1", action = "action-1")
        coEvery { intentRepository.getIntentCall("id-1") } returns intentCall
        val vm = createViewModel()

        vm.onAction(IntentDetailsUiAction.LoadIntent("id-1"))
        advanceUntilIdle()

        assertEquals("id-1", vm.state.value.intentData.id)
        assertEquals("action-1", vm.state.value.intentData.action)
    }

    @Test
    fun `copy fields action stores values and emits navigate effect`() = runTest {
        val fields = IntentFields(projectId = "p", userId = "u", moduleId = "m", metadata = "{}")
        val intentCall = IntentCall(id = "id-1", fields = fields)
        coEvery { intentRepository.getIntentCall("id-1") } returns intentCall
        coEvery {
            projectDataCache.save(
                projectId = any(),
                userId = any(),
                moduleId = any(),
                metadata = any(),
                guid = any(),
            )
        } returns mockk<Preferences>()
        val vm = createViewModel()
        vm.onAction(IntentDetailsUiAction.LoadIntent("id-1"))
        advanceUntilIdle()

        vm.effects.test {
            vm.onAction(IntentDetailsUiAction.CopyFieldsClicked)
            advanceUntilIdle()
            assertEquals(IntentDetailsUiEffect.NavigateToIntent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { projectDataCache.save("p", "u", "m", "{}", "") }
    }

    @Test
    fun `save preset emits toast and hides dialog`() = runTest {
        val fields = IntentFields(projectId = "project", userId = "u", moduleId = "m")
        val intentCall = IntentCall(id = "id-1", fields = fields)
        coEvery { intentRepository.getIntentCall("id-1") } returns intentCall
        coEvery { presetRepository.save(any(), any()) } returns Unit
        val vm = createViewModel()
        vm.onAction(IntentDetailsUiAction.LoadIntent("id-1"))
        vm.onAction(IntentDetailsUiAction.SavePresetDialogShown)
        advanceUntilIdle()

        vm.effects.test {
            vm.onAction(IntentDetailsUiAction.SavePresetConfirmed("Preset Name"))
            advanceUntilIdle()
            assertEquals(IntentDetailsUiEffect.ShowToast("Preset saved"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(false, vm.state.value.isSaveDialogVisible)
        coVerify(exactly = 1) { presetRepository.save("Preset Name", fields) }
    }

    @Test
    fun `delete intent emits navigate up`() = runTest {
        coEvery { intentRepository.deleteIntentCall("id-1") } returns Unit
        val vm = createViewModel()

        vm.effects.test {
            vm.onAction(IntentDetailsUiAction.DeleteIntentClicked("id-1"))
            advanceUntilIdle()
            assertEquals(IntentDetailsUiEffect.NavigateUp, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { intentRepository.deleteIntentCall("id-1") }
    }

    private fun createViewModel() = IntentDetailsViewModel(
        projectDataCache = projectDataCache,
        intentRepository = intentRepository,
        presetRepository = presetRepository,
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
