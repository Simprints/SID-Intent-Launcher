package com.simprints.intentlauncher.ui.intent

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.simprints.intentlauncher.model.domain.SimprintsIntent
import com.simprints.intentlauncher.model.local.LocalSimprintsResultDataSource
import com.simprints.intentlauncher.model.store.ProjectDataCache
import com.simprints.libsimprints.SimHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class IntentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val resultDao: LocalSimprintsResultDataSource,
    private val gson: Gson,
    private val projectDataCache: ProjectDataCache,
) : ViewModel() {

    private val _viewState = MutableStateFlow(IntentViewState())
    val viewState = _viewState.asStateFlow()

    fun updateProjectId(projectId: String) {
        _viewState.update { it.copy(projectId = projectId) }
    }

    fun updateUserId(userId: String) {
        _viewState.update { it.copy(userId = userId) }
    }

    fun updateModuleId(moduleId: String) {
        _viewState.update { it.copy(moduleId = moduleId) }
    }

    fun updateGuid(guid: String) {
        _viewState.update { it.copy(guid = guid) }
    }

    fun updateSessionId(sessionId: String) {
        _viewState.update { it.copy(sessionId = sessionId) }
    }


    fun enroll() = _viewState.update {
        val intent = SimHelper(it.projectId, it.userId).register(it.moduleId)
        copyWithCachedIntent(it, intent)
    }


    fun identify() = _viewState.update {
        val intent = SimHelper(it.projectId, it.userId).identify(it.moduleId)
        copyWithCachedIntent(it, intent)
    }

    fun verify() = _viewState.update {
        val intent = SimHelper(it.projectId, it.userId).verify(it.moduleId, it.guid)
        copyWithCachedIntent(it, intent)
    }


    fun confirm() = _viewState.update {
        /*
        TODO need to update SimHelper to not require the context in confirmIdentity first
        val intent = SimHelper(it.projectId, it.userId).confirmIdentity(it.sessionId, it.guid)
        copyWithCachedIntent(it, intent)
        */
        it
    }

    fun enrolLast() = _viewState.update {
        val intent =
            SimHelper(it.projectId, it.userId).registerLastBiometrics(it.moduleId, it.sessionId)
        copyWithCachedIntent(it, intent)
    }

    private fun copyWithCachedIntent(
        it: IntentViewState,
        intent: Intent,
    ) = it.copy(
        lastIntentSentTime = Date(),
        lastSentIntent = SimprintsIntent(intent),
        showIntent = triggered(intent),
    )

    fun intentShown() = _viewState.update {
        it.copy(showIntent = consumed())
    }

    fun intentReceived(result: Pair<Int, Intent?>) {
        val resultString = "${result.first} \n ${gson.toJson(result.second?.extras)}"

        _viewState.update { it.copy(result = resultString) }
    }
}

