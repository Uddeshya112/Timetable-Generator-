package com.example.data.local

import androidx.room.*
import com.example.data.model.MakeupOpportunity
import kotlinx.coroutines.flow.Flow

@Dao
interface MakeupDao {
    @Query("SELECT * FROM makeup_opportunities ORDER BY compatibilityScore DESC")
    fun getAllMakeupOpportunities(): Flow<List<MakeupOpportunity>>

    @Query("SELECT * FROM makeup_opportunities WHERE isAccepted = 0 AND isRejected = 0 ORDER BY compatibilityScore DESC")
    fun getPendingOpportunities(): Flow<List<MakeupOpportunity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunity(opp: MakeupOpportunity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOpportunities(opps: List<MakeupOpportunity>)

    @Update
    suspend fun updateOpportunity(opp: MakeupOpportunity)

    @Query("UPDATE makeup_opportunities SET isAccepted = 1 WHERE id = :id")
    suspend fun acceptOpportunity(id: Long)

    @Query("UPDATE makeup_opportunities SET isRejected = 1 WHERE id = :id")
    suspend fun rejectOpportunity(id: Long)

    @Query("UPDATE makeup_opportunities SET votesCount = votesCount + 1 WHERE id = :id")
    suspend fun voteForOpportunity(id: Long)
}
