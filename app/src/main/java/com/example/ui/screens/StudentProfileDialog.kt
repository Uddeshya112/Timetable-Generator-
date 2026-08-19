package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StudentProfile
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@Composable
fun StudentProfileDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val currentProfile by viewModel.studentProfile.collectAsState()
    val availableBatches by viewModel.availableBatches.collectAsState()
    val availableSubgroups by viewModel.availableSubgroups.collectAsState()

    var name by remember(currentProfile) { mutableStateOf(currentProfile.name) }
    var rollNumber by remember(currentProfile) { mutableStateOf(currentProfile.rollNumber) }
    var email by remember(currentProfile) { mutableStateOf(currentProfile.email) }
    var department by remember(currentProfile) { mutableStateOf(currentProfile.department) }
    var selectedSemester by remember(currentProfile) { mutableStateOf(currentProfile.semester) }
    var selectedBatch by remember(currentProfile) { mutableStateOf(currentProfile.batch) }
    var selectedSubgroup by remember(currentProfile) { mutableStateOf(currentProfile.subgroup) }
    var targetStudyHours by remember(currentProfile) { mutableStateOf(currentProfile.targetWeeklyStudyHours.toString()) }
    var notificationsEnabled by remember(currentProfile) { mutableStateOf(currentProfile.notificationsEnabled) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .background(MidnightBackground)
                .border(1.dp, SurfaceBorderStrong, RoundedCornerShape(24.dp)),
            color = MidnightBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .padding(20.dp)
            ) {
                // 1. Top Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Student Profile & Identity",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Academic record, cohort assignment & preferences",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
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

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Scrollable Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Hero Card
                    item {
                        GlassCard(
                            backgroundColor = SurfaceDark,
                            borderColor = ElectricCyan.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, ElectricCyan, CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(ElectricCyan, NeonMint)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (name.isNotBlank()) name.first().uppercase() else "A",
                                        color = DeepNavyOnPrimary,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name.ifBlank { "Alex Mercer" },
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Roll: $rollNumber • $department",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(CyanContainer)
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
                                }
                            }
                        }
                    }

                    // Academic Metrics Pills
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricItem("CGPA", "8.74", MintLight, Modifier.weight(1f))
                            MetricItem("Attendance", "91%", MintLight, Modifier.weight(1f))
                            MetricItem("Credits", "92.5", ElectricCyan, Modifier.weight(1f))
                            MetricItem("Target", "${targetStudyHours}h/wk", ElectricCyan, Modifier.weight(1f))
                        }
                    }

                    // Form: Personal Info
                    item {
                        Text(
                            text = "PERSONAL & ACADEMIC CREDENTIALS",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("profile_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = rollNumber,
                                onValueChange = { rollNumber = it },
                                label = { Text("Roll Number") },
                                modifier = Modifier.weight(1f).testTag("profile_roll_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceDark,
                                    unfocusedContainerColor = SurfaceDark,
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = SurfaceBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            OutlinedTextField(
                                value = department,
                                onValueChange = { department = it },
                                label = { Text("Branch / Dept") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceDark,
                                    unfocusedContainerColor = SurfaceDark,
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = SurfaceBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("University Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                focusedBorderColor = ElectricCyan,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    // Form: Semester Assignment
                    item {
                        Text(
                            text = "CURRENT SEMESTER (1 TO 8):",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items((1..8).toList()) { sem ->
                                val isSel = selectedSemester == sem
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) ElectricCyan else SurfaceDark)
                                        .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedSemester = sem }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Sem $sem",
                                        color = if (isSel) DeepNavyOnPrimary else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Form: Batch Assignment
                    item {
                        Text(
                            text = "ASSIGNED BATCH (${availableBatches.size} BATCHES):",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(availableBatches) { b ->
                                val isSel = selectedBatch == b
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) ElectricCyan else SurfaceDark)
                                        .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedBatch = b }
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

                    // Form: Subgroup Assignment
                    item {
                        Text(
                            text = "SUBGROUP IN $selectedBatch (${availableSubgroups.size} SUBGROUPS):",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(availableSubgroups) { g ->
                                val isSel = selectedSubgroup == g
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) CyanContainer else SurfaceDark)
                                        .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                        .clickable { selectedSubgroup = g }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = g,
                                        color = if (isSel) ElectricCyan else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Preferences & Notification Toggle
                    item {
                        GlassCard(backgroundColor = SurfaceDark) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Class Disruption Alerts", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Receive notifications on cancellations & makeups", color = TextMuted, fontSize = 11.sp)
                                }
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = DeepNavyOnPrimary,
                                        checkedTrackColor = ElectricCyan
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Save Button & Switch Account
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.logout()
                            onDismiss()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = VibrantCoral
                        ),
                        border = BorderStroke(1.dp, VibrantCoral.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(0.45f)
                            .height(48.dp)
                            .testTag("logout_profile_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Button(
                        onClick = {
                            val updated = currentProfile.copy(
                                name = name.trim().ifBlank { "Alex Mercer" },
                                rollNumber = rollNumber.trim().ifBlank { "102103456" },
                                email = email.trim(),
                                department = department.trim(),
                                semester = selectedSemester,
                                batch = selectedBatch,
                                subgroup = selectedSubgroup,
                                targetWeeklyStudyHours = targetStudyHours.toIntOrNull() ?: 25,
                                notificationsEnabled = notificationsEnabled
                            )
                            viewModel.updateStudentProfile(updated)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = DeepNavyOnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(0.55f)
                            .height(48.dp)
                            .testTag("save_profile_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Save Profile",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
