package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.simprints.simprintsidtester.model.domain.SimprintsResult
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

interface LocalSimprintsresultDataSource {

    fun update(simprintsResult: SimprintsResult)

    fun getResults(): LiveData<List<SimprintsResult>>
}

open class LocalSimprintsresultDataSourceImpl : LocalSimprintsresultDataSource, KoinComponent {

    val localSimprintsResultDao: LocalSimprintsResultDao by inject()

    override fun update(simprintsResult: SimprintsResult) =
        localSimprintsResultDao.save(LocalSimprintsResult.fromSimprintsResult(simprintsResult))

    override fun getResults(): LiveData<List<SimprintsResult>> =
        Transformations.map(localSimprintsResultDao.getResults()) { intents ->
            intents.map { it.toDomainClass() }
        }
}