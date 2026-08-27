package com.simprints.intentlauncher.ui.history

import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.ui.base.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val intentRepository: IntentCallRepository,
) : BaseMviViewModel<HistoryViewUiState, HistoryUiAction, HistoryUiEffect>(
        initialState = HistoryViewUiState(),
    ) {
    private var loadJob: Job? = null

    override suspend fun handleAction(action: HistoryUiAction) {
        when (action) {
            HistoryUiAction.LoadHistory -> loadHistory()
        }
    }

    private fun loadHistory() {
        if (loadJob != null) return
        loadJob = viewModelScope.launch {
            intentRepository.getAllIntentCalls().collect { calls ->
                reduce { it.copy(items = calls) }
            }
        }
    }
}
