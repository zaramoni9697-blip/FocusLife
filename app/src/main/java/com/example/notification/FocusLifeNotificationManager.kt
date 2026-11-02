package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class FocusLifeNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Channel: Tasks
            val taskChannel = NotificationChannel(
                CHANNEL_TASKS,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled deadlines and priority tasks"
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // Channel: Habits
            val habitChannel = NotificationChannel(
                CHANNEL_HABITS,
                "Habit Check-ins",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily habit streak reminders and celebrations"
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // Channel: Pomodoro
            val pomodoroChannel = NotificationChannel(
                CHANNEL_POMODORO,
                "Pomodoro Focus Timer",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when focus sessions and breaks complete"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // Channel: Calendar
            val calendarChannel = NotificationChannel(
                CHANNEL_CALENDAR,
                "Calendar & Events",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Meeting schedules and upcoming events"
                enableVibration(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // Channel: Daily Planning
            val planningChannel = NotificationChannel(
                CHANNEL_PLANNING,
                "Daily Planning",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Morning goal setting and evening productivity summaries"
                lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            }

            notificationManager.createNotificationChannels(
                listOf(taskChannel, habitChannel, pomodoroChannel, calendarChannel, planningChannel)
            )
        }
    }

    fun showNotification(
        id: Int,
        title: String,
        message: String,
        channelId: String = CHANNEL_TASKS,
        taskId: Long? = null
    ) {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            id,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Action button if it's a task reminder: "Mark Complete"
        if (taskId != null) {
            val completeIntent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                action = ACTION_COMPLETE_TASK
                putExtra(EXTRA_TASK_ID, taskId)
                putExtra(EXTRA_NOTIF_ID, id)
            }
            val completePendingIntent = PendingIntent.getBroadcast(
                context,
                id + 10000,
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(
                android.R.drawable.checkbox_on_background,
                "Mark Done",
                completePendingIntent
            )
        }

        notificationManager.notify(id, builder.build())
    }

    fun scheduleTaskReminder(taskId: Long, title: String, triggerEpochMs: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ACTION_TASK_REMINDER
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_TASK_TITLE, title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerEpochMs,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerEpochMs,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // In case exact alarm permission is restricted on specific devices
        }
    }

    companion object {
        const val CHANNEL_TASKS = "channel_tasks"
        const val CHANNEL_HABITS = "channel_habits"
        const val CHANNEL_POMODORO = "channel_pomodoro"
        const val CHANNEL_CALENDAR = "channel_calendar"
        const val CHANNEL_PLANNING = "channel_planning"

        const val ACTION_TASK_REMINDER = "com.example.focuslife.ACTION_TASK_REMINDER"
        const val ACTION_HABIT_REMINDER = "com.example.focuslife.ACTION_HABIT_REMINDER"
        const val ACTION_COMPLETE_TASK = "com.example.focuslife.ACTION_COMPLETE_TASK"
        const val ACTION_POMODORO_COMPLETE = "com.example.focuslife.ACTION_POMODORO_COMPLETE"

        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_NOTIF_ID = "extra_notif_id"
    }
}
