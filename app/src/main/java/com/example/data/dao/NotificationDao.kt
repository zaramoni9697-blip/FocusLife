package com.example.data.dao

import androidx.room.*
import com.example.data.model.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications_log ORDER BY timestampEpochMs DESC LIMIT 50")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications_log SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()

    @Query("DELETE FROM notifications_log")
    suspend fun clearAllNotifications()
}
