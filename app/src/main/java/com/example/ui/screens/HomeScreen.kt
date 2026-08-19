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
import androidx.compose.ui.graphics.Brush
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsState()
    val studentSessions by viewModel.studentCohortSessions.collectAsState()
    val studentProfile by viewModel.studentProfile.collectAsState()
    val makeups by viewModel.pendingMakeups.collectAsState()
    val syncState by viewModel.syncEngine.syncState.collectAsState()
    val isOffline by viewModel.syncEngine.isOfflineMode.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val selectedBatch by viewModel.selectedBatch.collectAsState()
    val selectedSubgroup by viewModel.selectedSubgroup.collectAsState()
    val availableBatches by viewModel.availableBatches.collectAsState()
    val availableSubgroups by viewModel.availableSubgroups.collectAsState()

    val todaySessions = remember(studentSessions) {
        studentSessions.filter { it.dayOfWeek == 1 }
    }
    val nextClass = remember(todaySessions) {
        todaySessions.firstOrNull { it.status != SessionStatus.CANCELLED }
    }
    val pendingTasks = remember(tasks) {
        tasks.filter { !it.isCompleted }.take(4)
    }

    var selectedSessionForDisruption by remember { mutableStateOf<ClassSession?>(null) }
    var disruptionReason by remember { mutableStateOf("") }
    var showBatchSelectorModal by remember { mutableStateOf(false) }
    var showRoutineStudioModal by remember { mutableStateOf(false) }
    var showProfileModal by remember { mutableStateOf(false) }
    var showWeeklyRoutineModal by remember { mutableStateOf(false) }

    val formattedDate = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // 1. Cyber Midnight Header with Electric Glow Avatar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hi, ${studentProfile.name.split(" ").firstOrNull() ?: "Alex"}",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = formattedDate,
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SyncStatusChip(
                        syncStatus = syncState,
                        isOfflineMode = isOffline,
                        onToggleOffline = { viewModel.syncEngine.toggleOfflineMode() },
                        onForceSync = { viewModel.syncEngine.triggerSync() }
                    )

                    // Profile Avatar Circle with Electric Glow (Clickable to open Student Profile)
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, ElectricCyan.copy(alpha = 0.8f), CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(ElectricCyan, NeonMint)
                                )
                            )
                            .clickable { showProfileModal = true }
                            .testTag("home_profile_avatar"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (studentProfile.name.isNotBlank()) studentProfile.name.first().uppercase() else "A",
                            color = DeepNavyOnPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // 2. Academic Cohort Selector Bar (Semester + Batch + Subgroup)
        item {
            GlassCard(
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder,
                shape = RoundedCornerShape(16.dp),
                onClick = { showBatchSelectorModal = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Semester $selectedSemester • B.Tech CSE",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Batch $selectedBatch • Subgroup $selectedSubgroup (1 of 10 in $selectedBatch)",
                                color = ElectricCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Switch",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Quick Actions: Weekly Routine Matrix & Routine Studio
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Weekly Routine Matrix Card
                GlassCard(
                    backgroundColor = SurfaceDark,
                    borderColor = NeonMint.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    onClick = { showWeeklyRoutineModal = true }
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MintContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarViewWeek,
                                    contentDescription = "Weekly Routine",
                                    tint = NeonMint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Weekly Matrix",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Full Week Routine Sheet",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                // Upload & Auto-Routine Studio Card
                GlassCard(
                    backgroundColor = SurfaceDark,
                    borderColor = ElectricCyan.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.setTab(AppNavTab.COORDINATOR) }
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyanContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Planner Studio",
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Planner Studio",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Auto-Solver & Rooms",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Next Class Spotlight Hero Banner (Cyber Cyan Signature Card)
        if (nextClass != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ElectricCyan, Color(0xFF00C6FF))
                            )
                        )
                        .padding(20.dp)
                        .testTag("next_class_hero_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (nextClass.isCommonBatchLecture) "NEXT CLASS • COMMON BATCH" else "NEXT CLASS • SUBGROUP LAB",
                            color = DeepNavyOnPrimary.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DeepNavyOnPrimary)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = nextClass.timeDisplay,
                                color = ElectricCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${nextClass.courseCode}: ${nextClass.courseName}",
                        color = DeepNavyOnPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = nextClass.building,
                            color = DeepNavyOnPrimary.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(DeepNavyOnPrimary.copy(alpha = 0.4f))
                        )
                        Text(
                            text = nextClass.roomName,
                            color = DeepNavyOnPrimary.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(DeepNavyOnPrimary.copy(alpha = 0.4f))
                        )
                        Text(
                            text = nextClass.instructorName,
                            color = DeepNavyOnPrimary.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 4. Self-Healing Makeup Alert (if any pending)
        if (makeups.isNotEmpty()) {
            val topMakeup = makeups.first()
            item {
                GlassCard(
                    backgroundColor = SurfaceDark,
                    borderColor = ElectricCyan.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { viewModel.setTab(AppNavTab.RECOVERY) },
                    modifier = Modifier.testTag("home_recovery_banner")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(CyanContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "CROSS-CANCELLATION RECOVERY",
                                    color = ElectricCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "${topMakeup.courseCode} ${topMakeup.courseName}",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MintContainer)
                                .border(1.dp, NeonMint.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${topMakeup.compatibilityScore}% MATCH",
                                color = MintLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Freed slot matched: Recover on ${topMakeup.targetDayName} at ${topMakeup.targetTimeSlot} in ${topMakeup.targetRoom}.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.acceptMakeup(topMakeup) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricCyan,
                                contentColor = DeepNavyOnPrimary
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("banner_accept_makeup_button")
                        ) {
                            Text("Accept Slot", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.voteMakeup(topMakeup) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderStrong),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("banner_vote_makeup_button")
                        ) {
                            Text(
                                "Vote (${topMakeup.votesCount}/${topMakeup.totalStudents})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 5. Priority Tasks Section
        item {
            SectionHeader(
                title = "Priority Tasks",
                actionLabel = "View All",
                onActionClick = { viewModel.setTab(AppNavTab.TASKS) }
            )
        }

        if (pendingTasks.isEmpty()) {
            item {
                GlassCard(
                    backgroundColor = SurfaceDark,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "All priority academic tasks completed.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(pendingTasks, key = { it.id }) { task ->
                val isCompleted = task.isCompleted
                val now = System.currentTimeMillis()
                val dueDiffHours = (task.dueDateMillis - now) / (1000 * 3600)

                val dueText = when {
                    isCompleted -> "Completed"
                    dueDiffHours < 0 -> "Overdue"
                    dueDiffHours < 24 -> "Due by 11:59 PM"
                    dueDiffHours < 48 -> "Tomorrow, 9:00 AM"
                    else -> "Due in ${dueDiffHours / 24} days"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCompleted) SurfaceDark.copy(alpha = 0.4f) else SurfaceDark)
                        .border(
                            1.dp,
                            if (task.priority == Priority.URGENT && !isCompleted) VibrantCoral.copy(alpha = 0.5f) else SurfaceBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.toggleTask(task) }
                        .padding(14.dp)
                        .testTag("home_task_item_${task.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Square Checkbox with Electric Glow
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCompleted) ElectricCyan else Color.Transparent)
                            .border(
                                2.dp,
                                if (isCompleted) ElectricCyan else ElectricCyan.copy(alpha = 0.8f),
                                RoundedCornerShape(6.dp)
                            )
                            .testTag("task_checkbox_${task.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = DeepNavyOnPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            color = if (isCompleted) TextMuted else TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )
                        Text(
                            text = dueText,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    PriorityBadge(priority = task.priority)
                }
            }
        }

        // 6. Today's Academic Routine Section
        item {
            SectionHeader(
                title = "Today's Routine ($selectedBatch • $selectedSubgroup)",
                subtitle = "50-min periods • Common Lectures & Subgroup Labs",
                actionLabel = "Full Week",
                onActionClick = { viewModel.setTab(AppNavTab.TIMETABLE) }
            )
        }

        items(todaySessions, key = { it.id }) { session ->
            val isCancelled = session.status == SessionStatus.CANCELLED
            GlassCard(
                backgroundColor = if (isCancelled) CoralContainer.copy(alpha = 0.3f) else SurfaceDark,
                borderColor = if (isCancelled) VibrantCoral.copy(alpha = 0.4f) else SurfaceBorder,
                modifier = Modifier.testTag("session_item_${session.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCancelled) CoralContainer else CyanContainer)
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "P${session.periodIndex}",
                                color = if (isCancelled) VibrantCoral else ElectricCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = session.courseCode,
                                    color = if (isCancelled) TextMuted else TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = if (isCancelled) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SurfaceElevated)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (session.isCommonBatchLecture) "Batch Lecture" else "Lab ($selectedSubgroup)",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Text(
                                text = "${session.courseName} • ${session.instructorName}",
                                color = TextMuted,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = session.timeDisplay,
                            color = if (isCancelled) TextMuted else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = session.roomName,
                            color = if (isCancelled) VibrantCoral else ElectricCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                if (isCancelled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ ${session.cancellationReason ?: "Cancelled. Recovery opportunity pending."}",
                        color = VibrantCoral,
                        fontSize = 11.sp
                    )
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                selectedSessionForDisruption = session
                                disruptionReason = "Faculty unavailable (Emergency/Conference)"
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("report_disruption_${session.id}")
                        ) {
                            Text("Simulate Disruption", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Cohort & Batch Selector Modal
    if (showBatchSelectorModal) {
        AlertDialog(
            onDismissRequest = { showBatchSelectorModal = false },
            title = {
                Text(
                    text = "Select Academic Cohort",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "SEMESTER (1 TO 8):",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items((1..8).toList()) { sem ->
                            val isSel = selectedSemester == sem
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setSelectedSemester(sem) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Sem $sem",
                                    color = if (isSel) ElectricCyan else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "BATCH (${availableBatches.size} BATCHES):",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableBatches) { b ->
                            val isSel = selectedBatch == b
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setSelectedBatch(b) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = b,
                                    color = if (isSel) ElectricCyan else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "SUBGROUP (${availableSubgroups.size} SUBGROUPS IN $selectedBatch):",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(availableSubgroups) { g ->
                            val isSel = selectedSubgroup == g
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setSelectedSubgroup(g) }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = g,
                                    color = if (isSel) ElectricCyan else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showBatchSelectorModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Custom Routine Studio Upload Dialog
    if (showRoutineStudioModal) {
        RoutineStudioUploadDialog(
            viewModel = viewModel,
            onDismiss = { showRoutineStudioModal = false }
        )
    }

    // Student Profile Dialog
    if (showProfileModal) {
        StudentProfileDialog(
            viewModel = viewModel,
            onDismiss = { showProfileModal = false }
        )
    }

    // Full Week Routine Matrix Sheet Dialog
    if (showWeeklyRoutineModal) {
        FullWeekRoutineSheetDialog(
            viewModel = viewModel,
            onDismiss = { showWeeklyRoutineModal = false },
            onSessionClick = { session ->
                selectedSessionForDisruption = session
            }
        )
    }

    // Disruption Dialog Modal
    selectedSessionForDisruption?.let { session ->
        AlertDialog(
            onDismissRequest = { selectedSessionForDisruption = null },
            title = { Text("Report / Simulate Class Disruption", color = TextPrimary) },
            text = {
                Column {
                    Text(
                        text = "Cancelling ${session.courseCode} (${session.courseName}) on Monday ${session.timeDisplay} will trigger the Self-Healing Recovery Engine.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = disruptionReason,
                        onValueChange = { disruptionReason = it },
                        label = { Text("Disruption Reason") },
                        modifier = Modifier.fillMaxWidth().testTag("disruption_reason_input")
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
                    modifier = Modifier.testTag("confirm_disruption_button")
                ) {
                    Text("Trigger Cancellation", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSessionForDisruption = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
