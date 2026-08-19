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
import com.example.ui.viewmodel.AppViewModel

@Composable
fun TasksScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val courses by viewModel.allCourses.collectAsState()
    val selectedCourse by viewModel.selectedCourseFilter.collectAsState()
    val selectedPriority by viewModel.selectedPriorityFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MidnightBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = ElectricCyan,
                contentColor = DeepNavyOnPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(bottom = 72.dp)
                    .testTag("add_task_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(28.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp)
        ) {
            // 1. Header & Search Bar
            item {
                Text(
                    text = "Academic Tasks",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Assignments, exam preparation & study goals",
                    color = TextMuted,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search tasks, courses, topics...", color = TextMuted) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = SurfaceDark,
                        unfocusedContainerColor = SurfaceDark,
                        cursorColor = ElectricCyan
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tasks_search_input")
                )
            }

            // 2. Course Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCourse == null,
                            onClick = { viewModel.setCourseFilter(null) },
                            label = { Text("All Subjects", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanContainer,
                                selectedLabelColor = ElectricCyan,
                                containerColor = SurfaceDark,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCourse == null,
                                borderColor = if (selectedCourse == null) ElectricCyan else SurfaceBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                    items(courses, key = { it.id }) { course ->
                        val isSel = selectedCourse == course.code
                        FilterChip(
                            selected = isSel,
                            onClick = {
                                viewModel.setCourseFilter(if (isSel) null else course.code)
                            },
                            label = { Text(course.shortName, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanContainer,
                                selectedLabelColor = ElectricCyan,
                                containerColor = SurfaceDark,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSel,
                                borderColor = if (isSel) ElectricCyan else SurfaceBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // 3. Priority Filter Chips
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("PRIORITY:", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Priority.entries.forEach { priority ->
                        val isSelected = selectedPriority == priority
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CyanContainer else SurfaceDark)
                                .border(1.dp, if (isSelected) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.setPriorityFilter(if (isSelected) null else priority)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = priority.label,
                                color = if (isSelected) ElectricCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 4. Tasks List
            if (tasks.isEmpty()) {
                item {
                    GlassCard(
                        backgroundColor = SurfaceDark,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No academic tasks found",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tap + to add assignments or study plans",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onStartPomodoro = { viewModel.startPomodoro(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }

    // Add Task Dialog Modal
    if (showAddTaskDialog) {
        AddTaskDialog(
            courses = courses,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, courseCode, courseName, dueMillis, priority, type, estMin ->
                viewModel.addTask(title, desc, courseCode, courseName, dueMillis, priority, type, estMin)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onToggle: () -> Unit,
    onStartPomodoro: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.isCompleted
    val now = System.currentTimeMillis()
    val dueDiffHours = (task.dueDateMillis - now) / (1000 * 3600)

    val dueLabel = when {
        isCompleted -> "Completed"
        dueDiffHours < 0 -> "Overdue"
        dueDiffHours < 24 -> "Due Today"
        dueDiffHours < 48 -> "Tomorrow"
        else -> "In ${dueDiffHours / 24}d"
    }

    val dueColor = when {
        isCompleted -> TextMuted
        dueDiffHours < 24 -> VibrantCoral
        dueDiffHours < 72 -> AmberLight
        else -> TextSecondary
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCompleted) SurfaceDark.copy(alpha = 0.4f) else SurfaceDark)
            .border(
                1.dp,
                if (task.priority == Priority.URGENT && !isCompleted) VibrantCoral.copy(alpha = 0.5f) else SurfaceBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onToggle() }
            .padding(14.dp)
            .testTag("task_card_${task.id}"),
        verticalAlignment = Alignment.Top
    ) {
        // Square Checkbox with Electric Glow
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isCompleted) ElectricCyan else Color.Transparent)
                .border(
                    2.dp,
                    if (isCompleted) ElectricCyan else ElectricCyan.copy(alpha = 0.8f),
                    RoundedCornerShape(6.dp)
                )
                .clickable { onToggle() }
                .testTag("checkbox_${task.id}"),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = DeepNavyOnPrimary,
                    modifier = Modifier.size(15.dp)
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
                    CourseBadge(courseCode = task.courseCode)
                    Spacer(modifier = Modifier.width(6.dp))
                    PriorityBadge(priority = task.priority)
                }

                Text(
                    text = dueLabel,
                    color = dueColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = task.title,
                color = if (isCompleted) TextMuted else TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.description,
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.taskType.label,
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    Text(
                        text = "⏱️ ${task.estimatedMinutes}m",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    if (task.pomodoroSessions > 0) {
                        Text(
                            text = "🍅 ${task.pomodoroSessions}",
                            color = VibrantCoral,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isCompleted) {
                        IconButton(
                            onClick = onStartPomodoro,
                            modifier = Modifier.size(30.dp).testTag("pomodoro_task_${task.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Focus",
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp).testTag("delete_task_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    courses: List<Course>,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        description: String,
        courseCode: String,
        courseName: String,
        dueDateMillis: Long,
        priority: Priority,
        taskType: TaskType,
        estimatedMinutes: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()?.code ?: "CS501") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var selectedTaskType by remember { mutableStateOf(TaskType.ASSIGNMENT) }
    var selectedDaysOffset by remember { mutableStateOf(1) }
    var estimatedMinutes by remember { mutableStateOf(45) }

    val courseName = courses.find { it.code == selectedCourse }?.name ?: "Course"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Academic Task", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Task Title *") },
                        placeholder = { Text("e.g. Discrete Math Proofs") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_task_title_input")
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Details & Scope (Optional)") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Text("Subject", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(courses) { c ->
                            val isSel = selectedCourse == c.code
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedCourse = c.code }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${c.shortName} (${c.code})",
                                    color = if (isSel) ElectricCyan else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Priority", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Priority.entries.forEach { p ->
                            val isSel = selectedPriority == p
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedPriority = p }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = p.label,
                                    color = if (isSel) ElectricCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Due Date Preset", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            Pair("Today", 0),
                            Pair("Tomorrow", 1),
                            Pair("3 Days", 3),
                            Pair("Next Week", 7)
                        ).forEach { (label, offset) ->
                            val isSel = selectedDaysOffset == offset
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanContainer else SurfaceElevated)
                                    .border(1.dp, if (isSel) ElectricCyan else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedDaysOffset = offset }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) ElectricCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val dueMillis = System.currentTimeMillis() + (selectedDaysOffset * 24 * 3600 * 1000L)
                        onConfirm(
                            title,
                            description,
                            selectedCourse,
                            courseName,
                            dueMillis,
                            selectedPriority,
                            selectedTaskType,
                            estimatedMinutes
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = DeepNavyOnPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("save_task_button")
            ) {
                Text("Save Task", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = SurfaceDark
    )
}
