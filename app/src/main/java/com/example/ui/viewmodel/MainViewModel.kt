package com.example.ui.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.*
import com.example.data.repository.FocusLifeRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.localization.Language
import com.example.notification.FocusLifeNotificationManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class PomodoroMode(val defaultMinutes: Int, val labelEn: String, val labelBn: String) {
    FOCUS(25, "Focus Session", "ফোকাস সেশন"),
    SHORT_BREAK(5, "Short Break", "ছোট বিরতি"),
    LONG_BREAK(15, "Long Break", "দীর্ঘ বিরতি")
}

data class TimerState(
    val mode: PomodoroMode = PomodoroMode.FOCUS,
    val secondsRemaining: Int = 25 * 60,
    val isRunning: Boolean = false,
    val completedSessions: Int = 0
)

data class ProductivityStats(
    val score: Int = 0,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val pendingTasks: Int = 0,
    val habitCompletionPercent: Int = 0,
    val focusHoursFormatted: String = "0.0h"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = FocusLifeRepository(db)
    val userPrefs = UserPreferencesRepository(application)
    val notificationManager = FocusLifeNotificationManager(application)

    // Data streams
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendarEvents: StateFlow<List<CalendarEventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Preferences
    val language: StateFlow<Language> = userPrefs.language
    val isDarkTheme: StateFlow<Boolean> = userPrefs.darkTheme
    val isOnboardingCompleted: StateFlow<Boolean> = userPrefs.onboardingCompleted
    val notificationsEnabled: StateFlow<Boolean> = userPrefs.notificationsEnabled
    val hapticsEnabled: StateFlow<Boolean> = userPrefs.hapticsEnabled

    // Pomodoro Timer State
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()
    private var countDownTimer: CountDownTimer? = null

    // Filter states
    val taskSearchQuery = MutableStateFlow("")
    val taskFilterCategory = MutableStateFlow<TaskCategory?>(null)
    val noteSearchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    // Computed Productivity Stats
    val productivityStats: StateFlow<ProductivityStats> = combine(
        tasks,
        habits,
        repository.totalFocusMinutes
    ) { currentTasks, currentHabits, totalMinutes ->
        val totalT = currentTasks.size
        val completedT = currentTasks.count { it.isCompleted }
        val pendingT = totalT - completedT

        val todayFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val completedHabitsToday = currentHabits.count {
            it.completedHistoryDates.contains(todayFmt)
        }
        val habitPercent = if (currentHabits.isNotEmpty()) {
            ((completedHabitsToday.toFloat() / currentHabits.size) * 100).toInt()
        } else 0

        val taskPercent = if (totalT > 0) ((completedT.toFloat() / totalT) * 100).toInt() else 0
        val mins = totalMinutes ?: 0
        val focusScore = (mins / 25 * 10).coerceAtMost(30)

        // Balanced Score: 40% tasks + 40% habits + 20% focus time
        val rawScore = ((taskPercent * 0.4) + (habitPercent * 0.4) + focusScore).toInt().coerceIn(0, 100)
        val focusHours = String.format(Locale.US, "%.1fh", mins / 60.0)

        ProductivityStats(
            score = if (rawScore == 0 && (totalT > 0 || currentHabits.isNotEmpty())) 45 else rawScore,
            totalTasks = totalT,
            completedTasks = completedT,
            pendingTasks = pendingT,
            habitCompletionPercent = habitPercent,
            focusHoursFormatted = focusHours
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProductivityStats())

    // Language & Theme
    fun setLanguage(lang: Language) {
        userPrefs.setLanguage(lang)
    }

    fun toggleLanguage() {
        val next = if (language.value == Language.ENGLISH) Language.BENGALI else Language.ENGLISH
        userPrefs.setLanguage(next)
    }

    fun setDarkTheme(isDark: Boolean) {
        userPrefs.setDarkTheme(isDark)
    }

    fun completeOnboarding() {
        userPrefs.setOnboardingCompleted(true)
    }

    // Tasks Actions
    fun createTask(
        title: String,
        description: String,
        priority: TaskPriority,
        category: TaskCategory,
        dueDateEpochMs: Long,
        dueTimeString: String,
        hasReminder: Boolean
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title.trim(),
                description = description.trim(),
                priority = priority,
                category = category,
                dueDateEpochMs = dueDateEpochMs,
                dueTimeString = dueTimeString,
                hasReminder = hasReminder
            )
            val newId = repository.insertTask(task)
            if (hasReminder) {
                notificationManager.scheduleTaskReminder(newId, title, dueDateEpochMs)
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            if (task.hasReminder) {
                notificationManager.scheduleTaskReminder(task.id, task.title, task.dueDateEpochMs)
            }
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Notes Actions
    fun saveNote(
        id: Long,
        title: String,
        content: String,
        colorHex: String,
        isPinned: Boolean,
        tag: String
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                repository.insertNote(
                    NoteEntity(
                        title = title.trim(),
                        content = content.trim(),
                        colorHex = colorHex,
                        isPinned = isPinned,
                        tag = tag
                    )
                )
            } else {
                repository.updateNote(
                    NoteEntity(
                        id = id,
                        title = title.trim(),
                        content = content.trim(),
                        colorHex = colorHex,
                        isPinned = isPinned,
                        tag = tag,
                        updatedAtEpochMs = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun togglePinNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.togglePinNote(note)
        }
    }

    // Habits Actions
    fun createHabit(title: String, description: String, iconName: String, colorHex: String, targetDays: Int) {
        viewModelScope.launch {
            repository.insertHabit(
                HabitEntity(
                    title = title.trim(),
                    description = description.trim(),
                    iconName = iconName,
                    colorHex = colorHex,
                    targetDaysPerWeek = targetDays
                )
            )
        }
    }

    fun toggleHabitToday(habit: HabitEntity) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            repository.toggleHabitToday(habit, todayStr)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // Calendar Events Actions
    fun createEvent(
        title: String,
        description: String,
        startEpochMs: Long,
        endEpochMs: Long,
        location: String,
        colorHex: String
    ) {
        viewModelScope.launch {
            repository.insertEvent(
                CalendarEventEntity(
                    title = title.trim(),
                    description = description.trim(),
                    startEpochMs = startEpochMs,
                    endEpochMs = endEpochMs,
                    location = location.trim(),
                    colorHex = colorHex
                )
            )
        }
    }

    fun deleteEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    // Pomodoro Timer Controls
    fun setPomodoroMode(mode: PomodoroMode) {
        countDownTimer?.cancel()
        _timerState.value = _timerState.value.copy(
            mode = mode,
            secondsRemaining = mode.defaultMinutes * 60,
            isRunning = false
        )
    }

    fun startTimer() {
        if (_timerState.value.isRunning) return
        val totalMs = _timerState.value.secondsRemaining * 1000L
        _timerState.value = _timerState.value.copy(isRunning = true)

        countDownTimer = object : CountDownTimer(totalMs, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _timerState.value = _timerState.value.copy(
                    secondsRemaining = (millisUntilFinished / 1000).toInt()
                )
            }

            override fun onFinish() {
                val current = _timerState.value
                val newSessions = current.completedSessions + if (current.mode == PomodoroMode.FOCUS) 1 else 0
                _timerState.value = current.copy(
                    secondsRemaining = 0,
                    isRunning = false,
                    completedSessions = newSessions
                )

                // Log focus session to DB
                if (current.mode == PomodoroMode.FOCUS) {
                    viewModelScope.launch {
                        repository.logFocusSession(
                            durationMinutes = current.mode.defaultMinutes,
                            sessionType = "FOCUS",
                            label = "Deep Work Session"
                        )
                    }
                }

                // Native notification alert
                notificationManager.showNotification(
                    id = 2001,
                    title = "Pomodoro Completed! ⏱️",
                    message = if (current.mode == PomodoroMode.FOCUS)
                        "Session finished! Great job staying in the zone."
                    else "Break finished! Ready to dive back in?",
                    channelId = FocusLifeNotificationManager.CHANNEL_POMODORO
                )
            }
        }.start()
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        val defaultSecs = _timerState.value.mode.defaultMinutes * 60
        _timerState.value = _timerState.value.copy(
            secondsRemaining = defaultSecs,
            isRunning = false
        )
    }

    // Notifications
    fun sendTestNotification() {
        notificationManager.showNotification(
            id = (System.currentTimeMillis() % 10000).toInt(),
            title = "FocusLife Native Alert 🔔",
            message = "Lock screen and banner notifications are active and working perfectly!",
            channelId = FocusLifeNotificationManager.CHANNEL_PLANNING
        )
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun clearNotifications() {
        viewModelScope.launch {
            repository.clearAllNotifications()
        }
    }

    // Profile & Auth
    fun updateProfile(name: String, email: String, avatarId: Int) {
        viewModelScope.launch {
            val updated = UserProfileEntity(
                id = "local_user",
                fullName = name.trim(),
                email = email.trim(),
                avatarId = avatarId,
                isGuest = false
            )
            repository.saveUserProfile(updated)
        }
    }

    fun logout() {
        viewModelScope.launch {
            val guest = UserProfileEntity(
                id = "local_user",
                fullName = "Guest User",
                email = "guest@focuslife.app",
                avatarId = 1,
                isGuest = true
            )
            repository.saveUserProfile(guest)
        }
    }

    suspend fun exportDataJson(): String {
        return repository.exportAllDataAsJson()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
