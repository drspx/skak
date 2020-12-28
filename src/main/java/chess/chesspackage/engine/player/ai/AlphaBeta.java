package chess.chesspackage.engine.player.ai;

import chess.chesspackage.engine.board.Board;
import chess.chesspackage.engine.board.Move;
import chess.chesspackage.engine.player.MoveTransition;

public class AlphaBeta implements MoveStrategy {

    private final BoardEvaluator boardEvaluator;
    private final int depthSearch;
    private int boardsEvaluated = 0;

    public AlphaBeta(final int depthSearch) {
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
        int currentValue;
        int amountOfMoves = board.currentPlayer().getLegalMoves().size();
        int moveNr = 1;
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            System.out.println(moveNr++ + "/" + amountOfMoves);
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = miniMax(moveTransition.getTransitionBoard(), depthSearch, highestSeenValue, lowestSeenValue);
                if (board.currentPlayer().getAlliance().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAlliance().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        System.out.println("boards evaluated:" + boardsEvaluated);
        System.out.println("time:" + executionTime);
        System.out.println();
        return bestMove;
    }


    private int miniMax(Board currentNode, int depth, int alpha, int beta) {
        if (depth <= 0 || endGame(currentNode)) {
            return this.boardEvaluator.evaluate(currentNode, depth);
        }
        if (currentNode.currentPlayer().getAlliance().isWhite()) {
            int currentAlpha = Integer.MIN_VALUE;
            for (Move child : currentNode.currentPlayer().getLegalMoves()) {
                currentAlpha = Math.max(currentAlpha, miniMax(currentNode.currentPlayer().makeMove(child).getTransitionBoard(), depth - 1, alpha, beta));
                alpha = Math.max(alpha, currentAlpha);
                this.boardsEvaluated++;
                if (alpha >= beta) {
                    return alpha;
                }
            }
            return currentAlpha;
        } else {
            int currentBeta = Integer.MAX_VALUE;
            for (Move child : currentNode.currentPlayer().getLegalMoves()) {
                currentBeta = Math.min(currentBeta, miniMax(currentNode.currentPlayer().makeMove(child).getTransitionBoard(), depth - 1, alpha, beta));
                beta = Math.min(beta, currentBeta);
                this.boardsEvaluated++;
                if (beta <= alpha) {
                    return beta;
                }
            }
            return currentBeta;
        }
    }

    private boolean endGame(Board board) {
        return board.currentPlayer().isCheckMate() ||
                board.currentPlayer().isInStaleMate() ||
                board.currentPlayer().onlyGotKingLeft();
    }

}

