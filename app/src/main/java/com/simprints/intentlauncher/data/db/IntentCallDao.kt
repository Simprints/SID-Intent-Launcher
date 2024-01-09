package com.simprints.intentlauncher.data.db

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

    @Query("SELECT *, DATETIME(timestamp) AS date_timestamp FROM IntentCallEntity ORDER BY date_timestamp DESC")
    fun getAll(): Flow<List<IntentCallEntity>>

    @Query("DELETE FROM IntentCallEntity WHERE id = :id")
    suspend fun delete(id: String)
}
