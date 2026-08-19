package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.TeacherQualification
import com.example.domain.TimetableHealthReport
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@Composable
fun SyllabusAnalyticsScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val semesterCourses by viewModel.semesterCourses.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val healthReport by viewModel.healthReport.collectAsState()
    val simulationResult by viewModel.activeSimulation.collectAsState()
    val isSolverRunning by viewModel.isSolverRunning.collectAsState()
    val teacherPool by viewModel.customTeacherPool.collectAsState()

    var selectedViewMode by remember { mutableIntStateOf(0) } // 0: Curriculum & Exam Intelligence, 1: Faculty Pool & Workload, 2: Health & Simulation, 3: Bottleneck Detection
    var showRoutineStudioModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
    ) {
        // 1. Header & Quick Optimization Trigger + Upload
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Curriculum & Intelligence",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Syllabus tracking, exam deficits & bottleneck analysis",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .clickable { showRoutineStudioModal = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Upload Scheme", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.triggerSolverOptimization() },
                        enabled = !isSolverRunning,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = DeepNavyOnPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("run_solver_button")
                    ) {
                        if (isSolverRunning) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = DeepNavyOnPrimary, strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Optimize", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 2. View Mode Tabs (Curriculum/Exams, Faculty/Workload, Health/Simulation, Bottlenecks)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "Syllabus & Exams" to 0,
                    "Faculty Workload" to 1,
                    "Health & Simulator" to 2,
                    "Bottlenecks" to 3
                ).forEach { (label, idx) ->
                    val isSel = selectedViewMode == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) ElectricCyan else Color.Transparent)
                            .clickable { selectedViewMode = idx }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) DeepNavyOnPrimary else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // 3. Tab Contents
        when (selectedViewMode) {
            0 -> {
                // =========================================================================
                // TAB 0: SYLLABUS INTELLIGENCE & EXAM-AWARE RECOVERY (Sections 35 & 36)
                // =========================================================================
                item {
                    // Semester Selector Strip
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "SEMESTER CURRICULUM SELECTOR (1 TO 8):",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items((1..8).toList()) { sem ->
                                val isSel = sem == selectedSemester
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) ElectricCyan else SurfaceDark)
                                        .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { viewModel.setSelectedSemester(sem) }
                                        .padding(horizontal = 14.dp, vertical = 7.dp)
                                ) {
                                    Text(
                                        text = "Semester $sem",
                                        color = if (isSel) DeepNavyOnPrimary else TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // Exam-Aware Recovery Alert Banner (Section 36)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CoralContainer.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, CoralLight.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.EventBusy, contentDescription = null, tint = CoralLight, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Exam in 21 Days • Academic Deficit Detected", color = CoralLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("DBMS has 8 lectures remaining, but timetable capacity has only 6 slots. Recommended: 2 makeup sessions required before exams.", color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
                            }
                        }
                    }
                }

                items(semesterCourses, key = { it.id }) { course ->
                    SyllabusIntelligenceCard(course = course)
                }
            }

            1 -> {
                // =========================================================================
                // TAB 1: FACULTY POOL & WORKLOAD DASHBOARD (Sections 37 & 38)
                // =========================================================================
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorderStrong),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Faculty Workload Dashboard", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("UGC/AICTE regulatory norms compliance", color = TextMuted, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MintContainer)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("All Compliant 🟢", color = MintLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = "UGC Regulations prescribe: Direct teaching hours 16h for Assistant Professors, 14h for Associate/Professors, with dedicated research & visiting time.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                items(teacherPool, key = { it.teacherId }) { teacher ->
                    FacultyWorkloadCard(teacher = teacher)
                }
            }

            2 -> {
                // =========================================================================
                // TAB 2: TIMETABLE HEALTH SCORE & WHAT-IF SIMULATOR (Sections 16 & 33)
                // =========================================================================
                item {
                    HealthScoreDetailedCard(report = healthReport)
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("What-If Disruption Simulator", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Simulates scenarios without altering live schedule", color = TextMuted, fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.Science, contentDescription = null, tint = ElectricCyan)
                            }

                            Text(
                                text = "Coordinator can ask: \"What if Computer Lab is unavailable next week?\" or \"What if Dr. Gupta is absent on Friday?\"",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.runSimulation(1) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, SurfaceBorderStrong),
                                    modifier = Modifier.weight(1f).testTag("sim_lab_btn")
                                ) {
                                    Text("Lab 3 Down", fontSize = 11.sp, color = TextPrimary)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.runSimulation(2) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, SurfaceBorderStrong),
                                    modifier = Modifier.weight(1f).testTag("sim_faculty_btn")
                                ) {
                                    Text("Dr. Gupta Absent", fontSize = 11.sp, color = TextPrimary)
                                }
                            }

                            // Simulation Result
                            simulationResult?.let { result ->
                                HorizontalDivider(color = SurfaceBorder)

                                Text(text = result.query, color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Affected: ${result.affectedSessionsCount}", color = TextSecondary, fontSize = 11.sp)
                                    Text("Room Changes: ${result.requiredRoomChanges}", color = TextSecondary, fontSize = 11.sp)
                                    Text("Stability: ${result.stabilityScorePercent}%", color = MintLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                result.recommendedActions.forEach { action ->
                                    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 1.dp)) {
                                        Text("• ", color = ElectricCyan, fontSize = 11.sp)
                                        Text(action, color = TextPrimary, fontSize = 11.sp)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { viewModel.clearSimulation() }) {
                                        Text("Clear Simulation", color = TextMuted, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // =========================================================================
                // TAB 3: BOTTLENECK DETECTION & INFEASIBILITY ANALYSIS (Sections 34 & 43)
                // =========================================================================
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorderStrong),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, tint = SolarAmber, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Predictive Bottleneck Detection", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "Continuously predicts operational stress points before timetable failures occur:",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            BottleneckAlertCard(
                                type = "Resource Bottleneck",
                                title = "Computer Lab utilization expected to reach 96%",
                                description = "Peak demand on Monday & Thursday periods. Shortage of 6 lab slots if elective lab expands.",
                                severity = "MEDIUM",
                                severityColor = SolarAmber
                            )

                            BottleneckAlertCard(
                                type = "Faculty Bottleneck",
                                title = "Only 2 faculty qualified for Cloud DevOps (PEC501)",
                                description = "Dr. Harish Chandra and Dr. Sneha Verma. If either takes medical leave, substitute compatibility drops to 70%.",
                                severity = "HIGH",
                                severityColor = VibrantCoral
                            )

                            BottleneckAlertCard(
                                type = "Syllabus Bottleneck",
                                title = "8 lectures remaining for DBMS with only 6 regular periods",
                                description = "Exam in 21 days. Requires 2 self-healing makeup classes to guarantee 100% syllabus completion.",
                                severity = "URGENT",
                                severityColor = VibrantCoral
                            )

                            // "No Feasible Solution" Engine Fixes (Section 43 & 44)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SurfaceElevated,
                                border = BorderStroke(1.dp, SurfaceBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("RECOMMENDED BOTTLENECK REMEDIES:", color = ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("1. Add secondary computer lab buffer in Block B.", color = TextPrimary, fontSize = 10.sp)
                                    Text("2. Extend lab operating hours into Saturday 09:00 - 12:00.", color = TextPrimary, fontSize = 10.sp)
                                    Text("3. Enable cross-department substitute teaching pool.", color = TextPrimary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRoutineStudioModal) {
        RoutineStudioUploadDialog(
            viewModel = viewModel,
            onDismiss = { showRoutineStudioModal = false }
        )
    }
}

@Composable
fun SyllabusIntelligenceCard(course: Course) {
    val totalRequired = course.totalPlannedLectures
    val completed = course.completedLectures
    val cancelled = course.cancelledLectures
    val remaining = totalRequired - completed
    val progressPercent = (completed.toFloat() / totalRequired.coerceAtLeast(1) * 100).toInt()

    val (riskLabel, riskColor, riskBg) = when {
        course.examDeficitLectures > 0 -> Triple("RED: Deficit ($remaining rem)", VibrantCoral, CoralContainer)
        remaining > 10 -> Triple("YELLOW: Recovery Advised", SolarAmber, AmberContainer)
        else -> Triple("GREEN: On Track", NeonMint, MintContainer)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CourseBadge(courseCode = course.code)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = course.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Instructor: ${course.primaryInstructorName} • ${course.preferredRoom}", color = TextMuted, fontSize = 10.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(riskBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = riskLabel, color = riskColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Stats row: Required, Completed, Cancelled, Remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCounter("Required", "$totalRequired")
                StatCounter("Completed", "$completed")
                StatCounter("Cancelled", "$cancelled")
                StatCounter("Remaining", "$remaining")
                StatCounter("Progress", "$progressPercent%")
            }

            // Progress bar
            LinearProgressIndicator(
                progress = { progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (course.examDeficitLectures > 0) VibrantCoral else ElectricCyan,
                trackColor = SurfaceElevated
            )
        }
    }
}

@Composable
fun StatCounter(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextMuted, fontSize = 9.sp)
    }
}

