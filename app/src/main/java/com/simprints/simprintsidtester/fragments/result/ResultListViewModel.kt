package com.simprints.simprintsidtester.fragments.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simprints.simprintsidtester.model.domain.SimprintsResult
import com.simprints.simprintsidtester.model.local.LocalSimprintsResultDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultListViewModel @Inject constructor(
    private val resultDataSource: LocalSimprintsResultDataSource,
) : ViewModel() {

    private val resultLiveData: MutableLiveData<List<SimprintsResult>> = MutableLiveData()
    private val mainResultList: MutableList<SimprintsResult> = mutableListOf()

    init {
        viewModelScope.launch {
            val results: List<SimprintsResult> = resultDataSource.getResults()

            mainResultList.clear()
            mainResultList.addAll(results)

            resultLiveData.value = mainResultList
        }
    }

    fun getResults(): LiveData<List<SimprintsResult>> = resultLiveData

    fun filterList(query: String) {
        resultLiveData.value = mainResultList.filter {
            it.dateTimeSent.contains(query, true) ||
                    it.intentSent.contains(query, true) ||
                    it.resultReceived.contains(query, true)
        }
    }
}