package chesspackage.engine.player.ai;

import chesspackage.engine.board.Board;
import chesspackage.engine.board.BoardUtils;
import chesspackage.engine.board.Move;
import chesspackage.engine.player.MoveTransition;

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int depthSearch;
    int counter = 0;

    public MiniMax(final int depthSearch){
        this.boardEvaluator = new StandardBoardEvaluator();
        this.depthSearch = depthSearch;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(final Board board) {
        final long startTime = System.currentTimeMillis();

        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue ;
//        System.out.println(board.currentPlayer() + "thinking with depth : " + depthSearch);
        int numMoves = board.currentPlayer().getLegalMoves().size();
        int moveindex = 1;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            System.out.println(moveindex++ + "/" +numMoves);
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                ++counter;;
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(),depthSearch-1) :
                        max(moveTransition.getTransitionBoard(),depthSearch-1);
                if (board.currentPlayer().getAlliance().isWhite() &&
                        currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() &&
                        currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("boards evaluated : " + counter);
        System.out.println("time:"+executionTime);
        System.out.println();
        return bestMove;
    }

    public int min (final Board board, final int depth) {
        if (depth<=0 || endGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                counter++;
                final int currentValue = max(moveTransition.getTransitionBoard(), depth-1);
                if (currentValue<=lowestSeenValue){
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    private boolean endGame(Board board) {
        return board.currentPlayer().isCheckMate() || board.currentPlayer().isInStaleMate() || board.currentPlayer().onlyGotKingLeft();
    }

    public int max (final Board board, final int depth){
        if (depth<=0 || endGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                counter++;
                final int currentValue = min(moveTransition.getTransitionBoard(), depth-1);
                if (currentValue>=highestSeenValue){
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }


}

