package chesspackage.engine.player.ai;

import chesspackage.engine.board.Board;
import chesspackage.engine.pieces.Piece;
import chesspackage.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {

    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board,board.blackPlayer(),depth);
    }

    private int scorePlayer(final Board board, final Player player, final int depth) {

        return pieceValue(player);
        // + checkmate, check, castled, mobility ...
    }

    private int pieceValue(final Player player) {
        int pieceValue = 0;
        for (Piece piece : player.getActivePieces()){
            pieceValue += piece.getPieceValue();
        }
        return pieceValue;
    }



}
