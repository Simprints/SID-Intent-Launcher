package com.simprints.intentlauncher.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import kotlinx.coroutines.flow.Flow

@Dao
interface IntentPresetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(intent: IntentPresetEntity)

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, DATETIME(timestamp) AS date_timestamp  FROM IntentPresetEntity ORDER BY date_timestamp DESC")
    fun getAll(): Flow<List<IntentPresetEntity>>

    @Query("DELETE FROM IntentPresetEntity WHERE id = :id")
    suspend fun delete(id: String)
}
