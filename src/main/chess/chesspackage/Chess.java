package chesspackage;

import chesspackage.gui.Table;
import chesspackage.tui.Tui;

public class Chess {
    public static void main(String[] args) {
        //Table.get().show();
        Tui.getInstance().run();
    }
}
