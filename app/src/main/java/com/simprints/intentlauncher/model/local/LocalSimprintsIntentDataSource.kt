package com.simprints.intentlauncher.model.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

import com.simprints.intentlauncher.model.domain.SimprintsIntent
import javax.inject.Inject

open class LocalSimprintsIntentDataSource @Inject constructor(
    private val localSimprintsIntentDao: LocalSimprintsIntentDao,
) {

    fun getIntents(): LiveData<List<SimprintsIntent>> = localSimprintsIntentDao
        .getIntents().map { intents -> intents.map { it.toDomainClass() } }

    fun getById(id: String): LiveData<SimprintsIntent?> = localSimprintsIntentDao
        .getById(id).map { intent -> intent?.toDomainClass() }

    suspend fun update(intent: SimprintsIntent) =
        localSimprintsIntentDao.save(LocalSimprintsIntent(intent))

    suspend fun delete(intent: SimprintsIntent) =
        localSimprintsIntentDao.delete(LocalSimprintsIntent(intent))

    suspend fun deleteUncompletedSimprintsIntent() =
        localSimprintsIntentDao.deleteUncompletedSimprintsIntent()
}
