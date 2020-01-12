package chesspackage.engine.board;

import static org.junit.jupiter.api.Assertions.*;

import chesspackage.engine.pieces.Piece;
import chesspackage.engine.player.MoveStatus;
import chesspackage.engine.player.MoveTransition;
import chesspackage.engine.player.ai.MiniMax;
import chesspackage.engine.player.ai.MoveStrategy;
import com.google.common.collect.Iterables;
import org.junit.jupiter.api.Test;

public class TestBoard {

    @Test
    public void initialBoard() {

        final Board board = Board.crateStandardBoard();
        assertEquals(board.currentPlayer().getLegalMoves().size(), 20);
        assertEquals(board.currentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertFalse(board.currentPlayer().isInCheck());
        assertFalse(board.currentPlayer().isCheckMate());
        assertFalse(board.currentPlayer().isCastled());
//        assertTrue(board.currentPlayer().isKingSideCastleCapable());
//        assertTrue(board.currentPlayer().isQueenSideCastleCapable());
        assertEquals(board.currentPlayer(), board.whitePlayer());
        assertEquals(board.currentPlayer().getOpponent(), board.blackPlayer());
        assertFalse(board.currentPlayer().getOpponent().isInCheck());
//        assertFalse(board.currentPlayer().getOpponent().isInCheckMate());
        assertFalse(board.currentPlayer().getOpponent().isCastled());
//        assertTrue(board.currentPlayer().getOpponent().isKingSideCastleCapable());
//        assertTrue(board.currentPlayer().getOpponent().isQueenSideCastleCapable());
        assertTrue(board.whitePlayer().toString().equals("WHITE"));
        assertTrue(board.blackPlayer().toString().equals("BLACK"));

        final Iterable<Piece> allPieces = board.getAllPieces();

        final Iterable<Move> allMoves = Iterables.concat(board.whitePlayer().getLegalMoves(), board.blackPlayer().getLegalMoves());
        for (final Move move : allMoves) {
            assertFalse(move.isAttack());
            assertFalse(move.isCastlingMove());
//            assertEquals(MoveUtils.exchangeScore(move), 1);

        }
    }

    @Test
    public void testFoolsMate() {
        final Board board = Board.crateStandardBoard();
        MoveTransition mt1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(
                        board,BoardUtils.getCoordinateAtPosition("f2"),BoardUtils.getCoordinateAtPosition("f3")));
        assertTrue(mt1.getMoveStatus().isDone());

     System.out.println(mt1.getTransitionBoard().toString());

     final MoveStrategy moveStrategy = new MiniMax(2);
     final Move aiMove1 = moveStrategy.execute(mt1.getTransitionBoard());

     MoveTransition mt2 = mt1.getTransitionBoard().currentPlayer()
             .makeMove(aiMove1);
     assertTrue(mt2.getMoveStatus().isDone());

     System.out.println(mt2.getTransitionBoard().toString());

     MoveTransition mt3 = mt2.getTransitionBoard().currentPlayer()
             .makeMove(Move.MoveFactory.createMove(
                     mt2.getTransitionBoard(),BoardUtils.getCoordinateAtPosition("g2"),BoardUtils.getCoordinateAtPosition("g4")));
     assertTrue(mt3.getMoveStatus().isDone());

     final Move aiMove2 = moveStrategy.execute(mt3.getTransitionBoard());

     final Move bestMove = Move.MoveFactory.createMove(mt3.getTransitionBoard(),
             BoardUtils.getCoordinateAtPosition("d8"),
             BoardUtils.getCoordinateAtPosition("h4"));

     System.out.println(BoardUtils.getPositionAtCoordinate(aiMove2.destinationCoordinate));

     MoveTransition mt4 = mt3.getTransitionBoard().currentPlayer().makeMove(aiMove2);

     assertTrue(mt4.getMoveStatus().isDone());

     System.out.println(mt4.getTransitionBoard().toString());

     assertEquals(aiMove2,bestMove);

    }
}

