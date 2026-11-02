package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "local_user",
    val fullName: String = "Productivity Champion",
    val email: String = "focus@focuslife.app",
    val avatarId: Int = 1, // 1 to 6 avatar indexes
    val isGuest: Boolean = true,
    val joinedEpochMs: Long = System.currentTimeMillis()
)
