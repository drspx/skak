package chesspackage.gui;

import chesspackage.engine.Alliance;
import chesspackage.engine.player.Player;
import chesspackage.gui.Table.AiType;
import chesspackage.gui.Table.PlayerType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameSetup extends JDialog {

    private PlayerType whitePlayerType;
    private PlayerType blackPlayerType;
    private JSpinner searchDepthSpinner;
    private AiType aiType;
    private JSpinner blackSearchDepthSpinner;
    private JSpinner whiteSearchDepthSpinner;
    private AiType whiteAiType;
    private AiType blackAiType;

    private static final String HUMAN_TEXT = "Human";
    private static final String MINIMAX_TEXT = "Minimax";
    private static final String AB_TEXT = "Alpha Beta";
    private static final String AB_STOCK_TEXT = "Alpha Beta Improved";

    GameSetup(final JFrame frame, final boolean modal, Table table) {
        super(frame, modal);
        final JPanel myPanel = new JPanel(new GridLayout(0, 1));
        final JRadioButton whiteHumanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton whiteComputerMinimaxButton = new JRadioButton(MINIMAX_TEXT);
        final JRadioButton whiteComputerABButton = new JRadioButton(AB_TEXT);
        final JRadioButton whiteComputerABStockButton = new JRadioButton(AB_STOCK_TEXT);

        final JRadioButton blackHumanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton blackComputerMinimaxButton = new JRadioButton(MINIMAX_TEXT);
        final JRadioButton blackComputerABButton = new JRadioButton(AB_TEXT);
        final JRadioButton blackComputerABStockButton = new JRadioButton(AB_STOCK_TEXT);


        //whiteHumanButton.setActionCommand(HUMAN_TEXT);
        final ButtonGroup whiteGroup = new ButtonGroup();
        whiteGroup.add(whiteHumanButton);
        whiteGroup.add(whiteComputerMinimaxButton);
        whiteGroup.add(whiteComputerABButton);
        whiteGroup.add(whiteComputerABStockButton);
        whiteHumanButton.setSelected(true);

        final ButtonGroup blackGroup = new ButtonGroup();
        blackGroup.add(blackHumanButton);
        blackGroup.add(blackComputerMinimaxButton);
        blackGroup.add(blackComputerABButton);
        blackGroup.add(blackComputerABStockButton);
        blackHumanButton.setSelected(true);

        getContentPane().add(myPanel);
        myPanel.add(new JLabel("White"));
        myPanel.add(whiteHumanButton);
        myPanel.add(whiteComputerMinimaxButton);
        myPanel.add(whiteComputerABButton);
        myPanel.add(whiteComputerABStockButton);
        this.whiteSearchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth", new SpinnerNumberModel(3, 0, Integer.MAX_VALUE, 1));

        myPanel.add(new JLabel("Black"));
        myPanel.add(blackHumanButton);
        myPanel.add(blackComputerMinimaxButton);
        myPanel.add(blackComputerABButton);
        myPanel.add(blackComputerABStockButton);
        this.blackSearchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth", new SpinnerNumberModel(3, 0, Integer.MAX_VALUE, 1));

//        final JRadioButton miniMax = new JRadioButton(AiType.MINIMAX.toString());
//        final JRadioButton abStock = new JRadioButton(AiType.ABSTOCK.toString());
//        final JRadioButton alphaBeta = new JRadioButton(AiType.ALPHABETA.toString());
//        final ButtonGroup aiGroup = new ButtonGroup();
//        aiGroup.add(miniMax);
//        aiGroup.add(alphaBeta);
//        aiGroup.add(abStock);
//        miniMax.setSelected(true);
//        myPanel.add(new JLabel("AI"));
//        myPanel.add(miniMax);
//        myPanel.add(alphaBeta);
//        myPanel.add(abStock);


//        myPanel.add(new JLabel("Search"));
//        this.searchDepthSpinner = addLabeledSpinner(myPanel, "Search Depth", new SpinnerNumberModel(3, 0, Integer.MAX_VALUE, 1));

        final JButton cancelButton = new JButton("Cancel");
        final JButton okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                whitePlayerType = whiteHumanButton.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER;
                blackPlayerType = blackHumanButton.isSelected() ? PlayerType.HUMAN : PlayerType.COMPUTER;

                if (whiteComputerMinimaxButton.isSelected()){
                    whiteAiType = AiType.MINIMAX;
                }
                if (whiteComputerABButton.isSelected()){
                    whiteAiType = AiType.ALPHABETA;
                }
                if (whiteComputerABStockButton.isSelected()){
                    whiteAiType = AiType.ABSTOCK;
                }

                if (blackComputerMinimaxButton.isSelected()){
                    blackAiType = AiType.MINIMAX;
                }
                if (blackComputerABButton.isSelected()){
                    blackAiType = AiType.ALPHABETA;
                }
                if (blackComputerABStockButton.isSelected()){
                    blackAiType = AiType.ABSTOCK;
                }

                GameSetup.this.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
                GameSetup.this.setVisible(false);
            }
        });

        myPanel.add(cancelButton);
        myPanel.add(okButton);

        setLocationRelativeTo(frame);
        pack();
        setVisible(false);
    }

    void promptUser() {
        setVisible(true);
        repaint();
    }

    boolean isAIPlayer(final Player player) {
        if (player.getAlliance() == Alliance.WHITE) {
            return getWhitePlayerType() == PlayerType.COMPUTER;
        }
        return getBlackPlayerType() == PlayerType.COMPUTER;
    }

    PlayerType getWhitePlayerType() {
        return this.whitePlayerType;
    }

    PlayerType getBlackPlayerType() {
        return this.blackPlayerType;
    }

    private static JSpinner addLabeledSpinner(final Container c,
                                              final String label,
                                              final SpinnerModel model) {
        final JLabel l = new JLabel(label);
        c.add(l);
        final JSpinner spinner = new JSpinner(model);
        l.setLabelFor(spinner);
        c.add(spinner);
        return spinner;
    }

//    int getSearchDepth() {
//        return (Integer) this.searchDepthSpinner.getValue();
//    }

    public int getBlackSearchDepth() {
        return (int) blackSearchDepthSpinner.getValue();
    }

    public int getWhiteSearchDepth() {
        return (int) whiteSearchDepthSpinner.getValue();
    }
    //    public AiType getAiType() {
//        return this.aiType;
//    }

    public AiType getBlackAiType() {
        return blackAiType;
    }

    public AiType getWhiteAiType() {
        return whiteAiType;
    }

}
