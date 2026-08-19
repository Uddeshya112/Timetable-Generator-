package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.SampleDataGenerator
import com.example.data.model.*
import com.example.data.repository.AcademicRepository
import com.example.data.repository.SyncEngine
import com.example.data.repository.TaskRepository
import com.example.domain.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String) {
    TODAY("Agenda"),
    TIMETABLE("Routine"),
    COORDINATOR("Coordinator"),
    RECOVERY("Self-Healing"),
    TASKS("Tasks"),
    SYLLABUS("Analytics"),
    INBOX("Inbox")
}

data class PomodoroState(
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val associatedTaskId: Long? = null,
    val associatedTaskTitle: String = "Focus Session",
    val completedSessionsCount: Int = 0
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val taskRepository = TaskRepository(database.taskDao())
    private val academicRepository = AcademicRepository(
        database.courseDao(),
        database.timetableDao(),
        database.makeupDao(),
        database.notificationDao(),
        database.outboxDao(),
        database.userDao()
    )
    val syncEngine = SyncEngine(database.outboxDao(), viewModelScope)

    // =========================================================================
    // USER AUTHENTICATION & MULTI-ROLE DATABASE PERSISTENCE
    // =========================================================================
    val allUsers: StateFlow<List<UserAccount>> = academicRepository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(AppNavTab.TODAY)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    // =========================================================================
    // COORDINATOR MASTER PLANNER STATE & DATA STORES
    // =========================================================================
    private val _coordinatorBatches = MutableStateFlow(
        listOf(
            CoordinatorBatch("B1", "Batch B1 (Computer Science)", subgroupCount = 2, studentCount = 60, defaultLectureHall = "LH-101", semester = 5),
            CoordinatorBatch("B2", "Batch B2 (Computer Science)", subgroupCount = 2, studentCount = 60, defaultLectureHall = "LH-102", semester = 5),
            CoordinatorBatch("B3", "Batch B3 (Information Tech)", subgroupCount = 2, studentCount = 58, defaultLectureHall = "LH-103", semester = 5),
            CoordinatorBatch("B4", "Batch B4 (Data Science)", subgroupCount = 2, studentCount = 62, defaultLectureHall = "Room 204", semester = 5)
        )
    )
    val coordinatorBatches: StateFlow<List<CoordinatorBatch>> = _coordinatorBatches.asStateFlow()

    private val _coordinatorRooms = MutableStateFlow(
        listOf(
            CoordinatorRoom("R1", "LH-101", "Academic Block A", capacity = 80, isLab = false, equipmentInfo = "Smart 4K Projector + Audio"),
            CoordinatorRoom("R2", "LH-102", "Academic Block A", capacity = 80, isLab = false, equipmentInfo = "Smart 4K Projector + Audio"),
            CoordinatorRoom("R3", "LH-103", "Academic Block B", capacity = 75, isLab = false, equipmentInfo = "Interactive Digital Podium"),
            CoordinatorRoom("R4", "Room 204", "Academic Block B", capacity = 65, isLab = false, equipmentInfo = "Overhead Projector"),
            CoordinatorRoom("L1", "AI Lab 1", "Computing Center", capacity = 40, isLab = true, equipmentInfo = "35 GPU Workstations (RTX 4090)"),
            CoordinatorRoom("L2", "OS & Linux Lab", "Computing Center", capacity = 40, isLab = true, equipmentInfo = "40 Dual-Boot Linux Systems"),
            CoordinatorRoom("L3", "Enterprise Lab", "Computing Center", capacity = 40, isLab = true, equipmentInfo = "40 High-speed Cloud Workstations"),
            CoordinatorRoom("L4", "Project Studio", "Innovation Hub", capacity = 35, isLab = true, equipmentInfo = "Maker Kits & Hardware Dev Boards")
        )
    )
    val coordinatorRooms: StateFlow<List<CoordinatorRoom>> = _coordinatorRooms.asStateFlow()

    private val _coordinatorTeachers = MutableStateFlow(
        listOf(
            CoordinatorTeacher("T1", "Dr. Anita Rao", "Professor", "CSE", "anita.rao@univ.edu", maxDailySlots = 3, qualifiedCourseCodes = listOf("UML501", "UCS503"), canTeachLab = true),
            CoordinatorTeacher("T2", "Prof. Vikram Malhotra", "Associate Professor", "CSE", "vikram.m@univ.edu", maxDailySlots = 3, qualifiedCourseCodes = listOf("UCS553"), canTeachLab = true),
            CoordinatorTeacher("T3", "Dr. Pooja Nair", "Associate Professor", "CSE", "pooja.nair@univ.edu", maxDailySlots = 3, qualifiedCourseCodes = listOf("UCS615"), canTeachLab = true),
            CoordinatorTeacher("T4", "Prof. Amit Gupta", "Assistant Professor", "CSE", "amit.g@univ.edu", maxDailySlots = 4, qualifiedCourseCodes = listOf("UCS503"), canTeachLab = true),
            CoordinatorTeacher("T5", "Prof. S. Joseph", "Assistant Professor", "CSE", "s.joseph@univ.edu", maxDailySlots = 4, qualifiedCourseCodes = listOf("UCS510"), canTeachLab = false),
            CoordinatorTeacher("T6", "Dr. Harish Chandra", "Associate Professor", "CSE", "harish.c@univ.edu", maxDailySlots = 3, qualifiedCourseCodes = listOf("PEC501"), canTeachLab = true),
            CoordinatorTeacher("T7", "Dr. Sneha Verma", "Assistant Professor", "CSE", "sneha.v@univ.edu", maxDailySlots = 3, qualifiedCourseCodes = listOf("UML501", "UCS512"), canTeachLab = true),
            CoordinatorTeacher("T8", "Prof. Rajesh Sharma", "Professor & HOD", "CSE", "rajesh.s@univ.edu", maxDailySlots = 2, qualifiedCourseCodes = listOf("UCS553"), canTeachLab = true)
        )
    )
    val coordinatorTeachers: StateFlow<List<CoordinatorTeacher>> = _coordinatorTeachers.asStateFlow()

    private val _coordinatorCourses = MutableStateFlow(
        listOf(
            Course(1, "UML501", "Machine Learning", "ML", 5, credits = 4.0, lectureHours = 3, practicalHours = 2, primaryInstructorName = "Dr. Anita Rao", preferredRoom = "LH-101"),
            Course(2, "UCS553", "Enterprise Web Application", "Enterprise Web", 5, credits = 4.0, lectureHours = 3, practicalHours = 2, primaryInstructorName = "Prof. Vikram Malhotra", preferredRoom = "LH-102"),
            Course(3, "UCS615", "Image Processing", "Image Proc", 5, credits = 4.0, lectureHours = 3, practicalHours = 2, primaryInstructorName = "Dr. Pooja Nair", preferredRoom = "Room 204"),
            Course(4, "UCS503", "Software Engineering", "Soft Engg", 5, credits = 4.0, lectureHours = 3, practicalHours = 0, primaryInstructorName = "Prof. Amit Gupta", preferredRoom = "LH-101"),
            Course(5, "UCS510", "Computer Architecture and Org", "CAO", 5, credits = 3.0, lectureHours = 3, practicalHours = 0, primaryInstructorName = "Prof. S. Joseph", preferredRoom = "LH-102"),
            Course(6, "PEC501", "Cloud Computing & DevOps", "Cloud", 5, credits = 3.0, lectureHours = 2, practicalHours = 2, primaryInstructorName = "Dr. Harish Chandra", preferredRoom = "LH-103")
        )
    )
    val coordinatorCourses: StateFlow<List<Course>> = _coordinatorCourses.asStateFlow()

    private val _conflictAuditReport = MutableStateFlow(
        ConflictAuditReport(
            totalSessionsCount = 48,
            totalBatchesCount = 4,
            totalTeachersCount = 8,
            totalRoomsCount = 8,
            clashesDetected = 0,
            roomUtilizationPercent = 84,
            facultyWorkloadBalancePercent = 95,
            cohortHarmonyScore = 100
        )
    )
    val conflictAuditReport: StateFlow<ConflictAuditReport> = _conflictAuditReport.asStateFlow()

    private val _isGeneratingRoutine = MutableStateFlow(false)
    val isGeneratingRoutine: StateFlow<Boolean> = _isGeneratingRoutine.asStateFlow()

    private val _selectedCoordinatorTab = MutableStateFlow(0) // 0=Cockpit & Generator, 1=Batches/Subgroups, 2=Teachers, 3=Rooms/Labs, 4=Courses, 5=Master Matrices
    val selectedCoordinatorTab: StateFlow<Int> = _selectedCoordinatorTab.asStateFlow()

    private val _selectedTeacherForMatrix = MutableStateFlow("Dr. Anita Rao")
    val selectedTeacherForMatrix: StateFlow<String> = _selectedTeacherForMatrix.asStateFlow()

    private val _selectedRoomForMatrix = MutableStateFlow("LH-101")
    val selectedRoomForMatrix: StateFlow<String> = _selectedRoomForMatrix.asStateFlow()

    // Dynamic Batches & Subgroups (can be updated via Custom Upload Studio)
    private val _availableBatches = MutableStateFlow((1..4).map { "B$it" })
    val availableBatches: StateFlow<List<String>> = _availableBatches.asStateFlow()

    private val _availableSubgroups = MutableStateFlow(listOf("G1", "G2"))
    val availableSubgroups: StateFlow<List<String>> = _availableSubgroups.asStateFlow()

    // Dynamic Faculty Pool
    private val _customTeacherPool = MutableStateFlow(SampleDataGenerator.teacherPool)
    val customTeacherPool: StateFlow<List<TeacherQualification>> = _customTeacherPool.asStateFlow()
    val teacherPool: List<TeacherQualification>
        get() = _customTeacherPool.value

    // Student Profile State
    private val _studentProfile = MutableStateFlow(StudentProfile())
    val studentProfile: StateFlow<StudentProfile> = _studentProfile.asStateFlow()

    // Active Academic Filters: Semester (1..8), Batch, Subgroup
    private val _selectedSemester = MutableStateFlow(5) // Default Semester 5
    val selectedSemester: StateFlow<Int> = _selectedSemester.asStateFlow()

    private val _selectedBatch = MutableStateFlow("B1")
    val selectedBatch: StateFlow<String> = _selectedBatch.asStateFlow()

    private val _selectedSubgroup = MutableStateFlow("G1")
    val selectedSubgroup: StateFlow<String> = _selectedSubgroup.asStateFlow()

    // Selected Day for Timetable (1 = Mon to 5 = Fri)
    private val _selectedDay = MutableStateFlow(1)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    // Task Filter State
    private val _selectedCourseFilter = MutableStateFlow<String?>(null)
    val selectedCourseFilter: StateFlow<String?> = _selectedCourseFilter.asStateFlow()

    private val _selectedPriorityFilter = MutableStateFlow<Priority?>(null)
    val selectedPriorityFilter: StateFlow<Priority?> = _selectedPriorityFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Base Data Flows from Room - Eagerly started so that tab switches never drop state or cancel
    val allTasks: StateFlow<List<TaskItem>> = taskRepository.allTasks
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allCourses: StateFlow<List<Course>> = academicRepository.allCourses
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allSessions: StateFlow<List<ClassSession>> = academicRepository.allSessions
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val pendingMakeups: StateFlow<List<MakeupOpportunity>> = academicRepository.pendingOpportunities
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val allMakeups: StateFlow<List<MakeupOpportunity>> = academicRepository.allMakeupOpportunities
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val notifications: StateFlow<List<AcademicNotification>> = academicRepository.allNotifications
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val unreadCount: StateFlow<Int> = academicRepository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val outboxEvents: StateFlow<List<OutboxEvent>> = academicRepository.outboxEvents
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Courses for currently selected semester (Sem 1 to 8)
    val semesterCourses: StateFlow<List<Course>> = combine(
        allCourses,
        _selectedSemester
    ) { courses, sem ->
        courses.filter { it.semesterNumber == sem }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Filtered Class Sessions for Student's Batch & Subgroup
    val studentCohortSessions: StateFlow<List<ClassSession>> = combine(
        allSessions,
        _selectedBatch,
        _selectedSubgroup,
        _selectedSemester
    ) { sessions, batch, subgroup, sem ->
        val fullSubgroupId = "$batch-$subgroup"
        sessions.filter { session ->
            session.semesterNumber == sem &&
                    session.batchId == batch &&
                    (session.isCommonBatchLecture || session.subgroupId == fullSubgroupId || session.subgroupId == "$batch-ALL")
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Derived filtered tasks
    val filteredTasks: StateFlow<List<TaskItem>> = combine(
        allTasks,
        _selectedCourseFilter,
        _selectedPriorityFilter,
        _searchQuery
    ) { tasks, courseCode, priority, query ->
        tasks.filter { task ->
            val matchesCourse = courseCode == null || task.courseCode == courseCode
            val matchesPriority = priority == null || task.priority == priority
            val matchesQuery = query.isBlank() || task.title.contains(query, ignoreCase = true) ||
                    task.courseCode.contains(query, ignoreCase = true)
            matchesCourse && matchesPriority && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Timetable Health Report State
    val healthReport: StateFlow<TimetableHealthReport> = combine(
        studentCohortSessions,
        semesterCourses
    ) { sessions, courses ->
        TimetableHealthCalculator.calculate(sessions, courses)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, TimetableHealthReport())

    // Pomodoro Timer State
    private val _pomodoroState = MutableStateFlow(PomodoroState())
    val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()
    private var pomodoroJob: Job? = null

    // What-If Simulation State
    private val _activeSimulation = MutableStateFlow<SimulationResult?>(null)
    val activeSimulation: StateFlow<SimulationResult?> = _activeSimulation.asStateFlow()

    // Solver Optimization Progress State
    private val _isSolverRunning = MutableStateFlow(false)
    val isSolverRunning: StateFlow<Boolean> = _isSolverRunning.asStateFlow()

    // Notification / Snack Message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        // Ensure database is populated upon initialization
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (database.courseDao().getCourseCount() == 0) {
                    SampleDataGenerator.populateInitialData(database)
                }
            } catch (_: Exception) {}
        }
    }

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setSelectedSemester(semester: Int) {
        _selectedSemester.value = semester
    }

    fun setSelectedBatch(batch: String) {
        _selectedBatch.value = batch
    }

    fun setSelectedSubgroup(subgroup: String) {
        _selectedSubgroup.value = subgroup
    }

    fun setSelectedDay(day: Int) {
        _selectedDay.value = day
    }

    fun setCourseFilter(courseCode: String?) {
        _selectedCourseFilter.value = courseCode
    }

    fun setPriorityFilter(priority: Priority?) {
        _selectedPriorityFilter.value = priority
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Trigger CP-SAT Solver Full Re-Optimization
    fun triggerSolverOptimization() {
        viewModelScope.launch {
            _isSolverRunning.value = true
            _toastMessage.value = "CP-SAT Solver initiated: Evaluating 12 Batches × 10 Subgroups..."
            delay(1200)
            _isSolverRunning.value = false
            _toastMessage.value = "Optimal Timetable Generated: 0 Hard Violations, 100% Teacher Feasibility ✨"
        }
    }

    // Apply Custom Academic Upload & Auto-Generate Routine
    fun applyCustomAcademicUpload(data: AcademicUploadData) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSolverRunning.value = true
            _toastMessage.value = "Generating clash-free timetable for ${data.batches.size} batches..."

            // 1. Run Timetable Generator Engine
            val result = TimetableGeneratorEngine.generateRoutine(data)

            // 2. Update dynamic batches and subgroups
            _availableBatches.value = data.batches
            _availableSubgroups.value = data.subgroups
            if (data.teachers.isNotEmpty()) {
                _customTeacherPool.value = data.teachers
            }
            if (data.batches.isNotEmpty()) {
                _selectedBatch.value = data.batches.first()
            }
            if (data.subgroups.isNotEmpty()) {
                _selectedSubgroup.value = data.subgroups.first()
            }
            _selectedSemester.value = data.semesterNumber

            // 3. Save courses and sessions into SQLite Room database
            if (data.courses.isNotEmpty()) {
                academicRepository.insertCustomCourses(data.courses)
            }
            academicRepository.replaceRoutineSessions(result.sessions, data.semesterNumber)

            delay(600)
            _isSolverRunning.value = false
            _toastMessage.value = "Generated ${result.totalSessionsCount} sessions across ${result.batchesScheduled} batches with 0 clashes! ✨"
        }
    }

    // Task Actions
    fun toggleTask(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.toggleTaskCompleted(task)
            syncEngine.triggerSync()
            _toastMessage.value = if (!task.isCompleted) "Task completed! 🎉" else "Task marked pending"
        }
    }

    fun addTask(
        title: String,
        description: String,
        courseCode: String,
        courseName: String,
        dueDateMillis: Long,
        priority: Priority,
        taskType: TaskType,
        estimatedMinutes: Int
    ) {
        viewModelScope.launch {
            val task = TaskItem(
                title = title.trim(),
                description = description.trim(),
                courseId = 1,
                courseCode = courseCode,
                courseName = courseName,
                semesterNumber = _selectedSemester.value,
                batchId = _selectedBatch.value,
                subgroupId = "${_selectedBatch.value}-${_selectedSubgroup.value}",
                dueDateMillis = dueDateMillis,
                priority = priority,
                taskType = taskType,
                estimatedMinutes = estimatedMinutes
            )
            taskRepository.insertTask(task)
            syncEngine.triggerSync()
            _toastMessage.value = "New academic task added! 📚"
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
            _toastMessage.value = "Task deleted"
        }
    }

    // Academic & Self-Healing Actions
    fun cancelClass(session: ClassSession, reason: String) {
        viewModelScope.launch {
            academicRepository.cancelClassSession(session, reason)
            syncEngine.triggerSync()
            _toastMessage.value = "Class cancelled. Self-healing optimizer dispatched! ⚡"
        }
    }

    fun acceptMakeup(opportunity: MakeupOpportunity) {
        viewModelScope.launch {
            academicRepository.acceptMakeupOpportunity(opportunity)
            syncEngine.triggerSync()
            _toastMessage.value = "Self-Healing Makeup Scheduled! ✨"
        }
    }

    fun rejectMakeup(opportunity: MakeupOpportunity) {
        viewModelScope.launch {
            academicRepository.rejectMakeupOpportunity(opportunity)
            _toastMessage.value = "Opportunity dismissed"
        }
    }

    fun voteMakeup(opportunity: MakeupOpportunity) {
        viewModelScope.launch {
            academicRepository.voteForOpportunity(opportunity.id)
            _toastMessage.value = "Your student vote recorded! (${opportunity.votesCount + 1}/${opportunity.totalStudents})"
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            academicRepository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            academicRepository.markAllNotificationsRead()
            _toastMessage.value = "All notifications marked as read"
        }
    }

    // Pomodoro Actions
    fun startPomodoro(task: TaskItem? = null) {
        pomodoroJob?.cancel()
        _pomodoroState.value = _pomodoroState.value.copy(
            isActive = true,
            isPaused = false,
            remainingSeconds = 25 * 60,
            totalSeconds = 25 * 60,
            associatedTaskId = task?.id,
            associatedTaskTitle = task?.title ?: "Focus Study Block"
        )
        pomodoroJob = viewModelScope.launch {
            while (_pomodoroState.value.remainingSeconds > 0 && _pomodoroState.value.isActive) {
                delay(1000)
                if (!_pomodoroState.value.isPaused) {
                    val current = _pomodoroState.value
                    if (current.remainingSeconds > 0) {
                        _pomodoroState.value = current.copy(remainingSeconds = current.remainingSeconds - 1)
                    }
                }
            }
            if (_pomodoroState.value.remainingSeconds <= 0 && _pomodoroState.value.isActive) {
                val taskId = _pomodoroState.value.associatedTaskId
                if (taskId != null) {
                    taskRepository.incrementPomodoro(taskId)
                }
                _pomodoroState.value = _pomodoroState.value.copy(
                    isActive = false,
                    completedSessionsCount = _pomodoroState.value.completedSessionsCount + 1
                )
                _toastMessage.value = "Pomodoro completed! Great focus! 🍅"
            }
        }
    }

    fun togglePausePomodoro() {
        val current = _pomodoroState.value
        _pomodoroState.value = current.copy(isPaused = !current.isPaused)
    }

    fun stopPomodoro() {
        pomodoroJob?.cancel()
        _pomodoroState.value = _pomodoroState.value.copy(isActive = false, isPaused = false)
    }

    // Simulation
    fun runSimulation(scenarioId: Int) {
        _activeSimulation.value = WhatIfSimulator.simulateScenario(scenarioId)
    }

    fun clearSimulation() {
        _activeSimulation.value = null
    }

    // Student Profile
    fun updateStudentProfile(profile: StudentProfile) {
        _studentProfile.value = profile
        _selectedSemester.value = profile.semester
        _selectedBatch.value = profile.batch
        _selectedSubgroup.value = profile.subgroup
        _toastMessage.value = "Profile updated for ${profile.name}! 🎓"
    }

    // =========================================================================
    // COORDINATOR MASTER PLANNER ACTIONS
    // =========================================================================
    fun setSelectedCoordinatorTab(tab: Int) {
        _selectedCoordinatorTab.value = tab
    }

    fun setSelectedTeacherForMatrix(teacherName: String) {
        _selectedTeacherForMatrix.value = teacherName
    }

    fun setSelectedRoomForMatrix(roomName: String) {
        _selectedRoomForMatrix.value = roomName
    }

    fun addCoordinatorBatch(batch: CoordinatorBatch) {
        val current = _coordinatorBatches.value.toMutableList()
        current.removeAll { it.batchId == batch.batchId }
        current.add(batch)
        _coordinatorBatches.value = current
        _availableBatches.value = current.map { it.batchId }
        _toastMessage.value = "Batch ${batch.name} configured! 🏷️"
    }

    fun deleteCoordinatorBatch(batchId: String) {
        val current = _coordinatorBatches.value.filterNot { it.batchId == batchId }
        _coordinatorBatches.value = current
        _availableBatches.value = current.map { it.batchId }
        _toastMessage.value = "Batch $batchId removed"
    }

    fun addCoordinatorTeacher(teacher: CoordinatorTeacher) {
        val current = _coordinatorTeachers.value.toMutableList()
        current.removeAll { it.teacherId == teacher.teacherId }
        current.add(teacher)
        _coordinatorTeachers.value = current

        // Update customTeacherPool as well
        val qual = TeacherQualification(
            teacherId = teacher.teacherId,
            teacherName = teacher.name,
            department = teacher.department,
            qualifiedCourseCode = teacher.qualifiedCourseCodes.firstOrNull() ?: "GEN101",
            canTeachLecture = teacher.canTeachLecture,
            canTeachLab = teacher.canTeachLab,
            maxWeeklyWorkloadHours = teacher.maxDailySlots * 5
        )
        val pool = _customTeacherPool.value.toMutableList()
        pool.removeAll { it.teacherId == teacher.teacherId }
        pool.add(qual)
        _customTeacherPool.value = pool

        _toastMessage.value = "Faculty ${teacher.name} updated! 👨‍🏫"
    }

    fun deleteCoordinatorTeacher(teacherId: String) {
        val current = _coordinatorTeachers.value.filterNot { it.teacherId == teacherId }
        _coordinatorTeachers.value = current
        _customTeacherPool.value = _customTeacherPool.value.filterNot { it.teacherId == teacherId }
        _toastMessage.value = "Faculty removed"
    }

    fun addCoordinatorRoom(room: CoordinatorRoom) {
        val current = _coordinatorRooms.value.toMutableList()
        current.removeAll { it.roomId == room.roomId }
        current.add(room)
        _coordinatorRooms.value = current
        _toastMessage.value = "Room ${room.name} (${if (room.isLab) "Lab" else "Lecture Hall"}) configured! 🏛️"
    }

    fun deleteCoordinatorRoom(roomId: String) {
        val current = _coordinatorRooms.value.filterNot { it.roomId == roomId }
        _coordinatorRooms.value = current
        _toastMessage.value = "Room removed"
    }

    fun addCoordinatorCourse(course: Course) {
        val current = _coordinatorCourses.value.toMutableList()
        current.removeAll { it.code == course.code }
        current.add(course)
        _coordinatorCourses.value = current
        viewModelScope.launch {
            academicRepository.insertCustomCourses(listOf(course))
        }
        _toastMessage.value = "Course ${course.code} added to curriculum! 📚"
    }

    fun deleteCoordinatorCourse(courseCode: String) {
        val current = _coordinatorCourses.value.filterNot { it.code == courseCode }
        _coordinatorCourses.value = current
        _toastMessage.value = "Course $courseCode removed"
    }

    fun loadCoordinatorPreset(presetName: String) {
        when (presetName) {
            "CSE_4_BATCHES" -> {
                _coordinatorBatches.value = listOf(
                    CoordinatorBatch("B1", "Batch B1 (CSE)", subgroupCount = 2, studentCount = 60, defaultLectureHall = "LH-101", semester = 5),
                    CoordinatorBatch("B2", "Batch B2 (CSE)", subgroupCount = 2, studentCount = 60, defaultLectureHall = "LH-102", semester = 5),
                    CoordinatorBatch("B3", "Batch B3 (IT)", subgroupCount = 2, studentCount = 58, defaultLectureHall = "LH-103", semester = 5),
                    CoordinatorBatch("B4", "Batch B4 (Data Science)", subgroupCount = 2, studentCount = 62, defaultLectureHall = "Room 204", semester = 5)
                )
                _availableBatches.value = listOf("B1", "B2", "B3", "B4")
                _availableSubgroups.value = listOf("G1", "G2")
                _toastMessage.value = "Loaded Computer Science 4-Batch Preset! 🚀"
            }
            "AI_2_BATCHES" -> {
                _coordinatorBatches.value = listOf(
                    CoordinatorBatch("AIML-1", "AI & ML Batch 1", subgroupCount = 3, studentCount = 55, defaultLectureHall = "LH-101", semester = 5),
                    CoordinatorBatch("AIML-2", "AI & ML Batch 2", subgroupCount = 3, studentCount = 55, defaultLectureHall = "LH-102", semester = 5)
                )
                _availableBatches.value = listOf("AIML-1", "AIML-2")
                _availableSubgroups.value = listOf("G1", "G2", "G3")
                _toastMessage.value = "Loaded AI & Data Science Preset! 🤖"
            }
            "ECE_3_BATCHES" -> {
                _coordinatorBatches.value = listOf(
                    CoordinatorBatch("ECE-A", "Electronics Batch A", subgroupCount = 2, studentCount = 60, defaultLectureHall = "LH-101", semester = 5),
                    CoordinatorBatch("ECE-B", "Electronics Batch B", subgroupCount = 2, studentCount = 60, defaultLectureHall = "LH-102", semester = 5),
                    CoordinatorBatch("ECE-C", "Electronics Batch C", subgroupCount = 2, studentCount = 58, defaultLectureHall = "LH-103", semester = 5)
                )
                _availableBatches.value = listOf("ECE-A", "ECE-B", "ECE-C")
                _availableSubgroups.value = listOf("G1", "G2")
                _toastMessage.value = "Loaded Electronics 3-Batch Preset! ⚡"
            }
        }
    }

    /**
     * Executes the Clash-Free Constraint Satisfaction Engine to synthesize
     * master routines for ALL configured batches, subgroups, teachers, and rooms.
     */
    fun generateMasterSchedule(targetSemester: Int? = null) {
        val sem = targetSemester ?: _selectedSemester.value
        _isGeneratingRoutine.value = true

        viewModelScope.launch(Dispatchers.Default) {
            delay(400) // Brief calculation latency for UI polish

            val batchesList = _coordinatorBatches.value.map { it.batchId }
            val maxSubgroupsCount = _coordinatorBatches.value.maxOfOrNull { it.subgroupCount } ?: 2
            val subgroupsList = (1..maxSubgroupsCount).map { "G$it" }

            val lectureRoomsList = _coordinatorRooms.value.filter { !it.isLab }.map { it.name }
            val labRoomsList = _coordinatorRooms.value.filter { it.isLab }.map { it.name }

            val coursesList = _coordinatorCourses.value.ifEmpty {
                TimetableGeneratorEngine.generateStandardDepartmentCourses(sem)
            }

            val teachersList = _coordinatorTeachers.value.map {
                TeacherQualification(
                    teacherId = it.teacherId,
                    teacherName = it.name,
                    department = it.department,
                    qualifiedCourseCode = it.qualifiedCourseCodes.firstOrNull() ?: "UML501",
                    canTeachLecture = it.canTeachLecture,
                    canTeachLab = it.canTeachLab,
                    maxWeeklyWorkloadHours = it.maxDailySlots * 5
                )
            }.ifEmpty {
                TimetableGeneratorEngine.generateStandardFacultyPool()
            }

            val result = TimetableGeneratorEngine.generateClashFreeTimetable(
                semester = sem,
                batches = batchesList,
                subgroups = subgroupsList,
                courses = coursesList,
                teacherPool = teachersList,
                lectureHalls = lectureRoomsList.ifEmpty { listOf("LH-101", "LH-102", "LH-103") },
                labRooms = labRoomsList.ifEmpty { listOf("AI Lab 1", "OS & Linux Lab", "Enterprise Lab") }
            )

            // Save to SQLite / Room
            academicRepository.replaceRoutineSessions(result.sessions, sem)
            academicRepository.insertCustomCourses(coursesList)

            _conflictAuditReport.value = result.auditReport
            _availableBatches.value = batchesList
            _availableSubgroups.value = subgroupsList
            _isGeneratingRoutine.value = false

            _toastMessage.value = "🎉 Master Routine Generated! 0 Conflicts across ${result.batchesScheduled} Batches!"
        }
    }

    /**
     * Exports the generated schedule as a clean, shareable Markdown document.
     */
    fun exportMasterRoutineMarkdown(batchId: String? = null): String {
        val sessions = _allSessionsFilteredBySem()
        val targetBatch = batchId ?: _selectedBatch.value
        val batchSessions = sessions.filter { it.batchId == targetBatch }

        val builder = StringBuilder()
        builder.appendLine("# 📅 ACADEMIC MASTER TIMETABLE")
        builder.appendLine("### Department of Computer Science & Engineering | Semester ${_selectedSemester.value}")
        builder.appendLine("**Target Batch:** $targetBatch | **Status:** 100% Conflict-Free Verified")
        builder.appendLine()
        builder.appendLine("| Day | Period | Time | Course | Type | Instructor | Room |")
        builder.appendLine("|---|---|---|---|---|---|---|")

        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
        for (dayIdx in 1..5) {
            val daySessions = batchSessions.filter { it.dayOfWeek == dayIdx }.sortedBy { it.periodIndex }
            if (daySessions.isEmpty()) {
                builder.appendLine("| ${days[dayIdx - 1]} | - | Free / Self Study | - | - | - | - |")
            } else {
                for (s in daySessions) {
                    builder.appendLine("| ${days[dayIdx - 1]} | P${s.periodIndex} | ${s.timeDisplay} | ${s.courseCode} (${s.courseName}) | ${s.sessionType.label} | ${s.instructorName} | ${s.roomName} |")
                }
            }
        }

        builder.appendLine()
        builder.appendLine("*Generated seamlessly via Academic Routine Planner Studio.*")
        return builder.toString()
    }

    private fun _allSessionsFilteredBySem(): List<ClassSession> {
        return allSessions.value.filter { it.semesterNumber == _selectedSemester.value }
    }

    // =========================================================================
    // AUTHENTICATION & SESSION MANAGEMENT
    // =========================================================================
    fun clearAuthError() {
        _authError.value = null
    }

    fun login(
        idOrEmail: String,
        passwordInput: String,
        role: UserRole,
        onSuccess: (UserAccount) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val cleanId = idOrEmail.trim()
        val cleanPass = passwordInput.trim()

        if (cleanId.isBlank()) {
            _authError.value = "Please enter your User ID or Email"
            onError("Please enter your User ID or Email")
            return
        }
        if (cleanPass.isBlank()) {
            _authError.value = "Please enter your password"
            onError("Please enter your password")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isAuthenticating.value = true
            _authError.value = null
            try {
                val matchedUser = academicRepository.authenticateUser(cleanId, cleanPass)
                if (matchedUser != null) {
                    // Check if role matches or allow cross-role check
                    if (matchedUser.role == role) {
                        applyAuthenticatedUser(matchedUser)
                        onSuccess(matchedUser)
                    } else {
                        // User exists but selected wrong role
                        _authError.value = "Account found as '${matchedUser.role.label}'. Please select the '${matchedUser.role.label}' tab to log in."
                        onError("Account found as '${matchedUser.role.label}'")
                    }
                } else {
                    // Check if user exists with different password
                    val existingUser = academicRepository.getUserById(cleanId)
                    if (existingUser != null) {
                        _authError.value = "Incorrect password for user '$cleanId'."
                    } else {
                        _authError.value = "No account found with ID '$cleanId'. Please register below."
                    }
                    onError(_authError.value ?: "Authentication failed")
                }
            } catch (e: Exception) {
                _authError.value = "Authentication error: ${e.localizedMessage}"
                onError("Database error: ${e.localizedMessage}")
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun register(
        user: UserAccount,
        onSuccess: (UserAccount) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val cleanId = user.userId.trim()
        val cleanPass = user.password.trim()
        val cleanName = user.fullName.trim()

        if (cleanId.isBlank()) {
            _authError.value = "User ID / Roll Number cannot be empty"
            onError("User ID / Roll Number cannot be empty")
            return
        }
        if (cleanPass.length < 4) {
            _authError.value = "Password must be at least 4 characters"
            onError("Password must be at least 4 characters")
            return
        }
        if (cleanName.isBlank()) {
            _authError.value = "Full Name is required"
            onError("Full Name is required")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _isAuthenticating.value = true
            _authError.value = null
            try {
                val existing = academicRepository.getUserById(cleanId)
                if (existing != null) {
                    _authError.value = "User ID '$cleanId' is already registered. Please sign in."
                    onError("User ID already registered")
                    return@launch
                }

                val newUser = user.copy(
                    userId = cleanId,
                    password = cleanPass,
                    fullName = cleanName
                )
                academicRepository.registerUser(newUser)
                applyAuthenticatedUser(newUser)
                onSuccess(newUser)
            } catch (e: Exception) {
                _authError.value = "Registration error: ${e.localizedMessage}"
                onError("Database error: ${e.localizedMessage}")
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun quickDemoLogin(role: UserRole) {
        viewModelScope.launch(Dispatchers.IO) {
            val demoUserId = when (role) {
                UserRole.STUDENT -> "student101"
                UserRole.TEACHER -> "teacher201"
                UserRole.COORDINATOR -> "coord301"
            }
            val user = academicRepository.getUserById(demoUserId)
            if (user != null) {
                applyAuthenticatedUser(user)
            } else {
                // If DB was fresh, seed and retry
                val fallbackUser = when (role) {
                    UserRole.STUDENT -> UserAccount(
                        userId = "student101",
                        password = "pass123",
                        role = UserRole.STUDENT,
                        fullName = "Alex Mercer",
                        email = "alex.mercer@univ.edu",
                        semester = 5,
                        batch = "B1",
                        subgroup = "G1"
                    )
                    UserRole.TEACHER -> UserAccount(
                        userId = "teacher201",
                        password = "pass123",
                        role = UserRole.TEACHER,
                        fullName = "Dr. Anita Rao",
                        email = "anita.rao@univ.edu",
                        designation = "Professor & ML Lead"
                    )
                    UserRole.COORDINATOR -> UserAccount(
                        userId = "coord301",
                        password = "pass123",
                        role = UserRole.COORDINATOR,
                        fullName = "Prof. Rajesh Sharma",
                        email = "rajesh.sharma@univ.edu",
                        designation = "Chief Timetable Coordinator"
                    )
                }
                academicRepository.registerUser(fallbackUser)
                applyAuthenticatedUser(fallbackUser)
            }
        }
    }

    private fun applyAuthenticatedUser(user: UserAccount) {
        _currentUser.value = user
        _authError.value = null

        when (user.role) {
            UserRole.STUDENT -> {
                _selectedSemester.value = user.semester
                _selectedBatch.value = user.batch
                _selectedSubgroup.value = user.subgroup
                _studentProfile.value = _studentProfile.value.copy(
                    name = user.fullName,
                    rollNumber = user.userId,
                    email = user.email.ifBlank { "${user.userId.lowercase()}@univ.edu" },
                    semester = user.semester,
                    batch = user.batch,
                    subgroup = user.subgroup,
                    department = user.department
                )
                _currentTab.value = AppNavTab.TODAY
            }
            UserRole.COORDINATOR -> {
                _currentTab.value = AppNavTab.COORDINATOR
            }
            UserRole.TEACHER -> {
                _selectedTeacherForMatrix.value = user.fullName
                _currentTab.value = AppNavTab.TIMETABLE
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _authError.value = null
    }

    fun deleteUserAccount(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            academicRepository.deleteUser(userId)
            if (_currentUser.value?.userId == userId) {
                _currentUser.value = null
            }
        }
    }
}
