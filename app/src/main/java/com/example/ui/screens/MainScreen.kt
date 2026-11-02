package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localization.AppStrings
import com.example.ui.navigation.MainTab
import com.example.ui.screens.ai.AiAssistantScreen
import com.example.ui.screens.calendar.CalendarScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.habits.HabitTrackerScreen
import com.example.ui.screens.notes.NotesScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.pomodoro.PomodoroScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.stats.StatisticsScreen
import com.example.ui.screens.tasks.TasksScreen
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    aiViewModel: AiViewModel,
    onLogoutToAuth: () -> Unit
) {
    var currentTab by remember { mutableStateOf(MainTab.DASHBOARD) }

    val currentLanguage by mainViewModel.language.collectAsStateWithLifecycle()
    val isDarkTheme by mainViewModel.isDarkTheme.collectAsStateWithLifecycle()
    val notificationsEnabled by mainViewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val hapticsEnabled by mainViewModel.hapticsEnabled.collectAsStateWithLifecycle()

    val tasks by mainViewModel.tasks.collectAsStateWithLifecycle()
    val habits by mainViewModel.habits.collectAsStateWithLifecycle()
    val notes by mainViewModel.notes.collectAsStateWithLifecycle()
    val calendarEvents by mainViewModel.calendarEvents.collectAsStateWithLifecycle()
    val focusSessions by mainViewModel.focusSessions.collectAsStateWithLifecycle()
    val notifications by mainViewModel.notifications.collectAsStateWithLifecycle()
    val userProfile by mainViewModel.userProfile.collectAsStateWithLifecycle()
    val stats by mainViewModel.productivityStats.collectAsStateWithLifecycle()
    val timerState by mainViewModel.timerState.collectAsStateWithLifecycle()

    val aiMessages by aiViewModel.messages.collectAsStateWithLifecycle()
    val aiIsLoading by aiViewModel.isLoading.collectAsStateWithLifecycle()

    // Handle back button: return to Dashboard if in another tab
    BackHandler(enabled = currentTab != MainTab.DASHBOARD) {
        currentTab = MainTab.DASHBOARD
    }

    val isSecondaryScreen = currentTab !in listOf(
        MainTab.DASHBOARD,
        MainTab.TASKS,
        MainTab.CALENDAR,
        MainTab.HABITS,
        MainTab.POMODORO
    )

    Scaffold(
        containerColor = if (isDarkTheme) BackgroundDark else BackgroundLight,
        topBar = {
            if (isSecondaryScreen) {
                // Secondary Screen Top App Bar with Back Arrow
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentTab = MainTab.DASHBOARD },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDarkTheme) SurfaceDark else SurfaceLight)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = if (isDarkTheme) Color.White else TextPrimaryLight
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = when (currentTab) {
                            MainTab.NOTES -> AppStrings.get("nav_notes", currentLanguage)
                            MainTab.AI_ASSISTANT -> AppStrings.get("nav_ai", currentLanguage)
                            MainTab.STATISTICS -> AppStrings.get("nav_stats", currentLanguage)
                            MainTab.NOTIFICATIONS -> AppStrings.get("nav_notifications", currentLanguage)
                            MainTab.PROFILE -> AppStrings.get("profile", currentLanguage)
                            MainTab.SETTINGS -> AppStrings.get("settings_title", currentLanguage)
                            else -> ""
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else TextPrimaryLight
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = if (isDarkTheme) SurfaceDark else SurfaceLight,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .border(
                        1.dp,
                        if (isDarkTheme) BorderDark else BorderLight,
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .testTag("main_bottom_nav")
            ) {
                // Tab 1: Dashboard
                NavigationBarItem(
                    selected = currentTab == MainTab.DASHBOARD,
                    onClick = { currentTab = MainTab.DASHBOARD },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.DASHBOARD) Icons.Default.Dashboard else Icons.Default.DashboardCustomize,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("nav_dashboard", currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainTab.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurplePrimary,
                        selectedTextColor = PurplePrimary,
                        indicatorColor = PurplePrimary.copy(alpha = 0.2f),
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag(MainTab.DASHBOARD.testTag)
                )

                // Tab 2: Tasks
                NavigationBarItem(
                    selected = currentTab == MainTab.TASKS,
                    onClick = { currentTab = MainTab.TASKS },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.TASKS) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Tasks"
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("nav_tasks", currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainTab.TASKS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GreenNeon,
                        selectedTextColor = GreenNeon,
                        indicatorColor = GreenNeon.copy(alpha = 0.2f),
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag(MainTab.TASKS.testTag)
                )

                // Tab 3: Calendar
                NavigationBarItem(
                    selected = currentTab == MainTab.CALENDAR,
                    onClick = { currentTab = MainTab.CALENDAR },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.CALENDAR) Icons.Default.CalendarMonth else Icons.Default.CalendarToday,
                            contentDescription = "Calendar"
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("nav_calendar", currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainTab.CALENDAR) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BlueElectric,
                        selectedTextColor = BlueElectric,
                        indicatorColor = BlueElectric.copy(alpha = 0.2f),
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag(MainTab.CALENDAR.testTag)
                )

                // Tab 4: Habits
                NavigationBarItem(
                    selected = currentTab == MainTab.HABITS,
                    onClick = { currentTab = MainTab.HABITS },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.HABITS) Icons.Default.LocalFireDepartment else Icons.Default.Whatshot,
                            contentDescription = "Habits"
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("nav_habits", currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainTab.HABITS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AmberWarning,
                        selectedTextColor = AmberWarning,
                        indicatorColor = AmberWarning.copy(alpha = 0.2f),
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag(MainTab.HABITS.testTag)
                )

                // Tab 5: Pomodoro Timer
                NavigationBarItem(
                    selected = currentTab == MainTab.POMODORO,
                    onClick = { currentTab = MainTab.POMODORO },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.POMODORO) Icons.Default.Timer else Icons.Default.HourglassEmpty,
                            contentDescription = "Timer"
                        )
                    },
                    label = {
                        Text(
                            text = AppStrings.get("nav_timer", currentLanguage),
                            fontSize = 11.sp,
                            fontWeight = if (currentTab == MainTab.POMODORO) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleLight,
                        selectedTextColor = PurpleLight,
                        indicatorColor = PurpleLight.copy(alpha = 0.2f),
                        unselectedIconColor = TextSecondaryDark,
                        unselectedTextColor = TextSecondaryDark
                    ),
                    modifier = Modifier.testTag(MainTab.POMODORO.testTag)
                )
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when (currentTab) {
                MainTab.DASHBOARD -> DashboardScreen(
                    currentLanguage = currentLanguage,
                    stats = stats,
                    tasks = tasks,
                    userProfile = userProfile,
                    onNavigateTab = { currentTab = it },
                    onToggleTask = { mainViewModel.toggleTask(it) },
                    onToggleLanguage = { mainViewModel.toggleLanguage() }
                )
                MainTab.TASKS -> TasksScreen(
                    currentLanguage = currentLanguage,
                    tasks = tasks,
                    onToggleTask = { mainViewModel.toggleTask(it) },
                    onDeleteTask = { mainViewModel.deleteTask(it) },
                    onSaveTask = { title, desc, priority, category, dueMs, dueTime, reminder ->
                        mainViewModel.createTask(title, desc, priority, category, dueMs, dueTime, reminder)
                    },
                    onUpdateTask = { mainViewModel.updateTask(it) }
                )
                MainTab.CALENDAR -> CalendarScreen(
                    currentLanguage = currentLanguage,
                    events = calendarEvents,
                    tasks = tasks,
                    onCreateEvent = { title, desc, startMs, endMs, loc, col ->
                        mainViewModel.createEvent(title, desc, startMs, endMs, loc, col)
                    },
                    onDeleteEvent = { mainViewModel.deleteEvent(it) }
                )
                MainTab.HABITS -> HabitTrackerScreen(
                    currentLanguage = currentLanguage,
                    habits = habits,
                    onToggleHabitToday = { mainViewModel.toggleHabitToday(it) },
                    onDeleteHabit = { mainViewModel.deleteHabit(it) },
                    onCreateHabit = { title, desc, icon, colorHex, targetDays ->
                        mainViewModel.createHabit(title, desc, icon, colorHex, targetDays)
                    }
                )
                MainTab.POMODORO -> PomodoroScreen(
                    currentLanguage = currentLanguage,
                    timerState = timerState,
                    focusSessions = focusSessions,
                    onSetMode = { mainViewModel.setPomodoroMode(it) },
                    onStartTimer = { mainViewModel.startTimer() },
                    onPauseTimer = { mainViewModel.pauseTimer() },
                    onResetTimer = { mainViewModel.resetTimer() }
                )
                MainTab.NOTES -> NotesScreen(
                    currentLanguage = currentLanguage,
                    notes = notes,
                    onSaveNote = { id, title, content, colorHex, isPinned, tag ->
                        mainViewModel.saveNote(id, title, content, colorHex, isPinned, tag)
                    },
                    onDeleteNote = { mainViewModel.deleteNote(it) },
                    onTogglePin = { mainViewModel.togglePinNote(it) }
                )
                MainTab.AI_ASSISTANT -> AiAssistantScreen(
                    currentLanguage = currentLanguage,
                    messages = aiMessages,
                    isLoading = aiIsLoading,
                    onSendMessage = { aiViewModel.sendMessage(it, currentLanguage) },
                    onClearChat = { aiViewModel.clearChat(currentLanguage) },
                    onToggleLanguage = { mainViewModel.toggleLanguage() }
                )
                MainTab.STATISTICS -> StatisticsScreen(
                    currentLanguage = currentLanguage,
                    stats = stats
                )
                MainTab.NOTIFICATIONS -> NotificationsScreen(
                    currentLanguage = currentLanguage,
                    notifications = notifications,
                    onSendTestNotification = { mainViewModel.sendTestNotification() },
                    onMarkAllRead = { mainViewModel.markNotificationsRead() },
                    onClearAll = { mainViewModel.clearNotifications() }
                )
                MainTab.PROFILE -> ProfileScreen(
                    currentLanguage = currentLanguage,
                    userProfile = userProfile,
                    stats = stats,
                    onUpdateProfile = { name, email, avatarId ->
                        mainViewModel.updateProfile(name, email, avatarId)
                    },
                    onLogout = {
                        mainViewModel.logout()
                        onLogoutToAuth()
                    },
                    onNavigateTab = { currentTab = it }
                )
                MainTab.SETTINGS -> SettingsScreen(
                    currentLanguage = currentLanguage,
                    isDarkTheme = isDarkTheme,
                    notificationsEnabled = notificationsEnabled,
                    hapticsEnabled = hapticsEnabled,
                    onSetLanguage = { mainViewModel.setLanguage(it) },
                    onSetDarkTheme = { mainViewModel.setDarkTheme(it) },
                    onExportData = { mainViewModel.exportDataJson() }
                )
            }
        }
    }
}
