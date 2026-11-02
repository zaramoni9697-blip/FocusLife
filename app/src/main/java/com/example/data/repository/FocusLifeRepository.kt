package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FocusLifeRepository(private val db: AppDatabase) {
    val allTasks: Flow<List<TaskEntity>> = db.taskDao().getAllTasks()
    val allNotes: Flow<List<NoteEntity>> = db.noteDao().getAllNotes()
    val allHabits: Flow<List<HabitEntity>> = db.habitDao().getAllHabits()
    val allEvents: Flow<List<CalendarEventEntity>> = db.calendarEventDao().getAllEvents()
    val allSessions: Flow<List<FocusSessionEntity>> = db.focusSessionDao().getAllSessions()
    val allNotifications: Flow<List<NotificationEntity>> = db.notificationDao().getAllNotifications()
    val userProfile: Flow<UserProfileEntity?> = db.userDao().getUserProfile()

    val totalTasksCount: Flow<Int> = db.taskDao().getTotalTasksCount()
    val completedTasksCount: Flow<Int> = db.taskDao().getCompletedTasksCount()
    val pendingTasksCount: Flow<Int> = db.taskDao().getPendingTasksCount()
    val totalFocusMinutes: Flow<Int?> = db.focusSessionDao().getTotalFocusMinutes()
    val totalFocusSessionsCount: Flow<Int> = db.focusSessionDao().getTotalFocusSessionsCount()

    // Tasks
    suspend fun insertTask(task: TaskEntity): Long = withContext(Dispatchers.IO) {
        db.taskDao().insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        db.taskDao().updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        db.taskDao().deleteTask(task)
    }

    suspend fun deleteTaskById(id: Long) = withContext(Dispatchers.IO) {
        db.taskDao().deleteTaskById(id)
    }

    suspend fun toggleTaskCompleted(task: TaskEntity) = withContext(Dispatchers.IO) {
        val newStatus = !task.isCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        db.taskDao().setTaskCompleted(task.id, newStatus, completedAt)

        if (newStatus) {
            db.notificationDao().insertNotification(
                NotificationEntity(
                    title = "Task Completed!",
                    body = "\"${task.title}\" is marked as done. Great progress!",
                    category = "TASK"
                )
            )
        }
    }

    // Notes
    suspend fun insertNote(note: NoteEntity): Long = withContext(Dispatchers.IO) {
        db.noteDao().insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        db.noteDao().updateNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        db.noteDao().deleteNote(note)
    }

    suspend fun togglePinNote(note: NoteEntity) = withContext(Dispatchers.IO) {
        db.noteDao().togglePin(note.id, !note.isPinned)
    }

    // Habits
    suspend fun insertHabit(habit: HabitEntity): Long = withContext(Dispatchers.IO) {
        db.habitDao().insertHabit(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        db.habitDao().updateHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        db.habitDao().deleteHabit(habit)
    }

    suspend fun toggleHabitToday(habit: HabitEntity, todayStr: String) = withContext(Dispatchers.IO) {
        val completedList = habit.completedHistoryDates
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toMutableList()

        val isAlreadyDone = completedList.contains(todayStr)
        val newStreak: Int
        if (isAlreadyDone) {
            completedList.remove(todayStr)
            newStreak = (habit.currentStreak - 1).coerceAtLeast(0)
        } else {
            completedList.add(todayStr)
            newStreak = habit.currentStreak + 1
        }

        val updated = habit.copy(
            lastCompletedDate = if (!isAlreadyDone) todayStr else habit.lastCompletedDate,
            completedHistoryDates = completedList.joinToString(","),
            currentStreak = newStreak,
            bestStreak = maxOf(habit.bestStreak, newStreak)
        )
        db.habitDao().updateHabit(updated)

        if (!isAlreadyDone) {
            db.notificationDao().insertNotification(
                NotificationEntity(
                    title = "Habit Streak +1! 🔥",
                    body = "Completed \"${habit.title}\"! Streak is now $newStreak days.",
                    category = "HABIT"
                )
            )
        }
    }

    // Calendar
    suspend fun insertEvent(event: CalendarEventEntity): Long = withContext(Dispatchers.IO) {
        db.calendarEventDao().insertEvent(event)
    }

    suspend fun updateEvent(event: CalendarEventEntity) = withContext(Dispatchers.IO) {
        db.calendarEventDao().updateEvent(event)
    }

    suspend fun deleteEvent(event: CalendarEventEntity) = withContext(Dispatchers.IO) {
        db.calendarEventDao().deleteEvent(event)
    }

    // Focus Sessions
    suspend fun logFocusSession(durationMinutes: Int, sessionType: String, label: String) = withContext(Dispatchers.IO) {
        val session = FocusSessionEntity(
            durationMinutes = durationMinutes,
            sessionType = sessionType,
            label = label
        )
        db.focusSessionDao().insertSession(session)

        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "Focus Session Finished! ⏱️",
                body = "Completed $durationMinutes mins of deep work. Great job staying focused!",
                category = "POMODORO"
            )
        )
    }

    // Notifications
    suspend fun insertNotification(notification: NotificationEntity) = withContext(Dispatchers.IO) {
        db.notificationDao().insertNotification(notification)
    }

    suspend fun markAllNotificationsRead() = withContext(Dispatchers.IO) {
        db.notificationDao().markAllAsRead()
    }

    suspend fun clearAllNotifications() = withContext(Dispatchers.IO) {
        db.notificationDao().clearAllNotifications()
    }

    // User Profile
    suspend fun saveUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        db.userDao().insertOrUpdateProfile(profile)
    }

    suspend fun seedSampleDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingTasks = db.taskDao().getAllTasks().first()
        if (existingTasks.isNotEmpty()) return@withContext

        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        val todayFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        // Seed default User Profile
        db.userDao().insertOrUpdateProfile(
            UserProfileEntity(
                id = "local_user",
                fullName = "Productivity Achiever",
                email = "achiever@focuslife.app",
                avatarId = 1,
                isGuest = true
            )
        )

        // Seed Sample Tasks
        db.taskDao().insertTask(
            TaskEntity(
                title = "Complete Product Strategy Presentation",
                description = "Finalize quarterly milestones and key performance indicators.",
                priority = TaskPriority.HIGH,
                category = TaskCategory.WORK,
                dueDateEpochMs = now,
                dueTimeString = "14:00",
                hasReminder = true
            )
        )
        db.taskDao().insertTask(
            TaskEntity(
                title = "Review Machine Learning System Architecture",
                description = "Study transformer attention layers and inference pipeline.",
                priority = TaskPriority.HIGH,
                category = TaskCategory.STUDY,
                dueDateEpochMs = now + 86400000L,
                dueTimeString = "17:30",
                hasReminder = true
            )
        )
        db.taskDao().insertTask(
            TaskEntity(
                title = "Daily 30-Minute Cardio & Stretching",
                description = "Morning jogging in the park followed by full-body mobility exercises.",
                priority = TaskPriority.MEDIUM,
                category = TaskCategory.HEALTH,
                dueDateEpochMs = now,
                dueTimeString = "07:30",
                isCompleted = true,
                completedAtEpochMs = now - 3600000L
            )
        )
        db.taskDao().insertTask(
            TaskEntity(
                title = "Budget Planning & Monthly Financial Audit",
                description = "Analyze savings rate, investment portfolio, and subscriptions.",
                priority = TaskPriority.LOW,
                category = TaskCategory.PERSONAL,
                dueDateEpochMs = now + 172800000L,
                dueTimeString = "20:00"
            )
        )

        // Seed Sample Habits
        db.habitDao().insertHabit(
            HabitEntity(
                title = "Morning Meditation & Mindfulness",
                description = "10 minutes of box breathing and grounding before screens.",
                iconName = "spa",
                colorHex = "#8B5CF6",
                targetDaysPerWeek = 7,
                currentStreak = 6,
                bestStreak = 14,
                lastCompletedDate = todayFmt,
                completedHistoryDates = "$todayFmt,2026-11-01,2026-10-31"
            )
        )
        db.habitDao().insertHabit(
            HabitEntity(
                title = "Deep Focus Reading (25 pages)",
                description = "Read non-fiction books on philosophy, science, or leadership.",
                iconName = "menu_book",
                colorHex = "#38BDF8",
                targetDaysPerWeek = 5,
                currentStreak = 4,
                bestStreak = 9,
                lastCompletedDate = todayFmt,
                completedHistoryDates = "$todayFmt,2026-11-01"
            )
        )
        db.habitDao().insertHabit(
            HabitEntity(
                title = "Drink 2.5 Liters of Water",
                description = "Stay hydrated throughout the day with regular intervals.",
                iconName = "water_drop",
                colorHex = "#10B981",
                targetDaysPerWeek = 7,
                currentStreak = 12,
                bestStreak = 12,
                lastCompletedDate = todayFmt,
                completedHistoryDates = "$todayFmt,2026-11-01,2026-10-31,2026-10-30"
            )
        )

        // Seed Sample Notes
        db.noteDao().insertNote(
            NoteEntity(
                title = "Core Life Principles & Vision 2026",
                content = "1. Focus on signal over noise.\n2. Master compound habits.\n3. Protect deep work blocks every morning.\n4. Maintain physical energy through consistent sleep and nutrition.",
                colorHex = "#8B5CF6",
                isPinned = true,
                tag = "Vision"
            )
        )
        db.noteDao().insertNote(
            NoteEntity(
                title = "Weekly Review Checklist",
                content = "• Clean up task inbox\n• Celebrate wins of the week\n• Identify friction points and solve them\n• Plan top 3 priorities for next week",
                colorHex = "#38BDF8",
                isPinned = false,
                tag = "Productivity"
            )
        )

        // Seed Sample Calendar Events
        db.calendarEventDao().insertEvent(
            CalendarEventEntity(
                title = "Product Strategy Sync",
                description = "Sprint goals alignment with design and engineering teams",
                startEpochMs = now + 7200000L,
                endEpochMs = now + 10800000L,
                location = "Virtual Meeting Room",
                colorHex = "#8B5CF6"
            )
        )
        db.calendarEventDao().insertEvent(
            CalendarEventEntity(
                title = "Deep Focus Block: Architecture Design",
                description = "Zero interruptions. Phone on silent.",
                startEpochMs = now + 18000000L,
                endEpochMs = now + 25200000L,
                location = "Home Office",
                colorHex = "#10B981"
            )
        )

        // Seed Sample Focus Sessions
        db.focusSessionDao().insertSession(
            FocusSessionEntity(
                durationMinutes = 25,
                sessionType = "FOCUS",
                label = "Sprint Planning",
                completedAtEpochMs = now - 7200000L
            )
        )
        db.focusSessionDao().insertSession(
            FocusSessionEntity(
                durationMinutes = 25,
                sessionType = "FOCUS",
                label = "Code Review",
                completedAtEpochMs = now - 3600000L
            )
        )

        // Seed Sample Notifications
        db.notificationDao().insertNotification(
            NotificationEntity(
                title = "Welcome to FocusLife!",
                body = "Your workspace is ready. Plan your goals, build habits, and grow.",
                category = "PLANNING"
            )
        )
    }

    suspend fun exportAllDataAsJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        val tasks = db.taskDao().getAllTasks().first()
        val notes = db.noteDao().getAllNotes().first()
        val habits = db.habitDao().getAllHabits().first()
        val events = db.calendarEventDao().getAllEvents().first()

        val tasksArray = JSONArray()
        tasks.forEach {
            val obj = JSONObject()
            obj.put("title", it.title)
            obj.put("description", it.description)
            obj.put("priority", it.priority.name)
            obj.put("category", it.category.name)
            obj.put("isCompleted", it.isCompleted)
            tasksArray.put(obj)
        }
        root.put("tasks", tasksArray)

        val habitsArray = JSONArray()
        habits.forEach {
            val obj = JSONObject()
            obj.put("title", it.title)
            obj.put("currentStreak", it.currentStreak)
            obj.put("bestStreak", it.bestStreak)
            habitsArray.put(obj)
        }
        root.put("habits", habitsArray)

        val notesArray = JSONArray()
        notes.forEach {
            val obj = JSONObject()
            obj.put("title", it.title)
            obj.put("content", it.content)
            notesArray.put(obj)
        }
        root.put("notes", notesArray)

        root.put("exportedAt", System.currentTimeMillis())
        root.put("version", "1.0")
        root.toString(2)
    }
}
