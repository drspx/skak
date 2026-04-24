package chess.chesspackage.engine.player.ai;

import chess.chesspackage.engine.Alliance;
import chess.chesspackage.engine.board.Board;
import chess.chesspackage.engine.board.Move;
import chess.chesspackage.engine.board.Tile;
import chess.chesspackage.engine.pieces.Piece;
import chess.chesspackage.engine.player.MoveTransition;

import java.util.*;

/**
 * Positional chess AI using minimax with alpha-beta pruning,
 * piece-square tables for positional evaluation, and quiescence search.
 */
public class PositionalAI implements MoveStrategy {

    private static final int MAX_DEPTH = 3;
    private static final int MAX_QUIESCENCE_DEPTH = 2;

    // Piece values
    private static final int[] PIECE_VALUES = {0, 100, 320, 330, 500, 900, 20000};

    // Piece-square tables — higher = better for that piece on that square
    // Tables are indexed 0..63, white perspective
    private static final int[][] PAWN_TABLE = {
            0,  0,  0,  0,  0,  0,  0,  0,
            50, 50, 50, 50, 50, 50, 50, 50,
            10, 10, 20, 30, 30, 20, 10, 10,
            5,  5, 10, 25, 25, 10,  5,  5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5, -5,-10,  0,  0,-10, -5,  5,
            5, 10, 10,-20,-20, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0
    };

