package com.simprints.intentlauncher.ui.intent

import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentResult
import com.simprints.intentlauncher.domain.IntentResultParser
import com.simprints.libsimprints.contracts.SimprintsResponse
import io.mockk.*
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
class IntentViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val savedStateHandle = SavedStateHandle()
    private val projectDataCache = mockk<ProjectDataCache>()
    private val intentCallRepository = mockk<IntentCallRepository>()
    private val intentResultParser = mockk<IntentResultParser>()
    private val gson = Gson()

    @Test
    fun `project id action updates state`() = runTest {
        val vm = createViewModel()

        vm.onAction(IntentUiAction.ProjectIdChanged("project-1"))
        advanceUntilIdle()

        assertEquals("project-1", vm.state.value.projectId)
    }

    @Test
    fun `all field actions update state`() = runTest {
        val vm = createViewModel()

        vm.onAction(IntentUiAction.UserIdChanged("user-1"))
        vm.onAction(IntentUiAction.ModuleIdChanged("module-1"))
        vm.onAction(IntentUiAction.MetadataChanged("{\"a\":1}"))
        vm.onAction(IntentUiAction.GuidChanged("guid-1"))
        vm.onAction(IntentUiAction.SessionIdChanged("session-1"))
        advanceUntilIdle()

        assertEquals("user-1", vm.state.value.userId)
        assertEquals("module-1", vm.state.value.moduleId)
        assertEquals("{\"a\":1}", vm.state.value.metadata)
        assertEquals("guid-1", vm.state.value.guid)
        assertEquals("session-1", vm.state.value.sessionId)
    }

    @Test
    fun `load cached fields action hydrates state`() = runTest {
        coEvery { projectDataCache.getProjectId() } returns "p"
        coEvery { projectDataCache.getUserId() } returns "u"
        coEvery { projectDataCache.getModuleId() } returns "m"
        coEvery { projectDataCache.getMetadata() } returns "{\"a\":1}"
        coEvery { projectDataCache.getGuid() } returns "g"

        val vm = createViewModel()
        vm.onAction(IntentUiAction.LoadCachedFields)
        advanceUntilIdle()

        assertEquals("p", vm.state.value.projectId)
        assertEquals("u", vm.state.value.userId)
        assertEquals("m", vm.state.value.moduleId)
        assertEquals("{\"a\":1}", vm.state.value.metadata)
        assertEquals("g", vm.state.value.guid)
    }

    @Test
    fun `null result updates ui state without persistence when no pending call`() = runTest {
        val vm = createViewModel()
        vm.onAction(IntentUiAction.IntentResultReceived(null))
        advanceUntilIdle()

        assertTrue(
            vm.state.value.result
                .contains("SIMPRINTS ID APP NOT INSTALLED"),
        )
        assertEquals("", vm.state.value.sessionId)
        assertEquals(null, vm.state.value.responseIntentId)
        coVerify(exactly = 0) { intentCallRepository.save(any(), any()) }
    }

    @Test
    fun `non-null response uses parser result`() = runTest {
        val response = mockk<SimprintsResponse>()
        val parsed = IntentResult(code = 200, json = "{\"data\":1}", sessionId = "session-x")
        every { intentResultParser(response) } returns parsed

        val vm = createViewModel()
        vm.onAction(IntentUiAction.IntentResultReceived(response))
        advanceUntilIdle()

        assertEquals("session-x", vm.state.value.sessionId)
        assertEquals("{\"data\":1}", vm.state.value.responseJson)
        assertTrue(
            vm.state.value.result
                .contains("200"),
        )
        verify(exactly = 1) { intentResultParser(response) }
    }

    @Test
    fun `wrong metadata dismiss action clears visible flag`() = runTest {
        val handle = SavedStateHandle(
            mapOf("is_wrong_metadata_alert_visible" to true),
        )
        val vm = createViewModel(handle)

        assertTrue(vm.state.value.isWrongMetadataAlertVisible)
        vm.onAction(IntentUiAction.WrongMetadataAlertDismissed)
        advanceUntilIdle()

        assertEquals(false, vm.state.value.isWrongMetadataAlertVisible)
    }

    @Test
    fun `clear fields action resets state`() = runTest {
        coEvery { projectDataCache.clear() } returns mockk<Preferences>()
        val vm = createViewModel()
        vm.onAction(IntentUiAction.ProjectIdChanged("project"))
        vm.onAction(IntentUiAction.UserIdChanged("user"))
        vm.onAction(IntentUiAction.ModuleIdChanged("module"))

        vm.onAction(IntentUiAction.ClearFieldsClicked)
        advanceUntilIdle()

        assertEquals("", vm.state.value.projectId)
        assertEquals("", vm.state.value.userId)
        assertEquals("", vm.state.value.moduleId)
    }

    private fun createViewModel(handle: SavedStateHandle = savedStateHandle) = IntentViewModel(
        savedStateHandle = handle,
        projectDataCache = projectDataCache,
        intentCallRepository = intentCallRepository,
        intentResultParser = intentResultParser,
        gson = gson,
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
