package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.components.PomodoroMiniPlayer
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.AppViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val pendingMakeups by viewModel.pendingMakeups.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val pomodoroState by viewModel.pomodoroState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        val msg = toastMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    if (currentUser == null) {
        // Render Login & Registration Portal
        AuthScreen(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    } else {
        val user = currentUser!!
        val roleColor = when (user.role) {
            UserRole.STUDENT -> NeonCyan
            UserRole.TEACHER -> NeonAmber
            UserRole.COORDINATOR -> NeonPurple
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MidnightBackground),
            containerColor = MidnightBackground,
            topBar = {
                // Top Global User & Role Switcher Bar
                Surface(
                    color = SurfaceDark,
                    tonalElevation = 4.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        SurfaceBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = roleColor.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, roleColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(user.role.badge, fontSize = 12.sp)
                                    Text(
                                        text = user.role.label.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = roleColor,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Column {
                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (user.role == UserRole.STUDENT) "ID: ${user.userId} • Batch ${user.batch}-${user.subgroup}" else "ID: ${user.userId}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextMuted
                                )
                            }
                        }

                        // Logout / Switch Account Button
                        TextButton(
                            onClick = { viewModel.logout() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Sign Out",
                                tint = VibrantCoral,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sign Out",
                                color = VibrantCoral,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(bottom = 70.dp)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = SurfaceElevated,
                        contentColor = TextPrimary,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    // Docked Pomodoro focus player
                    PomodoroMiniPlayer(
                        state = pomodoroState,
                        onTogglePause = { viewModel.togglePausePomodoro() },
                        onStop = { viewModel.stopPomodoro() }
                    )

                    // Tokyo Cyber Dark Bottom Navigation Bar
                    NavigationBar(
                        containerColor = SurfaceDark,
                        tonalElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                SurfaceBorder,
                                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                            )
                            .testTag("main_bottom_nav")
                    ) {
                        val roleTabs = when (user.role) {
                            UserRole.COORDINATOR -> listOf(AppNavTab.COORDINATOR, AppNavTab.RECOVERY, AppNavTab.SYLLABUS, AppNavTab.INBOX)
                            UserRole.TEACHER -> listOf(AppNavTab.TODAY, AppNavTab.TIMETABLE, AppNavTab.INBOX)
                            UserRole.STUDENT -> listOf(AppNavTab.TODAY, AppNavTab.TIMETABLE, AppNavTab.TASKS, AppNavTab.INBOX)
                        }

                        if (roleTabs.contains(AppNavTab.TODAY)) {
                            NavigationBarItem(
                                selected = currentTab == AppNavTab.TODAY,
                                onClick = { viewModel.setTab(AppNavTab.TODAY) },
                                icon = {
                                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                                },
                                label = { Text("HOME", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricCyan,
                                    selectedTextColor = ElectricCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = CyanContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_today")
                            )
                        }

                        if (roleTabs.contains(AppNavTab.TASKS)) {
                            NavigationBarItem(
                                selected = currentTab == AppNavTab.TASKS,
                                onClick = { viewModel.setTab(AppNavTab.TASKS) },
                                icon = {
                                    Icon(imageVector = Icons.Default.Assignment, contentDescription = "Tasks")
                                },
                                label = { Text("TASKS", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricCyan,
                                    selectedTextColor = ElectricCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = CyanContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_tasks")
                            )
                        }

                        if (roleTabs.contains(AppNavTab.TIMETABLE)) {
                            NavigationBarItem(
                                selected = currentTab == AppNavTab.TIMETABLE,
                                onClick = { viewModel.setTab(AppNavTab.TIMETABLE) },
                                icon = {
                                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "Schedule")
                                },
                                label = { Text("ROUTINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricCyan,
                                    selectedTextColor = ElectricCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = CyanContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_timetable")
                            )
                        }

                        if (roleTabs.contains(AppNavTab.COORDINATOR)) {
                            NavigationBarItem(
                                selected = currentTab == AppNavTab.COORDINATOR,
                                onClick = { viewModel.setTab(AppNavTab.COORDINATOR) },
                                icon = {
                                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Planner Studio")
                                },
                                label = { Text("PLANNER", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricCyan,
                                    selectedTextColor = ElectricCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = CyanContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_coordinator")
                            )
                        }

                        if (roleTabs.contains(AppNavTab.RECOVERY)) {
                            NavigationBarItem(
                                selected = currentTab == AppNavTab.RECOVERY,
                                onClick = { viewModel.setTab(AppNavTab.RECOVERY) },
                                icon = {
                                    BadgedBox(badge = {
                                        if (pendingMakeups.isNotEmpty()) {
                                            Badge(
                                                containerColor = VibrantCoral,
                                                contentColor = Color.White
                                            ) {
                                                Text("${pendingMakeups.size}", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }) {
                                        Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = "Self-Healing")
                                    }
                                },
                                label = { Text("HEAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricCyan,
                                    selectedTextColor = ElectricCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = CyanContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_recovery")
                            )
                        }

                        if (roleTabs.contains(AppNavTab.SYLLABUS)) {
                            NavigationBarItem(
                                selected = currentTab == AppNavTab.SYLLABUS,
                                onClick = { viewModel.setTab(AppNavTab.SYLLABUS) },
                                icon = {
                                    Icon(imageVector = Icons.Default.QueryStats, contentDescription = "Stats")
                                },
                                label = { Text("STATS", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricCyan,
                                    selectedTextColor = ElectricCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = CyanContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_syllabus")
                            )
                        }

                        if (roleTabs.contains(AppNavTab.INBOX)) {
                            NavigationBarItem(
                                selected = currentTab == AppNavTab.INBOX,
                                onClick = { viewModel.setTab(AppNavTab.INBOX) },
                                icon = {
                                    BadgedBox(badge = {
                                        if (unreadCount > 0) {
                                            Badge(
                                                containerColor = ElectricCyan,
                                                contentColor = DeepNavyOnPrimary
                                            ) {
                                                Text("$unreadCount", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }) {
                                        Icon(imageVector = Icons.Default.Inbox, contentDescription = "Inbox")
                                    }
                                },
                                label = { Text("INBOX", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ElectricCyan,
                                    selectedTextColor = ElectricCyan,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = CyanContainer
                                ),
                                modifier = Modifier.testTag("nav_tab_inbox")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    AppNavTab.TODAY -> HomeScreen(viewModel = viewModel)
                    AppNavTab.TASKS -> TasksScreen(viewModel = viewModel)
                    AppNavTab.TIMETABLE -> TimetableScreen(viewModel = viewModel)
                    AppNavTab.COORDINATOR -> CoordinatorStudioScreen(viewModel = viewModel)
                    AppNavTab.RECOVERY -> RecoveryHubScreen(viewModel = viewModel)
                    AppNavTab.SYLLABUS -> SyllabusAnalyticsScreen(viewModel = viewModel)
                    AppNavTab.INBOX -> AcademicInboxScreen(viewModel = viewModel)
                }
            }
        }
    }
}
