package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ClassSession
import com.example.data.model.SessionStatus
import com.example.data.model.SessionType
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

data class PeriodDefinition(
    val periodIndex: Int,
    val label: String,
    val timeSlot: String,
    val isLunch: Boolean = false
)

val PERIOD_SLOTS = listOf(
    PeriodDefinition(1, "P1", "08:30 - 09:20"),
    PeriodDefinition(2, "P2", "09:30 - 10:20"),
    PeriodDefinition(3, "P3", "10:30 - 11:20"),
    PeriodDefinition(4, "P4", "11:30 - 12:20"),
    PeriodDefinition(-1, "LUNCH", "12:20 - 01:20", isLunch = true),
    PeriodDefinition(5, "P5", "01:20 - 02:10"),
    PeriodDefinition(6, "P6", "02:15 - 03:05"),
    PeriodDefinition(7, "P7", "03:10 - 04:00"),
    PeriodDefinition(8, "P8", "04:00 - 04:50")
)

val WEEK_DAYS = listOf(
    1 to "Monday",
    2 to "Tuesday",
    3 to "Wednesday",
    4 to "Thursday",
    5 to "Friday"
)

@Composable
fun WeeklyRoutineMatrixView(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
    onSessionClick: (ClassSession) -> Unit = {}
) {
    val studentSessions by viewModel.studentCohortSessions.collectAsState()
    val studentProfile by viewModel.studentProfile.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val selectedBatch by viewModel.selectedBatch.collectAsState()
    val selectedSubgroup by viewModel.selectedSubgroup.collectAsState()

    var showFullImageDialog by remember { mutableStateOf(false) }
    val horizontalScrollState = rememberScrollState()

    // Calculate routine stats
    val totalWeeklyClasses = studentSessions.count { it.status != SessionStatus.CANCELLED }
    val totalLabs = studentSessions.count { it.sessionType == SessionType.LAB && it.status != SessionStatus.CANCELLED }
    val totalLectures = studentSessions.count { it.sessionType == SessionType.LECTURE && it.status != SessionStatus.CANCELLED }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Matrix Header & Action Bar
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, SurfaceBorderStrong),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(ElectricCyan.copy(alpha = 0.08f), Color.Transparent),
                                center = Offset(size.width * 0.9f, 0f),
                                radius = size.width * 0.4f
                            )
                        )
                    }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Weekly Routine Matrix",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanContainer)
                                    .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Sem $selectedSemester • $selectedBatch ($selectedSubgroup)",
                                    color = ElectricCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "$totalWeeklyClasses sessions ($totalLectures lectures • $totalLabs practical labs)",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = { showFullImageDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = DeepNavyOnPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("full_image_routine_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AspectRatio, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Full Sheet", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }
            }
        }

        // 2. Horizontal Scrollable Routine Matrix Table
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            border = BorderStroke(1.dp, SurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .horizontalScroll(horizontalScrollState)
            ) {
                // Table Header Row (Periods)
                Row(
                    modifier = Modifier.padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Day column header
                    Box(
                        modifier = Modifier
                            .width(86.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "DAY / TIME",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Period columns header
                    PERIOD_SLOTS.forEach { slot ->
                        val colWidth = if (slot.isLunch) 60.dp else 130.dp
                        Box(
                            modifier = Modifier
                                .width(colWidth)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (slot.isLunch) CoralContainer.copy(alpha = 0.25f) else SurfaceElevated)
                                .border(1.dp, if (slot.isLunch) CoralLight.copy(alpha = 0.3f) else SurfaceBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = slot.label,
                                    color = if (slot.isLunch) CoralLight else ElectricCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = slot.timeSlot.split("-").first().trim(),
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                // Table Day Rows (Monday through Friday)
                WEEK_DAYS.forEach { (dayIndex, dayName) ->
                    val daySessions = studentSessions.filter { it.dayOfWeek == dayIndex }

                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Day Label
                        Box(
                            modifier = Modifier
                                .width(86.dp)
                                .height(78.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MidnightBackground)
                                .border(1.dp, SurfaceBorderStrong, RoundedCornerShape(10.dp))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayName.take(3).uppercase(),
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "${daySessions.size} classes",
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Period Cells for this Day
                        var skipNextDueToTwoHourSlot = false

                        PERIOD_SLOTS.forEachIndexed { _, slot ->
                            if (skipNextDueToTwoHourSlot) {
                                skipNextDueToTwoHourSlot = false
                                return@forEachIndexed
                            }

                            if (slot.isLunch) {
                                // Lunch Break Column
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(78.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceElevated.copy(alpha = 0.4f))
                                        .border(1.dp, SurfaceBorder.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "🍱\nBreak",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                return@forEachIndexed
                            }

                            // Find session matching this period
                            val session = daySessions.find {
                                it.periodIndex == slot.periodIndex ||
                                        (it.durationSlots > 1 && (it.periodIndex until (it.periodIndex + it.durationSlots)).contains(slot.periodIndex))
                            }

                            val isLabStart = session?.sessionType == SessionType.LAB && session.periodIndex == slot.periodIndex
                            val cellWidth = if (isLabStart && session.durationSlots == 2) 266.dp else 130.dp

                            if (isLabStart && session.durationSlots == 2) {
                                skipNextDueToTwoHourSlot = true
                            }

                            if (session != null) {
                                RoutineGridCell(
                                    session = session,
                                    width = cellWidth,
                                    onClick = { onSessionClick(session) }
                                )
                            } else {
                                // Free Slot
                                Box(
                                    modifier = Modifier
                                        .width(cellWidth)
                                        .height(78.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceElevated.copy(alpha = 0.25f))
                                        .border(1.dp, SurfaceBorder.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Free Slot",
                                        color = TextMuted.copy(alpha = 0.5f),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }
            }
        }

        // 3. Matrix Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem("Lecture", CyanContainer, ElectricCyan)
            LegendItem("2-Hr Lab", MintContainer, NeonMint)
            LegendItem("Tutorial", PurpleContainer, CyberPurple)
            LegendItem("Cancelled", CoralContainer, CoralLight)
        }
    }

    // Fullsheet Image/Table Dialog
    if (showFullImageDialog) {
        FullWeekRoutineSheetDialog(
            viewModel = viewModel,
            onDismiss = { showFullImageDialog = false },
            onSessionClick = onSessionClick
        )
    }
}

@Composable
private fun RoutineGridCell(
    session: ClassSession,
    width: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    val isCancelled = session.status == SessionStatus.CANCELLED
    val isLab = session.sessionType == SessionType.LAB
    val isTut = session.sessionType == SessionType.TUTORIAL

    val (bgColor, borderColor, accentColor) = when {
        isCancelled -> Triple(CoralContainer, CoralLight, CoralLight)
        isLab -> Triple(MintContainer, NeonMint, NeonMint)
        isTut -> Triple(PurpleContainer, CyberPurple, CyberPurple)
        else -> Triple(CyanContainer, ElectricCyan, ElectricCyan)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.7f)),
        modifier = Modifier
            .width(width)
            .height(78.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.courseCode,
                    color = accentColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceDark.copy(alpha = 0.85f))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = if (isCancelled) "CANCELLED" else if (isLab) "2-HR LAB" else if (isTut) "TUT" else "LEC",
                        color = if (isCancelled) CoralLight else accentColor,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = session.courseName,
                color = TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.roomName,
                    color = TextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = session.instructorName.split(" ").lastOrNull() ?: "",
                    color = TextMuted,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, bg: Color, border: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(bg)
                .border(1.dp, border, RoundedCornerShape(3.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, color = TextMuted, fontSize = 10.sp)
    }
}

@Composable
fun FullWeekRoutineSheetDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit,
    onSessionClick: (ClassSession) -> Unit
) {
    val studentSessions by viewModel.studentCohortSessions.collectAsState()
    val studentProfile by viewModel.studentProfile.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val selectedBatch by viewModel.selectedBatch.collectAsState()
    val selectedSubgroup by viewModel.selectedSubgroup.collectAsState()

    val horizontalScrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MidnightBackground),
            color = MidnightBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar with Close and Student Identity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Weekly Timetable Sheet",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyanContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Sem $selectedSemester • $selectedBatch • $selectedSubgroup",
                                    color = ElectricCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Student: ${studentProfile.name} • Roll: ${studentProfile.rollNumber} • ${studentProfile.department}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Full Scrollable Routine Grid Table
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, SurfaceBorderStrong),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        // Header Row
                        Row(
                            modifier = Modifier.padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(95.dp)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SurfaceElevated)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("DAY / PERIOD", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            PERIOD_SLOTS.forEach { slot ->
                                val w = if (slot.isLunch) 64.dp else 145.dp
                                Box(
                                    modifier = Modifier
                                        .width(w)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (slot.isLunch) CoralContainer.copy(alpha = 0.25f) else SurfaceElevated)
                                        .border(1.dp, if (slot.isLunch) CoralLight.copy(alpha = 0.4f) else SurfaceBorder, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = slot.label,
                                            color = if (slot.isLunch) CoralLight else ElectricCyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = slot.timeSlot, color = TextMuted, fontSize = 9.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }

                        // Day Rows
                        WEEK_DAYS.forEach { (dayIdx, dayName) ->
                            val daySessions = studentSessions.filter { it.dayOfWeek == dayIdx }

                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Day Column
                                Box(
                                    modifier = Modifier
                                        .width(95.dp)
                                        .height(84.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MidnightBackground)
                                        .border(1.dp, SurfaceBorderStrong, RoundedCornerShape(10.dp))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dayName.take(3).uppercase(),
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "${daySessions.size} Classes",
                                            color = ElectricCyan,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                var skipNext = false

                                PERIOD_SLOTS.forEachIndexed { _, slot ->
                                    if (skipNext) {
                                        skipNext = false
                                        return@forEachIndexed
                                    }

                                    if (slot.isLunch) {
                                        Box(
                                            modifier = Modifier
                                                .width(64.dp)
                                                .height(84.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(SurfaceElevated.copy(alpha = 0.5f))
                                                .border(1.dp, SurfaceBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "🍱\nLUNCH\nBREAK",
                                                color = TextMuted,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 12.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        return@forEachIndexed
                                    }

                                    val session = daySessions.find {
                                        it.periodIndex == slot.periodIndex ||
                                                (it.durationSlots > 1 && (it.periodIndex until (it.periodIndex + it.durationSlots)).contains(slot.periodIndex))
                                    }

                                    val isLab = session?.sessionType == SessionType.LAB && session.periodIndex == slot.periodIndex
                                    val cellW = if (isLab && session.durationSlots == 2) 298.dp else 145.dp

                                    if (isLab && session.durationSlots == 2) {
                                        skipNext = true
                                    }

                                    if (session != null) {
                                        val isCancelled = session.status == SessionStatus.CANCELLED
                                        val (bg, bdr, accent) = when {
                                            isCancelled -> Triple(CoralContainer, CoralLight, CoralLight)
                                            session.sessionType == SessionType.LAB -> Triple(MintContainer, NeonMint, NeonMint)
                                            session.sessionType == SessionType.TUTORIAL -> Triple(PurpleContainer, CyberPurple, CyberPurple)
                                            else -> Triple(CyanContainer, ElectricCyan, ElectricCyan)
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = bg,
                                            border = BorderStroke(1.dp, bdr.copy(alpha = 0.8f)),
                                            modifier = Modifier
                                                .width(cellW)
                                                .height(84.dp)
                                                .clickable { onSessionClick(session) }
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp),
                                                verticalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = session.courseCode,
                                                        color = accent,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(SurfaceDark.copy(alpha = 0.85f))
                                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = if (isCancelled) "CANCELLED" else if (session.sessionType == SessionType.LAB) "2-HR LAB" else if (session.sessionType == SessionType.TUTORIAL) "TUT" else "LEC",
                                                            color = if (isCancelled) CoralLight else accent,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }

                                                Text(
                                                    text = session.courseName,
                                                    color = TextPrimary,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(11.dp))
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(text = session.roomName, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(11.dp))
                                                        Spacer(modifier = Modifier.width(3.dp))
                                                        Text(text = session.instructorName.split(" ").lastOrNull() ?: "", color = TextMuted, fontSize = 10.sp)
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .width(cellW)
                                                .height(84.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(SurfaceElevated.copy(alpha = 0.25f))
                                                .border(1.dp, SurfaceBorder.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = "Free Slot", color = TextMuted.copy(alpha = 0.5f), fontSize = 10.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
