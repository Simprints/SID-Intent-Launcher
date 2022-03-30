package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.simprints.simprintsidtester.model.domain.SimprintsIntent

open class LocalSimprintsIntentDataSourceImpl(private val localSimprintsIntentDao: LocalSimprintsIntentDao) :
    LocalSimprintsIntentDataSource {

    override fun getIntents(): LiveData<List<SimprintsIntent>> =
        Transformations.map(localSimprintsIntentDao.getIntents()) { intents ->
            intents.map { it.toDomainClass() }
        }

    override fun getById(id: String): LiveData<SimprintsIntent?> =
        Transformations.map(localSimprintsIntentDao.getById(id)) { intent ->
            intent?.toDomainClass()
        }

    override suspend fun update(intent: SimprintsIntent) =
        localSimprintsIntentDao.save(LocalSimprintsIntent(intent))

    override suspend fun delete(intent: SimprintsIntent) =
        localSimprintsIntentDao.delete(LocalSimprintsIntent(intent))

    override suspend fun deleteUncompletedSimprintsIntent() =
        localSimprintsIntentDao.deleteUncompletedSimprintsIntent()
}