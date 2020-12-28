package chess.chesspackage.engine.player.ai;

import chess.chesspackage.engine.board.Board;

public interface BoardEvaluator {

    int evaluate(Board board, int depth);

}
