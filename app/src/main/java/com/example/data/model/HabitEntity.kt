package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val iconName: String = "bolt",
    val colorHex: String = "#10B981", // Neon Green
    val targetDaysPerWeek: Int = 7,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: String = "", // "YYYY-MM-DD"
    val completedHistoryDates: String = "", // Comma-separated "YYYY-MM-DD,..."
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
