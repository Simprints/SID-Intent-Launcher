package com.simprints.intentlauncher.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalSimprintsResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(intent: LocalSimprintsResult)

    @Query("SELECT *  FROM LocalSimprintsResult  ORDER BY rowid  desc")
    suspend fun getResults(): List<LocalSimprintsResult>

}
