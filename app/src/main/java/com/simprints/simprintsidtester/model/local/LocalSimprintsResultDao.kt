package com.simprints.simprintsidtester.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalSimprintsResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(intent: LocalSimprintsResult)

    @Query("SELECT * FROM LocalSimprintsResult")
    suspend fun getResults(): List<LocalSimprintsResult>

}
