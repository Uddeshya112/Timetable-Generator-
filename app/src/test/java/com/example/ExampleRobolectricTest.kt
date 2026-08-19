package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CoordinatorBatch
import com.example.data.model.CoordinatorRoom
import com.example.data.model.CoordinatorTeacher
import com.example.data.model.StudentProfile
import com.example.domain.AcademicDataParser
import com.example.domain.TimetableGeneratorEngine
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("IntelliSchedule", appName)
  }

  @Test
  fun `academic parser parses template courses and teachers correctly`() {
    val template = AcademicDataParser.defaultCseTemplate
    val parsed = AcademicDataParser.parseCsvText(template, semester = 5)

    assertFalse("Parsed courses should not be empty", parsed.courses.isEmpty())
    assertFalse("Parsed teachers should not be empty", parsed.teachers.isEmpty())
    assertTrue("Parsed batches should contain B1", parsed.batches.contains("B1"))
    assertTrue("Parsed subgroups should contain G1", parsed.subgroups.contains("G1"))
  }

  @Test
  fun `timetable generator produces conflict-free schedule with zero clashes`() {
    val defaultBatches = listOf("B1", "B2", "B3", "B4")
    val defaultSubgroups = listOf("G1", "G2")
    val courses = TimetableGeneratorEngine.generateStandardDepartmentCourses(5)
    val teachers = TimetableGeneratorEngine.generateStandardFacultyPool()

    val result = TimetableGeneratorEngine.generateClashFreeTimetable(
      semester = 5,
      batches = defaultBatches,
      subgroups = defaultSubgroups,
      courses = courses,
      teacherPool = teachers
    )

    assertTrue("Schedule should contain sessions for all batches", result.sessions.isNotEmpty())
    assertEquals("Generated schedule must have exactly 0 clashes", 0, result.clashesDetected)
    assertEquals(100, result.auditReport.cohortHarmonyScore)

    // Verify subgroup B1-G1 has scheduled sessions across the week
    val b1Sessions = result.sessions.filter { it.batchId == "B1" }
    assertTrue("Batch B1 must have sessions scheduled", b1Sessions.isNotEmpty())
  }

  @Test
  fun `coordinator models hold attributes correctly`() {
    val batch = CoordinatorBatch("B1", "Batch B1 (CSE)", subgroupCount = 2, studentCount = 60)
    val teacher = CoordinatorTeacher("T1", "Dr. Anita Rao", designation = "Professor", canTeachLab = true)
    val room = CoordinatorRoom("R1", "LH-101", building = "Academic Block A", capacity = 80, isLab = false)

    assertEquals("B1", batch.batchId)
    assertEquals(60, batch.studentCount)
    assertEquals("Dr. Anita Rao", teacher.name)
    assertTrue(teacher.canTeachLab)
    assertEquals("LH-101", room.name)
    assertFalse(room.isLab)
  }

  @Test
  fun `student profile model holds correct attributes and defaults`() {
    val profile = StudentProfile(
      name = "Rahul Sharma",
      rollNumber = "102103456",
      semester = 5,
      batch = "B1",
      subgroup = "G1",
      department = "Computer Science & Engineering"
    )

    assertEquals("Rahul Sharma", profile.name)
    assertEquals("B1", profile.batch)
    assertEquals("G1", profile.subgroup)
  }
}
