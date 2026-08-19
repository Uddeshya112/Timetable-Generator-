package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class Priority(val label: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4)
}

enum class TaskType(val label: String) {
    ASSIGNMENT("Assignment"),
    LAB_REPORT("Lab Report"),
    PROJECT("Project"),
    EXAM_PREP("Exam Prep"),
    READING("Reading"),
    MAKEUP_CLASS("Makeup Class")
}

enum class SessionType(val label: String, val defaultDurationSlots: Int) {
    LECTURE("Lecture (Batch)", 1),
    TUTORIAL("Tutorial", 1),
    LAB("Laboratory (Subgroup)", 2),
    MAKEUP("Makeup (Self-Healed)", 1)
}

enum class TutorialScope {
    BATCH_LEVEL,
    SUBGROUP_LEVEL
}

enum class CourseCategory(val label: String) {
    BSC("Basic Science"),
    ESC("Engineering Science"),
    PCC("Program Core"),
    PEC("Professional Elective"),
    OEC("Generic Elective"),
    HSS("Humanities & Social"),
    PRJ("Project / Capstone"),
    OTH("Other Mandatory")
}

enum class SessionStatus(val label: String) {
    PLANNED("Planned"),
    PUBLISHED("Published"),
    CANCELLED("Cancelled"),
    RESCHEDULED("Rescheduled"),
    COMPLETED("Completed")
}

enum class SyncStatus(val label: String) {
    SYNCED("Synced"),
    SYNCING("Syncing..."),
    OFFLINE_QUEUED("Offline (SQLite)"),
    SYNC_ERROR("Sync conflict")
}

enum class NotificationCategory {
    CANCELLATION,
    MAKEUP_FOUND,
    ROOM_CHANGE,
    TASK_DEADLINE,
    EXAM_ALERT,
    SYSTEM
}

data class TeacherQualification(
    val teacherId: String,
    val teacherName: String,
    val department: String = "Computer Science & Engineering",
    val qualifiedCourseCode: String,
    val canTeachLecture: Boolean = true,
    val canTeachTutorial: Boolean = true,
    val canTeachLab: Boolean = true,
    val maxWeeklyWorkloadHours: Int = 18,
    val currentWeeklyWorkloadHours: Int = 14,
    val preferredPeriods: String = "Morning (P1-P4)",
    val researchPeriods: String = "Wednesday Afternoon",
    val availabilityStatus: String = "Available"
)

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val courseId: Long,
    val courseCode: String,
    val courseName: String,
    val semesterNumber: Int = 5,
    val batchId: String = "B1",
    val subgroupId: String = "B1-G1",
    val dueDateMillis: Long,
    val priority: Priority = Priority.MEDIUM,
    val taskType: TaskType = TaskType.ASSIGNMENT,
    val isCompleted: Boolean = false,
    val completedAtMillis: Long? = null,
    val estimatedMinutes: Int = 45,
    val pomodoroSessions: Int = 0,
    val subTasksJson: String = "[]",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // e.g. "UCS310", "UML501"
    val name: String, // e.g. "Database Management Systems"
    val shortName: String, // e.g. "DBMS"
    val semesterNumber: Int, // 1 to 8 (From official curriculum PDF)
    val category: CourseCategory = CourseCategory.PCC,
    val lectureHours: Int = 3, // L
    val tutorialHours: Int = 0, // T
    val practicalHours: Int = 2, // P
    val credits: Double = 4.0, // Cr
    val tutorialScope: TutorialScope = TutorialScope.BATCH_LEVEL,
    val primaryInstructorName: String = "Prof. Sharma",
    val totalPlannedLectures: Int = 45,
    val completedLectures: Int = 38,
    val cancelledLectures: Int = 2,
    val preferredRoom: String = "Room 204",
    val syllabusProgressPercent: Int = 85,
    val nextExamDateMillis: Long? = null,
    val examDeficitLectures: Int = 2
)

@Entity(tableName = "class_sessions")
data class ClassSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val courseCode: String,
    val courseName: String,
    val semesterNumber: Int = 5,
    val batchId: String = "B1", // B1 to B12
    val subgroupId: String = "B1-ALL", // "B1-ALL" for common batch lecture, or "B1-G1".."B1-G10" for lab
    val isCommonBatchLecture: Boolean = true, // If true, occupies all 10 subgroups (G1..G10) simultaneously
    val instructorId: String = "T1",
    val instructorName: String,
    val roomName: String,
    val building: String = "Engineering Block A",
    val dayOfWeek: Int, // 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday
    val periodIndex: Int, // 1 = 08:30-09:20, 2 = 09:30-10:20, etc.
    val durationSlots: Int = 1, // 1 for 50 min lecture, 2 for 100 min lab
    val timeDisplay: String, // e.g. "08:30 - 09:20"
    val sessionType: SessionType = SessionType.LECTURE,
    val status: SessionStatus = SessionStatus.PUBLISHED,
    val cancellationReason: String? = null,
    val originalSessionId: Long? = null,
    val isLocked: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "makeup_opportunities")
data class MakeupOpportunity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cancelledSessionId: Long,
    val courseCode: String,
    val courseName: String,
    val semesterNumber: Int = 5,
    val batchId: String = "B1",
    val subgroupId: String = "B1-ALL",
    val instructorName: String,
    val targetDayOfWeek: Int,
    val targetDayName: String, // e.g. "Thursday"
    val targetTimeSlot: String, // e.g. "11:30 - 12:20 (P4)"
    val targetPeriodIndex: Int,
    val targetDurationSlots: Int = 1,
    val targetRoom: String,
    val compatibilityScore: Int = 96,
    val teacherAvailable: Boolean = true,
    val studentsAvailable: Boolean = true,
    val roomSuitable: Boolean = true,
    val conflictReason: String = "Zero hard violations. Free slot matches cross-cancellation.",
    val isAccepted: Boolean = false,
    val isRejected: Boolean = false,
    val votesCount: Int = 42,
    val totalStudents: Int = 48
)

@Entity(tableName = "academic_notifications")
data class AcademicNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedEntityId: Long? = null,
    val actionText: String? = null
)

@Entity(tableName = "outbox_events")
data class OutboxEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientOperationId: String = UUID.randomUUID().toString(),
    val eventType: String,
    val payloadJson: String,
    val status: String = "SYNCED",
    val createdAtMillis: Long = System.currentTimeMillis()
)

data class StudentProfile(
    val name: String = "Alex Mercer",
    val rollNumber: String = "102103456",
    val department: String = "Computer Science & Engineering",
    val semester: Int = 5,
    val batch: String = "B1",
    val subgroup: String = "G1",
    val email: String = "alex.mercer@univ.edu",
    val academicYear: String = "2024-2025 (3rd Year)",
    val cgpa: Double = 8.74,
    val attendanceRatePercent: Int = 91,
    val creditsEarned: Double = 92.5,
    val targetWeeklyStudyHours: Int = 25,
    val notificationsEnabled: Boolean = true
)

enum class UserRole(val label: String, val badge: String) {
    STUDENT("Student", "🎓"),
    TEACHER("Faculty / Teacher", "👨‍🏫"),
    COORDINATOR("Academic Coordinator", "⚡")
}

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey
    val userId: String,
    val password: String,
    val role: UserRole = UserRole.STUDENT,
    val fullName: String,
    val email: String = "",
    val department: String = "Computer Science & Engineering",
    val semester: Int = 5,
    val batch: String = "B1",
    val subgroup: String = "G1",
    val designation: String = "",
    val phone: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)
