package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.BlockedDomainEntity
import com.example.data.db.StreakRecordEntity
import com.example.data.db.TrackerDatabase
import com.example.data.db.UsageLogEntity
import com.example.data.repository.TrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = TrackerDatabase.getDatabase(application)
    private val repository = TrackerRepository(db.trackerDao())

    val allBlockedDomains: StateFlow<List<BlockedDomainEntity>> = repository.getAllBlockedDomains()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allStreakRecords: StateFlow<List<StreakRecordEntity>> = repository.getAllStreakRecords()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val todayUsageLog: StateFlow<UsageLogEntity?> = repository.getTodayUsageLog()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Local ViewModel UI State
    private val _isShieldActive = MutableStateFlow(true)
    val isShieldActive: StateFlow<Boolean> = _isShieldActive.asStateFlow()

    private val _isNotchEnabled = MutableStateFlow(true)
    val isNotchEnabled: StateFlow<Boolean> = _isNotchEnabled.asStateFlow()

    private val _isScreenLocked = MutableStateFlow(false)
    val isScreenLocked: StateFlow<Boolean> = _isScreenLocked.asStateFlow()

    private val _streakDays = MutableStateFlow(12) // Default active streak 12 days
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _weeklyStatus = MutableStateFlow(listOf(true, true, true, true, true, true, true))
    val weeklyStatus: StateFlow<List<Boolean>> = _weeklyStatus.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDefaultBlocklist()
        }
    }

    fun toggleShield(active: Boolean) {
        _isShieldActive.value = active
    }

    fun toggleNotch(enabled: Boolean) {
        _isNotchEnabled.value = enabled
    }

    fun setScreenLocked(locked: Boolean) {
        _isScreenLocked.value = locked
    }

    fun checkUnlockPin(pin: String): Boolean {
        return if (pin == "1234" || pin.trim() == "1234") {
            _isScreenLocked.value = false
            true
        } else {
            false
        }
    }

    suspend fun testUrl(rawUrl: String): Boolean {
        return repository.isDomainBlocked(rawUrl)
    }

    fun addCustomDomain(domain: String) {
        viewModelScope.launch {
            repository.addCustomBlockedDomain(domain)
        }
    }

    fun removeBlockedDomain(domain: String) {
        viewModelScope.launch {
            repository.removeBlockedDomain(domain)
        }
    }

    fun logCheckIn(isClean: Boolean, urgeLevel: Int, note: String) {
        viewModelScope.launch {
            repository.logDailyCheckIn(isClean, urgeLevel, note)
            if (isClean) {
                _streakDays.value += 1
            }
        }
    }

    fun incrementShorts() {
        viewModelScope.launch {
            repository.incrementShortsCount(isReel = false)
        }
    }

    fun incrementReels() {
        viewModelScope.launch {
            repository.incrementShortsCount(isReel = true)
        }
    }

    fun updateShortsLimit(limit: Int) {
        viewModelScope.launch {
            val current = todayUsageLog.value ?: UsageLogEntity(date = repository.getTodayString())
            repository.updateUsageLimits(current.dailyLimitMinutes, limit)
        }
    }

    fun updateScreenTimeLimit(limitMinutes: Int) {
        viewModelScope.launch {
            val current = todayUsageLog.value ?: UsageLogEntity(date = repository.getTodayString())
            repository.updateUsageLimits(limitMinutes, current.dailyShortsLimit)
        }
    }

    fun logGameCompleted(gameType: String, mode: String, result: String) {
        viewModelScope.launch {
            repository.logGamePlayed(gameType, mode, result)
        }
    }
}
