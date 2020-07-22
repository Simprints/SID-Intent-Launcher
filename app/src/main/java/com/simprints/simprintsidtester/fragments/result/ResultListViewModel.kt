package com.simprints.simprintsidtester.fragments.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.simprints.simprintsidtester.fragments.ui.ViewModelForAdapter
import com.simprints.simprintsidtester.model.domain.SimprintsResult
import com.simprints.simprintsidtester.model.local.LocalSimprintsResultDataSource
import org.koin.standalone.KoinComponent

class ResultListViewModel(
    private val resultDataSource: LocalSimprintsResultDataSource
) : ViewModel(), KoinComponent, ViewModelForAdapter {
    private val resultList: MutableList<SimprintsResult> = mutableListOf()

    override fun getCount(): Int = resultList.size
    fun getSimprintsResultAt(position: Int) = resultList[position]
    fun getResults(): LiveData<List<SimprintsResult>> = resultDataSource.getResults()
}