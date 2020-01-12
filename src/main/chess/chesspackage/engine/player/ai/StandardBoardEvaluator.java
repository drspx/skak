package chesspackage.engine.player.ai;

import chesspackage.engine.board.Board;
import chesspackage.engine.pieces.Piece;
import chesspackage.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {

    private static final int MOBILITY_FACTOR = 2;
    private static final int PIECES_VALUE_FACTOR = 10;
    private static final int CHECK_BONUS = 20;
    private static final int CHECK_MATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 40;

    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board,board.blackPlayer(),depth);
    }

    private int scorePlayer(final Board board, final Player player, final int depth) {
        int points = pieceValue(player) + mobility(player) + check(player) + checkmate(player, depth) + castled(player);
        return points;
    }

    private int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private int checkmate(Player player, int depth) {
        return player.getOpponent().isCheckMate() ? CHECK_MATE_BONUS*depthbonus(depth) : 0;
    }

    private int depthbonus(int depth) {
        return depth==0 ? 1 : depth * DEPTH_BONUS;
    }

    private int check(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private int mobility(Player player) {
        return player.getLegalMoves().size()* MOBILITY_FACTOR;
    }

    private int pieceValue(final Player player) {
        int pieceValue = 0;
        for (Piece piece : player.getActivePieces()){
            pieceValue += piece.getPieceValue();
        }
        return pieceValue*PIECES_VALUE_FACTOR;
    }



}
