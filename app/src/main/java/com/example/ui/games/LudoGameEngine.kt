package com.example.ui.games

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

enum class LudoColor(val label: String, val color: Color) {
    RED("Red", Color(0xFFEF4444)),
    GREEN("Green", Color(0xFF10B981)),
    YELLOW("Yellow", Color(0xFFF59E0B)),
    BLUE("Blue", Color(0xFF3B82F6))
}

data class LudoPawn(
    val id: Int,
    val color: LudoColor,
    var position: Int = -1, // -1 means in yard/home base, 0..51 main track, 52..56 home stretch, 57 finished
    var isFinished: Boolean = false
)

class LudoGameEngine(
    val mode: String = "VS_AI", // "VS_AI", "WATCH_COMP_VS_COMP", "FRIEND_MODE"
    val teamCode: String = "LUDO-" + Random.nextInt(1000, 9999)
) {
    var currentTurnIndex by mutableIntStateOf(0)
    val players = listOf(LudoColor.RED, LudoColor.GREEN, LudoColor.YELLOW, LudoColor.BLUE)

    var currentDiceValue by mutableIntStateOf(1)
    var isRolling by mutableStateOf(false)
    var diceRolledForTurn by mutableStateOf(false)
    var gameMessage by mutableStateOf("Roll the dice to start!")
    var winner by mutableStateOf<LudoColor?>(null)

    val pawns = mutableStateListOf<LudoPawn>()

    init {
        resetGame()
    }

    fun resetGame() {
        pawns.clear()
        var pawnId = 0
        players.forEach { ludoColor ->
            for (i in 0 until 4) {
                pawns.add(LudoPawn(id = pawnId++, color = ludoColor, position = -1))
            }
        }
        currentTurnIndex = 0
        currentDiceValue = 1
        diceRolledForTurn = false
        winner = null
        gameMessage = when (mode) {
            "WATCH_COMP_VS_COMP" -> "Auto-Match active! Watch Red AI vs Green AI."
            "FRIEND_MODE" -> "Team Code Active: $teamCode. Share with friends!"
            else -> "Your turn (Red)! Roll the dice."
        }
    }

    fun rollDice(): Int {
        if (winner != null) return currentDiceValue
        isRolling = true
        val value = Random.nextInt(1, 7)
        currentDiceValue = value
        isRolling = false
        diceRolledForTurn = true

        val currentColor = players[currentTurnIndex]
        val movablePawns = getMovablePawns(currentColor, value)

        if (movablePawns.isEmpty()) {
            gameMessage = "$value rolled! No valid moves for ${currentColor.label}. Passing turn."
            nextTurn()
        } else if (movablePawns.size == 1 && (mode == "WATCH_COMP_VS_COMP" || isCurrentTurnAI())) {
            // Auto move if 1 option or AI turn
            movePawn(movablePawns.first())
        } else {
            gameMessage = "$value rolled! Tap a ${currentColor.label} pawn to move."
        }
        return value
    }

    fun isCurrentTurnAI(): Boolean {
        if (mode == "WATCH_COMP_VS_COMP") return true
        if (mode == "FRIEND_MODE") return false
        // In VS_AI mode: Red is Player, Green/Yellow/Blue are AI
        return players[currentTurnIndex] != LudoColor.RED
    }

    fun getMovablePawns(color: LudoColor, diceValue: Int): List<LudoPawn> {
        return pawns.filter { it.color == color }.filter { pawn ->
            if (pawn.position == -1) {
                diceValue == 6 // Need a 6 to enter
            } else if (pawn.position + diceValue <= 57) {
                true
            } else false
        }
    }

    fun movePawn(pawn: LudoPawn) {
        if (!diceRolledForTurn) return
        val dice = currentDiceValue

        if (pawn.position == -1) {
            if (dice == 6) {
                pawn.position = 0
                gameMessage = "${pawn.color.label} pawn entered the board!"
            }
        } else {
            pawn.position += dice
            if (pawn.position >= 57) {
                pawn.position = 57
                pawn.isFinished = true
                gameMessage = "${pawn.color.label} pawn reached Home Goal! 🎉"
            } else {
                gameMessage = "${pawn.color.label} pawn moved $dice steps."
            }
        }

        // Check winner
        val colorPawns = pawns.filter { it.color == pawn.color }
        if (colorPawns.all { it.isFinished }) {
            winner = pawn.color
            gameMessage = "🏆 ${pawn.color.label} WINS THE LUDO MATCH!"
            return
        }

        // If rolled 6, player gets another turn
        if (dice == 6 && winner == null) {
            diceRolledForTurn = false
            gameMessage += " Rolled a 6! Roll again."
        } else {
            nextTurn()
        }
    }

    fun nextTurn() {
        diceRolledForTurn = false
        currentTurnIndex = (currentTurnIndex + 1) % players.size
        val nextColor = players[currentTurnIndex]

        if (mode == "WATCH_COMP_VS_COMP" || isCurrentTurnAI()) {
            gameMessage = "${nextColor.label} (AI) thinking..."
        } else {
            gameMessage = "${nextColor.label}'s Turn! Roll the dice."
        }
    }

    fun triggerAiTurnIfApplicable() {
        if (winner != null) return
        if ((mode == "WATCH_COMP_VS_COMP" || isCurrentTurnAI()) && !isRolling) {
            if (!diceRolledForTurn) {
                rollDice()
            } else {
                val movable = getMovablePawns(players[currentTurnIndex], currentDiceValue)
                if (movable.isNotEmpty()) {
                    movePawn(movable.random())
                } else {
                    nextTurn()
                }
            }
        }
    }
}
