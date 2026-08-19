package com.example.data.local

import androidx.room.*
import com.example.data.model.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY code ASC")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT COUNT(*) FROM courses")
    suspend fun getCourseCount(): Int

    @Query("SELECT * FROM courses WHERE code = :code LIMIT 1")
    suspend fun getCourseByCode(code: String): Course?

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: Long): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Update
    suspend fun updateCourse(course: Course)

    @Query("UPDATE courses SET syllabusProgressPercent = :progress WHERE id = :id")
    suspend fun updateSyllabusProgress(id: Long, progress: Int)
}
