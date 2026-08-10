package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {

    // Streaks
    @Query("SELECT * FROM streak_records ORDER BY date DESC")
    fun getAllStreakRecords(): Flow<List<StreakRecordEntity>>

    @Query("SELECT * FROM streak_records WHERE date = :date LIMIT 1")
    suspend fun getStreakRecordForDate(date: String): StreakRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreakRecord(record: StreakRecordEntity)

    // Blocked Domains
    @Query("SELECT * FROM blocked_domains ORDER BY timesBlocked DESC, domain ASC")
    fun getAllBlockedDomains(): Flow<List<BlockedDomainEntity>>

    @Query("SELECT * FROM blocked_domains WHERE domain = :domain LIMIT 1")
    suspend fun getBlockedDomain(domain: String): BlockedDomainEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedDomain(domain: BlockedDomainEntity)

    @Query("DELETE FROM blocked_domains WHERE domain = :domain")
    suspend fun deleteBlockedDomain(domain: String)

    @Query("UPDATE blocked_domains SET timesBlocked = timesBlocked + 1 WHERE domain = :domain")
    suspend fun incrementBlockedCount(domain: String)

    // Usage Logs
    @Query("SELECT * FROM usage_logs WHERE date = :date LIMIT 1")
    fun getUsageLogForDate(date: String): Flow<UsageLogEntity?>

    @Query("SELECT * FROM usage_logs WHERE date = :date LIMIT 1")
    suspend fun getUsageLogOneShot(date: String): UsageLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageLog(usageLog: UsageLogEntity)

    // Game Records
    @Query("SELECT * FROM game_records ORDER BY timestamp DESC LIMIT 20")
    fun getRecentGameRecords(): Flow<List<GameRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameRecord(record: GameRecordEntity)
}
