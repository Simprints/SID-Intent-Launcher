package com.simprints.intentlauncher.ui.intent

import androidx.lifecycle.SavedStateHandle
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
import com.simprints.intentlauncher.ui.base.BaseMviViewModel
import com.simprints.libsimprints.Metadata
import com.simprints.libsimprints.Metadata.InvalidMetadataException
import com.simprints.libsimprints.contracts.SimprintsRequest
import com.simprints.libsimprints.contracts.SimprintsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
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
) : BaseMviViewModel<IntentViewUiState, IntentUiAction, IntentUiEffect>(
        initialState = IntentViewUiState(
            projectId = savedStateHandle[KEY_PROJECT_ID] ?: "",
            userId = savedStateHandle[KEY_USER_ID] ?: "",
            moduleId = savedStateHandle[KEY_MODULE_ID] ?: "",
            metadata = savedStateHandle[KEY_METADATA] ?: "",
            guid = savedStateHandle[KEY_GUID] ?: "",
            sessionId = savedStateHandle[KEY_SESSION_ID] ?: "",
            result = savedStateHandle[KEY_RESULT] ?: "",
            responseJson = savedStateHandle[KEY_RESPONSE_JSON] ?: "",
            responseIntentId = savedStateHandle[KEY_RESPONSE_INTENT_ID],
            isWrongMetadataAlertVisible = savedStateHandle[KEY_IS_WRONG_METADATA_ALERT_VISIBLE] ?: false,
            events = emptyList(),
        ),
    ) {
    override suspend fun handleAction(action: IntentUiAction) {
        when (action) {
            IntentUiAction.LoadCachedFields -> fetchCachedFieldValues()

            IntentUiAction.EnrollClicked -> executeSimHelperAction { state ->
                SimprintsRequest.Enrol(
                    state.projectId,
                    state.userId,
                    state.moduleId,
                    state.metadata.takeIf { it.isNotBlank() }?.let { Metadata(it) },
                )
            }

            IntentUiAction.IdentifyClicked -> executeSimHelperAction { state ->
                SimprintsRequest.Identify(
                    state.projectId,
                    state.userId,
                    state.moduleId,
                    state.metadata.takeIf { it.isNotBlank() }?.let { Metadata(it) },
                )
            }

            IntentUiAction.VerifyClicked -> executeSimHelperAction { state ->
                SimprintsRequest.Verify(
                    state.projectId,
                    state.userId,
                    state.moduleId,
                    state.guid,
                    state.metadata.takeIf { it.isNotBlank() }?.let { Metadata(it) },
                )
            }

            IntentUiAction.ConfirmClicked -> executeSimHelperAction { state ->
                SimprintsRequest.ConfirmIdentity(
                    state.projectId,
                    state.userId,
                    state.sessionId,
                    state.guid,
                    state.metadata.takeIf { it.isNotBlank() }?.let { Metadata(it) },
                )
            }

            IntentUiAction.EnrolLastClicked -> executeSimHelperAction { state ->
                SimprintsRequest.EnrolLastBiometrics(
                    state.projectId,
                    state.userId,
                    state.moduleId,
                    state.sessionId,
                    state.metadata.takeIf { it.isNotBlank() }?.let { Metadata(it) },
                )
            }

            IntentUiAction.ClearFieldsClicked -> clearFields()

            is IntentUiAction.IntentResultReceived -> intentResultReceived(action.result)

            is IntentUiAction.GuidChanged -> reduce { it.copy(guid = action.value) }

            is IntentUiAction.MetadataChanged -> reduce { it.copy(metadata = action.value) }

            is IntentUiAction.ModuleIdChanged -> reduce { it.copy(moduleId = action.value) }

            is IntentUiAction.ProjectIdChanged -> reduce { it.copy(projectId = action.value) }

            is IntentUiAction.SessionIdChanged -> reduce { it.copy(sessionId = action.value) }

            is IntentUiAction.UserIdChanged -> reduce { it.copy(userId = action.value) }

            IntentUiAction.WrongMetadataAlertDismissed -> reduce { it.copy(isWrongMetadataAlertVisible = false) }
        }
    }

    private fun executeSimHelperAction(createRequest: (IntentViewUiState) -> SimprintsRequest) = viewModelScope.launch {
        val state = state.value
        val request = try {
            createRequest(state)
        } catch (_: InvalidMetadataException) {
            reduce { it.copy(isWrongMetadataAlertVisible = true) }
            return@launch
        }
        cacheFields(state)
        val pendingCall = createPendingCall(state, request)
        setSavedState(KEY_PENDING_INTENT_CALL, pendingCall)
        emitEffect(IntentUiEffect.LaunchSimprintsIntent(request))
    }

    private fun cacheFields(state: IntentViewUiState) = viewModelScope.launch {
        savedStateHandle[KEY_PROJECT_ID] = state.projectId
        savedStateHandle[KEY_USER_ID] = state.userId
        savedStateHandle[KEY_MODULE_ID] = state.moduleId
        savedStateHandle[KEY_GUID] = state.guid
        savedStateHandle[KEY_METADATA] = state.metadata

        projectDataCache.save(
            projectId = state.projectId,
            userId = state.userId,
            moduleId = state.moduleId,
            metadata = state.metadata,
            guid = state.guid,
        )
    }

    private fun createPendingCall(
        state: IntentViewUiState,
        request: SimprintsRequest,
    ) = IntentCall(
        timestamp = Date(),
        intent = request.toIntent(),
        fields = IntentFields(
            projectId = state.projectId,
            userId = state.userId,
            moduleId = state.moduleId,
            metadata = state.metadata,
        ),
    )

    private fun intentResultReceived(result: SimprintsResponse?) = viewModelScope.launch {
        val intentResult = if (result == null) {
            IntentResult(code = SID_NOT_INSTALLED)
        } else {
            intentResultParser(result)
        }
        val pendingIntentCall = savedStateHandle.get<IntentCall>(KEY_PENDING_INTENT_CALL)

        pendingIntentCall?.let {
            intentCallRepository.save(call = it, result = intentResult)
        }
        setSavedState(KEY_PENDING_INTENT_CALL, null)
        reduce {
            it.copy(
                result = intentResult.simpleText(),
                sessionId = intentResult.sessionId.orEmpty(),
                responseIntentId = pendingIntentCall?.id,
                responseJson = intentResult.json.orEmpty(),
                events = intentResult.json?.extractEventsFromJson(gson).orEmpty(),
                isWrongMetadataAlertVisible = false,
            )
        }
    }

    private fun fetchCachedFieldValues() = viewModelScope.launch {
        val projectId = projectDataCache.getProjectId()
        val userId = projectDataCache.getUserId()
        val moduleId = projectDataCache.getModuleId()
        val metadata = projectDataCache.getMetadata()
        val guid = projectDataCache.getGuid()

        reduce {
            it.copy(
                projectId = projectId,
                userId = userId,
                moduleId = moduleId,
                metadata = metadata,
                guid = guid,
            )
        }
    }

    private fun clearFields() = viewModelScope.launch {
        projectDataCache.clear()
        reduce { IntentViewUiState() }
        setSavedState(KEY_PENDING_INTENT_CALL, null)
    }

    private fun <T> setSavedState(
        key: String,
        value: T,
    ) {
        savedStateHandle[key] = value
    }

    companion object {
        private const val KEY_PROJECT_ID = "project_id"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_MODULE_ID = "module_id"
        private const val KEY_METADATA = "metadata"
        private const val KEY_GUID = "guid"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_RESULT = "result"
        private const val KEY_RESPONSE_JSON = "response_json"
        private const val KEY_RESPONSE_INTENT_ID = "response_intent_id"
        private const val KEY_PENDING_INTENT_CALL = "pending_intent_call"
        private const val KEY_IS_WRONG_METADATA_ALERT_VISIBLE = "is_wrong_metadata_alert_visible"
    }
}
