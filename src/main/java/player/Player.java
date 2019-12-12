package player;

import board.Board;
import board.Move;
import pieces.King;
import pieces.Piece;

import java.util.Collection;

public abstract class Player {
    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;

    Player(final Board board,
           final Collection<Move> legalMoves,
           final Collection<Move> opponentMoves){
        this.board=board;
        this.playerKing=establishKing();
        this.legalMoves=legalMoves;
    }

    private King establishKing() {
        for (final Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("should not reach here ! not a valid board");
    }

    public abstract Collection<Piece> getActivePieces();

}
