package com.example.ui.games

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LudoBoardView(
    engine: LudoGameEngine,
    onGameEnd: (String) -> Unit = {}
) {
    // Auto turn loop for AI / Watch mode
    LaunchedEffect(engine.currentTurnIndex, engine.diceRolledForTurn, engine.mode) {
        if (engine.winner == null && (engine.mode == "WATCH_COMP_VS_COMP" || engine.isCurrentTurnAI())) {
            delay(600)
            engine.triggerAiTurnIfApplicable()
        }
    }

    LaunchedEffect(engine.winner) {
        engine.winner?.let {
            onGameEnd("LUDO winner: ${it.label}")
        }
    }

    val rotationAngle by animateFloatAsState(
        targetValue = if (engine.isRolling) 360f else 0f,
        animationSpec = tween(300)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status & Mode Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "LUDO ARENA",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = engine.gameMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = { engine.resetGame() },
                    modifier = Modifier.testTag("ludo_reset_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Game")
                }
            }

            if (engine.mode == "FRIEND_MODE") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Team Pairing Code: ${engine.teamCode}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Ludo Graphical Board Representation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Top Row: Red Home (Left) & Green Home (Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HomeYardCard(
                        color = LudoColor.RED,
                        pawns = engine.pawns.filter { it.color == LudoColor.RED },
                        isCurrentTurn = engine.players[engine.currentTurnIndex] == LudoColor.RED,
                        onPawnClick = { pawn -> engine.movePawn(pawn) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HomeYardCard(
                        color = LudoColor.GREEN,
                        pawns = engine.pawns.filter { it.color == LudoColor.GREEN },
                        isCurrentTurn = engine.players[engine.currentTurnIndex] == LudoColor.GREEN,
                        onPawnClick = { pawn -> engine.movePawn(pawn) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Center Goal Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF334155)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🎯 HOME GOAL 🎯",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Row: Blue Home (Left) & Yellow Home (Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HomeYardCard(
                        color = LudoColor.BLUE,
                        pawns = engine.pawns.filter { it.color == LudoColor.BLUE },
                        isCurrentTurn = engine.players[engine.currentTurnIndex] == LudoColor.BLUE,
                        onPawnClick = { pawn -> engine.movePawn(pawn) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HomeYardCard(
                        color = LudoColor.YELLOW,
                        pawns = engine.pawns.filter { it.color == LudoColor.YELLOW },
                        isCurrentTurn = engine.players[engine.currentTurnIndex] == LudoColor.YELLOW,
                        onPawnClick = { pawn -> engine.movePawn(pawn) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dice Roller & Action Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dice Visual
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .rotate(rotationAngle)
                    .clip(RoundedCornerShape(12.dp))
                    .background(engine.players[engine.currentTurnIndex].color)
                    .border(3.dp, Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = engine.currentDiceValue.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            // Roll Button
            Button(
                onClick = { engine.rollDice() },
                enabled = !engine.diceRolledForTurn && engine.winner == null && (!engine.isCurrentTurnAI() || engine.mode == "WATCH_COMP_VS_COMP"),
                colors = ButtonDefaults.buttonColors(containerColor = engine.players[engine.currentTurnIndex].color),
                modifier = Modifier
                    .height(52.dp)
                    .testTag("ludo_roll_dice_button")
            ) {
                Icon(Icons.Default.Casino, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (engine.diceRolledForTurn) "Select Pawn" else "ROLL DICE",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HomeYardCard(
    color: LudoColor,
    pawns: List<LudoPawn>,
    isCurrentTurn: Boolean,
    onPawnClick: (LudoPawn) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.color.copy(alpha = 0.2f))
            .border(
                width = if (isCurrentTurn) 3.dp else 1.dp,
                color = if (isCurrentTurn) color.color else Color.Gray.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = color.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = color.color
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pawns display grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                pawns.forEach { pawn ->
                    PawnChip(
                        pawn = pawn,
                        onClick = { onPawnClick(pawn) }
                    )
                }
            }
        }
    }
}

@Composable
fun PawnChip(
    pawn: LudoPawn,
    onClick: () -> Unit
) {
    val posText = when {
        pawn.isFinished -> "🎯"
        pawn.position == -1 -> "🏠"
        else -> "${pawn.position}"
    }

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(pawn.color.color)
            .border(2.dp, Color.White, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = posText,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
