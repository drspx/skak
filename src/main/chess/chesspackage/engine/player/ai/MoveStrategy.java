package chesspackage.engine.player.ai;

import chesspackage.engine.board.Board;
import chesspackage.engine.board.Move;

public interface MoveStrategy {

    Move execute(Board board, int depth);
}
