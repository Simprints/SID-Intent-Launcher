package com.simprints.intentlauncher.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.intentlauncher.domain.IntentCall
import com.simprints.intentlauncher.domain.IntentCallRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val intentRepository: IntentCallRepository,
) : ViewModel() {

    private val _data = MutableStateFlow<List<IntentCall>>(emptyList())
    val data: StateFlow<List<IntentCall>> = _data

    init {
        viewModelScope.launch {
            intentRepository.getAllIntentCalls().collect { newData ->
                _data.value = newData
            }
        }
    }
}
