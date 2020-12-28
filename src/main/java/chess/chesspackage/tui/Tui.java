package chess.chesspackage.tui;

import chess.chesspackage.engine.board.Board;
import chess.chesspackage.engine.board.BoardUtils;
import chess.chesspackage.engine.board.Move;
import chess.chesspackage.engine.player.MoveTransition;
import chess.chesspackage.engine.player.ai.AlphaBeta;
import chess.chesspackage.engine.player.ai.MiniMax;
import chess.chesspackage.engine.player.ai.MoveStrategy;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Tui {
    private Board board = Board.crateStandardBoard();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private static final Tui tui = new Tui();

    public static Tui getInstance(){
        return tui;
    }

    public void run () {
        while (!board.whitePlayer().isCheckMate() || !board.whitePlayer().isInStaleMate() ||
                !board.blackPlayer().isCheckMate() || !board.blackPlayer().isInStaleMate() ||
                !board.currentPlayer().onlyGotKingLeft()){
            System.out.println(board.toString());
            System.out.println(board.currentPlayer().getAlliance() + " player, your move");
            MoveTransition b = board.currentPlayer().makeMove(new MiniMax(4).execute(board));
            board = b.getTransitionBoard();
            MoveTransition a = board.currentPlayer().makeMove(new AlphaBeta(4).execute(board));
            board = a.getTransitionBoard();
        }
    }

    private boolean isLegal(String moveString) {
        System.out.println("from " + moveString.substring(0,2) + " to " + moveString.substring(2,4));
        int fromCoordinate = BoardUtils.getCoordinateAtPosition(moveString.substring(0,2));
        int toCoordinate = BoardUtils.getCoordinateAtPosition(moveString.substring(2,4));
        Move makeMove = Move.MoveFactory.createMove(this.board,fromCoordinate,toCoordinate);
        MoveTransition m = board.currentPlayer().makeMove(makeMove);
        return m.getMoveStatus().isDone();
    }


}
