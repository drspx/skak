package pieces;

import board.Board;
import board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final int piecePosition;
    protected final Alliance PieceAllience;

    Piece(final int piecePosition, final Alliance pieceAlliance){
        this.piecePosition = piecePosition;
        this.PieceAllience = pieceAlliance;

    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public Alliance getPieceAllience() {
        return PieceAllience;
    }
}