/**
 * }
 * <p>
 * assertEquals(Iterables.size(allMoves), 40);
 * assertEquals(Iterables.size(allPieces), 32);
 * assertFalse(BoardUtils.isEndGame(board));
 * assertFalse(BoardUtils.isThreatenedBoardImmediate(board));
 * assertEquals(StandardBoardEvaluator.get().evaluate(board, 0), 0);
 * assertEquals(board.getPiece(35), null);
 * }
 *
 * @Test public void testPlainKingMove() {
 * <p>
 * final Builder builder = new Builder();
 * // Black Layout
 * builder.setPiece(new King(Alliance.BLACK, 4, false, false));
 * builder.setPiece(new Pawn(Alliance.BLACK, 12));
 * // White Layout
 * builder.setPiece(new Pawn(Alliance.WHITE, 52));
 * builder.setPiece(new King(Alliance.WHITE, 60, false, false));
 * builder.setMoveMaker(Alliance.WHITE);
 * // Set the current player
 * final Board board = builder.build();
 * System.out.println(board);
 * <p>
 * assertEquals(board.whitePlayer().getLegalMoves().size(), 6);
 * assertEquals(board.blackPlayer().getLegalMoves().size(), 6);
 * assertFalse(board.currentPlayer().isInCheck());
 * assertFalse(board.currentPlayer().isInCheckMate());
 * assertFalse(board.currentPlayer().getOpponent().isInCheck());
 * assertFalse(board.currentPlayer().getOpponent().isInCheckMate());
 * assertEquals(board.currentPlayer(), board.whitePlayer());
 * assertEquals(board.currentPlayer().getOpponent(), board.blackPlayer());
 * BoardEvaluator evaluator = StandardBoardEvaluator.get();
 * System.out.println(evaluator.evaluate(board, 0));
 * assertEquals(StandardBoardEvaluator.get().evaluate(board, 0), 0);
 * <p>
 * final Move move = MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e1"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("f1"));
 * <p>
 * final MoveTransition moveTransition = board.currentPlayer()
 * .makeMove(move);
 * <p>
 * assertEquals(moveTransition.getTransitionMove(), move);
 * assertEquals(moveTransition.getFromBoard(), board);
 * assertEquals(moveTransition.getToBoard().currentPlayer(), moveTransition.getToBoard().blackPlayer());
 * <p>
 * assertTrue(moveTransition.getMoveStatus().isDone());
 * assertEquals(moveTransition.getToBoard().whitePlayer().getPlayerKing().getPiecePosition(), 61);
 * System.out.println(moveTransition.getToBoard());
 * <p>
 * }
 * @Test public void testBoardConsistency() {
 * final Board board = Board.createStandardBoard();
 * assertEquals(board.currentPlayer(), board.whitePlayer());
 * <p>
 * final MoveTransition t1 = board.currentPlayer()
 * .makeMove(MoveFactory.createMove(board, BoardUtils.INSTANCE.getCoordinateAtPosition("e2"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));
 * final MoveTransition t2 = t1.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t1.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e7"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("e5")));
 * <p>
 * final MoveTransition t3 = t2.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t2.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g1"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("f3")));
 * final MoveTransition t4 = t3.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t3.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d7"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("d5")));
 * <p>
 * final MoveTransition t5 = t4.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t4.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("e4"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("d5")));
 * final MoveTransition t6 = t5.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t5.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d8"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("d5")));
 * <p>
 * final MoveTransition t7 = t6.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t6.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f3"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("g5")));
 * final MoveTransition t8 = t7.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t7.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f7"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("f6")));
 * <p>
 * final MoveTransition t9 = t8.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t8.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d1"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("h5")));
 * final MoveTransition t10 = t9.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t9.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("g7"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("g6")));
 * <p>
 * final MoveTransition t11 = t10.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t10.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("h5"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("h4")));
 * final MoveTransition t12 = t11.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t11.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("f6"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("g5")));
 * <p>
 * final MoveTransition t13 = t12.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t12.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("h4"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("g5")));
 * final MoveTransition t14 = t13.getToBoard()
 * .currentPlayer()
 * .makeMove(MoveFactory.createMove(t13.getToBoard(), BoardUtils.INSTANCE.getCoordinateAtPosition("d5"),
 * BoardUtils.INSTANCE.getCoordinateAtPosition("e4")));
 * <p>
 * assertTrue(t14.getToBoard().whitePlayer().getActivePieces().size() == calculatedActivesFor(t14.getToBoard(), Alliance.WHITE));
 * assertTrue(t14.getToBoard().blackPlayer().getActivePieces().size() == calculatedActivesFor(t14.getToBoard(), Alliance.BLACK));
 * <p>
 * }
 * @Test(expected=RuntimeException.class) public void testInvalidBoard() {
 * <p>
 * final Builder builder = new Builder();
 * // Black Layout
 * builder.setPiece(new Rook(Alliance.BLACK, 0));
 * builder.setPiece(new Knight(Alliance.BLACK, 1));
 * builder.setPiece(new Bishop(Alliance.BLACK, 2));
 * builder.setPiece(new Queen(Alliance.BLACK, 3));
 * builder.setPiece(new Bishop(Alliance.BLACK, 5));
 * builder.setPiece(new Knight(Alliance.BLACK, 6));
 * builder.setPiece(new Rook(Alliance.BLACK, 7));
 * builder.setPiece(new Pawn(Alliance.BLACK, 8));
 * builder.setPiece(new Pawn(Alliance.BLACK, 9));
 * builder.setPiece(new Pawn(Alliance.BLACK, 10));
 * builder.setPiece(new Pawn(Alliance.BLACK, 11));
 * builder.setPiece(new Pawn(Alliance.BLACK, 12));
 * builder.setPiece(new Pawn(Alliance.BLACK, 13));
 * builder.setPiece(new Pawn(Alliance.BLACK, 14));
 * builder.setPiece(new Pawn(Alliance.BLACK, 15));
 * // White Layout
 * builder.setPiece(new Pawn(Alliance.WHITE, 48));
 * builder.setPiece(new Pawn(Alliance.WHITE, 49));
 * builder.setPiece(new Pawn(Alliance.WHITE, 50));
 * builder.setPiece(new Pawn(Alliance.WHITE, 51));
 * builder.setPiece(new Pawn(Alliance.WHITE, 52));
 * builder.setPiece(new Pawn(Alliance.WHITE, 53));
 * builder.setPiece(new Pawn(Alliance.WHITE, 54));
 * builder.setPiece(new Pawn(Alliance.WHITE, 55));
 * builder.setPiece(new Rook(Alliance.WHITE, 56));
 * builder.setPiece(new Knight(Alliance.WHITE, 57));
 * builder.setPiece(new Bishop(Alliance.WHITE, 58));
 * builder.setPiece(new Queen(Alliance.WHITE, 59));
 * builder.setPiece(new Bishop(Alliance.WHITE, 61));
 * builder.setPiece(new Knight(Alliance.WHITE, 62));
 * builder.setPiece(new Rook(Alliance.WHITE, 63));
 * //white to move
 * builder.setMoveMaker(Alliance.WHITE);
 * //build the board
 * builder.build();
 * }
 * @Test public void testAlgebreicNotation() {
 * assertEquals(BoardUtils.INSTANCE.getPositionAtCoordinate(0), "a8");
 * assertEquals(BoardUtils.INSTANCE.getPositionAtCoordinate(1), "b8");
 * assertEquals(BoardUtils.INSTANCE.getPositionAtCoordinate(2), "c8");
 * assertEquals(BoardUtils.INSTANCE.getPositionAtCoordinate(3), "d8");
 * assertEquals(BoardUtils.INSTANCE.getPositionAtCoordinate(4), "e8");
 * assertEquals(BoardUtils.INSTANCE.getPositionAtCoordinate(5), "f8");
 * assertEquals(BoardUtils.INSTANCE.getPositionAtCoordinate(6), "g8");
 * assertEquals(BoardUtils.INSTANCE.getPositionAtCoordinate(7), "h8");
 * }
 * @Test public void mem() {
 * final Runtime runtime = Runtime.getRuntime();
 * long start, end;
 * runtime.gc();
 * start = runtime.freeMemory();
 * Board board = Board.createStandardBoard();
 * end =  runtime.freeMemory();
 * System.out.println("That took " + (start-end) + " bytes.");
 * <p>
 * }
 * private static int calculatedActivesFor(final Board board,
 * final Alliance alliance) {
 * int count = 0;
 * for (final Piece p : board.getAllPieces()) {
 * if (p.getPieceAllegiance().equals(alliance)) {
 * count++;
 * }
 * }
 * return count;
 * }
 * <p>
 * }
 **/