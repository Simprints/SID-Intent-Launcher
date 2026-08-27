package com.simprints.intentlauncher.ui.presets

import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentPresetRepository
import com.simprints.intentlauncher.domain.Preset
import com.simprints.intentlauncher.ui.base.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresetsViewModel @Inject constructor(
    private val presetRepo: IntentPresetRepository,
    private val projectDataCache: ProjectDataCache,
) : BaseMviViewModel<PresetsViewUiState, PresetsUiAction, PresetsUiEffect>(
        initialState = PresetsViewUiState(),
    ) {
    init {
        viewModelScope.launch {
            presetRepo.getPresets().collectLatest { presets ->
                reduce { it.copy(items = presets) }
            }
        }
    }

    override suspend fun handleAction(action: PresetsUiAction) {
        when (action) {
            is PresetsUiAction.UsePresetClicked -> usePreset(action.item)
            is PresetsUiAction.DeletePresetClicked -> deletePreset(action.item)
        }
    }

    private suspend fun usePreset(item: Preset) {
        projectDataCache.save(
            projectId = item.fields.projectId,
            userId = item.fields.userId,
            moduleId = item.fields.moduleId,
            metadata = item.fields.metadata,
            guid = "",
        )
        emitEffect(PresetsUiEffect.NavigateToIntent)
    }

    private suspend fun deletePreset(item: Preset) {
        presetRepo.deletePreset(item.id)
        emitEffect(PresetsUiEffect.ShowToast("Preset deleted"))
    }
}
