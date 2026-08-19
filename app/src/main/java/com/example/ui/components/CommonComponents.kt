package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.data.model.SyncStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.PomodoroState

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceDark,
    borderColor: Color = SurfaceBorder,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    Column(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
            .then(clickableModifier)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun SyncStatusChip(
    syncStatus: SyncStatus,
    isOfflineMode: Boolean,
    onToggleOffline: () -> Unit,
    onForceSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when {
        isOfflineMode -> Triple(AmberContainer, AmberLight, Icons.Default.CloudOff)
        syncStatus == SyncStatus.SYNCING -> Triple(CyanContainer, ElectricCyan, Icons.Default.Sync)
        syncStatus == SyncStatus.SYNCED -> Triple(MintContainer, MintLight, Icons.Default.CloudDone)
        else -> Triple(CoralContainer, VibrantCoral, Icons.Default.CloudQueue)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable(onClick = onToggleOffline)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("sync_status_chip")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Sync State",
            tint = textColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (isOfflineMode) "Offline" else syncStatus.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (priority) {
        Priority.URGENT -> Pair(CoralContainer, VibrantCoral)
        Priority.HIGH -> Pair(AmberContainer, AmberLight)
        Priority.MEDIUM -> Pair(CyanContainer, ElectricCyan)
        Priority.LOW -> Pair(SurfaceElevated, TextMuted)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = priority.label.uppercase(),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun CourseBadge(courseCode: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SurfaceElevated)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = courseCode,
            color = ElectricCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    count: Int? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title.uppercase(),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (count != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CyanContainer)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$count",
                            color = ElectricCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
        if (actionLabel != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.testTag("section_action_${title.lowercase().replace(" ", "_")}")
            ) {
                Text(
                    text = actionLabel,
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun PomodoroMiniPlayer(
    state: PomodoroState,
    onTogglePause: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = state.isActive,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        val minutes = state.remainingSeconds / 60
        val seconds = state.remainingSeconds % 60
        val timeString = String.format("%02d:%02d", minutes, seconds)

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceDark,
            tonalElevation = 6.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("pomodoro_mini_player")
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyanContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Pomodoro Timer",
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = state.associatedTaskTitle,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Text(
                            text = "Focus Timer: $timeString",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePause,
                        modifier = Modifier.size(36.dp).testTag("pomodoro_pause_button")
                    ) {
                        Icon(
                            imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (state.isPaused) "Resume" else "Pause",
                            tint = TextPrimary
                        )
                    }
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier.size(36.dp).testTag("pomodoro_stop_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Stop",
                            tint = TextMuted
                        )
                    }
                }
            }
        }
    }
}
