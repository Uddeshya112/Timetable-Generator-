package com.example.data.model

data class CoordinatorBatch(
    val batchId: String,
    val name: String = "Batch $batchId",
    val subgroupCount: Int = 2,
    val studentCount: Int = 60,
    val defaultLectureHall: String = "LH-101",
    val semester: Int = 5
)

data class CoordinatorRoom(
    val roomId: String,
    val name: String,
    val building: String = "Academic Complex",
    val capacity: Int = 70,
    val isLab: Boolean = false,
    val equipmentInfo: String = "Projector & Audio System"
)

data class CoordinatorTeacher(
    val teacherId: String,
    val name: String,
    val designation: String = "Assistant Professor",
    val department: String = "Computer Science & Engineering",
    val email: String = "",
    val maxDailySlots: Int = 3,
    val qualifiedCourseCodes: List<String> = emptyList(),
    val canTeachLab: Boolean = true,
    val canTeachLecture: Boolean = true,
    val preferredShift: String = "Flexible"
)

data class ConflictAuditReport(
    val totalSessionsCount: Int = 0,
    val totalBatchesCount: Int = 0,
    val totalTeachersCount: Int = 0,
    val totalRoomsCount: Int = 0,
    val clashesDetected: Int = 0,
    val roomUtilizationPercent: Int = 82,
    val facultyWorkloadBalancePercent: Int = 94,
    val cohortHarmonyScore: Int = 100,
    val conflictMessages: List<String> = emptyList(),
    val generationTimestampMillis: Long = System.currentTimeMillis()
)
