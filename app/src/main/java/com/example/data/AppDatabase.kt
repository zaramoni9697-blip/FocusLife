package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.*
import com.example.data.model.*

@Database(
    entities = [
        TaskEntity::class,
        NoteEntity::class,
        HabitEntity::class,
        CalendarEventEntity::class,
        FocusSessionEntity::class,
        NotificationEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun habitDao(): HabitDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "focuslife_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
