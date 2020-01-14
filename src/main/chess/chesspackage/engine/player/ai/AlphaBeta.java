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
        return "AlphaBeta";
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
                currentValue = miniMax(moveTransition.getTransitionBoard(),depthSearch,highestSeenValue,lowestSeenValue);
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



    private int miniMax(Board currentNode, int depth, int alpha, int beta) {
        if (depth <= 0 || endGame(currentNode)){
            return this.boardEvaluator.evaluate(currentNode, depth);
        }
        if (currentNode.currentPlayer().getAlliance().isWhite()) {
            int currentAlpha = Integer.MIN_VALUE;
            for (Move child : currentNode.currentPlayer().getLegalMoves()) {
                currentAlpha = Math.max(currentAlpha, miniMax(currentNode.currentPlayer().makeMove(child).getTransitionBoard(), depth - 1, alpha, beta));
                alpha = Math.max(alpha, currentAlpha);
                if (alpha >= beta) {
                    return alpha;
                }
            }
            return currentAlpha;
        } else {
            int currentBeta = Integer.MAX_VALUE;
            for(Move child : currentNode.currentPlayer().getLegalMoves()) {
                currentBeta = Math.min(currentBeta, miniMax(currentNode.currentPlayer().makeMove(child).getTransitionBoard(), depth - 1, alpha, beta));
                beta = Math.min(beta, currentBeta);
                if (beta <= alpha) {
                    return beta;
                }
            }
            return currentBeta;
        }
    }

    private boolean endGame(Board board) {
        return board.currentPlayer().isCheckMate() || board.currentPlayer().isInStaleMate() || board.currentPlayer().onlyGotKingLeft();
    }

}

