package com.simprints.intentlauncher.ui.intent

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.model.domain.IntentCall
import com.simprints.intentlauncher.model.local.IntentCallRepository
import com.simprints.intentlauncher.model.store.ProjectDataCache
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
) : ViewModel() {

    val viewState = savedStateHandle.getStateFlow(KEY_VIEW_STATE, IntentViewState())
    private fun updateViewState(update: (IntentViewState) -> IntentViewState) {
        savedStateHandle[KEY_VIEW_STATE] = update(viewState.value)
    }

    fun updateProjectId(projectId: String) = updateViewState { it.copy(projectId = projectId) }
    fun updateUserId(userId: String) = updateViewState { it.copy(userId = userId) }
    fun updateModuleId(moduleId: String) = updateViewState { it.copy(moduleId = moduleId) }
    fun updateGuid(guid: String) = updateViewState { it.copy(guid = guid) }
    fun updateSessionId(sessionId: String) = updateViewState { it.copy(sessionId = sessionId) }
    fun intentShown() = updateViewState { it.copy(showIntent = consumed()) }

    fun enroll() = updateViewState {
        val intent = SimHelper(it.projectId, it.userId).register(it.moduleId)
        cacheFields(it)
        copyWithCachedIntent(it, intent)
    }

    fun identify() = updateViewState {
        val intent = SimHelper(it.projectId, it.userId).identify(it.moduleId)
        cacheFields(it)
        copyWithCachedIntent(it, intent)
    }

    fun verify() = updateViewState {
        val intent = SimHelper(it.projectId, it.userId).verify(it.moduleId, it.guid)
        cacheFields(it)
        copyWithCachedIntent(it, intent)
    }

    fun confirm() = updateViewState {
        /*
        TODO need to update SimHelper to not require the context in confirmIdentity first
        val intent = SimHelper(it.projectId, it.userId).confirmIdentity(it.sessionId, it.guid)
        cacheFields(it)
        copyWithCachedIntent(it, intent)
        */
        it
    }

    fun enrolLast() = updateViewState {
        val intent = SimHelper(it.projectId, it.userId)
            .registerLastBiometrics(it.moduleId, it.sessionId)

        cacheFields(it)
        copyWithCachedIntent(it, intent)
    }

    private fun cacheFields(state: IntentViewState) = viewModelScope.launch {
        projectDataCache.save(state.projectId, state.userId, state.moduleId, state.guid)
    }

    private fun copyWithCachedIntent(state: IntentViewState, intent: Intent) = state.copy(
        lastIntentCall = IntentCall(Date(), intent),
        showIntent = triggered(intent),
    )

    fun intentResultReceived(result: Pair<Int, Intent?>) = viewModelScope.launch {
        val (code, intent) = result

        val updatedCall = viewState.value.lastIntentCall?.let {
            intentCallRepository.save(
                call = it,
                resultCode = code,
                resultExtras = intent?.extras,
            )
        }
        updateViewState {
            it.copy(
                result = "${updatedCall?.resultCode} \n ${updatedCall?.resultReceived}",
                sessionId = updatedCall?.resultSessionId.orEmpty(),
            )
        }
    }

    fun fetchCachedFieldValues() = viewModelScope.launch {
        val projectId = projectDataCache.getProjectId()
        val userId = projectDataCache.getUserId()
        val moduleId = projectDataCache.getModuleId()
        val guid = projectDataCache.getGuid()

        updateViewState {
            it.copy(
                projectId = projectId,
                userId = userId,
                moduleId = moduleId,
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
