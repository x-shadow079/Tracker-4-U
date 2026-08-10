package com.example.ui.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ChessBoardView(
    engine: ChessGameEngine,
    onGameEnd: (String) -> Unit = {}
) {
    // Auto turn loop for AI / Watch Mode
    LaunchedEffect(engine.turnColor, engine.mode) {
        if (engine.winner == null && engine.isCurrentTurnAI()) {
            delay(700)
            engine.triggerAiTurnIfApplicable()
        }
    }

    LaunchedEffect(engine.winner) {
        engine.winner?.let {
            onGameEnd("CHESS winner: ${it.name}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Bar
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
                        text = "CHESS MASTER",
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
                    onClick = { engine.resetBoard() },
                    modifier = Modifier.testTag("chess_reset_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Chess")
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

        // 8x8 Chessboard Graphic
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0..7) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (c in 0..7) {
                            val pos = SquarePos(r, c)
                            val isLightSquare = (r + c) % 2 == 0
                            val squareColor = if (isLightSquare) Color(0xFFF0D9B5) else Color(0xFFB58863)

                            val isSelected = engine.selectedSquare == pos
                            val isValidTarget = engine.validMoves.contains(pos)
                            val piece = engine.board[r][c]

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .background(
                                        when {
                                            isSelected -> Color(0xFF769656)
                                            else -> squareColor
                                        }
                                    )
                                    .clickable { engine.onSquareClicked(pos) },
                                contentAlignment = Alignment.Center
                            ) {
                                // Valid target dot indicator
                                if (isValidTarget) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (piece != null) Color(0xFFEF4444)
                                                else Color(0xFF10B981).copy(alpha = 0.7f)
                                            )
                                    )
                                }

                                // Chess Piece rendering
                                if (piece != null) {
                                    Text(
                                        text = piece.type.symbol,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (piece.color == ChessColor.WHITE) Color.White else Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
