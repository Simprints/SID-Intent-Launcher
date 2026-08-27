package com.simprints.intentlauncher.ui.presets

import com.simprints.intentlauncher.domain.Preset
import com.simprints.intentlauncher.ui.base.UiAction
import com.simprints.intentlauncher.ui.base.UiEffect
import com.simprints.intentlauncher.ui.base.UiState

data class PresetsViewUiState(
    val items: List<Preset> = emptyList(),
) : UiState

sealed interface PresetsUiAction : UiAction {
    data class UsePresetClicked(
        val item: Preset,
    ) : PresetsUiAction

    data class DeletePresetClicked(
        val item: Preset,
    ) : PresetsUiAction
}

sealed interface PresetsUiEffect : UiEffect {
    data object NavigateToIntent : PresetsUiEffect

    data class ShowToast(
        val message: String,
    ) : PresetsUiEffect
}
