package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.simprints.simprintsidtester.model.domain.SimprintsResult

interface LocalSimprintsResultDataSource {

    fun update(simprintsResult: SimprintsResult)

    fun getResults(): LiveData<List<SimprintsResult>>
}

open class LocalSimprintsResultDataSourceImpl(private val localSimprintsResultDao: LocalSimprintsResultDao) :
    LocalSimprintsResultDataSource {

    override fun update(simprintsResult: SimprintsResult) =
        localSimprintsResultDao.save(LocalSimprintsResult.fromSimprintsResult(simprintsResult))

    override fun getResults(): LiveData<List<SimprintsResult>> =
        Transformations.map(localSimprintsResultDao.getResults()) { intents ->
            intents.map { it.toDomainClass() }
        }
}