    private static final int[][] KNIGHT_TABLE = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50
    };

    private static final int[][] BISHOP_TABLE = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-10,-10,-10,-10,-10,-20
    };

    private static final int[][] ROOK_TABLE = {
             0,  0,  0,  0,  0,  0,  0,  0,
             5, 10, 10, 10, 10, 10, 10,  5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
             0,  0,  0,  5,  5,  0,  0,  0
    };

    private static final int[][] QUEEN_TABLE = {
            -20,-10,-10, -5, -5,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5,  5,  5,  5,  0,-10,
             -5,  0,  5,  5,  5,  5,  0, -5,
              0,  0,  5,  5,  5,  5,  0, -5,
            -10,  5,  5,  5,  5,  5,  0,-10,
            -10,  0,  5,  0,  0,  0,  0,-10,
            -20,-10,-10, -5, -5,-10,-10,-20
    };

    private static final int[][] KING_TABLE = {
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -30,-40,-40,-50,-50,-40,-40,-30,
            -20,-30,-30,-40,-40,-30,-30,-20,
            -10,-20,-20,-20,-20,-20,-20,-10,
             20, 20,  0,  0,  0,  0, 20, 20,
             20, 30, 10,  0,  0, 10, 30, 20
    };

    // For black, mirror the king table (simplified — flip rows)
    private static final int[][] BLACK_KING_TABLE = mirrorRows(KING_TABLE);

    private static int[][] mirrorRows(int[][] table) {
        int[][] mirrored = new int[8][8];
        for (int row = 0; row < 8; row++) {
            System.arraycopy(table[7 - row], 0, mirrored[row], 0, 8);
        }
        return mirrored;
    }

    private final int searchDepth;

    public PositionalAI(final int searchDepth) {
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString() {
        return "PositionalAI";
    }

    @Override
    public Move execute(final Board board) {
        long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        final Collection<Move> legalMoves = board.currentPlayer().getLegalMoves();
        if (legalMoves.isEmpty()) {
            return null;
        }

        // Sort moves for better pruning
        final List<Move> sortedMoves = sortMoves(board, legalMoves);
        int moveCount = 0;

        for (final Move move : sortedMoves) {
            final MoveTransition transition = board.currentPlayer().makeMove(move);
            if (!transition.getMoveStatus().isDone()) {
                continue;
            }

            moveCount++;
            final Board nextBoard = transition.getTransitionBoard();
            final int value = -negamax(nextBoard, this.searchDepth - 1, -beta, -alpha);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }

            alpha = Math.max(alpha, bestValue);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        // System.out.println("PositionalAI: evaluated " + moveCount + " moves in " + elapsed + "ms, value=" + bestValue);
        return bestMove;
    }

    /** Negamax with alpha-beta pruning. Returns score from the perspective of the side to move. */
    private int negamax(final Board board, final int depth, final int alpha, final int beta) {
        if (depth == 0) {
            return -quiesce(board, -beta, -alpha);
        }

        // Check for game end
        if (board.currentPlayer().isCheckMate()) {
            if (board.currentPlayer().getAlliance().isWhite()) {
                return -100000 + (this.searchDepth - depth); // prefer faster mates
            } else {
                return 100000 - (this.searchDepth - depth);
            }
        }
        if (board.currentPlayer().isInStaleMate() || board.currentPlayer().onlyGotKingLeft()) {
            return 0; // draw
        }

        int value = Integer.MIN_VALUE;
        final List<Move> sortedMoves = sortMoves(board, board.currentPlayer().getLegalMoves());

        for (final Move move : sortedMoves) {
            final MoveTransition transition = board.currentPlayer().makeMove(move);
            if (!transition.getMoveStatus().isDone()) {
                continue;
            }

            value = -negamax(transition.getTransitionBoard(), depth - 1, -beta, -alpha);

            if (value > alpha) {
                alpha = value;
            }
            if (alpha >= beta) {
                break; // beta cutoff
            }
        }

        return alpha;
    }

    /** Quiescence search — only considers captures to avoid horizon effect. */
    private int quiesce(final Board board, final int alpha, final int beta) {
        int standPat = evaluate(board);

        if (standPat >= beta) {
            return beta;
        }
        if (standPat > alpha) {
            alpha = standPat;
        }

        // Only search captures
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            if (!isCapture(move)) {
                continue;
            }

            final MoveTransition transition = board.currentPlayer().makeMove(move);
            if (!transition.getMoveStatus().isDone()) {
                continue;
            }

            final int value = -quiesce(transition.getTransitionBoard(), -beta, -alpha);

            if (value > alpha) {
                alpha = value;
            }
            if (alpha >= beta) {
                return beta;
            }
        }

        return alpha;
    }

    private boolean isCapture(final Move move) {
        return move.getAttackedPiece() != null;
    }

    /** Evaluate from white's perspective. Caller must flip sign as needed. */
    private int evaluate(final Board board) {
        int score = 0;

        for (final Piece piece : board.getAllPieces()) {
            int value = PIECE_VALUES[piece.getPieceType().ordinal()];
            int positional = getPositionalScore(piece);
            int total = value + positional;

            if (piece.getPieceAlliance() == Alliance.WHITE) {
                score += total;
            } else {
                score -= total;
            }
        }

        // Mobility bonus
        score += (board.whitePlayer().getLegalMoves().size() - board.blackPlayer().getLegalMoves().size()) * 3;

        // Check bonus
        if (board.whitePlayer().isInCheck()) {
            score -= 50;
        }
        if (board.blackPlayer().isInCheck()) {
            score += 50;
        }

        return score;
    }

    private int getPositionalScore(final Piece piece) {
        int pos = piece.getPiecePosition();
        // For black, flip the position vertically (row 0↔7)
        int row = pos / 8;
        int col = pos % 8;

        int[][] table;
        switch (piece.getPieceType()) {
            case PAWN:  table = PAWN_TABLE; break;
            case KNIGHT: table = KNIGHT_TABLE; break;
            case BISHOP: table = BISHOP_TABLE; break;
            case ROOK:   table = ROOK_TABLE; break;
            case QUEEN:  table = QUEEN_TABLE; break;
            case KING:   table = piece.getPieceAlliance() == Alliance.WHITE ? KING_TABLE : BLACK_KING_TABLE; break;
            default:     return 0;
        }

        return table[row][col];
    }

    /** Sort moves: captures first (by MVV-LVA), then castling, then others. */
    private List<Move> sortMoves(final Board board, final Collection<Move> moves) {
        final List<Move> sorted = new ArrayList<>(moves);
        final Alliance alliance = board.currentPlayer().getAlliance();

        sorted.sort((m1, m2) -> {
            int score1 = moveScore(m1, alliance);
            int score2 = moveScore(m2, alliance);
            return Integer.compare(score2, score1); // descending
        });

        return sorted;
    }

    private int moveScore(final Move move, final Alliance alliance) {
        int score = 0;

        // Castling is most important
        if (move.isCastlingMove()) {
            score += 200;
        }

        // Captures — MVV-LVA (Most Valuable Victim — Least Valuable Attacker)
        Piece attacked = move.getAttackedPiece();
        Piece moved = move.getMovedPiece();
        if (attacked != null) {
            score += PIECE_VALUES[attacked.getPieceType().ordinal()] * 10
                    - PIECE_VALUES[moved.getPieceType().ordinal()];
        }

        // Pawn promotion
        if (move instanceof Move.PawnPromotion) {
            score += 800;
        }

        // Small random factor for variety
        score += (move.getCurrentCoordinate() + move.getDestinationCoordinate()) % 7;

        return score;
    }
}
