package chesspackage.engine.player.ai;

import chesspackage.engine.board.Board;
import chesspackage.engine.board.Move;
import chesspackage.engine.player.MoveTransition;

public class AlphaBeta implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int depthSearch;

    public AlphaBeta(final int depthSearch){
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
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getAlliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(),depthSearch-1, highestSeenValue, lowestSeenValue) :
                        max(moveTransition.getTransitionBoard(),depthSearch-1, highestSeenValue, lowestSeenValue);
                if (board.currentPlayer().getAlliance().isWhite() && currentValue > highestSeenValue){
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



    public int max(final Board board, final int depth, int highest, int lowest){
        if (depth<=0 || endGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                currentHighest=Math.max(currentHighest,min(moveTransition.getTransitionBoard(), depth-1,currentHighest,lowest));
                if (currentHighest>=lowest){
                    return lowest;
                }
            }
        }
        return currentHighest;
    }


    public int min(final Board board, final int depth, int highest, int lowest) {
        if (depth<=0 || endGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentLowest = lowest;
        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                currentLowest = Math.min(currentLowest,max(moveTransition.getTransitionBoard(), depth-1,highest,currentLowest));
                if (currentLowest <= highest){
                    return highest;
                }
            }
        }
        return currentLowest;
    }

    private boolean endGame(Board board) {
        return board.currentPlayer().isCheckMate() || board.currentPlayer().isInStaleMate() || board.currentPlayer().onlyGotKingLeft();
    }

}

