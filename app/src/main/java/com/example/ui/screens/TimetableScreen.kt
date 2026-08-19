package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.AppViewModel

@Composable
fun TimetableScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val studentCohortSessions by viewModel.studentCohortSessions.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val selectedBatch by viewModel.selectedBatch.collectAsState()
    val selectedSubgroup by viewModel.selectedSubgroup.collectAsState()
    val availableBatches by viewModel.availableBatches.collectAsState()
    val availableSubgroups by viewModel.availableSubgroups.collectAsState()
    val studentProfile by viewModel.studentProfile.collectAsState()

    var selectedViewMode by remember { mutableStateOf(0) } // 0 = Weekly Routine Matrix (Full Sheet), 1 = Day-by-Day Agenda
    var showProfileModal by remember { mutableStateOf(false) }

    val dayNames = listOf(
        Pair(1, "Mon"),
        Pair(2, "Tue"),
        Pair(3, "Wed"),
        Pair(4, "Thu"),
        Pair(5, "Fri")
    )

    val currentDaySessions = remember(studentCohortSessions, selectedDay) {
        studentCohortSessions.filter { it.dayOfWeek == selectedDay }.sortedBy { it.periodIndex }
    }

    var selectedSessionForDisruption by remember { mutableStateOf<ClassSession?>(null) }
    var disruptionReason by remember { mutableStateOf("") }
    var showCohortDialog by remember { mutableStateOf(false) }
    var showRoutineStudioModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
    ) {
        // 1. Header with Batch & Subgroup Badge + Studio Upload Button + Profile
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Academic Routine",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${studentProfile.name} • Sem $selectedSemester • $selectedBatch ($selectedSubgroup)",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricCyan)
                            .clickable { viewModel.setTab(AppNavTab.COORDINATOR) }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("upload_routine_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = DeepNavyOnPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "PLANNER",
                                color = DeepNavyOnPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyanContainer)
                            .clickable { showCohortDialog = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("timetable_cohort_chip")
                    ) {
                        Text(
                            text = "$selectedBatch • $selectedSubgroup ▾",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                            .border(1.dp, SurfaceBorder, CircleShape)
                            .clickable { showProfileModal = true }
                            .testTag("timetable_profile_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // View Mode Switcher: Weekly Routine Matrix (Full Sheet) vs Day-by-Day Agenda
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("Weekly Routine Matrix (Full Sheet)", "Day-by-Day Agenda").forEachIndexed { idx, label ->
                    val isSel = selectedViewMode == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) ElectricCyan else Color.Transparent)
                            .clickable { selectedViewMode = idx }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSel) DeepNavyOnPrimary else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Batch Selector Strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SELECT BATCH (${availableBatches.size} BATCHES):",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableBatches) { b ->
                        val isSel = selectedBatch == b
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ElectricCyan else SurfaceDark)
                                .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setSelectedBatch(b) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = b,
                                color = if (isSel) DeepNavyOnPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. Subgroup Selector Strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "SUBGROUP IN $selectedBatch (${availableSubgroups.size} SUBGROUPS):",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(availableSubgroups) { g ->
                        val isSel = selectedSubgroup == g
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CyanContainer else SurfaceDark)
                                .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { viewModel.setSelectedSubgroup(g) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = g,
                                color = if (isSel) ElectricCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (selectedViewMode == 0) {
            // VIEW MODE 0: FULL WEEKLY ROUTINE MATRIX
            item {
                WeeklyRoutineMatrixView(
                    viewModel = viewModel,
                    onSessionClick = { session ->
                        selectedSessionForDisruption = session
                        disruptionReason = "Faculty unavailable"
                    }
                )
            }
        } else {
            // VIEW MODE 1: DAY-BY-DAY AGENDA
            // 4. Day Selector Tabs (Mon to Fri)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                items(dayNames) { (dayIndex, dayLabel) ->
                    val isSelected = selectedDay == dayIndex
                    val sessionCount = studentCohortSessions.count { it.dayOfWeek == dayIndex && it.status != SessionStatus.CANCELLED }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) ElectricCyan else SurfaceDark)
                            .border(1.dp, if (isSelected) ElectricCyan else SurfaceBorder, RoundedCornerShape(14.dp))
                            .clickable { viewModel.setSelectedDay(dayIndex) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .testTag("day_tab_$dayIndex")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = dayLabel,
                                color = if (isSelected) DeepNavyOnPrimary else TextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$sessionCount sessions",
                                color = if (isSelected) DeepNavyOnPrimary.copy(alpha = 0.8f) else TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Day Summary Card
        item {
            val cancelledInDay = studentCohortSessions.count { it.dayOfWeek == selectedDay && it.status == SessionStatus.CANCELLED }
            GlassCard(
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val dayFullName = when (selectedDay) {
                            1 -> "Monday"
                            2 -> "Tuesday"
                            3 -> "Wednesday"
                            4 -> "Thursday"
                            else -> "Friday"
                        }
                        Text(
                            text = "$dayFullName • $selectedBatch-$selectedSubgroup Routine",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (cancelledInDay > 0) "$cancelledInDay cancelled • Slot available in Recovery Marketplace" else "No conflicts • Batch lectures & Subgroup labs synchronized",
                            color = if (cancelledInDay > 0) VibrantCoral else MintLight,
                            fontSize = 12.sp
                        )
                    }

                    if (cancelledInDay > 0) {
                        TextButton(
                            onClick = { viewModel.setTab(AppNavTab.RECOVERY) },
                            modifier = Modifier.testTag("goto_recovery_hub_btn")
                        ) {
                            Text("Recover Slot", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 6. Period-by-Period Timeline
        if (currentDaySessions.isEmpty()) {
            item {
                GlassCard(backgroundColor = SurfaceDark) {
                    Text(
                        text = "No scheduled sessions for this cohort and day.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(currentDaySessions, key = { it.id }) { session ->
                val isCancelled = session.status == SessionStatus.CANCELLED
                val isMakeup = session.sessionType == SessionType.MAKEUP
                val isLab = session.sessionType == SessionType.LAB

                GlassCard(
                    backgroundColor = when {
                        isCancelled -> CoralContainer.copy(alpha = 0.35f)
                        isMakeup -> MintContainer.copy(alpha = 0.35f)
                        else -> SurfaceDark
                    },
                    borderColor = when {
                        isCancelled -> VibrantCoral.copy(alpha = 0.4f)
                        isMakeup -> NeonMint.copy(alpha = 0.4f)
                        else -> SurfaceBorder
                    },
                    modifier = Modifier.testTag("timetable_session_${session.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Period Badge
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isCancelled -> CoralContainer
                                        isMakeup -> MintContainer
                                        else -> CyanContainer
                                    }
                                )
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "P${session.periodIndex}",
                                color = when {
                                    isCancelled -> VibrantCoral
                                    isMakeup -> MintLight
                                    else -> ElectricCyan
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isLab) {
                                Text(
                                    text = "2 Slots",
                                    color = ElectricCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CourseBadge(courseCode = session.courseCode)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SurfaceElevated)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (session.isCommonBatchLecture) "Batch Lecture" else "Subgroup Lab ($selectedSubgroup)",
                                            color = when {
                                                isMakeup -> MintLight
                                                else -> TextSecondary
                                            },
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (isCancelled) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CoralContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "CANCELLED",
                                            color = VibrantCoral,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = session.courseName,
                                color = if (isCancelled) TextMuted else TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (isCancelled) TextDecoration.LineThrough else TextDecoration.None
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PersonOutline,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = session.instructorName,
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = session.timeDisplay,
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Room,
                                        contentDescription = null,
                                        tint = if (isCancelled) TextMuted else ElectricCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${session.roomName} (${session.building})",
                                        color = if (isCancelled) TextMuted else ElectricCyan,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (!isCancelled) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            selectedSessionForDisruption = session
                                            disruptionReason = "Faculty unavailable"
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.testTag("disrupt_session_btn_${session.id}")
                                    ) {
                                        Text("Simulate Cancellation", color = TextMuted, fontSize = 11.sp)
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

    // Student Profile Dialog
    if (showProfileModal) {
        StudentProfileDialog(
            viewModel = viewModel,
            onDismiss = { showProfileModal = false }
        )
    }

    // Cohort Picker Dialog Modal
    if (showCohortDialog) {
        AlertDialog(
            onDismissRequest = { showCohortDialog = false },
            title = { Text("Switch Cohort / Batch", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("SEMESTER:", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items((1..8).toList()) { sem ->
                            val isSel = selectedSemester == sem
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setSelectedSemester(sem) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Sem $sem", color = if (isSel) ElectricCyan else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text("BATCH (${availableBatches.size} BATCHES):", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableBatches) { b ->
                            val isSel = selectedBatch == b
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setSelectedBatch(b) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(b, color = if (isSel) ElectricCyan else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text("SUBGROUP (${availableSubgroups.size} SUBGROUPS):", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableSubgroups) { g ->
                            val isSel = selectedSubgroup == g
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setSelectedSubgroup(g) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(g, color = if (isSel) ElectricCyan else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCohortDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                ) {
                    Text("Apply", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Custom Routine Studio Uploader Dialog
    if (showRoutineStudioModal) {
        RoutineStudioUploadDialog(
            viewModel = viewModel,
            onDismiss = { showRoutineStudioModal = false }
        )
    }

    // Cancellation modal dialog
    selectedSessionForDisruption?.let { session ->
        AlertDialog(
            onDismissRequest = { selectedSessionForDisruption = null },
            title = { Text("Simulate Class Cancellation", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Marking ${session.courseCode} on ${session.timeDisplay} as cancelled will search for cross-cancellation makeup slots with zero hard violations.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = disruptionReason,
                        onValueChange = { disruptionReason = it },
                        label = { Text("Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelClass(session, disruptionReason)
                        selectedSessionForDisruption = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VibrantCoral, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_cancel_class_btn")
                ) {
                    Text("Confirm Cancellation", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSessionForDisruption = null }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
