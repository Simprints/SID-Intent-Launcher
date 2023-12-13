package com.simprints.intentlauncher.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IntentCallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(intent: IntentCallEntity)

    @Query("SELECT * FROM IntentCallEntity WHERE id = :id LIMIT 1")
    suspend fun get(id: String): IntentCallEntity?

    @Query("SELECT *  FROM IntentCallEntity ORDER BY timestamp DESC")
    fun getAll(): Flow<List<IntentCallEntity>>
}
