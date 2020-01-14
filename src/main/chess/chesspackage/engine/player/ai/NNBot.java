package chesspackage.engine.player.ai;

import chesspackage.engine.board.Board;
import chesspackage.engine.board.BoardUtils;
import chesspackage.engine.board.Move;
import chesspackage.engine.player.ai.neural.Mat;
import chesspackage.engine.player.ai.neural.NeuralNetwork;

import java.util.Arrays;

import static chesspackage.engine.board.BoardUtils.*;

public class NNBot implements MoveStrategy {

    private StandardBoardEvaluator evaluator = new StandardBoardEvaluator();

    NeuralNetwork nn = new NeuralNetwork(NUM_TILES,20, NUM_TILES);

    public NNBot(){
        nn.setLearningRate(0.05);
        nn.setActivationFunction(new NeuralNetwork.ActivationFunction(Mat.SIGMOID, Mat.SIGMOID_DERIVATIVE));
    }

    @Override
    public Move execute(Board board) {
        double [] input = new double[NUM_TILES];

        for (int i = 0; i < input.length; i++) {
            input[i]=0.0;
        }

        for (int i = 0; i < NUM_TILES; i++) {
            if (board.getTile(i).getPiece() != null)
            input[i] = (double) (board.getTile(i).getPiece().getPieceValue() * board.getTile(i).getPiece().getPieceAlliance().getDirection());
        }
        double [] output = new double[NUM_TILES];

        for (int i = 0; i < output.length; i++) {
            output[i]=0.0;
        }

        for (Move move : board.currentPlayer().getLegalMoves()){
            output[move.getCurrentCoordinate()] = (double)  move.getDestinationCoordinate();
        }
        nn.train(input,output);

        double[] res = nn.process(input);

        for (int i = 0; i < res.length; i++) {
            //System.out.println(res[i]);
        }
        return null;
    }

}
