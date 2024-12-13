package com.simprints.intentlauncher.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.data.store.ProjectDataCache
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import com.simprints.intentlauncher.domain.IntentFields
import com.simprints.intentlauncher.domain.IntentPresetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntentDetailsViewModel @Inject constructor(
    private val projectDataCache: ProjectDataCache,
    private val intentRepository: IntentCallRepository,
    private val presetRepository: IntentPresetRepository,
) : ViewModel() {
    private val _data = MutableStateFlow(IntentCall())
    val data: StateFlow<IntentCall> = _data

    fun loadIntent(intentId: String) = viewModelScope.launch {
        _data.value = intentRepository.getIntentCall(intentId) ?: IntentCall()
    }

    fun copyFieldsToStore(fields: IntentFields) = viewModelScope.launch {
        projectDataCache.save(
            projectId = fields.projectId,
            userId = fields.userId,
            moduleId = fields.moduleId,
            metadata = fields.metadata,
            guid = "",
        )
    }

    fun savePreset(
        name: String,
        fields: IntentFields,
    ) = viewModelScope.launch {
        presetRepository.save(name, fields)
    }

    fun deleteIntent(intentId: String) = viewModelScope.launch {
        intentRepository.deleteIntentCall(intentId)
    }
}
