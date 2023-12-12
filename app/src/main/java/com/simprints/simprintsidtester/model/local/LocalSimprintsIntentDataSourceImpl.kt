package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

import com.simprints.simprintsidtester.model.domain.SimprintsIntent

open class LocalSimprintsIntentDataSourceImpl(private val localSimprintsIntentDao: LocalSimprintsIntentDao) :
    LocalSimprintsIntentDataSource {

    override fun getIntents(): LiveData<List<SimprintsIntent>> = localSimprintsIntentDao
        .getIntents().map { intents -> intents.map { it.toDomainClass() } }


    override fun getById(id: String): LiveData<SimprintsIntent?> = localSimprintsIntentDao
        .getById(id).map { intent -> intent?.toDomainClass() }

    override suspend fun update(intent: SimprintsIntent) =
        localSimprintsIntentDao.save(LocalSimprintsIntent(intent))

    override suspend fun delete(intent: SimprintsIntent) =
        localSimprintsIntentDao.delete(LocalSimprintsIntent(intent))

    override suspend fun deleteUncompletedSimprintsIntent() =
        localSimprintsIntentDao.deleteUncompletedSimprintsIntent()
}