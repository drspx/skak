package chesspackage.engine.player.ai;

import chesspackage.engine.board.Board;
import chesspackage.engine.board.Move;
import chesspackage.engine.player.MoveTransition;

public class MiniMax implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int depthSearch;

    public MiniMax(final int depthSearch){
        this.boardEvaluator = new StandardBoardEvaluator();
        this.depthSearch = depthSearch;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();

        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue ;
        System.out.println(board.currentPlayer() + "thinking with depth : " + depthSearch);
        int numMoves = board.currentPlayer().getLegalMoves().size();

        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(),depthSearch-1) : max(moveTransition.getTransitionBoard(),depthSearch-1);
                if (board.currentPlayer().getAlliance().isWhite() && currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
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
                final int currentValue = max(board, depth-1);
                if (currentValue<=lowestSeenValue){
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    private boolean endGame(Board board) {
        return board.currentPlayer().isCheckMate() || board.currentPlayer().isInStaleMate();
    }

    public int max (final Board board, final int depth){
        if (depth<=0 || endGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(board, depth-1);
                if (currentValue>=highestSeenValue){
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }


}

