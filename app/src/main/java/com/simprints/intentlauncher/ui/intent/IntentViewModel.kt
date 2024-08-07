package com.simprints.intentlauncher.ui.intent

import android.content.Intent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.domain.IntentResultParser
import com.simprints.intentlauncher.tools.extractEventsFromJson
import com.simprints.libsimprints.Metadata
import com.simprints.libsimprints.Metadata.InvalidMetadataException
import com.simprints.libsimprints.SimHelper
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

    private fun executeSimHelperAction(
        actionWithoutMetadata: (SimHelper) -> Intent,
        actionWithMetadata: (SimHelper, Metadata) -> Intent
    ) = updateViewState { state ->
        val simHelper = SimHelper(state.projectId, state.userId)
        val intent = try {
            if (state.metadata.isBlank()) {
                actionWithoutMetadata(simHelper)
            } else {
                actionWithMetadata(simHelper, Metadata(state.metadata))
            }
        } catch (e: InvalidMetadataException) {
            return@updateViewState state.copy(showWrongMetadataAlert = mutableStateOf(true))
        }
        cacheFields(state)
        copyWithCachedIntent(state, intent)
    }

    fun enroll() = executeSimHelperAction(
        { simHelper -> simHelper.register(viewState.value.moduleId) },
        { simHelper, metadata -> simHelper.register(viewState.value.moduleId, metadata) }
    )

    fun identify() = executeSimHelperAction(
        { simHelper -> simHelper.identify(viewState.value.moduleId) },
        { simHelper, metadata -> simHelper.identify(viewState.value.moduleId, metadata) }
    )

    fun verify() = executeSimHelperAction(
        { simHelper -> simHelper.verify(viewState.value.moduleId, viewState.value.guid) },
        { simHelper, metadata -> simHelper.verify(viewState.value.moduleId, viewState.value.guid, metadata) }
    )

    fun confirm() = updateViewState {
        val intent = SimHelper(it.projectId, it.userId).confirmIdentity(it.sessionId, it.guid)
        cacheFields(it)
        copyWithCachedIntent(it, intent)
    }

    fun enrolLast() = executeSimHelperAction(
        { simHelper -> simHelper.registerLastBiometrics(viewState.value.moduleId, viewState.value.sessionId) },
        { simHelper, metadata -> simHelper.registerLastBiometrics(viewState.value.moduleId, viewState.value.sessionId, metadata) }
    )

    private fun cacheFields(state: IntentViewState) = viewModelScope.launch {
        projectDataCache.save(
            projectId = state.projectId,
            userId = state.userId,
            moduleId = state.moduleId,
            metadata = state.metadata,
            guid = state.guid
        )
    }

    private fun copyWithCachedIntent(state: IntentViewState, intent: Intent) = state.copy(
        lastIntentCall = IntentCall(
            timestamp = Date(),
            intent = intent,
            fields = IntentFields(
                projectId = state.projectId,
                userId = state.userId,
                moduleId = state.moduleId,
                metadata = state.metadata,
            )
        ),
        showIntent = triggered(intent),
    )

    fun intentResultReceived(result: Pair<Int, Intent?>) = viewModelScope.launch {
        val (code, intent) = result
        val intentResult = intentResultParser(code, intent?.extras)
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
