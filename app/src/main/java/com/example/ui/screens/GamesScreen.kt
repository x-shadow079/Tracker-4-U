package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.games.ChessBoardView
import com.example.ui.games.ChessGameEngine
import com.example.ui.games.LudoBoardView
import com.example.ui.games.LudoGameEngine

@Composable
fun GamesScreen(
    onGameCompleted: (String, String, String) -> Unit
) {
    var selectedGameTab by remember { mutableIntStateOf(0) } // 0 = Ludo, 1 = Chess
    var selectedMode by remember { mutableStateOf("VS_AI") } // "VS_AI", "WATCH_COMP_VS_COMP", "FRIEND_MODE"

    val ludoEngine = remember(selectedMode) { LudoGameEngine(mode = selectedMode) }
    val chessEngine = remember(selectedMode) { ChessGameEngine(mode = selectedMode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Distractor Streak Protection Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF10B981)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Casino,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "URGE DISTRACTOR GAMES",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Focusing on games neutralizes anxiety and locks in your clean streak!",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCCFBF1)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Game Selection Tabs
        TabRow(
            selectedTabIndex = selectedGameTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedGameTab == 0,
                onClick = { selectedGameTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LUDO", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("ludo_game_tab")
            )

            Tab(
                selected = selectedGameTab == 1,
                onClick = { selectedGameTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CHESS", fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("chess_game_tab")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Game Mode Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = selectedMode == "VS_AI",
                onClick = { selectedMode = "VS_AI" },
                label = { Text("Vs AI Bot") },
                leadingIcon = { Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("mode_vs_ai_chip")
            )

            FilterChip(
                selected = selectedMode == "WATCH_COMP_VS_COMP",
                onClick = { selectedMode = "WATCH_COMP_VS_COMP" },
                label = { Text("Watch Comp vs Comp") },
                leadingIcon = { Icon(Icons.Default.SmartDisplay, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("mode_watch_chip")
            )

            FilterChip(
                selected = selectedMode == "FRIEND_MODE",
                onClick = { selectedMode = "FRIEND_MODE" },
                label = { Text("Friend Code") },
                leadingIcon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("mode_friend_chip")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Render selected game view
        if (selectedGameTab == 0) {
            LudoBoardView(
                engine = ludoEngine,
                onGameEnd = { result ->
                    onGameCompleted("LUDO", selectedMode, result)
                }
            )
        } else {
            ChessBoardView(
                engine = chessEngine,
                onGameEnd = { result ->
                    onGameCompleted("CHESS", selectedMode, result)
                }
            )
        }
    }
}
