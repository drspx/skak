package chesspackage.engine.player.ai;

import chesspackage.engine.board.Board;

public interface BoardEvaluator {

    int evaluate(Board board, int depth);

}
