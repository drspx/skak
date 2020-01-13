package chesspackage.tui;

import chesspackage.engine.board.Board;
import chesspackage.engine.board.BoardUtils;
import chesspackage.engine.board.Move;
import chesspackage.engine.player.MoveTransition;
import chesspackage.engine.player.ai.MiniMax;
import chesspackage.engine.player.ai.MoveStrategy;

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
                !board.blackPlayer().isCheckMate() || !board.blackPlayer().isInStaleMate()){
            System.out.println(board.toString());
            System.out.println(board.currentPlayer().getAlliance() + " player, your move");
            int randomInt = (int)(Math.random()*((5-1)+1))+1;
            MoveTransition b = board.currentPlayer().makeMove(new MiniMax(randomInt).execute(board));
            board=b.getTransitionBoard();
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
