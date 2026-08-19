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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MakeupOpportunity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@Composable
fun RecoveryHubScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val pendingMakeups by viewModel.pendingMakeups.collectAsState()

    var selectedForExplanation by remember { mutableStateOf<MakeupOpportunity?>(null) }
    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Queue & Cross-Matching, 1: Marketplace & Teacher Control, 2: CR Polling, 3: Substitute Faculty

    // Interactive Demo States
    var showAnnounceSlotDialog by remember { mutableStateOf(false) }
    var showSubstituteFinderDialog by remember { mutableStateOf(false) }
    var selectedAbsentTeacher by remember { mutableStateOf("Prof. Amit Gupta") }

    // Teacher Protected Time States
    var teacherProtectedSlots by remember {
        mutableStateOf(
            listOf(
                "Wednesday 02:15 - 04:00" to "Research & Thesis Review",
                "Friday 12:20 - 01:20" to "Departmental Faculty Meeting",
                "Daily 01:20 - 02:10" to "Office Visiting Hours"
            )
        )
    }

    // Student Polling State
    var pollVotes by remember {
        mutableStateOf(
            listOf(
                "Monday 03:10 - 04:00" to 12,
                "Tuesday 02:15 - 03:05" to 41,
                "Wednesday 04:00 - 04:50" to 8
            )
        )
    }
    var userVotedOption by remember { mutableStateOf<String?>("Tuesday 02:15 - 03:05") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
    ) {
        // 1. Header & Engine Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Self-Healing Recovery",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Dynamic Cross-Cancellation & Rescheduling Engine",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MintContainer,
                    border = BorderStroke(1.dp, NeonMint.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonMint))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("Active Engine", color = MintLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Navigation Sub-Tabs
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
                    "Recovery Queue" to 0,
                    "Free Marketplace" to 1,
                    "Student Polls" to 2,
                    "Substitute Faculty" to 3
                ).forEach { (label, idx) ->
                    val isSel = activeSubTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) ElectricCyan else Color.Transparent)
                            .clickable { activeSubTab = idx }
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
        when (activeSubTab) {
            0 -> {
                // =========================================================================
                // TAB 0: SELF-HEALING RECOVERY QUEUE & CROSS-CANCELLATION MATCHING
                // =========================================================================
                item {
                    // Cross-Cancellation Matching Concept Visualizer Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(ElectricCyan.copy(alpha = 0.08f), Color.Transparent),
                                            center = Offset(size.width * 0.85f, 0f),
                                            radius = size.width * 0.5f
                                        )
                                    )
                                }
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.AutoMode, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Cross-Cancellation Algorithm", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text("96% Compatibility", color = NeonMint, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = "When Teacher A cancels Monday 8–9 (DBMS) and Teacher B cancels Thursday 11–12 (OS), the engine verifies student cohort availability, faculty readiness, and room capacity to match the pending makeup automatically.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )

                                // Weighted Compatibility Score Breakdown (Section 26 & 59)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = SurfaceElevated,
                                    border = BorderStroke(1.dp, SurfaceBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "RECOVERY SCORE FORMULA: 30% T + 25% S + 15% R + 15% A + 10% P + 5% Stab",
                                            color = ElectricCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            ScorePill("Faculty Avail", "30%", NeonMint)
                                            ScorePill("Student Avail", "25%", NeonMint)
                                            ScorePill("Room Suitability", "15%", ElectricCyan)
                                            ScorePill("Syllabus Urgency", "15%", SolarAmber)
                                            ScorePill("Stability", "5%", CyberPurple)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pending Makeup Queue (${pendingMakeups.size})",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = {
                                if (pendingMakeups.isNotEmpty()) {
                                    viewModel.acceptMakeup(pendingMakeups.first())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Auto-Match Eligible", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (pendingMakeups.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SurfaceDark,
                            border = BorderStroke(1.dp, SurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.CheckCircleOutline, contentDescription = null, tint = MintLight, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("All Cancelled Classes Recovered", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("No pending makeup tasks in the system queue.", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    items(pendingMakeups, key = { it.id }) { opp ->
                        MakeupCard(
                            opportunity = opp,
                            onAccept = { viewModel.acceptMakeup(opp) },
                            onReject = { viewModel.rejectMakeup(opp) },
                            onVote = { viewModel.voteMakeup(opp) },
                            onExplain = { selectedForExplanation = opp }
                        )
                    }
                }
            }

            1 -> {
                // =========================================================================
                // TAB 1: FREE-SLOT MARKETPLACE & TEACHER CONTROL (Section 28 & 29)
                // =========================================================================
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorderStrong),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Free-Slot Marketplace", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Faculty voluntary availability announcements", color = TextMuted, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { showAnnounceSlotDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Announce Slot", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Active Marketplace Opportunities
                            MarketplaceItem(
                                timeSlot = "Thursday 11:30 - 12:20 (Period 4)",
                                freedBy = "Freed by OS Lecture cancellation",
                                matchedOption = "DBMS Makeup (CSE-A) • 96% Match",
                                altOptions = "Tutorial (CSE-B) • Doubt Session • Project Mentoring",
                                room = "LH-101 (Academic Block A)"
                            )

                            MarketplaceItem(
                                timeSlot = "Friday 02:15 - 03:05 (Period 6)",
                                freedBy = "Freed by Dept Seminar rescheduling",
                                matchedOption = "Enterprise Web Lab Makeup • 92% Match",
                                altOptions = "Remedial Coding Lab • Cloud DevOps Demo",
                                room = "OS & Linux Lab (Computing Center)"
                            )
                        }
                    }
                }

                item {
                    // Teacher Control Section (Section 29: Free != Available for teaching)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = SolarAmber, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Teacher Control & Protected Time", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "The platform never assumes free periods are available for teaching. Teachers can mark protected slots:",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            teacherProtectedSlots.forEach { (slot, reason) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SurfaceElevated)
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = slot, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = reason, color = SolarAmber, fontSize = 10.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AmberContainer)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Protected 🔒", color = AmberLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // =========================================================================
                // TAB 2: CR STUDENT REQUESTS & POLLING SYSTEM (Sections 30 & 31)
                // =========================================================================
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorderStrong),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Class Representative Demand", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Aggregated student cohort requests", color = TextMuted, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(CyanContainer)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text("47/52 Students Free", color = ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "Students do not individually spam faculty. CR aggregates availability for CSE-A and initiates democratic timetable voting.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )

                            HorizontalDivider(color = SurfaceBorder)

                            Text("ACTIVE MAKEUP TIME POLL (DBMS CSE-A)", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                            pollVotes.forEach { (slot, count) ->
                                val isVoted = userVotedOption == slot
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isVoted) CyanContainer else SurfaceElevated,
                                    border = BorderStroke(1.dp, if (isVoted) ElectricCyan else SurfaceBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            userVotedOption = slot
                                            pollVotes = pollVotes.map { if (it.first == slot) it.first to (it.second + 1) else it }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            RadioButton(
                                                selected = isVoted,
                                                onClick = null,
                                                colors = RadioButtonDefaults.colors(selectedColor = ElectricCyan)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Column {
                                                Text(text = slot, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                if (slot.contains("Tuesday")) {
                                                    Text("⭐ System Recommended (Highest Availability)", color = MintLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Text(
                                            text = "$count Votes",
                                            color = if (isVoted) ElectricCyan else TextSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // =========================================================================
                // TAB 3: SUBSTITUTE FACULTY ENGINE (Section 32)
                // =========================================================================
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SurfaceDark,
                        border = BorderStroke(1.dp, SurfaceBorderStrong),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Substitute Faculty Engine", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text("Automated matching for absent instructors", color = TextMuted, fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { showSubstituteFinderDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Find Substitute", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "Evaluates subject qualifications, workload limits, timetable conflicts, and student cohort compatibility to rank viable substitutes.",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            // Ranked Substitute Cards
                            SubstituteCandidateCard(
                                teacherName = "Dr. Anita Rao",
                                department = "CSE (Professor)",
                                matchScore = 95,
                                qualifiedSubjects = "DBMS, Machine Learning, Soft Engg",
                                workloadStatus = "14/18 hrs (Available)",
                                isTopRecommendation = true
                            )

                            SubstituteCandidateCard(
                                teacherName = "Prof. Vikram Malhotra",
                                department = "CSE (Associate Professor)",
                                matchScore = 88,
                                qualifiedSubjects = "Enterprise Web, Data Structures",
                                workloadStatus = "15/18 hrs (Available)",
                                isTopRecommendation = false
                            )

                            SubstituteCandidateCard(
                                teacherName = "Dr. Sneha Verma",
                                department = "CSE (Assistant Professor)",
                                matchScore = 82,
                                qualifiedSubjects = "DBMS Lab, Python Programming",
                                workloadStatus = "16/18 hrs (Available)",
                                isTopRecommendation = false
                            )
                        }
                    }
                }
            }
        }
    }

    // Diagnostics Dialog
    selectedForExplanation?.let { opp ->
        ExplainableSchedulingDialog(
            opportunity = opp,
            onDismiss = { selectedForExplanation = null },
            onAccept = {
                viewModel.acceptMakeup(opp)
                selectedForExplanation = null
            }
        )
    }

    // Announce Free Slot Dialog
    if (showAnnounceSlotDialog) {
        AlertDialog(
            onDismissRequest = { showAnnounceSlotDialog = false },
            title = { Text("Announce Free Teaching Slot", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Slot: Thursday 11:30 - 12:20 (Period 4)", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Designate available activity type:", color = TextSecondary, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(selected = true, onClick = {}, label = { Text("Makeup Class", fontSize = 10.sp) })
                        FilterChip(selected = false, onClick = {}, label = { Text("Tutorial", fontSize = 10.sp) })
                        FilterChip(selected = false, onClick = {}, label = { Text("Doubt Session", fontSize = 10.sp) })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAnnounceSlotDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                ) { Text("Publish to Marketplace") }
            },
            dismissButton = {
                TextButton(onClick = { showAnnounceSlotDialog = false }) { Text("Cancel", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }

    // Substitute Finder Dialog
    if (showSubstituteFinderDialog) {
        AlertDialog(
            onDismissRequest = { showSubstituteFinderDialog = false },
            title = { Text("Find Qualified Substitute", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Absent Instructor: Prof. Amit Gupta (Software Engg)", color = SolarAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Top algorithm-matched substitute: Dr. Anita Rao (95% subject qualification match, no slot clashes).", color = TextSecondary, fontSize = 11.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSubstituteFinderDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary)
                ) { Text("Assign Dr. Anita Rao") }
            },
            dismissButton = {
                TextButton(onClick = { showSubstituteFinderDialog = false }) { Text("Close", color = TextSecondary) }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun ScorePill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextMuted, fontSize = 8.sp)
    }
}

@Composable
fun MarketplaceItem(timeSlot: String, freedBy: String, matchedOption: String, altOptions: String, room: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceElevated,
        border = BorderStroke(1.dp, SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = timeSlot, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = room, color = ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = freedBy, color = MintLight, fontSize = 10.sp)
            Text(text = "Top Match: $matchedOption", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Alternatives: $altOptions", color = TextMuted, fontSize = 9.sp)
        }
    }
}

@Composable
fun SubstituteCandidateCard(teacherName: String, department: String, matchScore: Int, qualifiedSubjects: String, workloadStatus: String, isTopRecommendation: Boolean) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = SurfaceElevated,
        border = BorderStroke(1.dp, if (isTopRecommendation) ElectricCyan else SurfaceBorder),
        modifier = Modifier.fillMaxWidth()
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
                    Text(text = teacherName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    if (isTopRecommendation) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyanContainer)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text("Top Rank", color = ElectricCyan, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(text = "$department • Workload: $workloadStatus", color = TextMuted, fontSize = 10.sp)
                Text(text = "Qualified: $qualifiedSubjects", color = TextSecondary, fontSize = 9.sp)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MintContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(text = "$matchScore% Match", color = MintLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ExplainableSchedulingDialog(
    opportunity: MakeupOpportunity,
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = NeonMint, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Explainable Recovery Audit", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Candidate: ${opportunity.targetDayName} ${opportunity.targetTimeSlot} (${opportunity.courseCode})",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider(color = SurfaceBorder)

                // 5 Questions UX Principle (Section 69 of Document)
                Text("EXPLAINABLE QUESTIONS AUDIT:", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)

                QuestionAuditRow("1. What happened?", "Class was cancelled due to instructor absence.")
                QuestionAuditRow("2. Why?", "Approved departmental medical leave.")
                QuestionAuditRow("3. What is affected?", "CSE-A cohort and Room 204.")
                QuestionAuditRow("4. What can we do?", "Matched 3 recovery opportunities in freed slots.")
                QuestionAuditRow("5. What is best?", "${opportunity.targetDayName} ${opportunity.targetTimeSlot} (${opportunity.compatibilityScore}% Compatibility).")
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Confirm & Schedule", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        containerColor = SurfaceDark
    )
}

@Composable
fun QuestionAuditRow(question: String, answer: String) {
    Column {
        Text(text = question, color = ElectricCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = answer, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun MakeupCard(
    opportunity: MakeupOpportunity,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onVote: () -> Unit,
    onExplain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SurfaceDark,
        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("makeup_card_${opportunity.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CourseBadge(courseCode = opportunity.courseCode)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = opportunity.courseName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MintContainer)
                        .border(1.dp, NeonMint.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${opportunity.compatibilityScore}% MATCH",
                        color = MintLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("PROPOSED TIME SLOT", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${opportunity.targetDayName} • ${opportunity.targetTimeSlot}",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("ALLOCATED ROOM", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = opportunity.targetRoom,
                        color = ElectricCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = opportunity.conflictReason,
                color = TextSecondary,
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = DeepNavyOnPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("accept_makeup_btn_${opportunity.id}")
                ) {
                    Text("Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onVote,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SurfaceBorderStrong),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("vote_makeup_btn_${opportunity.id}")
                ) {
                    Text("Vote (${opportunity.votesCount})", fontSize = 11.sp, color = TextPrimary)
                }

                IconButton(
                    onClick = onExplain,
                    modifier = Modifier.size(38.dp).testTag("explain_makeup_btn_${opportunity.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Diagnostics",
                        tint = ElectricCyan
                    )
                }

                IconButton(
                    onClick = onReject,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = TextMuted
                    )
                }
            }
        }
    }
}

