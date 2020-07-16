package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocalSimprintsResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(intent: LocalSimprintsResult)

    @Query("SELECT * FROM LocalSimprintsResult")
    fun getResults(): LiveData<List<LocalSimprintsResult>>

}
