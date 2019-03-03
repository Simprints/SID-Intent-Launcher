package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import com.simprints.simprintsidtester.model.domain.SimprintsIntent

interface LocalSimprintsIntentDataSource {

    fun getIntents(): LiveData<List<SimprintsIntent>>
    fun getById(id: String): LiveData<SimprintsIntent?>
    fun deleteUncompletedSimprintsIntent()
    fun delete(intent: SimprintsIntent)
    fun update(intent: SimprintsIntent)
}