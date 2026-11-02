package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority {
    HIGH, MEDIUM, LOW
}

enum class TaskCategory {
    WORK, STUDY, PERSONAL, HEALTH
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val category: TaskCategory = TaskCategory.PERSONAL,
    val dueDateEpochMs: Long = System.currentTimeMillis(),
    val dueTimeString: String = "18:00",
    val isCompleted: Boolean = false,
    val completedAtEpochMs: Long? = null,
    val hasReminder: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)
