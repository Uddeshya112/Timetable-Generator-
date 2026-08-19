package com.example.data.local

import com.example.data.model.*
import java.util.Calendar

object SampleDataGenerator {

    val teacherPool = listOf(
        TeacherQualification("T1", "Prof. R. K. Sharma", "CSE", "UML501", canTeachLecture = true, canTeachTutorial = true, canTeachLab = true, maxWeeklyWorkloadHours = 18, currentWeeklyWorkloadHours = 14, preferredPeriods = "Morning P1-P4", researchPeriods = "Wed Afternoon"),
        TeacherQualification("T2", "Dr. Anita Rao", "CSE", "UML501", canTeachLecture = true, canTeachTutorial = true, canTeachLab = true, maxWeeklyWorkloadHours = 18, currentWeeklyWorkloadHours = 15, preferredPeriods = "P2-P5", researchPeriods = "Thu Afternoon"),
        TeacherQualification("T3", "Prof. Vikram Malhotra", "CSE", "UCS553", canTeachLecture = true, canTeachTutorial = true, canTeachLab = true, maxWeeklyWorkloadHours = 18, currentWeeklyWorkloadHours = 13, preferredPeriods = "Morning P1-P3", researchPeriods = "Tue Afternoon"),
        TeacherQualification("T4", "Dr. Sneha Verma", "CSE", "UCS553", canTeachLecture = true, canTeachTutorial = false, canTeachLab = true, maxWeeklyWorkloadHours = 16, currentWeeklyWorkloadHours = 12, preferredPeriods = "P3-P6", researchPeriods = "Mon Morning"),
        TeacherQualification("T5", "Prof. Amit Gupta", "CSE", "UCS503", canTeachLecture = true, canTeachTutorial = true, canTeachLab = true, maxWeeklyWorkloadHours = 18, currentWeeklyWorkloadHours = 16, preferredPeriods = "P1-P4", researchPeriods = "Fri Afternoon"),
        TeacherQualification("T6", "Dr. Pooja Nair", "CSE", "UCS615", canTeachLecture = true, canTeachTutorial = true, canTeachLab = true, maxWeeklyWorkloadHours = 16, currentWeeklyWorkloadHours = 14, preferredPeriods = "P2-P5", researchPeriods = "Wed Morning"),
        TeacherQualification("T7", "Prof. S. Joseph", "CSE", "UCS510", canTeachLecture = true, canTeachTutorial = true, canTeachLab = false, maxWeeklyWorkloadHours = 16, currentWeeklyWorkloadHours = 11, preferredPeriods = "Morning P1-P3", researchPeriods = "Thu Morning"),
        TeacherQualification("T8", "Dr. Harish Chandra", "CSE", "UCSXXX", canTeachLecture = true, canTeachTutorial = true, canTeachLab = true, maxWeeklyWorkloadHours = 18, currentWeeklyWorkloadHours = 14, preferredPeriods = "P3-P6", researchPeriods = "Fri Morning")
    )

