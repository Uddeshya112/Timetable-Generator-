package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskItem::class,
        Course::class,
        ClassSession::class,
        MakeupOpportunity::class,
        AcademicNotification::class,
        OutboxEvent::class,
        UserAccount::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun courseDao(): CourseDao
    abstract fun timetableDao(): TimetableDao
    abstract fun makeupDao(): MakeupDao
    abstract fun notificationDao(): NotificationDao
    abstract fun outboxDao(): OutboxDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "intellischedule_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                SampleDataGenerator.populateInitialData(database)
                            }
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        scope.launch(Dispatchers.IO) {
                            INSTANCE?.let { database ->
                                try {
                                    if (database.courseDao().getCourseCount() == 0) {
                                        SampleDataGenerator.populateInitialData(database)
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
