package com.example.ui.screens.tasks

import androidx.compose.animation.*
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
import com.example.data.model.TaskCategory
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*

@Composable
fun TasksScreen(
    currentLanguage: Language,
    tasks: List<TaskEntity>,
    onToggleTask: (TaskEntity) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    onSaveTask: (title: String, desc: String, priority: TaskPriority, category: TaskCategory, dueMs: Long, dueTime: String, reminder: Boolean) -> Unit,
    onUpdateTask: (TaskEntity) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("ALL") } // ALL, TODAY, COMPLETED
    var selectedCategory by remember { mutableStateOf<TaskCategory?>(null) }
    var selectedPriority by remember { mutableStateOf<TaskPriority?>(null) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

    val filteredTasks = tasks.filter { task ->
        val matchesSearch = task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            "TODAY" -> !task.isCompleted
            "COMPLETED" -> task.isCompleted
            else -> true
        }
        val matchesCategory = selectedCategory == null || task.category == selectedCategory
        val matchesPriority = selectedPriority == null || task.priority == selectedPriority

        matchesSearch && matchesTab && matchesCategory && matchesPriority
    }

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = PurplePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("tasks_add_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .testTag("tasks_screen")
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Title & Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get("nav_tasks", currentLanguage),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceElevatedDark
                ) {
                    Text(
                        text = "${tasks.count { it.isCompleted }}/${tasks.size} Done",
                        color = GreenNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(AppStrings.get("task_title_hint", currentLanguage), color = TextMutedDark) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryDark)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondaryDark)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PurplePrimary,
                    unfocusedBorderColor = BorderDark,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tasks_search_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Status Tabs (All, Today, Completed)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterTabButton(
                    label = AppStrings.get("all_tasks", currentLanguage),
                    isSelected = selectedTab == "ALL",
                    onClick = { selectedTab = "ALL" },
                    modifier = Modifier.weight(1f)
                )
                FilterTabButton(
                    label = AppStrings.get("today_tasks", currentLanguage),
                    isSelected = selectedTab == "TODAY",
                    onClick = { selectedTab = "TODAY" },
                    modifier = Modifier.weight(1f)
                )
                FilterTabButton(
                    label = AppStrings.get("completed", currentLanguage),
                    isSelected = selectedTab == "COMPLETED",
                    onClick = { selectedTab = "COMPLETED" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    CategoryChip(
                        label = "All Categories",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(TaskCategory.values()) { category ->
                    CategoryChip(
                        label = category.name.lowercase().replaceFirstChar { it.uppercase() },
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task List
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = PurpleLight,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (currentLanguage == Language.BENGALI) "কোনো কাজ পাওয়া যায়নি" else "No tasks found",
                            color = TextSecondaryDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onToggle = { onToggleTask(task) },
                            onEdit = { editingTask = task },
                            onDelete = { onDeleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    // Create / Edit Dialog
    if (showCreateDialog || editingTask != null) {
        val taskToEdit = editingTask
        TaskFormDialog(
            currentLanguage = currentLanguage,
            initialTask = taskToEdit,
            onDismiss = {
                showCreateDialog = false
                editingTask = null
            },
            onSave = { title, desc, priority, category, dueMs, dueTime, reminder ->
                if (taskToEdit == null) {
                    onSaveTask(title, desc, priority, category, dueMs, dueTime, reminder)
                } else {
                    onUpdateTask(
                        taskToEdit.copy(
                            title = title,
                            description = desc,
                            priority = priority,
                            category = category,
                            dueDateEpochMs = dueMs,
                            dueTimeString = dueTime,
                            hasReminder = reminder
                        )
                    )
                }
                showCreateDialog = false
                editingTask = null
            }
        )
    }
}

@Composable
fun FilterTabButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) PurplePrimary else SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PurpleLight else BorderDark),
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextSecondaryDark
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) SurfaceElevatedDark else SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BlueElectric else BorderDark),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) BlueElectric else TextSecondaryDark,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        TaskPriority.HIGH -> RedPriority
        TaskPriority.MEDIUM -> AmberWarning
        TaskPriority.LOW -> GreenNeon
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
            .testTag("task_item_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("task_toggle_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (task.isCompleted) GreenNeon else TextMutedDark,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) TextMutedDark else Color.White
                )

                if (task.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = priorityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = task.priority.name,
                            color = priorityColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Category Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SurfaceElevatedDark
                    ) {
                        Text(
                            text = task.category.name,
                            color = TextSecondaryDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Time
                    Text(
                        text = "⏰ ${task.dueTimeString}",
                        color = TextMutedDark,
                        fontSize = 11.sp
                    )

                    if (task.hasReminder) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Reminder Set",
                            tint = AmberWarning,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = RedPriority, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun TaskFormDialog(
    currentLanguage: Language,
    initialTask: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, priority: TaskPriority, category: TaskCategory, dueMs: Long, dueTime: String, reminder: Boolean) -> Unit
) {
    var title by remember { mutableStateOf(initialTask?.title ?: "") }
    var desc by remember { mutableStateOf(initialTask?.description ?: "") }
    var priority by remember { mutableStateOf(initialTask?.priority ?: TaskPriority.MEDIUM) }
    var category by remember { mutableStateOf(initialTask?.category ?: TaskCategory.WORK) }
    var dueTime by remember { mutableStateOf(initialTask?.dueTimeString ?: "18:00") }
    var hasReminder by remember { mutableStateOf(initialTask?.hasReminder ?: true) }
    var titleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = if (initialTask == null)
                    AppStrings.get("add_task", currentLanguage)
                else (if (currentLanguage == Language.BENGALI) "কাজ সম্পাদনা করুন" else "Edit Task"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text(AppStrings.get("task_title_hint", currentLanguage)) },
                    isError = titleError,
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text(AppStrings.get("task_desc_hint", currentLanguage)) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Priority Selection
                Text(
                    text = "Priority Level",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskPriority.values().forEach { p ->
                        val isSel = priority == p
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) PurplePrimary else SurfaceElevatedDark,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { priority = p }
                        ) {
                            Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    text = p.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else TextSecondaryDark
                                )
                            }
                        }
                    }
                }

                // Category Selection
                Text(
                    text = "Category",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(TaskCategory.values()) { c ->
                        val isSel = category == c
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) BlueElectric.copy(alpha = 0.2f) else SurfaceElevatedDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) BlueElectric else BorderDark),
                            modifier = Modifier.clickable { category = c }
                        ) {
                            Text(
                                text = c.name,
                                fontSize = 11.sp,
                                color = if (isSel) BlueElectric else TextSecondaryDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Due Time Input & Reminder Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = dueTime,
                        onValueChange = { dueTime = it },
                        label = { Text("Time (e.g. 18:00)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurplePrimary,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.width(130.dp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Reminder",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = hasReminder,
                            onCheckedChange = { hasReminder = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PurplePrimary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onSave(title, desc, priority, category, System.currentTimeMillis(), dueTime, hasReminder)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text(AppStrings.get("save_task", currentLanguage), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BENGALI) "বাতিল" else "Cancel", color = TextSecondaryDark)
            }
        }
    )
}
