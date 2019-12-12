package gui;

import engine.board.BoardUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Table {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);

    public Table(){
        this.gameFrame=new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("open pgn file");
            }
        });
        fileMenu.add(openPGN);
        final JMenuItem exitMenuItem = new JMenuItem("exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        private BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0 ; i < BoardUtils.NUM_TILES;i++){
                final TilePanel tilePanel = new TilePanel(this,i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }


    }
    private class TilePanel extends JPanel{
        private final int tilId;
        TilePanel(final BoardPanel boardPanel, final int tilId){
            super(new GridBagLayout());
            this.tilId = tilId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            validate();

        }

        private void assignTileColor() {
            if (BoardUtils.FIRST_ROW[this.tilId]||
                BoardUtils.THIRD_ROW[this.tilId]||
                BoardUtils.FIFTH_ROW[this.tilId]||
                BoardUtils.SEVENTH_ROW[this.tilId]){
                setBackground(this.tilId%2==0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SECOND_ROW[this.tilId]||
                        BoardUtils.FOURTH_ROW[this.tilId]||
                        BoardUtils.SIXTH_ROW[this.tilId]||
                        BoardUtils.EIGTH_ROW[this.tilId]){
                setBackground(this.tilId%2!=0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
