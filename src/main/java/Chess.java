import board.Board;

public class Chess {
    public static void main(String[] args) {
        Board board = Board.crateStandardBoard();
        System.out.println(board.toString());
    }
}
