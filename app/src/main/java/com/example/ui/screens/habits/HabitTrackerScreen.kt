package com.example.ui.screens.habits

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitEntity
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HabitTrackerScreen(
    currentLanguage: Language,
    habits: List<HabitEntity>,
    onToggleHabitToday: (HabitEntity) -> Unit,
    onDeleteHabit: (HabitEntity) -> Unit,
    onCreateHabit: (title: String, desc: String, icon: String, colorHex: String, targetDays: Int) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    val todayFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    // Calculate current week days (M T W T F S S)
    val weekDays = remember {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFmt = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFmt = SimpleDateFormat("d", Locale.getDefault())

        (0..6).map {
            val dateStr = fmt.format(cal.time)
            val dayName = dayFmt.format(cal.time)
            val dayNum = dateFmt.format(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
            Triple(dateStr, dayName, dayNum)
        }
    }

    val totalHabits = habits.size
    val completedTodayCount = habits.count { it.completedHistoryDates.contains(todayFmt) }
    val completionPercent = if (totalHabits > 0) ((completedTodayCount.toFloat() / totalHabits) * 100).toInt() else 0

    Scaffold(
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = GreenNeon,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("habits_add_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .testTag("habits_screen")
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = AppStrings.get("habits_title", currentLanguage),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (currentLanguage == Language.BENGALI) "প্রতিদিনের ছোট অভ্যাস, জীবনের বড় পরিবর্তন" else "Small daily habits, remarkable life change",
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceElevatedDark
                ) {
                    Text(
                        text = "🔥 $completionPercent% Done",
                        color = AmberWarning,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Weekly Strip
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    weekDays.forEach { (dateStr, dayName, dayNum) ->
                        val isToday = dateStr == todayFmt
                        val allDoneOnDay = habits.isNotEmpty() && habits.all { it.completedHistoryDates.contains(dateStr) }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = dayName.take(3).uppercase(),
                                fontSize = 11.sp,
                                color = if (isToday) GreenNeon else TextMutedDark,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isToday -> GreenNeon
                                            allDoneOnDay -> PurplePrimary.copy(alpha = 0.4f)
                                            else -> SurfaceElevatedDark
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayNum,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isToday) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Habit List
            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (currentLanguage == Language.BENGALI) "কোনো অভ্যাস পাওয়া যায়নি। নতুন একটি যোগ করুন!" else "No habits found. Create one to begin your streak!",
                            color = TextSecondaryDark,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(habits, key = { it.id }) { habit ->
                        HabitItemCard(
                            habit = habit,
                            todayStr = todayFmt,
                            currentLanguage = currentLanguage,
                            onToggle = { onToggleHabitToday(habit) },
                            onDelete = { onDeleteHabit(habit) }
                        )
                    }
                }
            }
        }
    }

    // Create Habit Dialog
    if (showCreateDialog) {
        CreateHabitDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, desc, icon, colorHex, targetDays ->
                onCreateHabit(title, desc, icon, colorHex, targetDays)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun HabitItemCard(
    habit: HabitEntity,
    todayStr: String,
    currentLanguage: Language,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isDoneToday = habit.completedHistoryDates.contains(todayStr)
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (_: Exception) {
        GreenNeon
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isDoneToday) parsedColor.copy(alpha = 0.5f) else BorderDark,
                RoundedCornerShape(20.dp)
            )
            .testTag("habit_item_${habit.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(parsedColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (habit.iconName) {
                        "water_drop" -> Icons.Default.WaterDrop
                        "menu_book" -> Icons.Default.MenuBook
                        "spa" -> Icons.Default.Spa
                        "fitness_center" -> Icons.Default.FitnessCenter
                        else -> Icons.Default.Bolt
                    },
                    contentDescription = null,
                    tint = parsedColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = habit.description,
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🔥 ${habit.currentStreak} ${if (currentLanguage == Language.BENGALI) "দিনের স্ট্রিক" else "Days Streak"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberWarning
                    )

                    Text(
                        text = "• Best: ${habit.bestStreak}",
                        fontSize = 11.sp,
                        color = TextMutedDark
                    )
                }
            }

            // Checkmark button (Today Complete Toggle)
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDoneToday) parsedColor else SurfaceElevatedDark)
                    .testTag("habit_toggle_${habit.id}")
            ) {
                Icon(
                    imageVector = if (isDoneToday) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = "Toggle Complete",
                    tint = if (isDoneToday) Color.Black else Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Habit",
                    tint = TextMutedDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CreateHabitDialog(
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, icon: String, colorHex: String, targetDays: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("bolt") }
    var selectedColor by remember { mutableStateOf("#10B981") }
    var targetDays by remember { mutableIntStateOf(7) }

    val iconOptions = listOf(
        "bolt" to Icons.Default.Bolt,
        "water_drop" to Icons.Default.WaterDrop,
        "menu_book" to Icons.Default.MenuBook,
        "spa" to Icons.Default.Spa,
        "fitness_center" to Icons.Default.FitnessCenter
    )

    val colorOptions = listOf(
        "#10B981", "#8B5CF6", "#38BDF8", "#F59E0B", "#F43F5E"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(
                text = AppStrings.get("add_habit", currentLanguage),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(AppStrings.get("habit_name_hint", currentLanguage)) },
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
                    label = { Text(if (currentLanguage == Language.BENGALI) "সংক্ষিপ্ত বিবরণ" else "Why this habit matters...") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Icons
                Text(
                    text = "Icon",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    iconOptions.forEach { (name, icon) ->
                        val isSel = selectedIcon == name
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSel) PurplePrimary else SurfaceElevatedDark)
                                .clickable { selectedIcon = name },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                // Colors
                Text(
                    text = "Color Accent",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    colorOptions.forEach { hex ->
                        val isSel = selectedColor == hex
                        val c = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(c)
                                .border(if (isSel) 2.dp else 0.dp, Color.White, CircleShape)
                                .clickable { selectedColor = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, desc, selectedIcon, selectedColor, targetDays)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenNeon)
            ) {
                Text(
                    text = if (currentLanguage == Language.BENGALI) "তৈরি করুন" else "Create",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (currentLanguage == Language.BENGALI) "বাতিল" else "Cancel", color = TextSecondaryDark)
            }
        }
    )
}
