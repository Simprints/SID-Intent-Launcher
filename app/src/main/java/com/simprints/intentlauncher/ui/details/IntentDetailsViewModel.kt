package com.simprints.intentlauncher.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.model.domain.IntentCall
import com.simprints.intentlauncher.model.domain.IntentFields
import com.simprints.intentlauncher.model.local.IntentCallRepository
import com.simprints.intentlauncher.model.store.ProjectDataCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntentDetailsViewModel @Inject constructor(
    private val projectDataCache: ProjectDataCache,
    private val intentRepository: IntentCallRepository,
) : ViewModel() {

    private val _data = MutableStateFlow(IntentCall())
    val data: StateFlow<IntentCall> = _data

    fun loadIntent(intentId: String) = viewModelScope.launch {
        _data.value = intentRepository.getIntentCall(intentId) ?: IntentCall()
    }

    fun copyFieldsToStore(fields: IntentFields) = viewModelScope.launch {
        projectDataCache.save(fields.projectId, fields.userId, fields.moduleId, "")
    }
}