@Composable
fun FacultyWorkloadCard(teacher: TeacherQualification) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = teacher.teacherName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${teacher.department} • Weekly Limit: ${teacher.maxWeeklyWorkloadHours}h", color = TextMuted, fontSize = 10.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MintContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = "${teacher.currentWeeklyWorkloadHours}h / ${teacher.maxWeeklyWorkloadHours}h (Balanced)", color = MintLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Breakdown of hours (Section 37)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WorkloadChip("Teaching: 10h")
                WorkloadChip("Tutorial: 2h")
                WorkloadChip("Lab: 2h")
                WorkloadChip("Research: 4h")
                WorkloadChip("Meetings: 1h")
            }
        }
    }
}

@Composable
fun WorkloadChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SurfaceElevated)
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(text = label, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun HealthScoreDetailedCard(report: TimetableHealthReport) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TIMETABLE HEALTH REPORT", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("Zero Hard Violations • Living Schedule Status", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MintContainer)
                        .border(1.dp, NeonMint.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(text = "${report.overallScore} / 100", color = MintLight, fontSize = 16.sp, fontWeight = FontWeight.Black)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricScorePill("Hard Constraints", "100%", NeonMint)
                MetricScorePill("Faculty Balance", "${report.facultyBalancePercent}%", MintLight)
                MetricScorePill("Student Balance", "${report.studentBalancePercent}%", MintLight)
                MetricScorePill("Room Util", "${report.roomUtilizationPercent}%", ElectricCyan)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricScorePill("Faculty Prefs", "${report.facultyPreferenceScore}%", SolarAmber)
                MetricScorePill("Stability", "${report.scheduleStabilityPercent}%", CyberPurple)
                MetricScorePill("Syllabus Align", "${report.syllabusAlignmentPercent}%", NeonMint)
            }
        }
    }
}

@Composable
fun MetricScorePill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextMuted, fontSize = 9.sp)
    }
}

@Composable
fun BottleneckAlertCard(type: String, title: String, description: String, severity: String, severityColor: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceElevated,
        border = BorderStroke(1.dp, severityColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = type, color = severityColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(severityColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = severity, color = severityColor, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(text = title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = description, color = TextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
        }
    }
}
