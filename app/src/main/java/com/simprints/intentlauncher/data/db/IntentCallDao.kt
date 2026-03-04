package com.simprints.intentlauncher.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomWarnings
import kotlinx.coroutines.flow.Flow

@Dao
interface IntentCallDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(intent: IntentCallEntity)

    @Query("SELECT * FROM IntentCallEntity WHERE id = :id LIMIT 1")
    suspend fun get(id: String): IntentCallEntity?

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, DATETIME(timestamp) AS date_timestamp FROM IntentCallEntity ORDER BY date_timestamp DESC")
    fun getAll(): Flow<List<IntentCallEntity>>

    @Query("DELETE FROM IntentCallEntity WHERE id = :id")
    suspend fun delete(id: String)
}
