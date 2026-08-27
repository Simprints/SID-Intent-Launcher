package com.simprints.intentlauncher.ui.details

import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentPresetRepository
import com.simprints.intentlauncher.ui.base.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntentDetailsViewModel @Inject constructor(
    private val projectDataCache: ProjectDataCache,
    private val intentRepository: IntentCallRepository,
    private val presetRepository: IntentPresetRepository,
) : BaseMviViewModel<IntentDetailsUiState, IntentDetailsUiAction, IntentDetailsUiEffect>(
        initialState = IntentDetailsUiState(),
    ) {
    override suspend fun handleAction(action: IntentDetailsUiAction) {
        when (action) {
            is IntentDetailsUiAction.LoadIntent -> loadIntent(action.intentId)
            is IntentDetailsUiAction.DeleteIntentClicked -> deleteIntent(action.intentId)
            IntentDetailsUiAction.CopyFieldsClicked -> copyFieldsToStore()
            IntentDetailsUiAction.SavePresetDialogShown -> reduce { it.copy(isSaveDialogVisible = true) }
            IntentDetailsUiAction.SavePresetDialogDismissed -> reduce { it.copy(isSaveDialogVisible = false) }
            is IntentDetailsUiAction.SavePresetConfirmed -> savePreset(action.name)
        }
    }

    private suspend fun loadIntent(intentId: String) {
        val intentData = intentRepository.getIntentCall(intentId)
        reduce {
            it.copy(
                intentData = intentData ?: it.intentData,
            )
        }
    }

    private suspend fun copyFieldsToStore() {
        val fields = state.value.intentData.fields
        projectDataCache.save(
            projectId = fields.projectId,
            userId = fields.userId,
            moduleId = fields.moduleId,
            metadata = fields.metadata,
            guid = "",
        )
        emitEffect(IntentDetailsUiEffect.NavigateToIntent)
    }

    private suspend fun savePreset(name: String) {
        val fields = state.value.intentData.fields
        presetRepository.save(name, fields)
        reduce { it.copy(isSaveDialogVisible = false) }
        emitEffect(IntentDetailsUiEffect.ShowToast("Preset saved"))
    }

    private suspend fun deleteIntent(intentId: String) {
        intentRepository.deleteIntentCall(intentId)
        emitEffect(IntentDetailsUiEffect.NavigateUp)
    }
}
