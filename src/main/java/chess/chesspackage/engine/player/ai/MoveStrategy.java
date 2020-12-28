package chess.chesspackage.engine.player.ai;

import chess.chesspackage.engine.board.Board;
import chess.chesspackage.engine.board.Move;

public interface MoveStrategy {

    Move execute(Board board);
}
