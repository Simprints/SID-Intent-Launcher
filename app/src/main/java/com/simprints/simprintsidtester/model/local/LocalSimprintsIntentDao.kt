package com.simprints.simprintsidtester.model.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LocalSimprintsIntentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(intent: LocalSimprintsIntent)

    @Delete
    fun delete(intent: LocalSimprintsIntent)

    @Query("DELETE FROM localsimprintsintent WHERE name = \"\"")
    fun deleteUncompletedSimprintsIntent()

    @Query("SELECT * FROM localsimprintsintent ORDER BY name")
    fun getIntents(): LiveData<List<LocalSimprintsIntent>>

    @Query("SELECT * from localsimprintsintent WHERE id= :id")
    fun getById(id: String): LiveData<LocalSimprintsIntent?>

}
