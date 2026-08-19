package com.example.data.local

import androidx.room.*
import com.example.data.model.ClassSession
import com.example.data.model.SessionStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TimetableDao {
    @Query("SELECT * FROM class_sessions ORDER BY dayOfWeek ASC, periodIndex ASC")
    fun getAllSessions(): Flow<List<ClassSession>>

    @Query("SELECT * FROM class_sessions WHERE dayOfWeek = :dayOfWeek ORDER BY periodIndex ASC")
    fun getSessionsForDay(dayOfWeek: Int): Flow<List<ClassSession>>

    @Query("SELECT * FROM class_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): ClassSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ClassSession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<ClassSession>)

    @Update
    suspend fun updateSession(session: ClassSession)

    @Query("UPDATE class_sessions SET status = :status, cancellationReason = :reason WHERE id = :id")
    suspend fun updateSessionStatus(id: Long, status: SessionStatus, reason: String?)

    @Query("DELETE FROM class_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    @Query("DELETE FROM class_sessions WHERE semesterNumber = :semesterNumber")
    suspend fun deleteSessionsForSemester(semesterNumber: Int)

    @Query("DELETE FROM class_sessions")
    suspend fun deleteAllSessions()
}
