package com.simprints.simprintsidtester.model.local

import com.simprints.simprintsidtester.model.domain.SimprintsResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


open class LocalSimprintsResultDataSource @Inject constructor(
    private val localSimprintsResultDao: LocalSimprintsResultDao,
) {

    suspend fun update(simprintsResult: SimprintsResult) =
        localSimprintsResultDao.save(LocalSimprintsResult.fromSimprintsResult(simprintsResult))

    suspend fun getResults(): List<SimprintsResult> = withContext(Dispatchers.IO) {
        localSimprintsResultDao.getResults().map { it.toDomainClass() }
    }
}
