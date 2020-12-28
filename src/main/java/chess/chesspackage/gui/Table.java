package chess.chesspackage.gui;

import chess.chesspackage.engine.board.Board;
import chess.chesspackage.engine.board.BoardUtils;
import chess.chesspackage.engine.board.Move;
import chess.chesspackage.engine.board.Tile;
import chess.chesspackage.engine.pieces.Piece;
import chess.chesspackage.engine.player.MoveTransition;
import chess.chesspackage.engine.player.ai.ABStock;
import chess.chesspackage.engine.player.ai.AlphaBeta;
import chess.chesspackage.engine.player.ai.MiniMax;
import chess.chesspackage.engine.player.ai.MoveStrategy;
import chess.chesspackage.pgn.FENUtilities;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table extends Observable {

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private MoveLog moveLog;

    private final GameSetup gameSetup;

    private Board chessBoard;

    public ArrayList<Board> boardArrayList = new ArrayList<>();

    private Move computerMove;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;

    private boolean highlightLegalMoves = true;

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);

    private static final Table table = new Table();


    private Table() {
        this.gameFrame = new JFrame("Chess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.crateStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        gameSetup = new GameSetup(this.gameFrame, true, this);

        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());

        this.boardDirection = BoardDirection.NORMAL;

        this.gameFrame.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.gameFrame.add(boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.gameFrame.setVisible(true);

    }

    public void setMoveLog(MoveLog moveLog) {
        this.moveLog = moveLog;
    }

    public static Table get() {
        return table;
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPane().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getChessBoard());
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionsMenu());
        return tableMenuBar;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip board");
        flipBoardMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                boardDirection = boardDirection.opposite();
                System.out.println("flip board");
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferencesMenu.add(flipBoardMenuItem);
        preferencesMenu.addSeparator();
        final JCheckBoxMenuItem legalMovesHighlighterCheckBox = new JCheckBoxMenuItem("highlight legal moves", true);
        legalMovesHighlighterCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                highlightLegalMoves = legalMovesHighlighterCheckBox.isSelected();
            }
        });
        preferencesMenu.add(legalMovesHighlighterCheckBox);
        return preferencesMenu;
    }

    private JMenu createOptionsMenu() {
        final JMenu optionsMenu = new JMenu("Options");

        final JMenuItem undoMenuItem = new JMenuItem("undo");
        undoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (Table.get().boardArrayList.size() > 2) {
                    Table.get().updateGameBoard(Table.get().boardArrayList.get(Table.get().boardArrayList.size() - 2));
                    Table.get().boardArrayList.remove(Table.get().boardArrayList.size() - 1);
                    Table.get().boardArrayList.remove(Table.get().boardArrayList.size() - 1);
                    Table.get().show();
                }
            }
        });
        optionsMenu.add(undoMenuItem);

        final JMenuItem setupGameMenuItem = new JMenuItem("Set AI");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().gameSetup);
            }
        });
        optionsMenu.add(setupGameMenuItem);


        final JMenuItem restartGameItem = new JMenuItem("Restart");
        restartGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Object[] options = {"yes", "no"};
                int result = JOptionPane.showOptionDialog(Table.get().gameFrame, "would you really like to restart?", "restart?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, null);

                if (result == JOptionPane.YES_OPTION) {
                    Table.get().updateGameBoard(Board.crateStandardBoard());
                    Table.get().boardArrayList = new ArrayList<>();
                    Table.get().setMoveLog(new MoveLog());
                    Table.get().show();
                }
            }
        });
        optionsMenu.add(restartGameItem);

        return optionsMenu;
    }

    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher implements Observer {

        @Override
        public void update(final Observable observable, final Object o) {
            if (Table.get().gameSetup.isAIPlayer(Table.get().getChessBoard().currentPlayer()) &&
                    !Table.get().getChessBoard().currentPlayer().isCheckMate() &&
                    !Table.get().getChessBoard().currentPlayer().isInStaleMate()) {
                //AI
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }
            if (Table.get().getChessBoard().currentPlayer().isCheckMate()) {
                System.out.println("Game over " + Table.get().getChessBoard().currentPlayer() + " check mate");
            }
            if (Table.get().getChessBoard().currentPlayer().isInStaleMate()) {
                System.out.println("Game over " + Table.get().getChessBoard().currentPlayer() + " stale mate");
            }

        }
    }

    public void updateGameBoard(final Board transitionBoard) {
        this.chessBoard = transitionBoard;
    }

    public void updateComputerMove(final Move move) {
        this.computerMove = move;
    }

    public void moveMadeUpdate(final PlayerType type) {
        setChanged();
        notifyObservers(type);
    }


    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {

        }

        @Override
        protected Move doInBackground() throws Exception {
            MoveStrategy strategy = new ABStock(3);
            if (Table.get().getChessBoard().currentPlayer().getAlliance().isWhite()) {
                if (Table.get().getGameSetup().getWhiteAiType().equals(AiType.MINIMAX)) {
                    strategy = new MiniMax(Table.get().gameSetup.getWhiteSearchDepth());
                }
                if (Table.get().getGameSetup().getWhiteAiType().equals(AiType.ALPHABETA)) {
                    strategy = new AlphaBeta(Table.get().gameSetup.getWhiteSearchDepth());
                }
                if (Table.get().getGameSetup().getWhiteAiType().equals(AiType.ABSTOCK)) {
                    strategy = new ABStock(Table.get().gameSetup.getWhiteSearchDepth());
                }
            }
            if (Table.get().getChessBoard().currentPlayer().getAlliance().isBlack()) {
                if (Table.get().getGameSetup().getBlackAiType().equals(AiType.MINIMAX)) {
                    strategy = new MiniMax(Table.get().gameSetup.getBlackSearchDepth());
                }
                if (Table.get().getGameSetup().getBlackAiType().equals(AiType.ALPHABETA)) {
                    strategy = new AlphaBeta(Table.get().gameSetup.getBlackSearchDepth());
                }
                if (Table.get().getGameSetup().getBlackAiType().equals(AiType.ABSTOCK)) {
                    strategy = new ABStock(Table.get().gameSetup.getBlackSearchDepth());
                }
            }

//            if (Table.get().getGameSetup().getAiType().equals(AiType.MINIMAX)) {
//                strategy = new MiniMax(Table.get().gameSetup.getSearchDepth());
//            } else if (Table.get().getGameSetup().getAiType().equals(AiType.ALPHABETA)){
//                strategy = new AlphaBeta(Table.get().gameSetup.getSearchDepth());
//            } else {
//                strategy = new ABStock(Table.get().gameSetup.getSearchDepth());
//            }

            System.out.println(strategy.getClass().toString() + " selected");
            final Move move = strategy.execute(Table.get().getChessBoard());
            return move;
        }

        @Override
        protected void done() {
            try {
                final Move bestMove = get();

                Table.get().updateComputerMove(bestMove);
                Table.get().boardArrayList.add(Table.get().chessBoard);
                Table.get().updateGameBoard(Table.get().getChessBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPane().redo(Table.get().getChessBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getChessBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public TakenPiecesPanel getTakenPiecesPanel() {
        return takenPiecesPanel;
    }

    public GameHistoryPanel getGameHistoryPane() {
        return gameHistoryPanel;
    }

    public MoveLog getMoveLog() {
        return moveLog;
    }

    public Board getChessBoard() {
        return this.chessBoard;
    }

    public GameSetup getGameSetup() {
        return this.gameSetup;
    }


    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem loadGameItem = new JMenuItem("Load game");
        loadGameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String FENString = JOptionPane.showInputDialog(Table.get().gameFrame, "Type FEN-type string here");
                    Table.get().chessBoard = FENUtilities.createGameFromFEN(FENString);
                    System.out.println("loaded");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("not loaded");
                }
                Table.get().show();
            }
        });
        fileMenu.add(loadGameItem);


        final JMenuItem saveGameMenuItem = new JMenuItem("Save Game");
        saveGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String FENString = FENUtilities.createFENFromBoard(Table.get().getChessBoard());
                String FENStringMessage = "your game in FEN format is : \n" + FENString;
                //JOptionPane.showMessageDialog(Table.get().gameFrame,FENStringMessage);
                JOptionPane.showInputDialog(Table.get().gameFrame, FENStringMessage, FENStringMessage);
                System.out.println(FENString);
            }
        });
        fileMenu.add(saveGameMenuItem);

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

    public enum BoardDirection {
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);

        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;

        private BoardPanel() {
            super(new GridLayout(8, 8));
            this.boardTiles = new ArrayList<>();
            for (int i = 0; i < BoardUtils.NUM_TILES; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }


        public void drawBoard(Board board) {
            removeAll();
            for (final TilePanel tilePanel : boardDirection.traverse(boardTiles)) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog {
        private final List<Move> moves;

        public MoveLog() {
            moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int i) {
            return this.moves.remove(i);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }

    }

    enum PlayerType {
        HUMAN,
        COMPUTER
    }

    enum AiType {
        ABSTOCK,
        ALPHABETA,
        MINIMAX
    }


    private class TilePanel extends JPanel {
        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent mouseEvent) {
                    if (isRightMouseButton(mouseEvent)) {
                        sourceTile = null;
                        humanMovedPiece = null;
                        destinationTile = null;

                    } else if (isLeftMouseButton(mouseEvent)) {
                        if (sourceTile == null) {
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if (humanMovedPiece == null) {
                                sourceTile = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                            if (transition.getMoveStatus().isDone()) {
                                Table.get().boardArrayList.add(Table.get().chessBoard);
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            humanMovedPiece = null;
                            destinationTile = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if (gameSetup.isAIPlayer(Table.get().chessBoard.currentPlayer())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }


                @Override
                public void mousePressed(final MouseEvent mouseEvent) {

                }

                @Override
                public void mouseReleased(final MouseEvent mouseEvent) {

                }

                @Override
                public void mouseEntered(final MouseEvent mouseEvent) {

                }

                @Override
                public void mouseExited(final MouseEvent mouseEvent) {

                }
            });
            validate();

        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(board);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board) {
            this.removeAll();
            if (board.getTile(this.tileId).isTileOccupied()) {
                final BufferedImage image = GUIUtils.getInstance().getPieceImages(board.getTile(this.tileId).getPiece());
                this.add(new JLabel(new ImageIcon(image)));
            }
        }


        private void highlightLegals(final Board board) {
            if (highlightLegalMoves) {
                Collection<Move> moves = pieceLegalMoves(board);
                for (final Move move : moves) {
                    if (move.getDestinationCoordinate() == this.tileId) {
                        try {
                            add(GUIUtils.getInstance().getGreenDot());
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()) {
                Collection<Move> moves = humanMovedPiece.calculateLegalMoves(board);
                if (humanMovedPiece.getPieceType().isKing()) {
                    Collection<Move> castles = board.currentPlayer().calculateKingCastles(board.currentPlayer().getLegalMoves(), board.currentPlayer().getOpponent().getLegalMoves());
                    return Lists.newArrayList(Iterables.concat(castles, moves));
                }
                return Collections.unmodifiableCollection(moves);
            }
            return Collections.emptyList();
        }


        private void assignTileColor() {
            if (BoardUtils.EIGTH_RANK[this.tileId] ||
                    BoardUtils.SIXTH_RANK[this.tileId] ||
                    BoardUtils.FOURTH_RANK[this.tileId] ||
                    BoardUtils.SECOND_RANK[this.tileId]) {
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            } else if (BoardUtils.SEVENTH_RANK[this.tileId] ||
                    BoardUtils.FIFTH_RANK[this.tileId] ||
                    BoardUtils.THIRD_RANK[this.tileId] ||
                    BoardUtils.FIRST_RANK[this.tileId]) {
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
