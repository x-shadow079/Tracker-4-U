package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        StreakRecordEntity::class,
        BlockedDomainEntity::class,
        UsageLogEntity::class,
        GameRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TrackerDatabase : RoomDatabase() {

    abstract fun trackerDao(): TrackerDao

    companion object {
        @Volatile
        private var INSTANCE: TrackerDatabase? = null

        fun getDatabase(context: Context): TrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackerDatabase::class.java,
                    "tracker_4u_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
