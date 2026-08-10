package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_records")
data class StreakRecordEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val isClean: Boolean = true,
    val urgeLevel: Int = 0, // 0 to 5
    val reflectionNote: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "blocked_domains")
data class BlockedDomainEntity(
    @PrimaryKey val domain: String,
    val category: String = "Adult/18+",
    val timesBlocked: Int = 0,
    val isCustom: Boolean = false,
    val addedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "usage_logs")
data class UsageLogEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val totalScreenTimeSeconds: Long = 0,
    val shortsWatchedCount: Int = 0,
    val reelsWatchedCount: Int = 0,
    val dailyLimitMinutes: Int = 120,
    val dailyShortsLimit: Int = 15
)

@Entity(tableName = "game_records")
data class GameRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gameType: String, // "LUDO" or "CHESS"
    val mode: String, // "VS_AI", "WATCH_COMP_VS_COMP", "FRIEND_MODE"
    val result: String, // "WIN", "LOSS", "DRAW", "COMPLETED"
    val teamCode: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
