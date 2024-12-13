package com.simprints.intentlauncher.ui.custom

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentResult.Companion.INVALID_CUSTOM_INTENT
import com.simprints.intentlauncher.domain.IntentResultParser
import dagger.hilt.android.lifecycle.HiltViewModel
import de.palm.composestateevents.consumed
import de.palm.composestateevents.triggered
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CustomIntentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val projectDataCache: ProjectDataCache,
    private val intentCallRepository: IntentCallRepository,
    private val intentResultParser: IntentResultParser,
) : ViewModel() {
    val viewState = savedStateHandle.getStateFlow(KEY_VIEW_STATE, CustomIntentViewState())

    private fun updateViewState(update: (CustomIntentViewState) -> CustomIntentViewState) {
        savedStateHandle[KEY_VIEW_STATE] = update(viewState.value)
    }

    fun fetchCachedFieldValues() = viewModelScope.launch {
        val action = projectDataCache.getCustomAction()
        val extras = projectDataCache.getCustomExtras()

        updateViewState {
            it.copy(
                action = action,
                extras = extras,
            )
        }
    }

    fun updateAction(newAction: String) = updateViewState { it.copy(action = newAction) }

    fun updateExtras(newExtras: String) = updateViewState { it.copy(extras = newExtras) }

    fun intentShown() = updateViewState { it.copy(showIntent = consumed()) }

    fun clearFields() = viewModelScope.launch {
        projectDataCache.clearCustomIntent()
        updateViewState { CustomIntentViewState() }
    }

    fun launchIntent() = updateViewState {
        cacheFields(it)

        val intent = Intent(it.action).apply {
            it.extras.lines().forEach {
                val (key, value) = it.split("=")
                putExtra(key, value)
            }
        }

        it.copy(
            lastIntentCall = IntentCall(
                timestamp = Date(),
                intent = intent,
            ),
            showIntent = triggered(intent),
        )
    }

    private fun cacheFields(it: CustomIntentViewState) = viewModelScope.launch {
        projectDataCache.saveCustomIntent(it.action, it.extras)
    }

    fun intentResultReceived(result: Pair<Int, Intent?>) = viewModelScope.launch {
        val (code, intent) = result
        val intentResult = intentResultParser(code, intent?.extras)

        if (code != INVALID_CUSTOM_INTENT) {
            viewState.value.lastIntentCall?.let {
                intentCallRepository.save(call = it, result = intentResult)
            }
        }

        updateViewState {
            it.copy(
                result = intentResult.simpleText(),
                lastIntentCall = null,
            )
        }
    }

    companion object {
        private const val KEY_VIEW_STATE = "view_state"
    }
}
