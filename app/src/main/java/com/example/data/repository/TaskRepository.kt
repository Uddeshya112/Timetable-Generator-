package com.example.data.repository

import com.example.data.local.TaskDao
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<TaskItem>> = taskDao.getPendingTasks()

    fun getTasksByCourse(courseCode: String): Flow<List<TaskItem>> =
        taskDao.getTasksByCourse(courseCode)

    suspend fun insertTask(task: TaskItem): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: TaskItem) =
        taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskItem) =
        taskDao.deleteTask(task)

    suspend fun toggleTaskCompleted(task: TaskItem) {
        val nextState = !task.isCompleted
        val completedAt = if (nextState) System.currentTimeMillis() else null
        taskDao.setTaskCompleted(task.id, nextState, completedAt)
    }

    suspend fun incrementPomodoro(taskId: Long) =
        taskDao.incrementPomodoro(taskId)
}