    suspend fun populateInitialData(db: AppDatabase) {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        // 1. Comprehensive Curriculum Covering SEMESTER I to SEMESTER VIII from SUGC/SPGC meeting
        val allCurriculumCourses = listOf(
            // SEMESTER I
            Course(1, "UCB009", "Chemistry", "Chemistry", 1, CourseCategory.BSC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. S. K. Roy", preferredRoom = "Chem Hall 1"),
            Course(2, "UES103", "Programming for Problem Solving", "PPS (C/C++)", 1, CourseCategory.ESC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. N. Patel", preferredRoom = "Lab 101"),
            Course(3, "UES013", "Electrical & Electronics Engineering", "EEE", 1, CourseCategory.ESC, 3, 1, 2, 4.5, primaryInstructorName = "Dr. M. K. Sen", preferredRoom = "EEE Lab"),
            Course(4, "UEN008", "Energy and Environment", "Energy & Env", 1, CourseCategory.OTH, 2, 0, 0, 2.0, primaryInstructorName = "Dr. A. Roy", preferredRoom = "Hall 201"),
            Course(5, "UMA022", "Calculus for Engineers", "Calculus", 1, CourseCategory.BSC, 3, 1, 0, 3.5, primaryInstructorName = "Prof. R. Das", preferredRoom = "Hall 302"),

            // SEMESTER II
            Course(6, "UPH013", "Physics", "Physics", 2, CourseCategory.BSC, 3, 1, 2, 4.5, primaryInstructorName = "Dr. K. Sharma", preferredRoom = "Physics Lab"),
            Course(7, "UES101", "Engineering Drawing", "Engg Drawing", 2, CourseCategory.ESC, 2, 4, 0, 4.0, primaryInstructorName = "Prof. S. Das", preferredRoom = "Drawing Hall A"),
            Course(8, "UHU003", "Professional Communication", "Prof Comm", 2, CourseCategory.HSS, 2, 0, 2, 3.0, primaryInstructorName = "Dr. N. Bose", preferredRoom = "Lang Lab"),
            Course(9, "UES102", "Manufacturing Processes", "Mfg Processes", 2, CourseCategory.ESC, 2, 0, 2, 3.0, primaryInstructorName = "Prof. T. Sen", preferredRoom = "Workshop 1"),
            Course(10, "UMA023", "Differential Equations and Linear Algebra", "Diff Eq & LA", 2, CourseCategory.BSC, 3, 1, 0, 3.5, primaryInstructorName = "Prof. B. Ghosh", preferredRoom = "Hall 204"),

            // SEMESTER III
            Course(11, "UCS303", "Operating System", "OS", 3, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Amit Gupta", preferredRoom = "Room 201"),
            Course(12, "UTA018", "Object Oriented Programming", "OOP (Java)", 3, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. V. Malhotra", preferredRoom = "Comp Lab 2"),
            Course(13, "UCS301", "Data Structures", "Data Structures", 3, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. R. K. Sharma", preferredRoom = "Room 204"),
            Course(14, "UCS405", "Discrete Mathematical Structures", "Discrete Math", 3, CourseCategory.PCC, 3, 1, 0, 3.5, primaryInstructorName = "Prof. R. Das", preferredRoom = "Hall 105"),
            Course(15, "UTA016", "Engineering Design Project I", "EDP-I", 3, CourseCategory.ESC, 1, 0, 2, 3.0, primaryInstructorName = "Dr. P. Nair", preferredRoom = "Maker Lab"),
            Course(16, "UMA021", "Numerical Linear Algebra", "Num Lin Alg", 3, CourseCategory.BSC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. B. Ghosh", preferredRoom = "Hall 302"),
            Course(17, "UHU052", "The Evolutionary Basis of Human Behaviour", "Human Behaviour", 3, CourseCategory.HSS, 1, 0, 0, 1.0, primaryInstructorName = "Dr. N. Bose", preferredRoom = "Hall 102"),
            Course(18, "UCS320", "Introduction to Sustainable Green Computing", "Green Computing", 3, CourseCategory.PCC, 1, 0, 0, 1.0, primaryInstructorName = "Dr. S. Joseph", preferredRoom = "Hall 102"),

            // SEMESTER IV
            Course(19, "UCS415", "Design and Analysis of Algorithms", "DAA", 4, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. Verma", preferredRoom = "Room 302"),
            Course(20, "UCS310", "Database Management Systems", "DBMS", 4, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. R. K. Sharma", preferredRoom = "Room 204"),
            Course(21, "UCS414", "Computer Networks", "Networks", 4, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. Joseph", preferredRoom = "Room 201"),
            Course(22, "UCS321", "AI for Engineers", "AI for Engg", 4, CourseCategory.PCC, 2, 0, 2, 3.0, primaryInstructorName = "Dr. Anita Rao", preferredRoom = "Room 105"),
            Course(23, "UMA401", "Probability and Statistics", "Prob & Stats", 4, CourseCategory.BSC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. R. Das", preferredRoom = "Hall 302"),
            Course(24, "UTA024", "Engineering Design Project II", "EDP-II", 4, CourseCategory.PCC, 1, 0, 4, 3.0, primaryInstructorName = "Dr. P. Nair", preferredRoom = "Project Studio"),
            Course(25, "UTD003", "Aptitude Skills Building", "Aptitude Skills", 4, CourseCategory.HSS, 2, 0, 0, 2.0, primaryInstructorName = "Dr. N. Bose", preferredRoom = "Auditorium"),

            // SEMESTER V (Current Active Semester)
            Course(26, "UML501", "Machine Learning", "ML", 5, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Anita Rao", totalPlannedLectures = 45, completedLectures = 38, cancelledLectures = 1, preferredRoom = "LH-101", nextExamDateMillis = now + (20 * oneDayMillis), examDeficitLectures = 1),
            Course(27, "UCS553", "Enterprise Web Application", "Enterprise Web", 5, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. Vikram Malhotra", totalPlannedLectures = 45, completedLectures = 40, cancelledLectures = 0, preferredRoom = "LH-102", nextExamDateMillis = now + (25 * oneDayMillis), examDeficitLectures = 0),
            Course(28, "UCS615", "Image Processing", "Image Proc", 5, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Dr. Pooja Nair", totalPlannedLectures = 42, completedLectures = 36, cancelledLectures = 1, preferredRoom = "Room 204", nextExamDateMillis = now + (28 * oneDayMillis), examDeficitLectures = 1),
            Course(29, "UCS503", "Software Engineering", "Soft Engg", 5, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. Amit Gupta", totalPlannedLectures = 45, completedLectures = 39, cancelledLectures = 0, preferredRoom = "LH-101", nextExamDateMillis = now + (22 * oneDayMillis), examDeficitLectures = 0),
            Course(30, "UCS510", "Computer Architecture and Organization", "CAO", 5, CourseCategory.PCC, 3, 0, 0, 3.0, primaryInstructorName = "Prof. S. Joseph", totalPlannedLectures = 40, completedLectures = 35, cancelledLectures = 0, preferredRoom = "LH-102", nextExamDateMillis = now + (30 * oneDayMillis), examDeficitLectures = 0),
            Course(31, "PEC501", "Elective-I: Cloud Computing", "Cloud Elective", 5, CourseCategory.PEC, 2, 0, 2, 3.0, primaryInstructorName = "Dr. Harish Chandra", totalPlannedLectures = 32, completedLectures = 28, cancelledLectures = 0, preferredRoom = "Room 302", nextExamDateMillis = now + (32 * oneDayMillis), examDeficitLectures = 0),
            Course(32, "UCS512", "Ethics and Risk Mitigation in AI", "AI Ethics", 5, CourseCategory.ESC, 3, 0, 0, 3.0, primaryInstructorName = "Dr. Sneha Verma", totalPlannedLectures = 38, completedLectures = 34, cancelledLectures = 0, preferredRoom = "Room 105", nextExamDateMillis = now + (35 * oneDayMillis), examDeficitLectures = 0),

            // SEMESTER VI
            Course(33, "UCS701", "Theory of Computation", "TOC", 6, CourseCategory.PCC, 3, 1, 0, 3.5, primaryInstructorName = "Prof. R. Das", preferredRoom = "Hall 204"),
            Course(34, "UMA035", "Numerical Optimization", "Num Optim", 6, CourseCategory.ESC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. B. Ghosh", preferredRoom = "Hall 302"),
            Course(35, "UTA025", "Innovation and Entrepreneurship", "I&E", 6, CourseCategory.PRJ, 1, 0, 2, 3.0, primaryInstructorName = "Dr. N. Bose", preferredRoom = "Incubation Lab"),
            Course(36, "PEC601", "Elective-II: Information Retrieval", "Info Retrieval", 6, CourseCategory.PEC, 2, 0, 2, 3.0, primaryInstructorName = "Dr. Anita Rao", preferredRoom = "Room 105"),
            Course(37, "PEC602", "Elective-III: Distributed Algorithms", "Dist Algorithms", 6, CourseCategory.PEC, 2, 0, 2, 3.0, primaryInstructorName = "Prof. Amit Gupta", preferredRoom = "Room 201"),
            Course(38, "UCS797", "Capstone Project* - Starts", "Capstone I", 6, CourseCategory.PRJ, 0, 0, 2, 0.0, primaryInstructorName = "Project Committee", preferredRoom = "Project Lab"),
            Course(39, "OEC601", "Generic Elective", "Open Elective", 6, CourseCategory.OEC, 2, 0, 0, 2.0, primaryInstructorName = "University Faculty", preferredRoom = "Auditorium"),
            Course(40, "UCS619", "Domain Specific Applications for Engg Graduates", "Domain Apps", 6, CourseCategory.PCC, 2, 0, 2, 3.0, primaryInstructorName = "Dr. P. Nair", preferredRoom = "Comp Lab 3"),

            // SEMESTER VII
            Course(41, "UCS802", "Compiler Construction", "Compiler", 7, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. Amit Gupta", preferredRoom = "LH-101"),
            Course(42, "UHU005", "Humanities for Engineers", "Humanities", 7, CourseCategory.HSS, 2, 0, 2, 3.0, primaryInstructorName = "Dr. N. Bose", preferredRoom = "Hall 105"),
            Course(43, "UCS714", "Agentic AI", "Agentic AI", 7, CourseCategory.PCC, 2, 0, 2, 3.0, primaryInstructorName = "Dr. Anita Rao", preferredRoom = "AI Lab 1"),
            Course(44, "PEC701", "Elective-IV: Quantum Computing", "Quantum Comp", 7, CourseCategory.PEC, 2, 0, 2, 3.0, primaryInstructorName = "Dr. Harish Chandra", preferredRoom = "Room 302"),
            Course(45, "UCS798", "Capstone Project", "Capstone Final", 7, CourseCategory.PRJ, 0, 0, 2, 8.0, primaryInstructorName = "Project Committee", preferredRoom = "Project Studio"),

            // SEMESTER VIII
            Course(46, "UCS898", "Project Semester (Industry / Research)", "Project Semester", 8, CourseCategory.PRJ, 0, 0, 0, 15.0, primaryInstructorName = "Industry Mentors", preferredRoom = "External/Research Lab"),
            Course(47, "UCS813", "Social Network Analysis", "SNA", 8, CourseCategory.PCC, 2, 0, 2, 3.0, primaryInstructorName = "Prof. V. Malhotra", preferredRoom = "Room 204"),
            Course(48, "UCS806", "Ethical Hacking & Cyber Security", "Ethical Hacking", 8, CourseCategory.PCC, 3, 0, 2, 4.0, primaryInstructorName = "Prof. S. Joseph", preferredRoom = "Security Lab"),
            Course(49, "UCS899", "Project", "Major Project", 8, CourseCategory.PRJ, 0, 0, 8, 8.0, primaryInstructorName = "Faculty Advisors", preferredRoom = "Research Wing"),
            Course(50, "UCS900", "Start-Up Semester", "Start-Up", 8, CourseCategory.PRJ, 0, 0, 0, 15.0, primaryInstructorName = "Incubation Center", preferredRoom = "TBI Hub")
        )
        db.courseDao().insertCourses(allCurriculumCourses)

        // 2. Multi-Batch & Multi-Subgroup Class Sessions for Semester 5
        // Enforcing Rule: Lectures are Common for Batch B1 (all subgroups G1..G10 occupied).
        // Labs are Subgroup-Specific (G1..G10 in respective labs with assigned faculty).
        val sessions = mutableListOf<ClassSession>()

        // Generate sessions for Batches B1 to B12
        for (batchNum in 1..12) {
            val bId = "B$batchNum"
            val roomBase = if (batchNum % 2 == 1) "LH-101" else "LH-102"

            // MONDAY
            // P1: Common Batch Lecture: Machine Learning (UML501)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 1L,
                    courseId = 26,
                    courseCode = "UML501",
                    courseName = "Machine Learning",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T1",
                    instructorName = if (batchNum % 2 == 1) "Prof. R. K. Sharma" else "Dr. Anita Rao",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 1, // Monday
                    periodIndex = 1,
                    durationSlots = 1,
                    timeDisplay = "08:30 - 09:20",
                    sessionType = SessionType.LECTURE,
                    status = if (batchNum == 1) SessionStatus.CANCELLED else SessionStatus.PUBLISHED,
                    cancellationReason = if (batchNum == 1) "Faculty unavailable (Medical Leave). Outbox event dispatched to Self-Healing Engine." else null
                )
            )

            // P2: Common Batch Lecture: Enterprise Web Application (UCS553)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 2L,
                    courseId = 27,
                    courseCode = "UCS553",
                    courseName = "Enterprise Web Application",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T3",
                    instructorName = "Prof. Vikram Malhotra",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 1,
                    periodIndex = 2,
                    durationSlots = 1,
                    timeDisplay = "09:30 - 10:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P3: Common Batch Lecture: Software Engineering (UCS503)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 3L,
                    courseId = 29,
                    courseCode = "UCS503",
                    courseName = "Software Engineering",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T5",
                    instructorName = "Prof. Amit Gupta",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 1,
                    periodIndex = 3,
                    durationSlots = 1,
                    timeDisplay = "10:30 - 11:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P5 & P6 (13:10 - 14:50): Subgroup-Specific ML Practical Labs (G1 to G10)
            for (subGroupNum in 1..10) {
                val gId = "$bId-G$subGroupNum"
                val labRoom = "Comp Lab ${(subGroupNum % 4) + 1}"
                val teacherIndex = (subGroupNum % 8)
                val assignedTeacher = teacherPool[teacherIndex]

                sessions.add(
                    ClassSession(
                        id = (batchNum * 1000) + (subGroupNum * 10) + 5L,
                        courseId = 26,
                        courseCode = "UML501",
                        courseName = "Machine Learning Lab",
                        semesterNumber = 5,
                        batchId = bId,
                        subgroupId = gId,
                        isCommonBatchLecture = false,
                        instructorId = assignedTeacher.teacherId,
                        instructorName = assignedTeacher.teacherName,
                        roomName = labRoom,
                        building = "IT Wing, 1st Fl",
                        dayOfWeek = 1,
                        periodIndex = 5,
                        durationSlots = 2,
                        timeDisplay = "13:10 - 14:50 (2 Slots)",
                        sessionType = SessionType.LAB,
                        status = SessionStatus.PUBLISHED
                    )
                )
            }

            // TUESDAY
            // P1: Image Processing Lecture (UCS615)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 6L,
                    courseId = 28,
                    courseCode = "UCS615",
                    courseName = "Image Processing",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T6",
                    instructorName = "Dr. Pooja Nair",
                    roomName = "Room 204",
                    building = "Engineering Block A",
                    dayOfWeek = 2,
                    periodIndex = 1,
                    durationSlots = 1,
                    timeDisplay = "08:30 - 09:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P2: Computer Architecture Lecture (UCS510)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 7L,
                    courseId = 30,
                    courseCode = "UCS510",
                    courseName = "Computer Architecture & Organization",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T7",
                    instructorName = "Prof. S. Joseph",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 2,
                    periodIndex = 2,
                    durationSlots = 1,
                    timeDisplay = "09:30 - 10:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P3: Elective-I Cloud Computing (PEC501)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 8L,
                    courseId = 31,
                    courseCode = "PEC501",
                    courseName = "Cloud Computing",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T8",
                    instructorName = "Dr. Harish Chandra",
                    roomName = "Room 302",
                    building = "Engineering Block B",
                    dayOfWeek = 2,
                    periodIndex = 3,
                    durationSlots = 1,
                    timeDisplay = "10:30 - 11:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // WEDNESDAY
            // P1: Machine Learning (UML501)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 9L,
                    courseId = 26,
                    courseCode = "UML501",
                    courseName = "Machine Learning",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T2",
                    instructorName = "Dr. Anita Rao",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 3,
                    periodIndex = 1,
                    durationSlots = 1,
                    timeDisplay = "08:30 - 09:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P2: Ethics and Risk Mitigation in AI (UCS512)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 10L,
                    courseId = 32,
                    courseCode = "UCS512",
                    courseName = "Ethics and Risk Mitigation in AI",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T4",
                    instructorName = "Dr. Sneha Verma",
                    roomName = "Room 105",
                    building = "Engineering Block C",
                    dayOfWeek = 3,
                    periodIndex = 2,
                    durationSlots = 1,
                    timeDisplay = "09:30 - 10:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P4 & P5 (11:30 - 13:10): Subgroup Enterprise Web Application Lab (G1..G10)
            for (subGroupNum in 1..10) {
                val gId = "$bId-G$subGroupNum"
                val labRoom = "Web Studio ${(subGroupNum % 3) + 1}"
                val assignedTeacher = teacherPool[(subGroupNum + 2) % 8]

                sessions.add(
                    ClassSession(
                        id = (batchNum * 1000) + (subGroupNum * 10) + 11L,
                        courseId = 27,
                        courseCode = "UCS553",
                        courseName = "Enterprise Web Lab",
                        semesterNumber = 5,
                        batchId = bId,
                        subgroupId = gId,
                        isCommonBatchLecture = false,
                        instructorId = assignedTeacher.teacherId,
                        instructorName = assignedTeacher.teacherName,
                        roomName = labRoom,
                        building = "IT Wing, Ground Fl",
                        dayOfWeek = 3,
                        periodIndex = 4,
                        durationSlots = 2,
                        timeDisplay = "11:30 - 13:10 (2 Slots)",
                        sessionType = SessionType.LAB,
                        status = SessionStatus.PUBLISHED
                    )
                )
            }

            // THURSDAY
            // P2: Software Engineering (UCS503)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 12L,
                    courseId = 29,
                    courseCode = "UCS503",
                    courseName = "Software Engineering",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T5",
                    instructorName = "Prof. Amit Gupta",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 4,
                    periodIndex = 2,
                    durationSlots = 1,
                    timeDisplay = "09:30 - 10:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P4 (11:30 - 12:20): CAO Lecture (Cancelled on B1 to demonstrate Cross-Cancellation!)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 13L,
                    courseId = 30,
                    courseCode = "UCS510",
                    courseName = "Computer Architecture",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T7",
                    instructorName = "Prof. S. Joseph",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 4,
                    periodIndex = 4,
                    durationSlots = 1,
                    timeDisplay = "11:30 - 12:20",
                    sessionType = SessionType.LECTURE,
                    status = if (batchNum == 1) SessionStatus.CANCELLED else SessionStatus.PUBLISHED,
                    cancellationReason = if (batchNum == 1) "Prof. S. Joseph on Research Committee Duty. Slot liberated into Free-Slot Marketplace!" else null
                )
            )

            // FRIDAY
            // P1: Machine Learning (UML501)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 14L,
                    courseId = 26,
                    courseCode = "UML501",
                    courseName = "Machine Learning",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T1",
                    instructorName = "Prof. R. K. Sharma",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 5,
                    periodIndex = 1,
                    durationSlots = 1,
                    timeDisplay = "08:30 - 09:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P2: Image Processing (UCS615)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 15L,
                    courseId = 28,
                    courseCode = "UCS615",
                    courseName = "Image Processing",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T6",
                    instructorName = "Dr. Pooja Nair",
                    roomName = "Room 204",
                    building = "Engineering Block A",
                    dayOfWeek = 5,
                    periodIndex = 2,
                    durationSlots = 1,
                    timeDisplay = "09:30 - 10:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )

            // P3: Enterprise Web Application (UCS553)
            sessions.add(
                ClassSession(
                    id = (batchNum * 100) + 16L,
                    courseId = 27,
                    courseCode = "UCS553",
                    courseName = "Enterprise Web Application",
                    semesterNumber = 5,
                    batchId = bId,
                    subgroupId = "$bId-ALL",
                    isCommonBatchLecture = true,
                    instructorId = "T3",
                    instructorName = "Prof. Vikram Malhotra",
                    roomName = roomBase,
                    building = "Engineering Block A",
                    dayOfWeek = 5,
                    periodIndex = 3,
                    durationSlots = 1,
                    timeDisplay = "10:30 - 11:20",
                    sessionType = SessionType.LECTURE,
                    status = SessionStatus.PUBLISHED
                )
            )
        }
        db.timetableDao().insertSessions(sessions)

        // 3. Self-Healing Makeup Opportunity (Matched via Cross-Cancellation Engine!)
        val makeups = listOf(
            MakeupOpportunity(
                id = 1,
                cancelledSessionId = 101, // Monday UML501 Batch 1 cancelled
                courseCode = "UML501",
                courseName = "Machine Learning",
                semesterNumber = 5,
                batchId = "B1",
                subgroupId = "B1-ALL",
                instructorName = "Prof. R. K. Sharma",
                targetDayOfWeek = 4, // Thursday
                targetDayName = "Thursday",
                targetTimeSlot = "11:30 - 12:20 (Period 4)",
                targetPeriodIndex = 4,
                targetDurationSlots = 1,
                targetRoom = "LH-101 (Smart Lecture Hall)",
                compatibilityScore = 98,
                teacherAvailable = true,
                studentsAvailable = true,
                roomSuitable = true,
                conflictReason = "Optimal Cross-Cancellation Match: UCS510 CAO was cancelled on Thursday P4, liberating all 10 B1 subgroups & LH-101. Zero hard violations.",
                isAccepted = false,
                isRejected = false,
                votesCount = 46,
                totalStudents = 50
            ),
            MakeupOpportunity(
                id = 2,
                cancelledSessionId = 101,
                courseCode = "UML501",
                courseName = "Machine Learning",
                semesterNumber = 5,
                batchId = "B1",
                subgroupId = "B1-ALL",
                instructorName = "Dr. Anita Rao",
                targetDayOfWeek = 5, // Friday
                targetDayName = "Friday",
                targetTimeSlot = "14:10 - 15:00 (Period 6)",
                targetPeriodIndex = 6,
                targetDurationSlots = 1,
                targetRoom = "Room 204",
                compatibilityScore = 89,
                teacherAvailable = true,
                studentsAvailable = true,
                roomSuitable = true,
                conflictReason = "Feasible alternative. Soft penalty: late Friday afternoon period.",
                isAccepted = false,
                isRejected = false,
                votesCount = 14,
                totalStudents = 50
            )
        )
        db.makeupDao().insertOpportunities(makeups)

        // 4. Initial Academic Tasks & Assignments
        val tasks = listOf(
            TaskItem(
                id = 1,
                title = "Loss Function Backpropagation Implementation",
                description = "Implement gradient descent from scratch for Multi-Layer Perceptron on CIFAR-10 with Cross-Entropy Loss.",
                courseId = 26,
                courseCode = "UML501",
                courseName = "Machine Learning",
                semesterNumber = 5,
                batchId = "B1",
                subgroupId = "B1-G1",
                dueDateMillis = now + (1 * oneDayMillis) + (6 * 3600 * 1000L),
                priority = Priority.URGENT,
                taskType = TaskType.ASSIGNMENT,
                isCompleted = false,
                estimatedMinutes = 60,
                pomodoroSessions = 1
            ),
            TaskItem(
                id = 2,
                title = "Spring Boot RESTful Microservices & JWT Security",
                description = "Build secure authentication filter with access & refresh tokens and integrate PostgreSQL with JPA repository.",
                courseId = 27,
                courseCode = "UCS553",
                courseName = "Enterprise Web Application",
                semesterNumber = 5,
                batchId = "B1",
                subgroupId = "B1-G1",
                dueDateMillis = now + (2 * oneDayMillis),
                priority = Priority.HIGH,
                taskType = TaskType.PROJECT,
                isCompleted = false,
                estimatedMinutes = 90,
                pomodoroSessions = 2
            ),
            TaskItem(
                id = 3,
                title = "Sobel & Canny Edge Detection Filters",
                description = "Perform spatial convolution matrix transformations in Python OpenCV and compare gradient thresholds.",
                courseId = 28,
                courseCode = "UCS615",
                courseName = "Image Processing",
                semesterNumber = 5,
                batchId = "B1",
                subgroupId = "B1-G1",
                dueDateMillis = now + (4 * oneDayMillis),
                priority = Priority.HIGH,
                taskType = TaskType.LAB_REPORT,
                isCompleted = false,
                estimatedMinutes = 120,
                pomodoroSessions = 0
            ),
            TaskItem(
                id = 4,
                title = "Agile Scrum Sprint Planning & UML Sequence Diagrams",
                description = "Draw architectural sequence diagrams for payment gateway orchestration and prepare user stories.",
                courseId = 29,
                courseCode = "UCS503",
                courseName = "Software Engineering",
                semesterNumber = 5,
                batchId = "B1",
                subgroupId = "B1-G1",
                dueDateMillis = now + (6 * oneDayMillis),
                priority = Priority.MEDIUM,
                taskType = TaskType.ASSIGNMENT,
                isCompleted = false,
                estimatedMinutes = 45,
                pomodoroSessions = 0
            ),
            TaskItem(
                id = 5,
                title = "Pipelining Hazard Resolution & Branch Prediction",
                description = "Analyze structural, data and control hazards in MIPS 5-stage instruction pipeline.",
                courseId = 30,
                courseCode = "UCS510",
                courseName = "Computer Architecture",
                semesterNumber = 5,
                batchId = "B1",
                subgroupId = "B1-G1",
                dueDateMillis = now + (8 * oneDayMillis),
                priority = Priority.LOW,
                taskType = TaskType.EXAM_PREP,
                isCompleted = false,
                estimatedMinutes = 50,
                pomodoroSessions = 0
            ),
            TaskItem(
                id = 6,
                title = "Linear Regression & Feature Scaling Exercises",
                description = "Review L1 Lasso and L2 Ridge regularization mathematical derivations.",
                courseId = 26,
                courseCode = "UML501",
                courseName = "Machine Learning",
                semesterNumber = 5,
                batchId = "B1",
                subgroupId = "B1-G1",
                dueDateMillis = now - (1 * oneDayMillis),
                priority = Priority.MEDIUM,
                taskType = TaskType.READING,
                isCompleted = true,
                completedAtMillis = now - (10 * 3600 * 1000L),
                estimatedMinutes = 35,
                pomodoroSessions = 2
            )
        )
        tasks.forEach { db.taskDao().insertTask(it) }

        // 5. Initial Notifications
        val notifications = listOf(
            AcademicNotification(
                id = 1,
                title = "Self-Healing: Cross-Cancellation Makeup! ⚡",
                message = "UML501 Machine Learning recovered on Thursday 11:30 - 12:20 (98% Compatibility Match in LH-101). Tap to vote or confirm.",
                category = NotificationCategory.MAKEUP_FOUND,
                timestampMillis = now - (15 * 60 * 1000L),
                isRead = false,
                actionText = "Review Slot"
            ),
            AcademicNotification(
                id = 2,
                title = "Class Disruption Alert",
                message = "Prof. Sharma's Monday 08:30 Machine Learning lecture cancelled. Self-Healing recovery algorithm initiated.",
                category = NotificationCategory.CANCELLATION,
                timestampMillis = now - (2 * 3600 * 1000L),
                isRead = false,
                actionText = "View Routine"
            ),
            AcademicNotification(
                id = 3,
                title = "Assignment Deadline Approaching",
                message = "UML501 Backpropagation Loss Function assignment due tomorrow at 11:59 PM.",
                category = NotificationCategory.TASK_DEADLINE,
                timestampMillis = now - (4 * 3600 * 1000L),
                isRead = true,
                actionText = "Start Focus"
            )
        )
        db.notificationDao().insertNotifications(notifications)

        // 6. Default User Accounts Stored in Database for Students, Teachers & Coordinators
        val initialUsers = listOf(
            UserAccount(
                userId = "student101",
                password = "pass123",
                role = UserRole.STUDENT,
                fullName = "Alex Mercer",
                email = "alex.mercer@univ.edu",
                department = "Computer Science & Engineering",
                semester = 5,
                batch = "B1",
                subgroup = "G1",
                phone = "+1 (555) 234-5678"
            ),
            UserAccount(
                userId = "rahul502",
                password = "pass123",
                role = UserRole.STUDENT,
                fullName = "Rahul Sharma",
                email = "rahul.sharma@univ.edu",
                department = "Computer Science & Engineering",
                semester = 5,
                batch = "B2",
                subgroup = "G2",
                phone = "+1 (555) 876-5432"
            ),
            UserAccount(
                userId = "teacher201",
                password = "pass123",
                role = UserRole.TEACHER,
                fullName = "Dr. Anita Rao",
                email = "anita.rao@univ.edu",
                department = "Computer Science & Engineering",
                designation = "Professor & ML Lab Head",
                phone = "+1 (555) 345-6789"
            ),
            UserAccount(
                userId = "coord301",
                password = "pass123",
                role = UserRole.COORDINATOR,
                fullName = "Prof. Rajesh Sharma",
                email = "rajesh.sharma@univ.edu",
                department = "Computer Science & Engineering",
                designation = "Chief Timetable Coordinator",
                phone = "+1 (555) 456-7890"
            )
        )
        db.userDao().insertUsers(initialUsers)
    }
}
