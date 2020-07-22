package com.simprints.simprintsidtester.fragments.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.simprints.simprintsidtester.model.domain.SimprintsResult
import com.simprints.simprintsidtester.model.local.LocalSimprintsResultDataSource

class ResultListViewModel(private val resultDataSource: LocalSimprintsResultDataSource) : ViewModel() {
    private val resultLiveData: MutableLiveData<List<SimprintsResult>> = MutableLiveData()
    private val mainResultList: MutableList<SimprintsResult> = mutableListOf()

    init {
        val results: List<SimprintsResult> = resultDataSource.getResults()

        mainResultList.clear()
        mainResultList.addAll(results)

        resultLiveData.value = mainResultList
    }

    fun getResults(): LiveData<List<SimprintsResult>> = resultLiveData

    fun filterList(query: String) {
        resultLiveData.value = mainResultList.filter {
            it.dateTimeSent.contains(query) ||
                    it.intentSent.contains(query) ||
                    it.resultReceived.contains(query)
        }
    }
}