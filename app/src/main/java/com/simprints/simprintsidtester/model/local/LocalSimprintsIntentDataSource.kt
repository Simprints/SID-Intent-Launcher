package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import com.simprints.simprintsidtester.model.domain.SimprintsIntent

interface LocalSimprintsIntentDataSource {

    fun getIntents(): LiveData<List<SimprintsIntent>>
    fun getById(id: String): LiveData<SimprintsIntent?>
    suspend fun deleteUncompletedSimprintsIntent()
    suspend fun delete(intent: SimprintsIntent)
    suspend fun update(intent: SimprintsIntent)
}