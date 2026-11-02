package com.example.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = FocusLifeNotificationManager(context)

        when (intent.action) {
            FocusLifeNotificationManager.ACTION_TASK_REMINDER -> {
                val taskId = intent.getLongExtra(FocusLifeNotificationManager.EXTRA_TASK_ID, -1L)
                val taskTitle = intent.getStringExtra(FocusLifeNotificationManager.EXTRA_TASK_TITLE) ?: "Task Reminder"
                notificationManager.showNotification(
                    id = (taskId.takeIf { it > 0 } ?: System.currentTimeMillis()).toInt(),
                    title = "Priority Reminder 📌",
                    message = "Don't forget: $taskTitle",
                    channelId = FocusLifeNotificationManager.CHANNEL_TASKS,
                    taskId = if (taskId > 0) taskId else null
                )
            }
            FocusLifeNotificationManager.ACTION_HABIT_REMINDER -> {
                val habitTitle = intent.getStringExtra(FocusLifeNotificationManager.EXTRA_TASK_TITLE) ?: "Daily Habit"
                notificationManager.showNotification(
                    id = 1002,
                    title = "Keep Your Streak Alive! 🔥",
                    message = "Time for your habit: $habitTitle",
                    channelId = FocusLifeNotificationManager.CHANNEL_HABITS
                )
            }
            FocusLifeNotificationManager.ACTION_POMODORO_COMPLETE -> {
                notificationManager.showNotification(
                    id = 1003,
                    title = "Pomodoro Finished! ⏱️",
                    message = "Well done! Take a well-deserved 5-minute break.",
                    channelId = FocusLifeNotificationManager.CHANNEL_POMODORO
                )
                triggerVibration(context)
            }
            FocusLifeNotificationManager.ACTION_COMPLETE_TASK -> {
                val taskId = intent.getLongExtra(FocusLifeNotificationManager.EXTRA_TASK_ID, -1L)
                val notifId = intent.getIntExtra(FocusLifeNotificationManager.EXTRA_NOTIF_ID, -1)

                if (notifId != -1) {
                    val sysNotifManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    sysNotifManager.cancel(notifId)
                }

                if (taskId > 0) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getDatabase(context)
                        db.taskDao().setTaskCompleted(taskId, true, System.currentTimeMillis())
                    }
                }
            }
        }
    }

    private fun triggerVibration(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 400), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 300, 150, 400), -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(500)
                }
            }
        } catch (_: Exception) {}
    }
}
