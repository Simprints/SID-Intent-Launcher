package com.simprints.intentlauncher.ui.presets

import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.domain.IntentPresetRepository
import com.simprints.intentlauncher.domain.Preset
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
class PresetsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val presetRepo = mockk<IntentPresetRepository>()
    private val projectDataCache = mockk<ProjectDataCache>()

    @Test
    fun `load presets updates state from repository flow`() = runTest {
        val presetsFlow = MutableStateFlow(emptyList<Preset>())
        every { presetRepo.getPresets() } returns presetsFlow

        val vm = createViewModel()
        vm.onAction(PresetsUiAction.LoadPresets)
        advanceUntilIdle()
        assertEquals(0, vm.state.value.items.size)

        presetsFlow.value = listOf(
            Preset(id = "one", name = "One"),
            Preset(id = "two", name = "Two"),
        )
        advanceUntilIdle()

        assertEquals(2, vm.state.value.items.size)
        assertEquals("one", vm.state.value.items[0].id)
    }

    @Test
    fun `use preset saves values and emits navigation effect`() = runTest {
        every { presetRepo.getPresets() } returns MutableStateFlow(emptyList())
        coEvery {
            projectDataCache.save(
                projectId = any(),
                userId = any(),
                moduleId = any(),
                metadata = any(),
                guid = any(),
            )
        } returns mockk<Preferences>()
        val preset = Preset(
            id = "p1",
            name = "Preset 1",
            fields = IntentFields(
                projectId = "project",
                userId = "user",
                moduleId = "module",
                metadata = "{\"a\":1}",
            ),
        )

        val vm = createViewModel()
        vm.effects.test {
            vm.onAction(PresetsUiAction.UsePresetClicked(preset))
            advanceUntilIdle()
            assertEquals(PresetsUiEffect.NavigateToIntent, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) {
            projectDataCache.save("project", "user", "module", "{\"a\":1}", "")
        }
    }

    @Test
    fun `delete preset calls repository and emits toast effect`() = runTest {
        every { presetRepo.getPresets() } returns MutableStateFlow(emptyList())
        coEvery { presetRepo.deletePreset(any()) } returns Unit
        val preset = Preset(id = "p1", name = "Preset 1")

        val vm = createViewModel()
        vm.effects.test {
            vm.onAction(PresetsUiAction.DeletePresetClicked(preset))
            advanceUntilIdle()
            assertEquals(PresetsUiEffect.ShowToast("Preset deleted"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { presetRepo.deletePreset("p1") }
    }

    private fun createViewModel() = PresetsViewModel(
        presetRepo = presetRepo,
        projectDataCache = projectDataCache,
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
