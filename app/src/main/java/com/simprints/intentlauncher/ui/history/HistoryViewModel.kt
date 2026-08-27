package com.simprints.intentlauncher.ui.history

import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.ui.base.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val intentRepository: IntentCallRepository,
) : BaseMviViewModel<HistoryViewUiState, HistoryUiAction, HistoryUiEffect>(
        initialState = HistoryViewUiState(),
    ) {
    init {
        viewModelScope.launch {
            intentRepository.getAllIntentCalls().collectLatest { calls ->
                reduce { it.copy(items = calls) }
            }
        }
    }

    override suspend fun handleAction(action: HistoryUiAction) = Unit
}
