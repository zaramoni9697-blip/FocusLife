package com.example.ui.screens.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.data.model.UserProfileEntity
import com.example.localization.AppStrings
import com.example.localization.Language
import com.example.ui.navigation.MainTab
import com.example.ui.theme.*
import com.example.ui.viewmodel.ProductivityStats
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    currentLanguage: Language,
    stats: ProductivityStats,
    tasks: List<TaskEntity>,
    userProfile: UserProfileEntity?,
    onNavigateTab: (MainTab) -> Unit,
    onToggleTask: (TaskEntity) -> Unit,
    onToggleLanguage: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greetingKey = when (hour) {
        in 5..11 -> "greeting_morning"
        in 12..16 -> "greeting_afternoon"
        else -> "greeting_evening"
    }

    val datePattern = if (currentLanguage == Language.BENGALI) "EEEE, d MMMM" else "EEEE, MMMM d, yyyy"
    val formattedDate = SimpleDateFormat(datePattern, Locale.getDefault()).format(calendar.time)

    val animatedScore by animateFloatAsState(
        targetValue = stats.score.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "productivityScore"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 20.dp)
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${AppStrings.get(greetingKey, currentLanguage)}, ${userProfile?.fullName?.split(" ")?.firstOrNull() ?: "Achiever"} 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedDate,
                        fontSize = 13.sp,
                        color = TextSecondaryDark
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Language Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceElevatedDark)
                            .clickable { onToggleLanguage() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == Language.ENGLISH) "বাংলা" else "EN",
                            color = PurpleLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Notification bell button
                    IconButton(
                        onClick = { onNavigateTab(MainTab.NOTIFICATIONS) },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevatedDark)
                            .testTag("dashboard_notifications_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Hero Productivity Score Circular Card
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderDark, RoundedCornerShape(26.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    SurfaceDark,
                                    Color(0xFF1E1B4B).copy(alpha = 0.5f),
                                    SurfaceDark
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PurplePrimary.copy(alpha = 0.2f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = if (currentLanguage == Language.BENGALI) "🚀 দৈনিক স্কোর" else "🚀 DAILY MOMENTUM",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleLight,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = AppStrings.get("productivity_score", currentLanguage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (stats.score >= 70)
                                    (if (currentLanguage == Language.BENGALI) "অসাধারণ পারফরম্যান্স! আপনি দুর্দান্ত গতিতে এগোচ্ছেন।" else "Peak Performance! You're dominating your goals.")
                                else
                                    (if (currentLanguage == Language.BENGALI) "ধারাবাহিক থাকুন! আরও কয়েকটি কাজ শেষ করুন।" else "Stay consistent! Complete more tasks to boost momentum."),
                                fontSize = 13.sp,
                                color = TextSecondaryDark,
                                lineHeight = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Circular Gauge
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(92.dp)
                        ) {
                            Canvas(modifier = Modifier.size(92.dp)) {
                                drawArc(
                                    color = SurfaceElevatedDark,
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    brush = Brush.sweepGradient(listOf(PurplePrimary, BlueElectric, GreenNeon, PurplePrimary)),
                                    startAngle = -90f,
                                    sweepAngle = (animatedScore / 100f) * 360f,
                                    useCenter = false,
                                    style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${stats.score}%",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(18.dp)) }

        // Metrics Grid (Tasks, Habits, Focus Time, Completed)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = AppStrings.get("completed", currentLanguage),
                    value = "${stats.completedTasks}/${stats.totalTasks}",
                    subtitle = if (currentLanguage == Language.BENGALI) "কাজ সম্পন্ন" else "Tasks Done",
                    icon = Icons.Default.CheckCircle,
                    color = GreenNeon,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(MainTab.TASKS) }
                )
                MetricCard(
                    title = AppStrings.get("habit_streak", currentLanguage),
                    value = "${stats.habitCompletionPercent}%",
                    subtitle = if (currentLanguage == Language.BENGALI) "অভ্যাস বজায়" else "Habit Consistency",
                    icon = Icons.Default.LocalFireDepartment,
                    color = AmberWarning,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(MainTab.HABITS) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = AppStrings.get("focus_time", currentLanguage),
                    value = stats.focusHoursFormatted,
                    subtitle = if (currentLanguage == Language.BENGALI) "গভীর মনোযোগ" else "Deep Work",
                    icon = Icons.Default.Timer,
                    color = BlueElectric,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(MainTab.POMODORO) }
                )
                MetricCard(
                    title = AppStrings.get("pending", currentLanguage),
                    value = "${stats.pendingTasks}",
                    subtitle = if (currentLanguage == Language.BENGALI) "বাকি কাজ" else "Pending Items",
                    icon = Icons.Default.PendingActions,
                    color = PurpleLight,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(MainTab.TASKS) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Quick Actions Row
        item {
            Text(
                text = AppStrings.get("quick_actions", currentLanguage),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    QuickActionPill(
                        icon = Icons.Default.Add,
                        label = AppStrings.get("add_task", currentLanguage),
                        color = GreenNeon,
                        onClick = { onNavigateTab(MainTab.TASKS) }
                    )
                }
                item {
                    QuickActionPill(
                        icon = Icons.Default.PlayArrow,
                        label = AppStrings.get("start_focus", currentLanguage),
                        color = BlueElectric,
                        onClick = { onNavigateTab(MainTab.POMODORO) }
                    )
                }
                item {
                    QuickActionPill(
                        icon = Icons.Default.AutoAwesome,
                        label = if (currentLanguage == Language.BENGALI) "এআই পরিকল্পনা" else "AI Planner",
                        color = PurplePrimary,
                        onClick = { onNavigateTab(MainTab.AI_ASSISTANT) }
                    )
                }
                item {
                    QuickActionPill(
                        icon = Icons.Default.NoteAlt,
                        label = AppStrings.get("new_note", currentLanguage),
                        color = AmberWarning,
                        onClick = { onNavigateTab(MainTab.NOTES) }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // Upcoming Reminders & Tasks Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.get("upcoming_reminders", currentLanguage),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TextButton(onClick = { onNavigateTab(MainTab.TASKS) }) {
                    Text(
                        text = if (currentLanguage == Language.BENGALI) "সব দেখুন" else "View All",
                        color = PurpleLight,
                        fontSize = 13.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        val topTasks = tasks.take(4)
        if (topTasks.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = GreenNeon,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (currentLanguage == Language.BENGALI) "সব কাজ সম্পন্ন! নতুন কাজ যোগ করুন।" else "All caught up! Add a new task to conquer today.",
                                color = TextSecondaryDark,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            items(topTasks, key = { it.id }) { task ->
                TaskRowDashboard(
                    task = task,
                    onToggle = { onToggleTask(task) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = modifier
            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextMutedDark
            )
        }
    }
}

@Composable
fun QuickActionPill(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier
            .clickable { onClick() }
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun TaskRowDashboard(
    task: TaskEntity,
    onToggle: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = SurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(32.dp)
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
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) TextMutedDark else Color.White
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        maxLines = 1
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (task.priority.name) {
                    "HIGH" -> RedPriority.copy(alpha = 0.2f)
                    "MEDIUM" -> AmberWarning.copy(alpha = 0.2f)
                    else -> BlueElectric.copy(alpha = 0.2f)
                }
            ) {
                Text(
                    text = task.dueTimeString,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (task.priority.name) {
                        "HIGH" -> RedPriority
                        "MEDIUM" -> AmberWarning
                        else -> BlueElectric
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}
