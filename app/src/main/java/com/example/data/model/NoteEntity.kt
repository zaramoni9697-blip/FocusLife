package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val colorHex: String = "#8B5CF6", // Purple default
    val isPinned: Boolean = false,
    val tag: String = "General",
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)
