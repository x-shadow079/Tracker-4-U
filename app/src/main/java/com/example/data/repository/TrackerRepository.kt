package com.example.data.repository

import com.example.data.db.BlockedDomainEntity
import com.example.data.db.GameRecordEntity
import com.example.data.db.StreakRecordEntity
import com.example.data.db.TrackerDao
import com.example.data.db.UsageLogEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackerRepository(private val dao: TrackerDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayString(): String = dateFormat.format(Date())

    // Default built-in adult sites list
    val defaultAdultDomains = listOf(
        "xhamstar.com", "xhamster.com", "pornhub.com", "xvideos.com",
        "redtube.com", "youporn.com", "xnxx.com", "brazzers.com",
        "spankbang.com", "stripchat.com", "chaturbate.com", "eporner.com",
        "heavy-r.com", "hqporner.com", "beeg.com", "tnaflix.com",
        "motherless.com", "porn.com", "tube8.com", "rule34.xxx"
    )

    suspend fun initializeDefaultBlocklist() {
        for (domain in defaultAdultDomains) {
            val existing = dao.getBlockedDomain(domain)
            if (existing == null) {
                dao.insertBlockedDomain(
                    BlockedDomainEntity(
                        domain = domain,
                        category = "Adult/18+ Explicit",
                        isCustom = false
                    )
                )
            }
        }
    }

    fun getAllBlockedDomains(): Flow<List<BlockedDomainEntity>> = dao.getAllBlockedDomains()

    suspend fun isDomainBlocked(rawInput: String): Boolean {
        val clean = rawInput.trim().lowercase()
            .replace("https://", "")
            .replace("http://", "")
            .replace("www.", "")
            .split("/")[0]

        if (clean.isBlank()) return false

        // Check exact match or keyword match for adult terms
        val adultKeywords = listOf("pornhub", "xhamster", "xhamstar", "xvideos", "redtube", "youporn", "xnxx", "brazzers", "erotic", "adult", "porn", "xxx")
        for (keyword in adultKeywords) {
            if (clean.contains(keyword)) {
                dao.getBlockedDomain(clean)?.let {
                    dao.incrementBlockedCount(clean)
                } ?: run {
                    dao.insertBlockedDomain(
                        BlockedDomainEntity(
                            domain = clean,
                            category = "Adult/18+ Keyword Match",
                            timesBlocked = 1,
                            isCustom = false
                        )
                    )
                }
                return true
            }
        }

        val domainInDb = dao.getBlockedDomain(clean)
        if (domainInDb != null) {
            dao.incrementBlockedCount(clean)
            return true
        }

        return false
    }

    suspend fun addCustomBlockedDomain(domain: String) {
        val clean = domain.trim().lowercase()
            .replace("https://", "")
            .replace("http://", "")
            .replace("www.", "")
            .split("/")[0]

        if (clean.isNotBlank()) {
            dao.insertBlockedDomain(
                BlockedDomainEntity(
                    domain = clean,
                    category = "Custom Block",
                    timesBlocked = 0,
                    isCustom = true
                )
            )
        }
    }

    suspend fun removeBlockedDomain(domain: String) {
        dao.deleteBlockedDomain(domain)
    }

    // Streaks
    fun getAllStreakRecords(): Flow<List<StreakRecordEntity>> = dao.getAllStreakRecords()

    suspend fun logDailyCheckIn(isClean: Boolean, urgeLevel: Int, reflectionNote: String) {
        val today = getTodayString()
        dao.insertStreakRecord(
            StreakRecordEntity(
                date = today,
                isClean = isClean,
                urgeLevel = urgeLevel,
                reflectionNote = reflectionNote,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // Usage & Shorts
    fun getTodayUsageLog(): Flow<UsageLogEntity?> {
        val today = getTodayString()
        return dao.getUsageLogForDate(today)
    }

    suspend fun updateTodayScreenTime(seconds: Long) {
        val today = getTodayString()
        val current = dao.getUsageLogOneShot(today) ?: UsageLogEntity(date = today)
        dao.insertUsageLog(current.copy(totalScreenTimeSeconds = seconds))
    }

    suspend fun incrementShortsCount(isReel: Boolean) {
        val today = getTodayString()
        val current = dao.getUsageLogOneShot(today) ?: UsageLogEntity(date = today)
        val updated = if (isReel) {
            current.copy(reelsWatchedCount = current.reelsWatchedCount + 1)
        } else {
            current.copy(shortsWatchedCount = current.shortsWatchedCount + 1)
        }
        dao.insertUsageLog(updated)
    }

    suspend fun updateUsageLimits(limitMinutes: Int, dailyShortsLimit: Int) {
        val today = getTodayString()
        val current = dao.getUsageLogOneShot(today) ?: UsageLogEntity(date = today)
        dao.insertUsageLog(current.copy(dailyLimitMinutes = limitMinutes, dailyShortsLimit = dailyShortsLimit))
    }

    // Games
    fun getRecentGames(): Flow<List<GameRecordEntity>> = dao.getRecentGameRecords()

    suspend fun logGamePlayed(gameType: String, mode: String, result: String, teamCode: String = "") {
        dao.insertGameRecord(
            GameRecordEntity(
                gameType = gameType,
                mode = mode,
                result = result,
                teamCode = teamCode,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
