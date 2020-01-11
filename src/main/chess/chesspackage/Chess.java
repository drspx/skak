package chesspackage;

import chesspackage.engine.board.Board;
import chesspackage.gui.Table;

public class Chess {
    public static void main(String[] args) {
        Board board = Board.crateStandardBoard();
        System.out.println(board.toString());

        Table table = new Table();
        
    }
}
