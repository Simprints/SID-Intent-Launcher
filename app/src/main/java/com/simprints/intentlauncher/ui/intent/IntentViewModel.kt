package com.simprints.intentlauncher.ui.intent

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.domain.IntentResult
import com.simprints.intentlauncher.domain.IntentResult.Companion.SID_NOT_INSTALLED
import com.simprints.intentlauncher.domain.IntentResultParser
import com.simprints.intentlauncher.tools.extractEventsFromJson
import com.simprints.libsimprints.Metadata
import com.simprints.libsimprints.Metadata.InvalidMetadataException
import com.simprints.libsimprints.contracts.SimprintsRequest
import com.simprints.libsimprints.contracts.SimprintsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class IntentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val projectDataCache: ProjectDataCache,
    private val intentCallRepository: IntentCallRepository,
    private val intentResultParser: IntentResultParser,
    private val gson: Gson,
) : ViewModel() {
    val viewState = savedStateHandle.getStateFlow(KEY_VIEW_STATE, IntentViewState())

    private fun updateViewState(update: (IntentViewState) -> IntentViewState) {
        savedStateHandle[KEY_VIEW_STATE] = update(viewState.value)
    }

    fun updateProjectId(projectId: String) = updateViewState { it.copy(projectId = projectId) }

    fun updateUserId(userId: String) = updateViewState { it.copy(userId = userId) }

    fun updateModuleId(moduleId: String) = updateViewState { it.copy(moduleId = moduleId) }

    fun updateMetadata(metadata: String) = updateViewState { it.copy(metadata = metadata) }

    fun updateGuid(guid: String) = updateViewState { it.copy(guid = guid) }

    fun updateSessionId(sessionId: String) = updateViewState { it.copy(sessionId = sessionId) }

    fun intentShown() = updateViewState { it.copy(showIntent = consumed()) }

    private fun executeSimHelperAction(createRequest: (IntentViewState) -> SimprintsRequest) = updateViewState { state ->
        val request = try {
            createRequest(state)
        } catch (_: InvalidMetadataException) {
            return@updateViewState state.copy(showWrongMetadataAlert = mutableStateOf(true))
        }
        cacheFields(state)
        copyWithCachedIntent(state, request)
    }

    fun enroll() = executeSimHelperAction { viewState ->
        SimprintsRequest.Enrol(
            viewState.projectId,
            viewState.userId,
            viewState.moduleId,
            viewState.metadata.takeIf { it.isNotBlank() }?.let { Metadata(it) },
        )
    }

    fun identify() = executeSimHelperAction { viewState ->
        SimprintsRequest.Identify(
            viewState.projectId,
            viewState.userId,
            viewState.moduleId,
            viewState.metadata.takeIf { it.isNotBlank() }?.let { Metadata(it) },
        )
    }

    fun verify() = executeSimHelperAction { viewState ->
        SimprintsRequest.Verify(
            viewState.projectId,
            viewState.userId,
            viewState.moduleId,
            viewState.guid,
        )
    }

    fun confirm() = executeSimHelperAction { viewState ->
        SimprintsRequest.ConfirmIdentity(
            viewState.projectId,
            viewState.userId,
            viewState.sessionId,
            viewState.guid,
        )
    }

    fun enrolLast() = executeSimHelperAction { viewState ->
        SimprintsRequest.EnrolLastBiometrics(
            viewState.projectId,
            viewState.userId,
            viewState.moduleId,
            viewState.sessionId,
        )
    }

    private fun cacheFields(state: IntentViewState) = viewModelScope.launch {
        projectDataCache.save(
            projectId = state.projectId,
            userId = state.userId,
            moduleId = state.moduleId,
            metadata = state.metadata,
            guid = state.guid,
        )
    }

    private fun copyWithCachedIntent(
        state: IntentViewState,
        request: SimprintsRequest,
    ) = state.copy(
        lastIntentCall = IntentCall(
            timestamp = Date(),
            intent = request.toIntent(),
            fields = IntentFields(
                projectId = state.projectId,
                userId = state.userId,
                moduleId = state.moduleId,
                metadata = state.metadata,
            ),
        ),
        showIntent = triggered(request),
    )

    fun intentResultReceived(result: SimprintsResponse?) = viewModelScope.launch {
        val intentResult = if (result == null) {
            IntentResult(code = SID_NOT_INSTALLED)
        } else {
            intentResultParser(result)
        }
        val lastIntentCall = viewState.value.lastIntentCall

        lastIntentCall?.let {
            intentCallRepository.save(call = it, result = intentResult)
        }
        updateViewState {
            it.copy(
                result = intentResult.simpleText(),
                sessionId = intentResult.sessionId.orEmpty(),
                lastIntentCall = null,
                responseIntentId = lastIntentCall?.id,
                responseJson = intentResult.json.orEmpty(),
                events = intentResult.json?.extractEventsFromJson(gson).orEmpty(),
            )
        }
    }

    fun fetchCachedFieldValues() = viewModelScope.launch {
        val projectId = projectDataCache.getProjectId()
        val userId = projectDataCache.getUserId()
        val moduleId = projectDataCache.getModuleId()
        val metadata = projectDataCache.getMetadata()
        val guid = projectDataCache.getGuid()

        updateViewState {
            it.copy(
                projectId = projectId,
                userId = userId,
                moduleId = moduleId,
                metadata = metadata,
                guid = guid,
            )
        }
    }

    fun clearFields() = viewModelScope.launch {
        projectDataCache.clear()
        updateViewState { IntentViewState() }
    }

    companion object {
        private const val KEY_VIEW_STATE = "view_state"
    }
}
