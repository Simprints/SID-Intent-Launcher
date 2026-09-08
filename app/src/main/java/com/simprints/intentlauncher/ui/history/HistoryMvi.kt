package com.simprints.intentlauncher.ui.history

import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.ui.base.UiAction
import com.simprints.intentlauncher.ui.base.UiEffect
import com.simprints.intentlauncher.ui.base.UiState

data class HistoryViewUiState(
    val items: List<IntentCall> = emptyList(),
) : UiState

sealed interface HistoryUiAction : UiAction

sealed interface HistoryUiEffect : UiEffect
