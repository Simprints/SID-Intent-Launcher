package com.simprints.intentlauncher.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IntentPresetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(intent: IntentPresetEntity)

    @Query("SELECT *  FROM IntentPresetEntity ORDER BY timestamp DESC")
    fun getAll(): Flow<List<IntentPresetEntity>>

    @Query("DELETE FROM IntentPresetEntity WHERE id = :id")
    suspend fun delete(id: String)
}
