package chess.chesspackage.gui;

import chess.chesspackage.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class GUIUtils {

    public static final String HOLY_WARRIORS = "/art/holywarriors/";
    public static final String SIMPLE = "/art/simple/";

    private static final String GREEN_DOT = "/art/green_dot.png";

    private static GUIUtils utils;

    public static GUIUtils getInstance() {
        if (utils == null) {
            utils = new GUIUtils();
        }
        return utils;
    }

    public BufferedImage getPieceImages(Piece takenPiece) {
        String path = SIMPLE + takenPiece.getPieceAlliance().toString().charAt(0) + takenPiece.toString() + ".gif";

        URL resource = this.getClass().getResource(path);

        BufferedImage read = null;
        try {
            read = ImageIO.read(resource);
        } catch (IOException e) {
            System.out.println("res failed");
        }
        return read;
    }

    public Component getGreenDot() throws Exception {
        URL resource = this.getClass().getResource(GREEN_DOT);
        BufferedImage read = ImageIO.read(resource);
        return new JLabel(new ImageIcon(read));
    }

}
