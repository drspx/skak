package chess.chesspackage.gui;

import chess.chesspackage.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class GUIUtils {

    public static final String HOLY_WARRIORS = "art/holywarriors/";
    public static final String SIMPLE = "art/simple/";

    private static final String GREEN_DOT = "art/green_dot.png";

    public static BufferedImage getPieceImages(Piece takenPiece) {
        String path = SIMPLE;

        path = path + takenPiece.getPieceAlliance().toString().charAt(0)
                + takenPiece.toString() + ".gif";

        BufferedImage read = null;

        URL resource = ClassLoader.getSystemClassLoader().getResource(path);
        String file = resource.getPath();

        try {
            read = ImageIO.read(new File(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return read;
    }

    public static Component getGreenDot() throws IOException {
        return new JLabel(new ImageIcon(ImageIO.read(new File(ClassLoader.getSystemResource(GREEN_DOT).getPath()))));
    }


}
