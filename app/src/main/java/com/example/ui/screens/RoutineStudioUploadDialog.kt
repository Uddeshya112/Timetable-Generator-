package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Course
import com.example.data.model.CourseCategory
import com.example.data.model.TeacherQualification
import com.example.domain.AcademicDataParser
import com.example.domain.AcademicUploadData
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

enum class SchemeSection(val label: String, val iconName: String) {
    COURSES("Subjects & Scheme", "MenuBook"),
    TEACHERS("Teachers (6/Subject)", "School"),
    BATCHES("Batches & Groups", "People"),
    ROOMS("Halls & Labs", "MeetingRoom"),
    MASTER_CSV("Master Excel / CSV", "Description")
}

@Composable
fun RoutineStudioUploadDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var selectedSemester by remember { mutableStateOf(1) }
    var currentSection by remember { mutableStateOf(SchemeSection.COURSES) }
    var copyNotice by remember { mutableStateOf(false) }

    // Active working state for coordinator
    var activeData by remember(selectedSemester) {
        val initialCurriculum = AcademicDataParser.getFullCurriculumForSemester(selectedSemester)
        mutableStateOf(
            AcademicUploadData(
                semesterNumber = selectedSemester,
                batches = AcademicDataParser.standard12Batches,
                subgroups = AcademicDataParser.standard4Subgroups,
                teachers = initialCurriculum.second,
                courses = initialCurriculum.first
            )
        )
    }

    // Master CSV Text buffer
    var masterCsvText by remember(activeData) {
        mutableStateOf(AcademicDataParser.buildCsvSchemeExport(activeData))
    }

    // Dialog states for CRUD
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var subjectToEdit by remember { mutableStateOf<Course?>(null) }

    var showAddTeacherDialog by remember { mutableStateOf(false) }
    var teacherToEdit by remember { mutableStateOf<TeacherQualification?>(null) }

    var showAddBatchDialog by remember { mutableStateOf(false) }
    var showAddRoomDialog by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .background(MidnightBackground)
                .border(1.dp, SurfaceBorderStrong, RoundedCornerShape(24.dp)),
            color = MidnightBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(16.dp)
            ) {
                // 1. Top Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CyanContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Coordinator Routine Studio",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "Full Control: Add Subjects, Assign Faculty & Batches",
                                color = TextMuted,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Academic Semester Selector (Sem 1 to 8)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MANAGING SEMESTER:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val yearLabel = when (selectedSemester) {
                        1, 2 -> "1st Year (B.Tech • Sem 1 & 2)"
                        3, 4 -> "2nd Year (B.Tech • Sem 3 & 4)"
                        5, 6 -> "3rd Year (B.Tech • Sem 5 & 6)"
                        else -> "4th Year (B.Tech • Sem 7 & 8)"
                    }
                    Text(
                        text = yearLabel,
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items((1..8).toList()) { sem ->
                        val isSel = selectedSemester == sem
                        val yr = when (sem) {
                            1, 2 -> "Yr 1"
                            3, 4 -> "Yr 2"
                            5, 6 -> "Yr 3"
                            else -> "Yr 4"
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) ElectricCyan else SurfaceDark,
                            border = BorderStroke(1.dp, if (isSel) ElectricCyan else SurfaceBorder),
                            modifier = Modifier
                                .clickable {
                                    selectedSemester = sem
                                    val newCurr = AcademicDataParser.getFullCurriculumForSemester(sem)
                                    activeData = activeData.copy(
                                        semesterNumber = sem,
                                        courses = newCurr.first,
                                        teachers = newCurr.second
                                    )
                                    masterCsvText = AcademicDataParser.buildCsvSchemeExport(activeData)
                                }
                                .testTag("coordinator_sem_btn_$sem")
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Sem $sem",
                                    color = if (isSel) DeepNavyOnPrimary else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = yr,
                                    color = if (isSel) DeepNavyOnPrimary.copy(alpha = 0.8f) else TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = currentSection.ordinal,
                    containerColor = SurfaceDark,
                    contentColor = ElectricCyan,
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                ) {
                    SchemeSection.values().forEach { section ->
                        val isSelected = currentSection == section
                        Tab(
                            selected = isSelected,
                            onClick = {
                                if (section == SchemeSection.MASTER_CSV) {
                                    masterCsvText = AcademicDataParser.buildCsvSchemeExport(activeData)
                                }
                                currentSection = section
                            },
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    val icon = when (section) {
                                        SchemeSection.COURSES -> Icons.Default.MenuBook
                                        SchemeSection.TEACHERS -> Icons.Default.School
                                        SchemeSection.BATCHES -> Icons.Default.People
                                        SchemeSection.ROOMS -> Icons.Default.MeetingRoom
                                        SchemeSection.MASTER_CSV -> Icons.Default.Description
                                    }
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = if (isSelected) ElectricCyan else TextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = section.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) ElectricCyan else TextSecondary,
                                        maxLines = 1
                                    )
                                }
                            },
                            modifier = Modifier
                                .background(if (isSelected) CyanContainer.copy(alpha = 0.35f) else Color.Transparent)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4. Main Section Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (currentSection) {
                        SchemeSection.COURSES -> {
                            CoordinatorCoursesSection(
                                courses = activeData.courses,
                                semester = selectedSemester,
                                onUpdateCourses = { activeData = activeData.copy(courses = it) },
                                onAddCourseClick = { showAddSubjectDialog = true },
                                onEditCourseClick = { subjectToEdit = it },
                                onAutoPopulateTeachersForSubject = { course ->
                                    val newTeachers = generate6TeachersForCourse(course, activeData.teachers.size)
                                    activeData = activeData.copy(
                                        teachers = activeData.teachers.filter { it.qualifiedCourseCode != course.code } + newTeachers
                                    )
                                }
                            )
                        }
                        SchemeSection.TEACHERS -> {
                            CoordinatorTeachersSection(
                                teachers = activeData.teachers,
                                courses = activeData.courses,
                                onUpdateTeachers = { activeData = activeData.copy(teachers = it) },
                                onAddTeacherClick = { showAddTeacherDialog = true },
                                onEditTeacherClick = { teacherToEdit = it },
                                onAutoFillAllMissingStaff = {
                                    val updatedTeachers = activeData.teachers.toMutableList()
                                    activeData.courses.forEach { course ->
                                        val existing = updatedTeachers.count { it.qualifiedCourseCode == course.code }
                                        if (existing < 6) {
                                            val toAdd = generate6TeachersForCourse(course, updatedTeachers.size).take(6 - existing)
                                            updatedTeachers.addAll(toAdd)
                                        }
                                    }
                                    activeData = activeData.copy(teachers = updatedTeachers)
                                }
                            )
                        }
                        SchemeSection.BATCHES -> {
                            CoordinatorBatchesSection(
                                data = activeData,
                                onUpdateBatches = { activeData = activeData.copy(batches = it) },
                                onUpdateSubgroups = { activeData = activeData.copy(subgroups = it) },
                                onAddBatchClick = { showAddBatchDialog = true }
                            )
                        }
                        SchemeSection.ROOMS -> {
                            CoordinatorRoomsSection(
                                lectureHalls = activeData.lectureHalls,
                                labs = activeData.labs,
                                onUpdateHalls = { activeData = activeData.copy(lectureHalls = it) },
                                onUpdateLabs = { activeData = activeData.copy(labs = it) },
                                onAddRoomClick = { showAddRoomDialog = true }
                            )
                        }
                        SchemeSection.MASTER_CSV -> {
                            MasterCsvSection(
                                csvText = masterCsvText,
                                onCsvTextChange = { newText ->
                                    masterCsvText = newText
                                    try {
                                        activeData = AcademicDataParser.parseCsvText(newText, selectedSemester)
                                    } catch (_: Exception) {}
                                },
                                semester = selectedSemester,
                                onResetDefault = {
                                    val defaultCurr = AcademicDataParser.getFullCurriculumForSemester(selectedSemester)
                                    activeData = AcademicUploadData(
                                        semesterNumber = selectedSemester,
                                        batches = AcademicDataParser.standard12Batches,
                                        subgroups = AcademicDataParser.standard4Subgroups,
                                        teachers = defaultCurr.second,
                                        courses = defaultCurr.first
                                    )
                                    masterCsvText = AcademicDataParser.buildCsvSchemeExport(activeData)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Live Summary Footer & Final Generation Button
                GlassCard(backgroundColor = SurfaceDark, borderColor = ElectricCyan.copy(alpha = 0.5f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(NeonMint)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ready to Auto-Solve Sem $selectedSemester",
                                    color = NeonMint,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${activeData.courses.size} Subjects • ${activeData.teachers.size} Faculty • ${activeData.batches.size} Batches • ${activeData.subgroups.size} Groups",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = {
                                    val exported = AcademicDataParser.buildCsvSchemeExport(activeData)
                                    clipboardManager.setText(AnnotatedString(exported))
                                    copyNotice = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, SurfaceBorderStrong),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = if (copyNotice) NeonMint else TextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (copyNotice) "Copied" else "Copy CSV",
                                    color = if (copyNotice) NeonMint else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.applyCustomAcademicUpload(activeData)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricCyan,
                                    contentColor = DeepNavyOnPrimary
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("coordinator_generate_routine_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Generate Routine ✨",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // CRUD Dialogs
    if (showAddSubjectDialog) {
        CourseFormDialog(
            semester = selectedSemester,
            initialCourse = null,
            onDismiss = { showAddSubjectDialog = false },
            onSave = { newCourse ->
                activeData = activeData.copy(courses = activeData.courses + newCourse)
                // Automatically ensure 6 teachers exist for this course if none
                if (activeData.teachers.none { it.qualifiedCourseCode == newCourse.code }) {
                    val genTeachers = generate6TeachersForCourse(newCourse, activeData.teachers.size)
                    activeData = activeData.copy(teachers = activeData.teachers + genTeachers)
                }
                showAddSubjectDialog = false
            }
        )
    }

    subjectToEdit?.let { course ->
        CourseFormDialog(
            semester = selectedSemester,
            initialCourse = course,
            onDismiss = { subjectToEdit = null },
            onSave = { updatedCourse ->
                activeData = activeData.copy(
                    courses = activeData.courses.map { if (it.id == course.id) updatedCourse else it }
                )
                subjectToEdit = null
            }
        )
    }

    if (showAddTeacherDialog) {
        TeacherFormDialog(
            courses = activeData.courses,
            initialTeacher = null,
            existingCount = activeData.teachers.size,
            onDismiss = { showAddTeacherDialog = false },
            onSave = { newTeacher ->
                activeData = activeData.copy(teachers = activeData.teachers + newTeacher)
                showAddTeacherDialog = false
            }
        )
    }

    teacherToEdit?.let { teacher ->
        TeacherFormDialog(
            courses = activeData.courses,
            initialTeacher = teacher,
            existingCount = activeData.teachers.size,
            onDismiss = { teacherToEdit = null },
            onSave = { updatedTeacher ->
                activeData = activeData.copy(
                    teachers = activeData.teachers.map { if (it.teacherId == teacher.teacherId) updatedTeacher else it }
                )
                teacherToEdit = null
            }
        )
    }

    if (showAddBatchDialog) {
        QuickAddBatchDialog(
            existingBatches = activeData.batches,
            onDismiss = { showAddBatchDialog = false },
            onAdd = { newBatchName ->
                if (newBatchName.isNotBlank() && !activeData.batches.contains(newBatchName)) {
                    activeData = activeData.copy(batches = activeData.batches + newBatchName)
                }
                showAddBatchDialog = false
            }
        )
    }

    if (showAddRoomDialog) {
        QuickAddRoomDialog(
            onDismiss = { showAddRoomDialog = false },
            onAdd = { isLab, name ->
                if (isLab) {
                    activeData = activeData.copy(labs = activeData.labs + name)
                } else {
                    activeData = activeData.copy(lectureHalls = activeData.lectureHalls + name)
                }
                showAddRoomDialog = false
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// 1. COURSES & SUBJECTS SECTION (FULL CRUD + TEACHER LINKING)
// -------------------------------------------------------------------------------------------------
@Composable
fun CoordinatorCoursesSection(
    courses: List<Course>,
    semester: Int,
    onUpdateCourses: (List<Course>) -> Unit,
    onAddCourseClick: () -> Unit,
    onEditCourseClick: (Course) -> Unit,
    onAutoPopulateTeachersForSubject: (Course) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header with Add Subject Button (Fixed padding, no text wrapping)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SEMESTER $semester SUBJECTS (${courses.size} TOTAL)",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap any subject to edit L-T-P, credits, room, or add new subjects",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Button(
                onClick = onAddCourseClick,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_subject_btn")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add Subject",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        if (courses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No subjects in Semester $semester yet", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAddCourseClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                    ) {
                        Text("Add First Subject +", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(courses) { course ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditCourseClick(course) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = course.code,
                                        color = ElectricCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyanContainer)
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = course.category.name,
                                            color = ElectricCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "L-T-P: ${course.lectureHours}-${course.tutorialHours}-${course.practicalHours}",
                                        color = NeonMint,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = course.name,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    text = "Cr: ${course.credits} • Room: ${course.preferredRoom} • Lead: ${course.primaryInstructorName}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onEditCourseClick(course) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Course",
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { onUpdateCourses(courses.filter { it.code != course.code }) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Delete Course",
                                        tint = VibrantCoral.copy(alpha = 0.8f),
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 2. TEACHERS & FACULTY SECTION (FULL CRUD + 6 TEACHERS/SUBJECT AUTO-FILL)
// -------------------------------------------------------------------------------------------------
@Composable
fun CoordinatorTeachersSection(
    teachers: List<TeacherQualification>,
    courses: List<Course>,
    onUpdateTeachers: (List<TeacherQualification>) -> Unit,
    onAddTeacherClick: () -> Unit,
    onEditTeacherClick: (TeacherQualification) -> Unit,
    onAutoFillAllMissingStaff: () -> Unit
) {
    var selectedSubjectFilter by remember { mutableStateOf("ALL") }

    val distinctSubjects = remember(teachers, courses) {
        listOf("ALL") + (courses.map { it.code } + teachers.map { it.qualifiedCourseCode }).distinct()
    }

    val filteredTeachers = remember(teachers, selectedSubjectFilter) {
        if (selectedSubjectFilter == "ALL") teachers
        else teachers.filter { it.qualifiedCourseCode == selectedSubjectFilter }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "FACULTY & TEACHERS (${teachers.size} TOTAL)",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "6 Teachers required per subject for zero clashes",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = onAutoFillAllMissingStaff,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoMode, contentDescription = null, tint = NeonMint, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Auto 6/Subj", color = NeonMint, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }

                Button(
                    onClick = onAddTeacherClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Add Faculty", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // Subject Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(distinctSubjects) { sub ->
                val isSel = selectedSubjectFilter == sub
                val count = if (sub == "ALL") teachers.size else teachers.count { it.qualifiedCourseCode == sub }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) ElectricCyan else SurfaceDark,
                    border = BorderStroke(1.dp, if (isSel) ElectricCyan else SurfaceBorder),
                    modifier = Modifier.clickable { selectedSubjectFilter = sub }
                ) {
                    Text(
                        text = "$sub ($count)",
                        color = if (isSel) DeepNavyOnPrimary else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1
                    )
                }
            }
        }

        // Faculty Cards List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredTeachers) { teacher ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEditTeacherClick(teacher) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = CircleShape,
                                color = CyanContainer,
                                modifier = Modifier.size(30.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = teacher.teacherName.take(1),
                                        color = ElectricCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = teacher.teacherName,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${teacher.teacherId} • ${teacher.department} • Subj: ${teacher.qualifiedCourseCode}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (teacher.canTeachLecture) BadgePill(text = "L", color = ElectricCyan)
                                    if (teacher.canTeachTutorial) BadgePill(text = "T", color = SolarAmber)
                                    if (teacher.canTeachLab) BadgePill(text = "P", color = NeonMint)
                                    Text(
                                        text = "Max ${teacher.maxWeeklyWorkloadHours}h/wk",
                                        color = TextSecondary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onEditTeacherClick(teacher) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            IconButton(
                                onClick = { onUpdateTeachers(teachers.filter { it.teacherId != teacher.teacherId }) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Delete Teacher",
                                    tint = VibrantCoral.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 3. BATCHES & SUBGROUPS SECTION
// -------------------------------------------------------------------------------------------------
@Composable
fun CoordinatorBatchesSection(
    data: AcademicUploadData,
    onUpdateBatches: (List<String>) -> Unit,
    onUpdateSubgroups: (List<String>) -> Unit,
    onAddBatchClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACADEMIC BATCHES & SUBGROUPS",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "1 Batch = 120 Capacity (Lectures) • 4 Subgroups = 30 Capacity (Labs)",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
                Button(
                    onClick = onAddBatchClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Add Batch", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // BATCHES LIST
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Batches (${data.batches.size} Total • 120 Students Each)",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            AssistChip(
                                onClick = { onUpdateBatches((1..12).map { "B$it" }) },
                                label = { Text("12 Batches", fontSize = 9.sp) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceDark, labelColor = ElectricCyan)
                            )
                            AssistChip(
                                onClick = { onUpdateBatches((1..6).map { "B$it" }) },
                                label = { Text("6 Batches", fontSize = 9.sp) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceDark, labelColor = TextSecondary)
                            )
                        }
                    }

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(data.batches) { batch ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Groups,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = batch, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    if (data.batches.size > 1) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        IconButton(
                                            onClick = { onUpdateBatches(data.batches.filter { it != batch }) },
                                            modifier = Modifier.size(16.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = VibrantCoral,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // SUBGROUPS LIST
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Subgroups per Batch (${data.subgroups.size} Groups • 30 Capacity)",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        data.subgroups.forEach { sg ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = sg, color = NeonMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "30 std", color = TextMuted, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 4. ROOMS & LABS SECTION
// -------------------------------------------------------------------------------------------------
@Composable
fun CoordinatorRoomsSection(
    lectureHalls: List<String>,
    labs: List<String>,
    onUpdateHalls: (List<String>) -> Unit,
    onUpdateLabs: (List<String>) -> Unit,
    onAddRoomClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LECTURE HALLS & PRACTICAL LABS",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${lectureHalls.size} Lecture Halls • ${labs.size} Practical Laboratories",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = onAddRoomClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Add Room", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }
        }

        // LECTURE HALLS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Lecture Halls (120 Capacity for Common Batch Lectures)",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(lectureHalls) { hall ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = hall, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onUpdateHalls(lectureHalls.filter { it != hall }) },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Delete", tint = VibrantCoral, modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // PRACTICAL LABS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Specialized Practical Labs (30 Capacity for Subgroup Practical Labs)",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(labs) { lab ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SurfaceDark,
                                border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = NeonMint, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = lab, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onUpdateLabs(labs.filter { it != lab }) },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Delete", tint = VibrantCoral, modifier = Modifier.size(10.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// 5. COMPLETE MASTER CSV SECTION
// -------------------------------------------------------------------------------------------------
@Composable
fun MasterCsvSection(
    csvText: String,
    onCsvTextChange: (String) -> Unit,
    semester: Int,
    onResetDefault: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Master CSV Scheme Editor (Excel Compatible):",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            AssistChip(
                onClick = onResetDefault,
                label = { Text("Preload Sem $semester Preset", fontSize = 10.sp) },
                colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceDark, labelColor = ElectricCyan)
            )
        }

        OutlinedTextField(
            value = csvText,
            onValueChange = onCsvTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("master_csv_text_area"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = SurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            placeholder = {
                Text("Paste complete CSV scheme with # BATCHES, # TEACHERS, # COURSES, # ROOMS...", color = TextMuted, fontSize = 11.sp)
            }
        )
    }
}

// -------------------------------------------------------------------------------------------------
// HELPER GENERATOR FOR 6 TEACHERS PER COURSE
// -------------------------------------------------------------------------------------------------
fun generate6TeachersForCourse(course: Course, existingTotalCount: Int): List<TeacherQualification> {
    val sampleProfessors = listOf(
        "Dr. Alex Thorne", "Prof. Sarah Connor", "Dr. Alan Turing",
        "Prof. Ada Lovelace", "Dr. Richard Feynman", "Prof. Grace Hopper"
    )
    val dept = when (course.category) {
        CourseCategory.BSC -> "Basic Sciences"
        CourseCategory.ESC -> "Engineering Sciences"
        CourseCategory.HSS -> "Humanities & Mgmt"
        else -> "Computer Science"
    }

    return (1..6).map { idx ->
        val id = "T${existingTotalCount + idx + 100}"
        val name = sampleProfessors.getOrElse(idx - 1) { "Faculty $idx (${course.code})" }
        TeacherQualification(
            teacherId = id,
            teacherName = name,
            department = dept,
            qualifiedCourseCode = course.code,
            canTeachLecture = true,
            canTeachTutorial = course.tutorialHours > 0,
            canTeachLab = course.practicalHours > 0,
            maxWeeklyWorkloadHours = 18
        )
    }
}

// -------------------------------------------------------------------------------------------------
// CRUD MODAL DIALOGS: COURSE FORM
// -------------------------------------------------------------------------------------------------
@Composable
fun CourseFormDialog(
    semester: Int,
    initialCourse: Course?,
    onDismiss: () -> Unit,
    onSave: (Course) -> Unit
) {
    var code by remember { mutableStateOf(initialCourse?.code ?: "") }
    var name by remember { mutableStateOf(initialCourse?.name ?: "") }
    var category by remember { mutableStateOf(initialCourse?.category ?: CourseCategory.PCC) }
    var lectureHrs by remember { mutableStateOf((initialCourse?.lectureHours ?: 3).toString()) }
    var tutorialHrs by remember { mutableStateOf((initialCourse?.tutorialHours ?: 0).toString()) }
    var practicalHrs by remember { mutableStateOf((initialCourse?.practicalHours ?: 2).toString()) }
    var credits by remember { mutableStateOf((initialCourse?.credits ?: 4.0).toString()) }
    var instructor by remember { mutableStateOf(initialCourse?.primaryInstructorName ?: "Prof. Lead Faculty") }
    var room by remember { mutableStateOf(initialCourse?.preferredRoom ?: "LH-101") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialCourse == null) "Add Subject to Sem $semester" else "Edit Subject ${initialCourse.code}",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Subject Code (e.g. UCS501)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name (e.g. Machine Learning)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = lectureHrs,
                        onValueChange = { lectureHrs = it },
                        label = { Text("L (Hrs)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = tutorialHrs,
                        onValueChange = { tutorialHrs = it },
                        label = { Text("T (Hrs)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = practicalHrs,
                        onValueChange = { practicalHrs = it },
                        label = { Text("P (Hrs)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = credits,
                        onValueChange = { credits = it },
                        label = { Text("Credits") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = room,
                        onValueChange = { room = it },
                        label = { Text("Preferred Hall") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceDark,
                            unfocusedContainerColor = SurfaceDark,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && name.isNotBlank()) {
                        val finalCourse = Course(
                            id = initialCourse?.id ?: System.currentTimeMillis(),
                            code = code.trim(),
                            name = name.trim(),
                            shortName = code.trim(),
                            semesterNumber = semester,
                            category = category,
                            lectureHours = lectureHrs.toIntOrNull() ?: 3,
                            tutorialHours = tutorialHrs.toIntOrNull() ?: 0,
                            practicalHours = practicalHrs.toIntOrNull() ?: 2,
                            credits = credits.toDoubleOrNull() ?: 4.0,
                            primaryInstructorName = instructor.trim(),
                            preferredRoom = room.trim()
                        )
                        onSave(finalCourse)
                    }
                },
                enabled = code.isNotBlank() && name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
            ) {
                Text(if (initialCourse == null) "Add Subject" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = MidnightBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

// -------------------------------------------------------------------------------------------------
// CRUD MODAL DIALOGS: TEACHER FORM
// -------------------------------------------------------------------------------------------------
@Composable
fun TeacherFormDialog(
    courses: List<Course>,
    initialTeacher: TeacherQualification?,
    existingCount: Int,
    onDismiss: () -> Unit,
    onSave: (TeacherQualification) -> Unit
) {
    val defaultCode = courses.firstOrNull()?.code ?: "UCS501"
    var teacherId by remember { mutableStateOf(initialTeacher?.teacherId ?: "T${existingCount + 101}") }
    var teacherName by remember { mutableStateOf(initialTeacher?.teacherName ?: "") }
    var department by remember { mutableStateOf(initialTeacher?.department ?: "Computer Science & Engineering") }
    var qualifiedSubject by remember { mutableStateOf(initialTeacher?.qualifiedCourseCode ?: defaultCode) }
    var maxHours by remember { mutableStateOf((initialTeacher?.maxWeeklyWorkloadHours ?: 18).toString()) }
    var canL by remember { mutableStateOf(initialTeacher?.canTeachLecture ?: true) }
    var canT by remember { mutableStateOf(initialTeacher?.canTeachTutorial ?: true) }
    var canP by remember { mutableStateOf(initialTeacher?.canTeachLab ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTeacher == null) "Add Faculty Member" else "Edit Faculty ${initialTeacher.teacherName}",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = teacherName,
                    onValueChange = { teacherName = it },
                    label = { Text("Teacher Name (e.g. Dr. Jane Smith)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                OutlinedTextField(
                    value = qualifiedSubject,
                    onValueChange = { qualifiedSubject = it.uppercase() },
                    label = { Text("Assigned Subject Code (e.g. UML501)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = canL, onCheckedChange = { canL = it })
                        Text("Lecture", color = TextPrimary, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = canT, onCheckedChange = { canT = it })
                        Text("Tutorial", color = TextPrimary, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = canP, onCheckedChange = { canP = it })
                        Text("Lab", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (teacherName.isNotBlank()) {
                        onSave(
                            TeacherQualification(
                                teacherId = teacherId.trim(),
                                teacherName = teacherName.trim(),
                                department = department.trim(),
                                qualifiedCourseCode = qualifiedSubject.trim(),
                                canTeachLecture = canL,
                                canTeachTutorial = canT,
                                canTeachLab = canP,
                                maxWeeklyWorkloadHours = maxHours.toIntOrNull() ?: 18
                            )
                        )
                    }
                },
                enabled = teacherName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
            ) {
                Text(if (initialTeacher == null) "Add Faculty" else "Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = MidnightBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun BadgePill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Text(text = text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun QuickAddBatchDialog(
    existingBatches: List<String>,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    val nextBatchName = "B${existingBatches.size + 1}"
    var batchName by remember { mutableStateOf(nextBatchName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Academic Batch", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Batch Name (120 Capacity):", color = TextSecondary, fontSize = 11.sp)
                OutlinedTextField(
                    value = batchName,
                    onValueChange = { batchName = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(batchName.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
            ) {
                Text("Add Batch")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = MidnightBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun QuickAddRoomDialog(
    onDismiss: () -> Unit,
    onAdd: (isLab: Boolean, name: String) -> Unit
) {
    var roomName by remember { mutableStateOf("") }
    var isLab by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Room or Laboratory", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = !isLab,
                        onClick = { isLab = false },
                        label = { Text("Lecture Hall (120 Cap)") }
                    )
                    FilterChip(
                        selected = isLab,
                        onClick = { isLab = true },
                        label = { Text("Practical Lab (30 Cap)") }
                    )
                }

                OutlinedTextField(
                    value = roomName,
                    onValueChange = { roomName = it },
                    label = { Text(if (isLab) "Lab Name (e.g. AI Lab 3)" else "Hall Name (e.g. LH-113)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (roomName.isNotBlank()) {
                        onAdd(isLab, roomName.trim())
                    }
                },
                enabled = roomName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
            ) {
                Text("Add Room")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        },
        containerColor = MidnightBackground,
        shape = RoundedCornerShape(16.dp)
    )
}
