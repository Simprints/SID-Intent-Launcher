package com.simprints.intentlauncher.ui.custom

import android.content.Intent
import com.simprints.intentlauncher.ui.base.UiAction
import com.simprints.intentlauncher.ui.base.UiEffect
import com.simprints.intentlauncher.ui.base.UiState

data class CustomIntentViewUiState(
    val action: String = "",
    val extras: String = "",
    val result: String = "",
) : UiState

sealed interface CustomIntentUiAction : UiAction {
    data object LoadCachedFields : CustomIntentUiAction
    data class ActionChanged(val value: String) : CustomIntentUiAction
    data class ExtrasChanged(val value: String) : CustomIntentUiAction
    data object LaunchClicked : CustomIntentUiAction
    data class IntentResultReceived(val result: Pair<Int, Intent?>) : CustomIntentUiAction
    data object ClearFieldsClicked : CustomIntentUiAction
}

sealed interface CustomIntentUiEffect : UiEffect {
    data class LaunchIntent(
        val action: String,
        val extras: Map<String, String>,
    ) : CustomIntentUiEffect
}
