package chesspackage.engine.board;

import chesspackage.engine.player.MoveTransition;
import chesspackage.engine.player.ai.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AITest {

    @Test
    public void testFoolsMateMinMax() {
        final Board board = Board.crateStandardBoard();
        MoveTransition mt1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(
                        board,BoardUtils.getCoordinateAtPosition("f2"),BoardUtils.getCoordinateAtPosition("f3")));
        assertTrue(mt1.getMoveStatus().isDone());

        final MoveStrategy moveStrategy = new MiniMax(2);
        final Move aiMove1 = moveStrategy.execute(mt1.getTransitionBoard());

        MoveTransition mt2 = mt1.getTransitionBoard().currentPlayer()
                .makeMove(aiMove1);
        assertTrue(mt2.getMoveStatus().isDone());

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
//        System.out.println(mt4.getTransitionBoard().toString());
        assertEquals(aiMove2,bestMove);

    }

    @Test
    public void TestFoolsMateAlphaBeta(){

        Board board = Board.crateStandardBoard();

        MoveTransition mt1 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(
                        board,BoardUtils.getCoordinateAtPosition("f2"),BoardUtils.getCoordinateAtPosition("f3")));
        assertTrue(mt1.getMoveStatus().isDone());
        board=mt1.getTransitionBoard();

        MoveTransition mt2 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(
                        board,BoardUtils.getCoordinateAtPosition("e7"),BoardUtils.getCoordinateAtPosition("e5")));
        assertTrue(mt2.getMoveStatus().isDone());
        board=mt2.getTransitionBoard();

        MoveTransition mt3 = board.currentPlayer()
                .makeMove(Move.MoveFactory.createMove(
                        board,BoardUtils.getCoordinateAtPosition("g2"),BoardUtils.getCoordinateAtPosition("g4")));
        assertTrue(mt3.getMoveStatus().isDone());
        board=mt3.getTransitionBoard();

        final Move aiMove2 = new ABStock(2).execute(mt3.getTransitionBoard());

        final Move bestMove = Move.MoveFactory.createMove(mt3.getTransitionBoard(),
                BoardUtils.getCoordinateAtPosition("d8"),
                BoardUtils.getCoordinateAtPosition("h4"));

        System.out.println(BoardUtils.getPositionAtCoordinate(aiMove2.destinationCoordinate));
        MoveTransition mt4 = mt3.getTransitionBoard().currentPlayer().makeMove(aiMove2);
        assertTrue(mt4.getMoveStatus().isDone());
        System.out.println(mt4.getTransitionBoard().toString());
        assertEquals(aiMove2,bestMove);

    }

    @Test
    public void speedTestAlphaBeta(){

        long al = alpha();
        long mi = mini();
        long ab = ABS();

        System.out.println("alpha took millis : " + al);
        System.out.println("mini took millis : " + mi);
        System.out.println("ABS took millis : " + ab);

    }

    private long ABS() {
        long currentTime = System.currentTimeMillis();
        Board board = Board.crateStandardBoard();
        board.currentPlayer().makeMove(new ABStock(4).execute(board));
        return (System.currentTimeMillis()-currentTime);
    }

    private long alpha() {
        long currentTime = System.currentTimeMillis();
        Board board = Board.crateStandardBoard();
        board.currentPlayer().makeMove(new AlphaBeta(4).execute(board));
        return (System.currentTimeMillis()-currentTime);
    }
    private long mini() {
        long currentTime = System.currentTimeMillis();
        Board board = Board.crateStandardBoard();
        board.currentPlayer().makeMove(new MiniMax(4).execute(board));
        return (System.currentTimeMillis()-currentTime);
    }

    @Test
    public void AImatchMiniVsABS(){
        Board board = Board.crateStandardBoard();
        MoveStrategy m = new MiniMax(2);
        MoveStrategy a = new ABStock(3);
        ArrayList<MoveTransition> mts = new ArrayList<>();
        mts.add(board.currentPlayer().makeMove(m.execute(board)));
        for (int i = 0; i < 10; i++) {
            mts.add(mts.get(mts.size()-1).getTransitionBoard().currentPlayer().makeMove(a.execute(mts.get(mts.size()-1).getTransitionBoard()))); //black
            mts.add(mts.get(mts.size()-1).getTransitionBoard().currentPlayer().makeMove(m.execute(mts.get(mts.size()-1).getTransitionBoard()))); //white
        }
        BoardEvaluator evaluator = new StandardBoardEvaluator();
        int s = evaluator.evaluate(mts.get(mts.size()-1).getTransitionBoard(),0);
        System.out.println(mts.get(mts.size()-1).getTransitionBoard().toString() + "\n with points : " + s);
        assertTrue(s<0);

    }

    private boolean endGame(Board board) {
        return (board.currentPlayer().isCheckMate()||
                board.currentPlayer().onlyGotKingLeft()||
                board.currentPlayer().isInStaleMate());
    }
    @Test
    public void AImatchMiniVsAlpha(){
        Board board = Board.crateStandardBoard();
        MoveStrategy m = new MiniMax(2);
        MoveStrategy a = new AlphaBeta(3);
        ArrayList<MoveTransition> mts = new ArrayList<>();
        mts.add(board.currentPlayer().makeMove(m.execute(board)));
        for (int i = 0; i < 10; i++) {
            mts.add(mts.get(mts.size()-1).getTransitionBoard().currentPlayer().makeMove(a.execute(mts.get(mts.size()-1).getTransitionBoard()))); //black
            mts.add(mts.get(mts.size()-1).getTransitionBoard().currentPlayer().makeMove(m.execute(mts.get(mts.size()-1).getTransitionBoard()))); //white
        }
        BoardEvaluator evaluator = new StandardBoardEvaluator();
        int s = evaluator.evaluate(mts.get(mts.size()-1).getTransitionBoard(),0);
        System.out.println(mts.get(mts.size()-1).getTransitionBoard().toString() + "\n with points : " + s);
        assertTrue(s<0);

    }



}
