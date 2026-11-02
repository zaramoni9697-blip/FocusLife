package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications_log")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val body: String,
    val category: String, // "TASK", "HABIT", "POMODORO", "PLANNING"
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
