package com.simprints.intentlauncher.ui.details

import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.ui.base.UiAction
import com.simprints.intentlauncher.ui.base.UiEffect
import com.simprints.intentlauncher.ui.base.UiState

data class IntentDetailsUiState(
    val intentData: IntentCall = IntentCall(),
    val isSaveDialogVisible: Boolean = false,
) : UiState

sealed interface IntentDetailsUiAction : UiAction {
    data class LoadIntent(
        val intentId: String,
    ) : IntentDetailsUiAction

    data class DeleteIntentClicked(
        val intentId: String,
    ) : IntentDetailsUiAction

    data object CopyFieldsClicked : IntentDetailsUiAction

    data object SavePresetDialogShown : IntentDetailsUiAction

    data object SavePresetDialogDismissed : IntentDetailsUiAction

    data class SavePresetConfirmed(
        val name: String,
    ) : IntentDetailsUiAction
}

sealed interface IntentDetailsUiEffect : UiEffect {
    data object NavigateUp : IntentDetailsUiEffect

    data object NavigateToIntent : IntentDetailsUiEffect

    data class ShowToast(
        val message: String,
    ) : IntentDetailsUiEffect
}
