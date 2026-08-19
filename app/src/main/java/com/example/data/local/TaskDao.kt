package com.example.data.local

import androidx.room.*
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDateMillis ASC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE courseCode = :courseCode ORDER BY dueDateMillis ASC")
    fun getTasksByCourse(courseCode: String): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDateMillis ASC")
    fun getPendingTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("UPDATE tasks SET isCompleted = :completed, completedAtMillis = :completedAt WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean, completedAt: Long?)

    @Query("UPDATE tasks SET pomodoroSessions = pomodoroSessions + 1 WHERE id = :id")
    suspend fun incrementPomodoro(id: Long)
}
