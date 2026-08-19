package com.example.data.local

import androidx.room.*
import com.example.data.model.AcademicNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM academic_notifications ORDER BY timestampMillis DESC")
    fun getAllNotifications(): Flow<List<AcademicNotification>>

    @Query("SELECT COUNT(*) FROM academic_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AcademicNotification): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AcademicNotification>)

    @Query("UPDATE academic_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE academic_notifications SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM academic_notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)
}
