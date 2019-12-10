package board;

import pieces.Piece;

public abstract class Move {

    final Board board;
    final Piece movedpiece;
    final int destinationCoordinate;

    private Move(final Board board, Piece movedpiece, int destinationCoordinate){
        this.board=board;
        this.movedpiece=movedpiece;
        this.destinationCoordinate=destinationCoordinate;
    }

    public static final class MajorMove extends Move {
        public MajorMove(final Board board, final Piece movedpiece, final int destinationCoordinate) {
            super(board, movedpiece, destinationCoordinate);
        }
    }

    public static final class AttackMove extends Move {
        final Piece attackedPiece;
        public AttackMove(final Board board, final Piece movedpiece, final int destinationCoordinate, final Piece attackedPiece) {
            super(board, movedpiece, destinationCoordinate);
            this.attackedPiece=attackedPiece;
        }
    }

}
