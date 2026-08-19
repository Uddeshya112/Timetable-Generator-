package com.example.data.local

import androidx.room.*
import com.example.data.model.OutboxEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface OutboxDao {
    @Query("SELECT * FROM outbox_events ORDER BY createdAtMillis DESC")
    fun getAllEvents(): Flow<List<OutboxEvent>>

    @Query("SELECT * FROM outbox_events WHERE status = 'PENDING' ORDER BY createdAtMillis ASC")
    suspend fun getPendingEvents(): List<OutboxEvent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: OutboxEvent): Long

    @Query("UPDATE outbox_events SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM outbox_events WHERE status = 'SYNCED'")
    suspend fun clearSyncedEvents()
}
