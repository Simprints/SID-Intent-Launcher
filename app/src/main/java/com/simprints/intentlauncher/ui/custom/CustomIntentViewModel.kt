package com.simprints.intentlauncher.ui.custom

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentResult.Companion.INVALID_CUSTOM_INTENT
import com.simprints.intentlauncher.domain.IntentResultParser
import com.simprints.intentlauncher.tools.toIsoString
import com.simprints.intentlauncher.ui.base.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CustomIntentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val projectDataCache: ProjectDataCache,
    private val intentCallRepository: IntentCallRepository,
    private val intentResultParser: IntentResultParser,
) : BaseMviViewModel<CustomIntentViewUiState, CustomIntentUiAction, CustomIntentUiEffect>(
        initialState = CustomIntentViewUiState(
            action = savedStateHandle[KEY_ACTION] ?: "",
            extras = savedStateHandle[KEY_EXTRAS] ?: "",
            result = savedStateHandle[KEY_RESULT] ?: "",
        ),
    ) {
    private var pendingIntentCall: IntentCall? = null

    init {
        viewModelScope.launch { fetchCachedFieldValues() }
    }

    override suspend fun handleAction(action: CustomIntentUiAction) {
        when (action) {
            is CustomIntentUiAction.ActionChanged -> updateAndPersist { it.copy(action = action.value) }
            is CustomIntentUiAction.ExtrasChanged -> updateAndPersist { it.copy(extras = action.value) }
            CustomIntentUiAction.LaunchClicked -> launchIntent()
            is CustomIntentUiAction.IntentResultReceived -> intentResultReceived(action.result)
            CustomIntentUiAction.ClearFieldsClicked -> clearFields()
        }
    }

    private suspend fun fetchCachedFieldValues() {
        val action = projectDataCache.getCustomAction()
        val extras = projectDataCache.getCustomExtras()
        updateAndPersist {
            it.copy(
                action = action,
                extras = extras,
            )
        }
    }

    private suspend fun clearFields() {
        projectDataCache.clearCustomIntent()
        updateAndPersist { CustomIntentViewUiState() }
        pendingIntentCall = null
    }

    private suspend fun launchIntent() {
        val uiState = state.value
        projectDataCache.saveCustomIntent(uiState.action, uiState.extras)
        val extras = parseExtras(uiState.extras)
        val pendingCall = IntentCall(
            timestamp = Date().toIsoString(),
            action = uiState.action,
            extra = extras,
        )
        pendingIntentCall = pendingCall
        emitEffect(
            CustomIntentUiEffect.LaunchIntent(
                action = uiState.action,
                extras = extras,
            ),
        )
    }

    private suspend fun intentResultReceived(result: Pair<Int, Intent?>) {
        val (code, intent) = result
        val intentResult = intentResultParser(code, intent?.extras)

        if (code != INVALID_CUSTOM_INTENT) {
            pendingIntentCall?.let {
                intentCallRepository.save(call = it, result = intentResult)
            }
        }
        pendingIntentCall = null
        updateAndPersist {
            it.copy(
                result = intentResult.simpleText(),
            )
        }
    }

    private fun updateAndPersist(update: (CustomIntentViewUiState) -> CustomIntentViewUiState) {
        updateState(update)
        val newState = state.value
        setSavedState(KEY_ACTION, newState.action)
        setSavedState(KEY_EXTRAS, newState.extras)
        setSavedState(KEY_RESULT, newState.result)
    }

    private fun parseExtras(extras: String): Map<String, String> = extras
        .lines()
        .filter { "=" in it }
        .associate { line ->
            val (key, value) = line.split("=", limit = 2)
            key to value
        }

    private fun <T> setSavedState(
        key: String,
        value: T,
    ) {
        savedStateHandle[key] = value
    }

    companion object {
        private const val KEY_ACTION = "custom_action"
        private const val KEY_EXTRAS = "custom_extras"
        private const val KEY_RESULT = "custom_result"
    }
}
