package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val startEpochMs: Long,
    val endEpochMs: Long,
    val location: String = "",
    val colorHex: String = "#38BDF8", // Electric Blue
    val isAllDay: Boolean = false,
    val reminderMinutesBefore: Int = 15
)
