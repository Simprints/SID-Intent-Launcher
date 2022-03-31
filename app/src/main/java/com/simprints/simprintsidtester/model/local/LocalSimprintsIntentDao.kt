package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LocalSimprintsIntentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(intent: LocalSimprintsIntent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveIntentsList(intents: List<LocalSimprintsIntent>)

    @Delete
    suspend fun delete(intent: LocalSimprintsIntent)

    @Query("DELETE FROM localsimprintsintent WHERE name = \"\"")
    suspend fun deleteUncompletedSimprintsIntent()

    @Query("SELECT * FROM localsimprintsintent ORDER BY name")
    fun getIntents(): LiveData<List<LocalSimprintsIntent>>

    @Query("SELECT * from localsimprintsintent WHERE id= :id")
    fun getById(id: String): LiveData<LocalSimprintsIntent?>

}
