package com.simprints.simprintsidtester.model.local

import com.simprints.simprintsidtester.model.domain.SimprintsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface LocalSimprintsResultDataSource {

    fun update(simprintsResult: SimprintsResult)

    suspend fun getResults(): List<SimprintsResult>
}

open class LocalSimprintsResultDataSourceImpl(private val localSimprintsResultDao: LocalSimprintsResultDao) :
    LocalSimprintsResultDataSource {

    override fun update(simprintsResult: SimprintsResult) =
        localSimprintsResultDao.save(LocalSimprintsResult.fromSimprintsResult(simprintsResult))

    override suspend fun getResults(): List<SimprintsResult> = withContext(Dispatchers.IO) {
        localSimprintsResultDao.getResults().map { it.toDomainClass() }
    }
}