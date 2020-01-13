package chesspackage.engine.pieces;

import chesspackage.engine.Alliance;
import chesspackage.engine.board.Board;
import chesspackage.engine.board.BoardUtils;
import chesspackage.engine.board.Move;
import chesspackage.engine.board.Move.MajorAttackMove;
import chesspackage.engine.board.Move.MajorMove;
import chesspackage.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATES = {-9, -8, -7, -1, 1, 7, 8, 9 };

    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;
    private final boolean isCastled;


    public King(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance,true);
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
        this.isCastled = false;
    }
    public King(final int piecePosition,
                final Alliance pieceAlliance,
                final boolean isFirstMove,
                final boolean kingSideCastleCapable,
                final boolean queenSideCastleCapable,
                final boolean isCastled) {
        super(PieceType.KING, piecePosition, pieceAlliance,isFirstMove);
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable=queenSideCastleCapable;
        this.isCastled=isCastled;
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateOffset: CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            if (isFirstColumnExclusion(this.piecePosition,currentCandidateOffset) ||
                    isEigthColumnExclusion(this.piecePosition,currentCandidateOffset)){
                continue;
            }

            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()){
                    legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));
                } else{
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance){
                        legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(),
                move.getMovedPiece().getPieceAlliance(),
                false,
                move.isCastlingMove(),
                false,
                false);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] &&(candidateOffset== -9 || candidateOffset==-1 || candidateOffset==7 );
    }

    private static boolean isEigthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGTH_COLUMN[currentPosition] && (candidateOffset==-7||candidateOffset==1|| candidateOffset==9);
    }

    public boolean isKingSideCastleCapable() {
        return this.kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return this.queenSideCastleCapable;
    }

    public boolean isCastled() {
        return isCastled;
    }
}
