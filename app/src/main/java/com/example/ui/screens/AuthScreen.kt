package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val authError by viewModel.authError.collectAsState()
    val isAuthenticating by viewModel.isAuthenticating.collectAsState()

    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }
    var isSignUpMode by remember { mutableStateOf(false) }

    // Form states
    var userIdInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var fullNameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var departmentInput by remember { mutableStateOf("Computer Science & Engineering") }
    var semesterInput by remember { mutableIntStateOf(5) }
    var batchInput by remember { mutableStateOf("B1") }
    var subgroupInput by remember { mutableStateOf("G1") }
    var designationInput by remember { mutableStateOf("Assistant Professor") }
    var showPassword by remember { mutableStateOf(false) }
    var showDatabaseAccountsSheet by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    // Background gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.background
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. BRAND HERO HEADER
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (selectedRole) {
                                UserRole.STUDENT -> Icons.Filled.School
                                UserRole.TEACHER -> Icons.Filled.Person
                                UserRole.COORDINATOR -> Icons.Filled.AutoGraph
                            },
                            contentDescription = "App Logo",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "INTELLISCHEDULE",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Academic Routine & Timetable Portal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. ROLE SELECTOR PILLS (STUDENT, TEACHER, COORDINATOR)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SELECT YOUR PORTAL ROLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = role == selectedRole
                            val roleColor = when (role) {
                                UserRole.STUDENT -> NeonCyan
                                UserRole.TEACHER -> NeonAmber
                                UserRole.COORDINATOR -> NeonPurple
                            }

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedRole = role
                                        viewModel.clearAuthError()
                                        // Update default placeholder fields
                                        when (role) {
                                            UserRole.STUDENT -> {
                                                if (userIdInput.startsWith("teacher") || userIdInput.startsWith("coord")) {
                                                    userIdInput = "student101"
                                                }
                                            }
                                            UserRole.TEACHER -> {
                                                if (userIdInput.startsWith("student") || userIdInput.startsWith("coord")) {
                                                    userIdInput = "teacher201"
                                                }
                                            }
                                            UserRole.COORDINATOR -> {
                                                if (userIdInput.startsWith("student") || userIdInput.startsWith("teacher")) {
                                                    userIdInput = "coord301"
                                                }
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        roleColor.copy(alpha = 0.18f)
                                    } else {
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                                    }
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) roleColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = role.badge,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = when (role) {
                                            UserRole.STUDENT -> "Student"
                                            UserRole.TEACHER -> "Teacher"
                                            UserRole.COORDINATOR -> "Coordinator"
                                        },
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. AUTH FORM CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Switch between Sign In and Register
                        TabRow(
                            selectedTabIndex = if (isSignUpMode) 1 else 0,
                            containerColor = Color.Transparent,
                            divider = {},
                            indicator = {}
                        ) {
                            Tab(
                                selected = !isSignUpMode,
                                onClick = {
                                    isSignUpMode = false
                                    viewModel.clearAuthError()
                                },
                                text = {
                                    Text(
                                        text = "SIGN IN",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (!isSignUpMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                            Tab(
                                selected = isSignUpMode,
                                onClick = {
                                    isSignUpMode = true
                                    viewModel.clearAuthError()
                                },
                                text = {
                                    Text(
                                        text = "REGISTER NEW",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSignUpMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

                        // Error Banner if authentication fails
                        AnimatedVisibility(
                            visible = authError != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            authError?.let { err ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = "Error",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = err,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        }

                        // FORM FIELDS
                        if (isSignUpMode) {
                            // REGISTER FORM
                            OutlinedTextField(
                                value = fullNameInput,
                                onValueChange = { fullNameInput = it },
                                label = { Text("Full Name *") },
                                placeholder = { Text("e.g. Alex Mercer") },
                                leadingIcon = {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            OutlinedTextField(
                                value = userIdInput,
                                onValueChange = { userIdInput = it },
                                label = {
                                    Text(
                                        when (selectedRole) {
                                            UserRole.STUDENT -> "Roll Number / Student ID *"
                                            UserRole.TEACHER -> "Faculty Code / ID *"
                                            UserRole.COORDINATOR -> "Coordinator ID *"
                                        }
                                    )
                                },
                                placeholder = {
                                    Text(
                                        when (selectedRole) {
                                            UserRole.STUDENT -> "e.g. 102103456 or student101"
                                            UserRole.TEACHER -> "e.g. FAC501 or teacher201"
                                            UserRole.COORDINATOR -> "e.g. COORD301"
                                        }
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Address") },
                                placeholder = { Text("e.g. name@univ.edu") },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password * (Min 4 chars)") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (showPassword) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            // ROLE-SPECIFIC REGISTRATION DETAILS
                            when (selectedRole) {
                                UserRole.STUDENT -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Semester
                                        OutlinedTextField(
                                            value = semesterInput.toString(),
                                            onValueChange = {
                                                it.toIntOrNull()?.let { sem ->
                                                    if (sem in 1..8) semesterInput = sem
                                                }
                                            },
                                            label = { Text("Sem (1-8)") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )

                                        // Batch
                                        OutlinedTextField(
                                            value = batchInput,
                                            onValueChange = { batchInput = it.uppercase() },
                                            label = { Text("Batch (B1-B12)") },
                                            placeholder = { Text("B1") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )

                                        // Subgroup
                                        OutlinedTextField(
                                            value = subgroupInput,
                                            onValueChange = { subgroupInput = it.uppercase() },
                                            label = { Text("Group") },
                                            placeholder = { Text("G1") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    }
                                }
                                UserRole.TEACHER -> {
                                    OutlinedTextField(
                                        value = designationInput,
                                        onValueChange = { designationInput = it },
                                        label = { Text("Designation / Title") },
                                        placeholder = { Text("e.g. Professor & ML Lab Head") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                                UserRole.COORDINATOR -> {
                                    OutlinedTextField(
                                        value = designationInput,
                                        onValueChange = { designationInput = it },
                                        label = { Text("Coordinator Role") },
                                        placeholder = { Text("e.g. Chief Timetable Coordinator") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // REGISTER BUTTON
                            Button(
                                onClick = {
                                    val newUser = UserAccount(
                                        userId = userIdInput,
                                        password = passwordInput,
                                        role = selectedRole,
                                        fullName = fullNameInput,
                                        email = emailInput,
                                        department = departmentInput,
                                        semester = semesterInput,
                                        batch = batchInput,
                                        subgroup = subgroupInput,
                                        designation = designationInput
                                    )
                                    viewModel.register(newUser)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                enabled = !isAuthenticating
                            ) {
                                if (isAuthenticating) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Register & Save to Database",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        } else {
                            // SIGN IN FORM
                            OutlinedTextField(
                                value = userIdInput,
                                onValueChange = { userIdInput = it },
                                label = {
                                    Text(
                                        when (selectedRole) {
                                            UserRole.STUDENT -> "Roll No / Student ID / Email"
                                            UserRole.TEACHER -> "Faculty Code / ID / Email"
                                            UserRole.COORDINATOR -> "Coordinator ID / Email"
                                        }
                                    )
                                },
                                placeholder = {
                                    Text(
                                        when (selectedRole) {
                                            UserRole.STUDENT -> "student101"
                                            UserRole.TEACHER -> "teacher201"
                                            UserRole.COORDINATOR -> "coord301"
                                        }
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password") },
                                placeholder = { Text("pass123") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (showPassword) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = {
                                    focusManager.clearFocus()
                                    viewModel.login(userIdInput, passwordInput, selectedRole)
                                })
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // SIGN IN BUTTON
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.login(userIdInput, passwordInput, selectedRole)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = when (selectedRole) {
                                        UserRole.STUDENT -> MaterialTheme.colorScheme.primary
                                        UserRole.TEACHER -> MaterialTheme.colorScheme.secondary
                                        UserRole.COORDINATOR -> MaterialTheme.colorScheme.tertiary
                                    }
                                ),
                                enabled = !isAuthenticating
                            ) {
                                if (isAuthenticating) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Filled.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Sign In as ${selectedRole.label}",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. INSTANT DEMO LOGIN CHIPS
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ INSTANT 1-TAP DEMO ACCESS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NeonMint.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Ready in SQLite",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = NeonMint,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Demo account buttons for each role
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Student Demo
                            OutlinedButton(
                                onClick = {
                                    selectedRole = UserRole.STUDENT
                                    userIdInput = "student101"
                                    passwordInput = "pass123"
                                    viewModel.quickDemoLogin(UserRole.STUDENT)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎓 Student", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("Alex (B1)", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Teacher Demo
                            OutlinedButton(
                                onClick = {
                                    selectedRole = UserRole.TEACHER
                                    userIdInput = "teacher201"
                                    passwordInput = "pass123"
                                    viewModel.quickDemoLogin(UserRole.TEACHER)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.5f))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("👨‍🏫 Teacher", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("Dr. Anita", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            // Coordinator Demo
                            OutlinedButton(
                                onClick = {
                                    selectedRole = UserRole.COORDINATOR
                                    userIdInput = "coord301"
                                    passwordInput = "pass123"
                                    viewModel.quickDemoLogin(UserRole.COORDINATOR)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
                                border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.5f))
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("⚡ Coord", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    Text("Prof. Rajesh", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // 5. VIEW ALL DATABASE STORED ACCOUNTS
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatabaseAccountsSheet = !showDatabaseAccountsSheet },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "Database Accounts",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Accounts in Database (${allUsers.size})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Icon(
                                imageVector = if (showDatabaseAccountsSheet) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Database Accounts",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = "All registered login credentials, IDs, and passwords are saved securely in Room SQLite database.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        AnimatedVisibility(
                            visible = showDatabaseAccountsSheet,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allUsers.forEach { user ->
                                    val roleColor = when (user.role) {
                                        UserRole.STUDENT -> NeonCyan
                                        UserRole.TEACHER -> NeonAmber
                                        UserRole.COORDINATOR -> NeonPurple
                                    }

                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedRole = user.role
                                                userIdInput = user.userId
                                                passwordInput = user.password
                                                viewModel.login(user.userId, user.password, user.role)
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = BorderStroke(1.dp, roleColor.copy(alpha = 0.3f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(roleColor.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(user.role.badge, fontSize = 16.sp)
                                                }

                                                Column {
                                                    Text(
                                                        text = user.fullName,
                                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = "ID: ${user.userId} • Pass: ${user.password}",
                                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    if (user.role == UserRole.STUDENT) {
                                                        Text(
                                                            text = "Sem ${user.semester} • Batch ${user.batch} • Subgroup ${user.subgroup}",
                                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                            color = roleColor
                                                        )
                                                    } else {
                                                        Text(
                                                            text = user.designation.ifBlank { user.role.label },
                                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                            color = roleColor
                                                        )
                                                    }
                                                }
                                            }

                                            FilledTonalButton(
                                                onClick = {
                                                    selectedRole = user.role
                                                    userIdInput = user.userId
                                                    passwordInput = user.password
                                                    viewModel.login(user.userId, user.password, user.role)
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                colors = ButtonDefaults.filledTonalButtonColors(
                                                    containerColor = roleColor.copy(alpha = 0.2f),
                                                    contentColor = roleColor
                                                )
                                            ) {
                                                Text("Sign In", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
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
    }
}
