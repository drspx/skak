package chesspackage.gui;

import chesspackage.engine.pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GUIUtils {

    public static final String HOLY_WARRIORS = "art/holywarriors/";
    public static final String SIMPLE = "art/simple/";

    private static final String GREEN_DOT = "art/green_dot.png";

    public static BufferedImage getPieceImages(Piece takenPiece) throws IOException {
        return ImageIO.read(new File(new File(new File(SIMPLE
                + takenPiece.getPieceAlliance().toString().substring(0, 1))
                + takenPiece.toString())
                + ".gif"));
    }

    public static Component getGreenDot() throws IOException {
        return new JLabel(new ImageIcon(ImageIO.read(new File(GREEN_DOT))));
    }


}
