package gui;

import engine.board.Board;
import engine.board.BoardUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final Board chessBoard;

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600,600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private static final String HOLY_WARRIORS = "holywarriors/";

    public Table(){
        this.gameFrame=new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.crateStandardBoard();
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(boardPanel, BorderLayout.CENTER);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
            assignTilePieceIcon(chessBoard);
            validate();

        }

        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if (board.getTile(this.tilId).isTileOccupied()){
                //String pieceIconPath = "/home/alexander/git/BlackWidow-Chess/art/holywarriors/";
                String pieceIconPath = HOLY_WARRIORS;
                try {
                    final BufferedImage image = ImageIO.read(new File(new File(new File(pieceIconPath
                            + board.getTile(this.tilId).getPiece().getPieceAlliance().toString().substring(0, 1))
                            + board.getTile(this.tilId).getPiece().toString())
                            + ".gif"));
                    this.add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if (BoardUtils.EIGTH_RANK[this.tilId]||
                BoardUtils.SIXTH_RANK[this.tilId]||
                BoardUtils.FOURTH_RANK[this.tilId]||
                BoardUtils.SECOND_RANK[this.tilId]){
                setBackground(this.tilId%2==0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SEVENTH_RANK[this.tilId]||
                        BoardUtils.FIFTH_RANK[this.tilId]||
                        BoardUtils.THIRD_RANK[this.tilId]||
                        BoardUtils.FIRST_RANK[this.tilId]){
                setBackground(this.tilId%2!=0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
