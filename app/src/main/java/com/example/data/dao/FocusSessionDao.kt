package com.example.data.dao

import androidx.room.*
import com.example.data.model.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY completedAtEpochMs DESC")
    fun getAllSessions(): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Query("SELECT SUM(durationMinutes) FROM focus_sessions WHERE sessionType = 'FOCUS'")
    fun getTotalFocusMinutes(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE sessionType = 'FOCUS'")
    fun getTotalFocusSessionsCount(): Flow<Int>
}
