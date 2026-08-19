package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AcademicNotification
import com.example.data.model.NotificationCategory
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.AppViewModel

@Composable
fun AcademicInboxScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val outboxEvents by viewModel.outboxEvents.collectAsState()
    val syncStatus by viewModel.syncEngine.syncState.collectAsState()
    val isOffline by viewModel.syncEngine.isOfflineMode.collectAsState()

    var showOutboxModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
    ) {
        // 1. Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Inbox & Notifications",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Academic alerts & outbox queue",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                if (unreadCount > 0) {
                    TextButton(
                        onClick = { viewModel.markAllNotificationsRead() },
                        modifier = Modifier.testTag("mark_all_read_btn")
                    ) {
                        Text("Mark all read", color = ElectricCyan, fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Offline Sync Engine Card
        item {
            GlassCard(
                backgroundColor = SurfaceDark,
                borderColor = SurfaceBorder,
                onClick = { showOutboxModal = true }
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
                                imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudSync,
                                contentDescription = null,
                                tint = if (isOffline) AmberLight else ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Offline-First Sync Engine",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isOffline) "Running offline (Queued in SQLite)" else "Connected & Synced with server",
                                color = if (isOffline) AmberLight else MintLight,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Details",
                        tint = TextMuted
                    )
                }
            }
        }

        // 3. Notification Stream
        item {
            SectionHeader(
                title = "Contextual Notifications",
                count = notifications.size
            )
        }

        if (notifications.isEmpty()) {
            item {
                GlassCard(backgroundColor = SurfaceDark) {
                    Text(
                        text = "No academic notifications at this time.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(notifications, key = { it.id }) { notification ->
                NotificationCard(
                    notification = notification,
                    onClick = {
                        viewModel.markNotificationRead(notification.id)
                        if (notification.category == NotificationCategory.MAKEUP_FOUND || notification.category == NotificationCategory.CANCELLATION) {
                            viewModel.setTab(AppNavTab.RECOVERY)
                        } else if (notification.category == NotificationCategory.TASK_DEADLINE) {
                            viewModel.setTab(AppNavTab.TASKS)
                        }
                    }
                )
            }
        }
    }

    // Outbox Events Modal Dialog
    if (showOutboxModal) {
        AlertDialog(
            onDismissRequest = { showOutboxModal = false },
            title = {
                Text("Transactional Outbox State", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Every offline mutation generates a transaction-safe OutboxEvent with unique clientOperationId to guarantee idempotency and prevent duplicate bookings.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    HorizontalDivider(color = SurfaceBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Network Status:", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = if (isOffline) "OFFLINE" else "ONLINE",
                            color = if (isOffline) AmberLight else MintLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Queued Outbox Records:", color = TextSecondary, fontSize = 12.sp)
                        Text("${outboxEvents.size} records", color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.syncEngine.toggleOfflineMode()
                                showOutboxModal = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isOffline) NeonMint else SolarAmber,
                                contentColor = DeepNavyOnPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isOffline) "Go Online" else "Offline Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.syncEngine.triggerSync()
                                showOutboxModal = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Force Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showOutboxModal = false }) {
                    Text("Close", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
fun NotificationCard(
    notification: AcademicNotification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (iconColor, icon) = when (notification.category) {
        NotificationCategory.MAKEUP_FOUND -> Pair(MintLight, Icons.Default.Bolt)
        NotificationCategory.CANCELLATION -> Pair(VibrantCoral, Icons.Default.Cancel)
        NotificationCategory.TASK_DEADLINE -> Pair(AmberLight, Icons.Default.Alarm)
        NotificationCategory.ROOM_CHANGE -> Pair(ElectricCyan, Icons.Default.MeetingRoom)
        else -> Pair(ElectricCyan, Icons.Default.Notifications)
    }

    GlassCard(
        backgroundColor = if (notification.isRead) SurfaceDark.copy(alpha = 0.6f) else SurfaceDark,
        borderColor = if (notification.isRead) SurfaceBorder else ElectricCyan.copy(alpha = 0.4f),
        onClick = onClick,
        modifier = modifier.testTag("notification_card_${notification.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CyanContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        color = if (notification.isRead) TextSecondary else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ElectricCyan)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    color = if (notification.isRead) TextMuted else TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                if (notification.actionText != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "→ ${notification.actionText}",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
