package com.simprints.intentlauncher.ui.intent

import com.simprints.intentlauncher.ui.base.UiAction
import com.simprints.intentlauncher.ui.base.UiEffect
import com.simprints.intentlauncher.ui.base.UiState
import com.simprints.intentlauncher.ui.intent.model.ResponseEvent
import com.simprints.libsimprints.contracts.SimprintsRequest
import com.simprints.libsimprints.contracts.SimprintsResponse

data class IntentViewUiState(
    val projectId: String = "",
    val userId: String = "",
    val moduleId: String = "",
    val metadata: String = "",
    val guid: String = "",
    val sessionId: String = "",
    val result: String = "",
    val responseJson: String = "",
    val responseIntentId: String? = null,
    val isWrongMetadataAlertVisible: Boolean = false,
    val events: List<ResponseEvent> = emptyList(),
) : UiState

sealed interface IntentUiAction : UiAction {
    data object LoadCachedFields : IntentUiAction

    data class ProjectIdChanged(
        val value: String,
    ) : IntentUiAction

    data class UserIdChanged(
        val value: String,
    ) : IntentUiAction

    data class ModuleIdChanged(
        val value: String,
    ) : IntentUiAction

    data class MetadataChanged(
        val value: String,
    ) : IntentUiAction

    data class GuidChanged(
        val value: String,
    ) : IntentUiAction

    data class SessionIdChanged(
        val value: String,
    ) : IntentUiAction

    data object EnrollClicked : IntentUiAction

    data object IdentifyClicked : IntentUiAction

    data object VerifyClicked : IntentUiAction

    data object ConfirmClicked : IntentUiAction

    data object EnrolLastClicked : IntentUiAction

    data class IntentResultReceived(
        val result: SimprintsResponse?,
    ) : IntentUiAction

    data object ClearFieldsClicked : IntentUiAction

    data object WrongMetadataAlertDismissed : IntentUiAction
}

sealed interface IntentUiEffect : UiEffect {
    data class LaunchSimprintsIntent(
        val request: SimprintsRequest,
    ) : IntentUiEffect
}
