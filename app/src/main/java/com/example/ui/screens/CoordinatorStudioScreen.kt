package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.domain.TimetableGeneratorEngine
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinatorStudioScreen(viewModel: AppViewModel) {
    val selectedTab by viewModel.selectedCoordinatorTab.collectAsState()
    val isGenerating by viewModel.isGeneratingRoutine.collectAsState()
    val auditReport by viewModel.conflictAuditReport.collectAsState()
    val batches by viewModel.coordinatorBatches.collectAsState()
    val rooms by viewModel.coordinatorRooms.collectAsState()
    val teachers by viewModel.coordinatorTeachers.collectAsState()
    val courses by viewModel.coordinatorCourses.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()

    var showAddBatchDialog by remember { mutableStateOf(false) }
    var showAddTeacherDialog by remember { mutableStateOf(false) }
    var showAddRoomDialog by remember { mutableStateOf(false) }
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        containerColor = MidnightBackground,
        floatingActionButton = {
            if (selectedTab in 1..4) {
                FloatingActionButton(
                    onClick = {
                        when (selectedTab) {
                            1 -> showAddBatchDialog = true
                            2 -> showAddTeacherDialog = true
                            3 -> showAddRoomDialog = true
                            4 -> showAddCourseDialog = true
                        }
                    },
                    containerColor = ElectricCyan,
                    contentColor = DeepNavyOnPrimary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("coordinator_fab_add")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Item"
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Studio Header
                CoordinatorStudioHeader(
                    semester = selectedSemester,
                    onSemesterSelect = { viewModel.setSelectedSemester(it) },
                    onLoadPreset = { viewModel.loadCoordinatorPreset(it) },
                    onUploadClick = { showUploadDialog = true },
                    onExportClick = { showExportDialog = true }
                )
            }

            item {
                // Auto-Solver Master Trigger Card
                AutoSolverTriggerCard(
                    isGenerating = isGenerating,
                    auditReport = auditReport,
                    batchesCount = batches.size,
                    teachersCount = teachers.size,
                    roomsCount = rooms.size,
                    onGenerate = { viewModel.generateMasterSchedule(selectedSemester) },
                    onUploadScheme = { showUploadDialog = true }
                )
            }

            item {
                // Coordinator Navigation Chips
                CoordinatorTabPills(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.setSelectedCoordinatorTab(it) }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Cockpit Summary & Matrix Preview
                    item {
                        PlannerCockpitSection(
                            auditReport = auditReport,
                            batches = batches,
                            rooms = rooms,
                            teachers = teachers,
                            courses = courses,
                            onSwitchTab = { viewModel.setSelectedCoordinatorTab(it) }
                        )
                    }
                }
                1 -> {
                    // Batches & Subgroups Configurator
                    item {
                        SectionHeader(
                            title = "Configured Batches & Subgroups (${batches.size})",
                            subtitle = "Define cohort strengths, subgroup splits (G1, G2...), and designated lecture halls."
                        )
                    }
                    items(batches, key = { it.batchId }) { batch ->
                        CoordinatorBatchCard(
                            batch = batch,
                            onDelete = { viewModel.deleteCoordinatorBatch(batch.batchId) }
                        )
                    }
                }
                2 -> {
                    // Faculty & Teachers Registry
                    item {
                        SectionHeader(
                            title = "Faculty & Instructors Directory (${teachers.size})",
                            subtitle = "Manage teacher assignments, max daily sessions, lab eligibility, and subject qualifications."
                        )
                    }
                    items(teachers, key = { it.teacherId }) { teacher ->
                        CoordinatorTeacherCard(
                            teacher = teacher,
                            onDelete = { viewModel.deleteCoordinatorTeacher(teacher.teacherId) }
                        )
                    }
                }
                3 -> {
                    // Rooms & Labs Inventory
                    item {
                        SectionHeader(
                            title = "Rooms & Specialized Labs (${rooms.size})",
                            subtitle = "Configure lecture halls and computer/hardware labs with equipment specs and seating capacity."
                        )
                    }
                    items(rooms, key = { it.roomId }) { room ->
                        CoordinatorRoomCard(
                            room = room,
                            onDelete = { viewModel.deleteCoordinatorRoom(room.roomId) }
                        )
                    }
                }
                4 -> {
                    // Curriculum Courses
                    item {
                        SectionHeader(
                            title = "Curriculum Scheme & Workload (${courses.size})",
                            subtitle = "Course credits, weekly lecture hours (L), and 2-hour practical lab requirements (P)."
                        )
                    }
                    items(courses, key = { it.id }) { course ->
                        CoordinatorCourseCard(
                            course = course,
                            onDelete = { viewModel.deleteCoordinatorCourse(course.code) }
                        )
                    }
                }
                5 -> {
                    // Master Faculty & Room Matrices
                    item {
                        TeacherAndRoomMatricesSection(
                            viewModel = viewModel,
                            teachers = teachers,
                            rooms = rooms,
                            allSessions = allSessions.filter { it.semesterNumber == selectedSemester }
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Dialogs
    if (showAddBatchDialog) {
        AddBatchDialog(
            semester = selectedSemester,
            availableRooms = rooms.filter { !it.isLab }.map { it.name },
            onDismiss = { showAddBatchDialog = false },
            onAdd = { newBatch ->
                viewModel.addCoordinatorBatch(newBatch)
                showAddBatchDialog = false
            }
        )
    }

    if (showAddTeacherDialog) {
        AddTeacherDialog(
            availableCourses = courses.map { it.code },
            onDismiss = { showAddTeacherDialog = false },
            onAdd = { newTeacher ->
                viewModel.addCoordinatorTeacher(newTeacher)
                showAddTeacherDialog = false
            }
        )
    }

    if (showAddRoomDialog) {
        AddRoomDialog(
            onDismiss = { showAddRoomDialog = false },
            onAdd = { newRoom ->
                viewModel.addCoordinatorRoom(newRoom)
                showAddRoomDialog = false
            }
        )
    }

    if (showAddCourseDialog) {
        AddCourseDialog(
            semester = selectedSemester,
            availableTeachers = teachers.map { it.name },
            availableRooms = rooms.filter { !it.isLab }.map { it.name },
            onDismiss = { showAddCourseDialog = false },
            onAdd = { newCourse ->
                viewModel.addCoordinatorCourse(newCourse)
                showAddCourseDialog = false
            }
        )
    }

    if (showExportDialog) {
        ExportScheduleDialog(
            batches = batches,
            onDismiss = { showExportDialog = false },
            onExport = { targetBatch ->
                val md = viewModel.exportMasterRoutineMarkdown(targetBatch)
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Academic Timetable", md)
                clipboard.setPrimaryClip(clip)
                showExportDialog = false
            }
        )
    }

    if (showUploadDialog) {
        RoutineStudioUploadDialog(
            viewModel = viewModel,
            onDismiss = { showUploadDialog = false }
        )
    }
}

@Composable
fun CoordinatorStudioHeader(
    semester: Int,
    onSemesterSelect: (Int) -> Unit,
    onLoadPreset: (String) -> Unit,
    onUploadClick: () -> Unit,
    onExportClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ElectricCyan.copy(0.4f), PurpleContainer))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = CyanContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Planner",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "COORDINATOR STUDIO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp,
                            color = ElectricCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Automated Routine Planner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Upload / Import CSV Scheme Button
                    FilledTonalButton(
                        onClick = onUploadClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ElectricCyan.copy(alpha = 0.18f),
                            contentColor = ElectricCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("upload_scheme_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Upload Scheme",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Upload Scheme", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Export Button
                    IconButton(
                        onClick = onExportClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .testTag("export_routine_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export & Share",
                            tint = ElectricCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Semester Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Active Semester:",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items((1..8).toList()) { sem ->
                        val isSelected = sem == semester
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ElectricCyan else SurfaceElevated,
                            modifier = Modifier
                                .clickable { onSemesterSelect(sem) }
                                .testTag("sem_selector_$sem")
                        ) {
                            Text(
                                text = "Sem $sem",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                color = if (isSelected) DeepNavyOnPrimary else TextMuted,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Presets Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Presets:",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                )
                AssistChip(
                    onClick = { onLoadPreset("CSE_12_BATCHES") },
                    label = { Text("CSE (12 Batches)", fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceElevated, labelColor = ElectricCyan),
                    border = BorderStroke(1.dp, SurfaceBorder)
                )
                AssistChip(
                    onClick = { onLoadPreset("AI_12_BATCHES") },
                    label = { Text("AI & ML (12 Batches)", fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceElevated, labelColor = TextSecondary),
                    border = BorderStroke(1.dp, SurfaceBorder)
                )
                AssistChip(
                    onClick = { onLoadPreset("ECE_12_BATCHES") },
                    label = { Text("ECE (12 Batches)", fontSize = 10.sp) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = SurfaceElevated, labelColor = TextSecondary),
                    border = BorderStroke(1.dp, SurfaceBorder)
                )
            }
        }
    }
}

@Composable
fun AutoSolverTriggerCard(
    isGenerating: Boolean,
    auditReport: ConflictAuditReport,
    batchesCount: Int,
    teachersCount: Int,
    roomsCount: Int,
    onGenerate: () -> Unit,
    onUploadScheme: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(ElectricCyan.copy(0.6f), NeonMint.copy(0.4f)))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = null,
                        tint = NeonMint,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CONFLICT-FREE ENGINE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonMint,
                        letterSpacing = 0.8.sp
                    )
                }

                // Zero Clash Guarantee Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NeonMint.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(NeonMint, ElectricCyan)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonMint,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "0 Hard Clashes",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonMint
                        )
                    }
                }
            }

            Text(
                text = "Automatically synthesizes collision-free routines for all $batchesCount batches (120 cap), 4 subgroups per batch (30 cap), and 6 teachers per subject with 2-hour lab constraints.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            // Health Audit Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AuditPill(label = "Sessions", value = "${auditReport.totalSessionsCount}")
                AuditPill(label = "Room Util", value = "${auditReport.roomUtilizationPercent}%")
                AuditPill(label = "Faculty Balance", value = "${auditReport.facultyWorkloadBalancePercent}%")
                AuditPill(label = "Harmony", value = "${auditReport.cohortHarmonyScore}/100")
            }

            // Big Generate Button & Upload Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onUploadScheme,
                    border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Upload Scheme",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onGenerate,
                    enabled = !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp)
                        .testTag("generate_master_routine_button")
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = DeepNavyOnPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SOLVING...",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "AUTO-GENERATE (12 BATCHES)",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 0.6.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuditPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceDark,
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder)))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = ElectricCyan
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = TextMuted,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun CoordinatorTabPills(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        "⚡ Cockpit",
        "🏷️ Batches",
        "👨‍🏫 Faculty",
        "🏛️ Rooms",
        "📚 Courses",
        "📊 Matrices"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs.indices.toList()) { index ->
            val isSelected = selectedTab == index
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) ElectricCyan else SurfaceElevated,
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(if (isSelected) listOf(ElectricCyan, ElectricCyan) else listOf(SurfaceBorder, SurfaceBorder))),
                modifier = Modifier
                    .clickable { onTabSelected(index) }
                    .testTag("coordinator_tab_$index")
            ) {
                Text(
                    text = tabs[index],
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) DeepNavyOnPrimary else TextSecondary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PlannerCockpitSection(
    auditReport: ConflictAuditReport,
    batches: List<CoordinatorBatch>,
    rooms: List<CoordinatorRoom>,
    teachers: List<CoordinatorTeacher>,
    courses: List<Course>,
    onSwitchTab: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Academic Configuration Overview",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryActionCard(
                title = "Batches",
                count = "${batches.size}",
                caption = "${batches.sumOf { it.studentCount }} Students",
                icon = Icons.Default.Groups,
                modifier = Modifier.weight(1f),
                onClick = { onSwitchTab(1) }
            )
            SummaryActionCard(
                title = "Faculty",
                count = "${teachers.size}",
                caption = "${teachers.count { it.canTeachLab }} Lab Eligible",
                icon = Icons.Default.School,
                modifier = Modifier.weight(1f),
                onClick = { onSwitchTab(2) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryActionCard(
                title = "Rooms & Labs",
                count = "${rooms.size}",
                caption = "${rooms.count { it.isLab }} Specialized Labs",
                icon = Icons.Default.Apartment,
                modifier = Modifier.weight(1f),
                onClick = { onSwitchTab(3) }
            )
            SummaryActionCard(
                title = "Curriculum",
                count = "${courses.size}",
                caption = "${courses.sumOf { it.credits }} Total Cr",
                icon = Icons.Default.MenuBook,
                modifier = Modifier.weight(1f),
                onClick = { onSwitchTab(4) }
            )
        }

        // Live Diagnostic Log
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CONSTRAINTS ENGINE LOG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricCyan,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "100% Deterministic",
                        fontSize = 10.sp,
                        color = NeonMint,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Constraint 1: Zero Faculty double-booking across all ${teachers.size} faculty members.\n" +
                            "• Constraint 2: Zero room collisions across lecture halls & dedicated labs.\n" +
                            "• Constraint 3: Subgroup practical labs locked to uninterrupted 2-period blocks.\n" +
                            "• Constraint 4: Maximum daily course lecture spread strictly enforced.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun SummaryActionCard(
    title: String,
    count: String,
    caption: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = count,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = caption,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

@Composable
fun CoordinatorBatchCard(
    batch: CoordinatorBatch,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyanContainer,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = batch.batchId,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = batch.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Strength: ${batch.studentCount} Students • Subgroups: ${batch.subgroupCount} (G1..G${batch.subgroupCount}) • Room: ${batch.defaultLectureHall}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Batch",
                    tint = VibrantCoral,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CoordinatorTeacherCard(
    teacher: CoordinatorTeacher,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = teacher.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = PurpleContainer
                    ) {
                        Text(
                            text = teacher.designation,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CyberPurple,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dept: ${teacher.department} • Max Daily: ${teacher.maxDailySlots} slots • Lab: ${if (teacher.canTeachLab) "Eligible" else "Lecture Only"}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                if (teacher.qualifiedCourseCodes.isNotEmpty()) {
                    Text(
                        text = "Teaches: ${teacher.qualifiedCourseCodes.joinToString(", ")}",
                        fontSize = 10.sp,
                        color = ElectricCyan,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Teacher",
                    tint = VibrantCoral,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CoordinatorRoomCard(
    room: CoordinatorRoom,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (room.isLab) NeonMint.copy(0.15f) else CyanContainer
                    ) {
                        Text(
                            text = if (room.isLab) "LAB" else "HALL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = if (room.isLab) NeonMint else ElectricCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = room.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${room.building} • Capacity: ${room.capacity} seats",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "Equip: ${room.equipmentInfo}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Room",
                    tint = VibrantCoral,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CoordinatorCourseCard(
    course: Course,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyanContainer
                    ) {
                        Text(
                            text = course.code,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = course.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "L-T-P: ${course.lectureHours}-${course.tutorialHours}-${course.practicalHours} (${course.credits} Cr) • Instructor: ${course.primaryInstructorName}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Course",
                    tint = VibrantCoral,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun TeacherAndRoomMatricesSection(
    viewModel: AppViewModel,
    teachers: List<CoordinatorTeacher>,
    rooms: List<CoordinatorRoom>,
    allSessions: List<ClassSession>
) {
    val selectedTeacher by viewModel.selectedTeacherForMatrix.collectAsState()
    val selectedRoom by viewModel.selectedRoomForMatrix.collectAsState()
    var viewMode by remember { mutableStateOf(0) } // 0 = Teacher View, 1 = Room View

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Toggle Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewMode = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewMode == 0) ElectricCyan else SurfaceElevated,
                    contentColor = if (viewMode == 0) DeepNavyOnPrimary else TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("👨‍🏫 Teacher Timetables", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewMode = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewMode == 1) ElectricCyan else SurfaceElevated,
                    contentColor = if (viewMode == 1) DeepNavyOnPrimary else TextSecondary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text("🏛️ Room Occupancy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (viewMode == 0) {
            // Teacher Selector Dropdown/Chips
            Text(
                text = "Select Faculty Member:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(teachers) { t ->
                    val isSelected = t.name == selectedTeacher
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) ElectricCyan else SurfaceElevated,
                        modifier = Modifier.clickable { viewModel.setSelectedTeacherForMatrix(t.name) }
                    ) {
                        Text(
                            text = t.name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DeepNavyOnPrimary else TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Teacher Weekly Timetable Grid
            val teacherSessions = allSessions.filter { it.instructorName == selectedTeacher }
            WeeklyFacultyMatrixView(
                facultyName = selectedTeacher,
                sessions = teacherSessions
            )
        } else {
            // Room Selector
            Text(
                text = "Select Room / Lab:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(rooms) { r ->
                    val isSelected = r.name == selectedRoom
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) ElectricCyan else SurfaceElevated,
                        modifier = Modifier.clickable { viewModel.setSelectedRoomForMatrix(r.name) }
                    ) {
                        Text(
                            text = "${if (r.isLab) "🧪 " else "🏛️ "}${r.name}",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DeepNavyOnPrimary else TextSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            val roomSessions = allSessions.filter { it.roomName == selectedRoom }
            WeeklyRoomMatrixView(
                roomName = selectedRoom,
                sessions = roomSessions
            )
        }
    }
}

@Composable
fun WeeklyFacultyMatrixView(
    facultyName: String,
    sessions: List<ClassSession>
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri")

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$facultyName's Weekly Schedule",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${sessions.size} Sessions (${sessions.sumOf { it.durationSlots }} Hrs)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            // 5 Day Rows
            days.forEachIndexed { dayIdx, dayName ->
                val dayNum = dayIdx + 1
                val daySessions = sessions.filter { it.dayOfWeek == dayNum }.sortedBy { it.periodIndex }

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = dayName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (daySessions.isEmpty()) {
                        Text(
                            text = "Free Slot / Research & Office Hours",
                            fontSize = 10.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            daySessions.forEach { s ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (s.sessionType == SessionType.LAB) PurpleContainer else CyanContainer,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                        Text(
                                            text = "P${s.periodIndex}${if (s.durationSlots > 1) "-P${s.periodIndex + s.durationSlots - 1}" else ""}: ${s.courseCode}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (s.sessionType == SessionType.LAB) CyberPurple else ElectricCyan
                                        )
                                        Text(
                                            text = "${s.batchId} (${s.roomName})",
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = SurfaceBorder.copy(alpha = 0.5f), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun WeeklyRoomMatrixView(
    roomName: String,
    sessions: List<ClassSession>
) {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri")

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceBorder))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Room $roomName Occupancy Grid",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${sessions.size} Bookings",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonMint
                )
            }
            Spacer(modifier = Modifier.height(10.dp))

            days.forEachIndexed { dayIdx, dayName ->
                val dayNum = dayIdx + 1
                val daySessions = sessions.filter { it.dayOfWeek == dayNum }.sortedBy { it.periodIndex }

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = dayName.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonMint
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (daySessions.isEmpty()) {
                        Text(
                            text = "Room Available / Unoccupied",
                            fontSize = 10.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            daySessions.forEach { s ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceDark,
                                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(NeonMint.copy(0.4f), ElectricCyan.copy(0.4f)))),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                        Text(
                                            text = "P${s.periodIndex}: ${s.batchId} - ${s.courseCode}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = s.instructorName,
                                            fontSize = 9.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = SurfaceBorder.copy(alpha = 0.5f), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

// =========================================================================
// DIALOGS FOR ADDING CONFIGURATION
// =========================================================================

@Composable
fun AddBatchDialog(
    semester: Int,
    availableRooms: List<String>,
    onDismiss: () -> Unit,
    onAdd: (CoordinatorBatch) -> Unit
) {
    var batchId by remember { mutableStateOf("B5") }
    var name by remember { mutableStateOf("Batch B5") }
    var strength by remember { mutableStateOf("60") }
    var subgroupCount by remember { mutableStateOf("2") }
    var room by remember { mutableStateOf(availableRooms.firstOrNull() ?: "LH-101") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Academic Batch",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = batchId,
                    onValueChange = { batchId = it },
                    label = { Text("Batch Code (e.g. B5)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Batch Full Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = strength,
                        onValueChange = { strength = it },
                        label = { Text("Student Count") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = subgroupCount,
                        onValueChange = { subgroupCount = it },
                        label = { Text("Subgroups (e.g. 2)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val count = strength.toIntOrNull() ?: 60
                            val subs = subgroupCount.toIntOrNull() ?: 2
                            onAdd(
                                CoordinatorBatch(
                                    batchId = batchId.trim(),
                                    name = name.trim(),
                                    subgroupCount = subs,
                                    studentCount = count,
                                    defaultLectureHall = room,
                                    semester = semester
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                    ) {
                        Text("Add Batch", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddTeacherDialog(
    availableCourses: List<String>,
    onDismiss: () -> Unit,
    onAdd: (CoordinatorTeacher) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("Assistant Professor") }
    var maxDaily by remember { mutableStateOf("3") }
    var qualifiedCourse by remember { mutableStateOf(availableCourses.firstOrNull() ?: "UML501") }
    var canTeachLab by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Faculty / Instructor",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Teacher Name (e.g. Dr. Ramesh Gupta)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("Designation") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = maxDaily,
                    onValueChange = { maxDaily = it },
                    label = { Text("Max Daily Periods (e.g. 3)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Can Teach Practical Labs", fontSize = 12.sp, color = TextPrimary)
                    Switch(
                        checked = canTeachLab,
                        onCheckedChange = { canTeachLab = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepNavyOnPrimary,
                            checkedTrackColor = ElectricCyan
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(
                                    CoordinatorTeacher(
                                        teacherId = "T${System.currentTimeMillis() % 1000}",
                                        name = name.trim(),
                                        designation = designation.trim(),
                                        maxDailySlots = maxDaily.toIntOrNull() ?: 3,
                                        qualifiedCourseCodes = listOf(qualifiedCourse),
                                        canTeachLab = canTeachLab
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                    ) {
                        Text("Save Faculty", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddRoomDialog(
    onDismiss: () -> Unit,
    onAdd: (CoordinatorRoom) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var building by remember { mutableStateOf("Academic Block A") }
    var capacity by remember { mutableStateOf("70") }
    var isLab by remember { mutableStateOf(false) }
    var equipment by remember { mutableStateOf("Projector & Audio System") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Room or Lab",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Room Name (e.g. LH-201 or AI Lab 2)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = building,
                    onValueChange = { building = it },
                    label = { Text("Building / Wing") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Seating Capacity") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Is this a Specialized Lab?", fontSize = 12.sp, color = TextPrimary)
                    Switch(
                        checked = isLab,
                        onCheckedChange = {
                            isLab = it
                            if (it) equipment = "40 Workstations & Network Gear"
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepNavyOnPrimary,
                            checkedTrackColor = NeonMint
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(
                                    CoordinatorRoom(
                                        roomId = "R${System.currentTimeMillis() % 1000}",
                                        name = name.trim(),
                                        building = building.trim(),
                                        capacity = capacity.toIntOrNull() ?: 70,
                                        isLab = isLab,
                                        equipmentInfo = equipment.trim()
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                    ) {
                        Text("Save Room", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddCourseDialog(
    semester: Int,
    availableTeachers: List<String>,
    availableRooms: List<String>,
    onDismiss: () -> Unit,
    onAdd: (Course) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var lectureHours by remember { mutableStateOf("3") }
    var practicalHours by remember { mutableStateOf("2") }
    var credits by remember { mutableStateOf("4.0") }
    var teacher by remember { mutableStateOf(availableTeachers.firstOrNull() ?: "Faculty") }
    var room by remember { mutableStateOf(availableRooms.firstOrNull() ?: "LH-101") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Curriculum Course",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course Code (e.g. UCS520)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Course Name (e.g. Distributed Systems)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lectureHours,
                        onValueChange = { lectureHours = it },
                        label = { Text("Lectures (L)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = practicalHours,
                        onValueChange = { practicalHours = it },
                        label = { Text("Labs (P)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = credits,
                        onValueChange = { credits = it },
                        label = { Text("Credits") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = SurfaceBorder
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (code.isNotBlank() && name.isNotBlank()) {
                                onAdd(
                                    Course(
                                        id = System.currentTimeMillis() % 10000,
                                        code = code.trim().uppercase(),
                                        name = name.trim(),
                                        shortName = code.trim(),
                                        semesterNumber = semester,
                                        lectureHours = lectureHours.toIntOrNull() ?: 3,
                                        practicalHours = practicalHours.toIntOrNull() ?: 0,
                                        credits = credits.toDoubleOrNull() ?: 4.0,
                                        primaryInstructorName = teacher,
                                        preferredRoom = room
                                    )
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                    ) {
                        Text("Save Course", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ExportScheduleDialog(
    batches: List<CoordinatorBatch>,
    onDismiss: () -> Unit,
    onExport: (String?) -> Unit
) {
    var selectedBatch by remember { mutableStateOf(batches.firstOrNull()?.batchId) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Export Academic Timetable",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "Select a batch to copy a Markdown formatted schedule ready for sharing via WhatsApp, Email, or printing.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(batches) { b ->
                        val isSelected = b.batchId == selectedBatch
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ElectricCyan else SurfaceDark,
                            modifier = Modifier.clickable { selectedBatch = b.batchId }
                        ) {
                            Text(
                                text = b.batchId,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) DeepNavyOnPrimary else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onExport(selectedBatch) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                    ) {
                        Text("Copy to Clipboard", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
