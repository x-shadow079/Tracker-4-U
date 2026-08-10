package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.lock.ScreenTimerLockOverlay
import com.example.ui.notch.NotchCounterOverlay
import com.example.ui.screens.BlockerScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ScreenTimeScreen
import com.example.ui.screens.ShortsScreen

@Composable
fun MainScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val streakDays by viewModel.streakDays.collectAsStateWithLifecycle()
    val weeklyStatus by viewModel.weeklyStatus.collectAsStateWithLifecycle()
    val isShieldActive by viewModel.isShieldActive.collectAsStateWithLifecycle()
    val blockedDomains by viewModel.allBlockedDomains.collectAsStateWithLifecycle()
    val usageLog by viewModel.todayUsageLog.collectAsStateWithLifecycle()
    val isNotchEnabled by viewModel.isNotchEnabled.collectAsStateWithLifecycle()
    val isScreenLocked by viewModel.isScreenLocked.collectAsStateWithLifecycle()

    val shortsWatched = usageLog?.shortsWatchedCount ?: 4
    val reelsWatched = usageLog?.reelsWatchedCount ?: 2
    val dailyShortsLimit = usageLog?.dailyShortsLimit ?: 15
    val screenTimeSeconds = usageLog?.totalScreenTimeSeconds ?: 3900L // 1h 5m default
    val dailyLimitMinutes = usageLog?.dailyLimitMinutes ?: 120

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = "Streaks") },
                    label = { Text("Streaks", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFF59E0B)),
                    modifier = Modifier.testTag("nav_tab_streaks")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Blocker") },
                    label = { Text("Blocker", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF10B981)),
                    modifier = Modifier.testTag("nav_tab_blocker")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Games") },
                    label = { Text("Games", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF0F766E)),
                    modifier = Modifier.testTag("nav_tab_games")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.SlowMotionVideo, contentDescription = "Shorts") },
                    label = { Text("Shorts", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFEF4444)),
                    modifier = Modifier.testTag("nav_tab_shorts")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = "Timer") },
                    label = { Text("Timer", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF0284C7)),
                    modifier = Modifier.testTag("nav_tab_timer")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    streakDays = streakDays,
                    weeklyStatus = weeklyStatus,
                    onCheckIn = { isClean, urge, note ->
                        viewModel.logCheckIn(isClean, urge, note)
                    },
                    onNavigateToGames = { selectedTab = 2 }
                )

                1 -> BlockerScreen(
                    isShieldActive = isShieldActive,
                    blockedDomains = blockedDomains,
                    onToggleShield = { viewModel.toggleShield(it) },
                    onTestUrl = { viewModel.testUrl(it) },
                    onAddCustomDomain = { viewModel.addCustomDomain(it) },
                    onRemoveDomain = { viewModel.removeBlockedDomain(it) }
                )

                2 -> GamesScreen(
                    onGameCompleted = { game, mode, res ->
                        viewModel.logGameCompleted(game, mode, res)
                    }
                )

                3 -> ShortsScreen(
                    shortsCount = shortsWatched,
                    reelsCount = reelsWatched,
                    dailyLimit = dailyShortsLimit,
                    isNotchEnabled = isNotchEnabled,
                    onToggleNotch = { viewModel.toggleNotch(it) },
                    onIncrementShorts = { viewModel.incrementShorts() },
                    onIncrementReels = { viewModel.incrementReels() },
                    onUpdateLimit = { viewModel.updateShortsLimit(it) }
                )

                4 -> ScreenTimeScreen(
                    timeUsedSeconds = screenTimeSeconds,
                    limitMinutes = dailyLimitMinutes,
                    onUpdateLimit = { viewModel.updateScreenTimeLimit(it) },
                    onSimulateLockScreen = { viewModel.setScreenLocked(true) }
                )
            }

            // Top Camera Notch Floating Badge Counter Layer
            NotchCounterOverlay(
                isVisible = isNotchEnabled,
                shortsCount = shortsWatched,
                reelsCount = reelsWatched,
                dailyLimit = dailyShortsLimit,
                onIncrementShorts = { viewModel.incrementShorts() },
                onIncrementReels = { viewModel.incrementReels() },
                onNotchClick = { selectedTab = 3 }
            )

            // Full Screen Timer Lock Overlay Layer
            ScreenTimerLockOverlay(
                isLocked = isScreenLocked,
                timeUsedSeconds = screenTimeSeconds,
                limitMinutes = dailyLimitMinutes,
                onUnlockWithPin = { pin -> viewModel.checkUnlockPin(pin) },
                onDismissLock = { viewModel.setScreenLocked(false) }
            )
        }
    }
}
