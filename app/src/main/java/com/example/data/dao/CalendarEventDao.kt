package com.example.data.dao

import androidx.room.*
import com.example.data.model.CalendarEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEventDao {
    @Query("SELECT * FROM calendar_events ORDER BY startEpochMs ASC")
    fun getAllEvents(): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CalendarEventEntity): Long

    @Update
    suspend fun updateEvent(event: CalendarEventEntity)

    @Delete
    suspend fun deleteEvent(event: CalendarEventEntity)

    @Query("DELETE FROM calendar_events WHERE id = :id")
    suspend fun deleteEventById(id: Long)
}
