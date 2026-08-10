package com.example.ui.games

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

enum class ChessColor { WHITE, BLACK }

enum class PieceType(val symbol: String, val value: Int) {
    PAWN("♟", 1),
    KNIGHT("♞", 3),
    BISHOP("♝", 3),
    ROOK("♜", 5),
    QUEEN("♛", 9),
    KING("♚", 100)
}

data class ChessPiece(
    val type: PieceType,
    val color: ChessColor
)

data class SquarePos(val row: Int, val col: Int)

class ChessGameEngine(
    val mode: String = "VS_AI", // "VS_AI", "WATCH_COMP_VS_COMP", "FRIEND_MODE"
    val teamCode: String = "CHESS-" + Random.nextInt(1000, 9999)
) {
    var turnColor by mutableStateOf(ChessColor.WHITE)
    var selectedSquare by mutableStateOf<SquarePos?>(null)
    val validMoves = mutableStateListOf<SquarePos>()
    var gameMessage by mutableStateOf("White's turn! Tap a piece to move.")
    var winner by mutableStateOf<ChessColor?>(null)

    // 8x8 Board matrix
    val board = Array(8) { arrayOfNulls<ChessPiece>(8) }

    init {
        resetBoard()
    }

    fun resetBoard() {
        for (r in 0..7) {
            for (c in 0..7) {
                board[r][c] = null
            }
        }

        // Black Pawns row 1, White Pawns row 6
        for (c in 0..7) {
            board[1][c] = ChessPiece(PieceType.PAWN, ChessColor.BLACK)
            board[6][c] = ChessPiece(PieceType.PAWN, ChessColor.WHITE)
        }

        // Major pieces Black row 0
        board[0][0] = ChessPiece(PieceType.ROOK, ChessColor.BLACK)
        board[0][1] = ChessPiece(PieceType.KNIGHT, ChessColor.BLACK)
        board[0][2] = ChessPiece(PieceType.BISHOP, ChessColor.BLACK)
        board[0][3] = ChessPiece(PieceType.QUEEN, ChessColor.BLACK)
        board[0][4] = ChessPiece(PieceType.KING, ChessColor.BLACK)
        board[0][5] = ChessPiece(PieceType.BISHOP, ChessColor.BLACK)
        board[0][6] = ChessPiece(PieceType.KNIGHT, ChessColor.BLACK)
        board[0][7] = ChessPiece(PieceType.ROOK, ChessColor.BLACK)

        // Major pieces White row 7
        board[7][0] = ChessPiece(PieceType.ROOK, ChessColor.WHITE)
        board[7][1] = ChessPiece(PieceType.KNIGHT, ChessColor.WHITE)
        board[7][2] = ChessPiece(PieceType.BISHOP, ChessColor.WHITE)
        board[7][3] = ChessPiece(PieceType.QUEEN, ChessColor.WHITE)
        board[7][4] = ChessPiece(PieceType.KING, ChessColor.WHITE)
        board[7][5] = ChessPiece(PieceType.BISHOP, ChessColor.WHITE)
        board[7][6] = ChessPiece(PieceType.KNIGHT, ChessColor.WHITE)
        board[7][7] = ChessPiece(PieceType.ROOK, ChessColor.WHITE)

        turnColor = ChessColor.WHITE
        selectedSquare = null
        validMoves.clear()
        winner = null
        gameMessage = when (mode) {
            "WATCH_COMP_VS_COMP" -> "Watch Mode Active! White Bot vs Black Bot."
            "FRIEND_MODE" -> "Team Code Active: $teamCode. Share with friend!"
            else -> "White's Turn! Tap a piece to select."
        }
    }

    fun onSquareClicked(pos: SquarePos) {
        if (winner != null) return
        if (mode == "WATCH_COMP_VS_COMP" || isCurrentTurnAI()) return

        val pieceAtPos = board[pos.row][pos.col]

        val sel = selectedSquare
        if (sel == null) {
            if (pieceAtPos != null && pieceAtPos.color == turnColor) {
                selectedSquare = pos
                computeValidMoves(pos)
                gameMessage = "Selected ${pieceAtPos.type.name}. Tap target square."
            }
        } else {
            if (validMoves.contains(pos)) {
                executeMove(sel, pos)
            } else if (pieceAtPos != null && pieceAtPos.color == turnColor) {
                selectedSquare = pos
                computeValidMoves(pos)
            } else {
                selectedSquare = null
                validMoves.clear()
                gameMessage = "Move cancelled. Tap your piece."
            }
        }
    }

    fun isCurrentTurnAI(): Boolean {
        if (mode == "WATCH_COMP_VS_COMP") return true
        if (mode == "FRIEND_MODE") return false
        // In VS_AI: White is Player, Black is AI
        return turnColor == ChessColor.BLACK
    }

    private fun computeValidMoves(from: SquarePos) {
        validMoves.clear()
        val piece = board[from.row][from.col] ?: return

        for (r in 0..7) {
            for (c in 0..7) {
                val target = SquarePos(r, c)
                if (canPieceMove(from, target, piece)) {
                    validMoves.add(target)
                }
            }
        }
    }

    private fun canPieceMove(from: SquarePos, to: SquarePos, piece: ChessPiece): Boolean {
        if (from == to) return false
        val targetPiece = board[to.row][to.col]
        if (targetPiece?.color == piece.color) return false

        val dr = to.row - from.row
        val dc = to.col - from.col

        return when (piece.type) {
            PieceType.PAWN -> {
                val dir = if (piece.color == ChessColor.WHITE) -1 else 1
                val startRow = if (piece.color == ChessColor.WHITE) 6 else 1
                if (dc == 0 && targetPiece == null) {
                    if (dr == dir) true
                    else if (from.row == startRow && dr == 2 * dir && board[from.row + dir][from.col] == null) true
                    else false
                } else if (Math.abs(dc) == 1 && dr == dir && targetPiece != null) {
                    true
                } else false
            }
            PieceType.KNIGHT -> (Math.abs(dr) == 2 && Math.abs(dc) == 1) || (Math.abs(dr) == 1 && Math.abs(dc) == 2)
            PieceType.BISHOP -> Math.abs(dr) == Math.abs(dc)
            PieceType.ROOK -> dr == 0 || dc == 0
            PieceType.QUEEN -> Math.abs(dr) == Math.abs(dc) || dr == 0 || dc == 0
            PieceType.KING -> Math.abs(dr) <= 1 && Math.abs(dc) <= 1
        }
    }

    fun executeMove(from: SquarePos, to: SquarePos) {
        val piece = board[from.row][from.col] ?: return
        val captured = board[to.row][to.col]

        board[to.row][to.col] = piece
        board[from.row][from.col] = null

        selectedSquare = null
        validMoves.clear()

        if (captured?.type == PieceType.KING) {
            winner = piece.color
            gameMessage = "🎉 CHECKMATE! ${piece.color.name} WINS!"
            return
        }

        turnColor = if (turnColor == ChessColor.WHITE) ChessColor.BLACK else ChessColor.WHITE
        val capturedMsg = if (captured != null) " (Captured ${captured.type.name}!)" else ""
        gameMessage = "${piece.color.name} moved to ${to.row},${to.col}$capturedMsg. ${turnColor.name}'s turn."
    }

    fun triggerAiTurnIfApplicable() {
        if (winner != null) return
        if (!isCurrentTurnAI()) return

        // Find all pieces for current turn
        val myPieces = mutableListOf<SquarePos>()
        for (r in 0..7) {
            for (c in 0..7) {
                if (board[r][c]?.color == turnColor) {
                    myPieces.add(SquarePos(r, c))
                }
            }
        }

        // Evaluate moves, prioritize captures
        var bestFrom: SquarePos? = null
        var bestTo: SquarePos? = null
        var maxCaptureVal = -1

        myPieces.shuffle()
        for (from in myPieces) {
            val piece = board[from.row][from.col] ?: continue
            for (r in 0..7) {
                for (c in 0..7) {
                    val to = SquarePos(r, c)
                    if (canPieceMove(from, to, piece)) {
                        val target = board[r][c]
                        val captureVal = target?.type?.value ?: 0
                        if (captureVal > maxCaptureVal || bestFrom == null) {
                            maxCaptureVal = captureVal
                            bestFrom = from
                            bestTo = to
                        }
                    }
                }
            }
        }

        if (bestFrom != null && bestTo != null) {
            executeMove(bestFrom, bestTo)
        } else {
            // Stalemate / No moves
            gameMessage = "No valid moves for ${turnColor.name}. Turn passed."
            turnColor = if (turnColor == ChessColor.WHITE) ChessColor.BLACK else ChessColor.WHITE
        }
    }
}
