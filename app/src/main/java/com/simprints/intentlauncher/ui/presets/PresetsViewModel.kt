package com.simprints.intentlauncher.ui.presets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentPresetRepository
import com.simprints.intentlauncher.domain.Preset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PresetsViewModel @Inject constructor(
    private val presetRepo: IntentPresetRepository,
    private val projectDataCache: ProjectDataCache,
) : ViewModel() {

    private val _data = MutableStateFlow<List<Preset>>(emptyList())
    val data: StateFlow<List<Preset>> = _data

    init {
        viewModelScope.launch {
            presetRepo.getPresets().collect { newData ->
                _data.value = newData
            }
        }
    }

    fun usePreset(item: Preset) = viewModelScope.launch {
        projectDataCache.save(item.fields.projectId, item.fields.userId, item.fields.moduleId, "")
    }

    fun deletePreset(item: Preset) = viewModelScope.launch {
        presetRepo.deletePreset(item.id)
    }
}
