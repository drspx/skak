package chesspackage.engine.board;

import chesspackage.engine.player.MoveTransition;
import chesspackage.engine.player.ai.*;
import chesspackage.engine.player.ai.neural.Mat;
import chesspackage.engine.player.ai.neural.NeuralNetwork;
import chesspackage.engine.player.ai.neural.NeuralNetwork.ActivationFunction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TestNeuralNetwork {

    @Test
    public void test(){
        NeuralNetwork brain = new NeuralNetwork(2, 2, 1);
        brain.setLearningRate(0.05);
        brain.setActivationFunction(new ActivationFunction(Mat.TANH, Mat.TANH_DERIVATIVE));

        int[][] xorInputs = { {0, 0}, {0, 1}, {1, 0}, {1, 1} };
        for(int i = 0; i < xorInputs.length * 2000; i++) {
            int[] arr = xorInputs[i % 4];
            double[] inputs = { arr[0], arr[1] };
            double[] outputs = { arr[0] ^ arr[1] };
            brain.train(inputs, outputs);
        }

        for(int i = 0; i < xorInputs.length; i++)
        {
            double[] inputs = { xorInputs[i][0], xorInputs[i][1] };
            double[] outputs = brain.process(inputs);
            System.out.println("Test: " + Arrays.toString(inputs) + " -> " + Arrays.toString(outputs));
        }
    }

    @Test
    public void TestChessSkills(){

        int numberOfGames = 1000;
        MoveStrategy m = new NNBot();

        int ABWins = 0 ;
        int NNWins = 0 ;

        for (int i = 0; i < numberOfGames; i++) {
            System.out.println("Game nr : " + i);

            Board board = Board.crateStandardBoard();
            MoveStrategy a = new ABStock(1);
            ArrayList<MoveTransition> mts = new ArrayList<>();
            mts.add(board.currentPlayer().makeMove(m.execute(board)));
            for (int j = 0; j < 30; j++) {
                mts.add(mts.get(mts.size()-1).getTransitionBoard().currentPlayer().makeMove(a.execute(mts.get(mts.size()-1).getTransitionBoard()))); //black
                mts.add(mts.get(mts.size()-1).getTransitionBoard().currentPlayer().makeMove(m.execute(mts.get(mts.size()-1).getTransitionBoard()))); //white
            }
            BoardEvaluator evaluator = new StandardBoardEvaluator();
            int s = evaluator.evaluate(mts.get(mts.size()-1).getTransitionBoard(),0);
            if (s>0){
                ABWins++;
            } else {
                NNWins++;
            }

        }
        System.out.println("AB : " + ABWins + "\nNN : " + NNWins);


    }
}
