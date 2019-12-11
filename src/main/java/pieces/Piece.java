package pieces;

import board.Board;
import board.Move;

import java.util.Collection;

public abstract class Piece {

    protected final int piecePosition;
    protected final Alliance PieceAllience;
    protected final boolean isFirstMove;

    Piece(final int piecePosition, final Alliance pieceAlliance){
        this.piecePosition = piecePosition;
        this.PieceAllience = pieceAlliance;
        //TODO
        this.isFirstMove=false;
    }

    public int getPiecePosition() {
        return piecePosition;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public Alliance getPieceAllience() {
        return PieceAllience;
    }

    public enum PieceType {

        PAWN("P"),
        KNIGHT("K"),
        BISHOP("B"),
        ROOK("R"),
        QUEEN("Q"),
        KING("K");

        private String pieceName;

        PieceType(final String pieceName){
            this.pieceName = pieceName;
        }


        @Override
        public String toString() {
            return this.pieceName;
        }
    }
}
