package chess.chesspackage.engine.pieces;

import chess.chesspackage.engine.Alliance;
import chess.chesspackage.engine.board.Board;
import chess.chesspackage.engine.board.BoardUtils;
import chess.chesspackage.engine.board.Move;
import chess.chesspackage.engine.board.Move.MajorAttackMove;
import chess.chesspackage.engine.board.Move.MajorMove;
import chess.chesspackage.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Queen extends Piece{

    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9,-8,-7,-1,1,7,8,9 };

    public Queen(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.QUEEN,piecePosition, pieceAlliance,true);
    }

    public Queen(final int piecePosition, final Alliance pieceAlliance,final boolean isFirstMove) {
        super(PieceType.QUEEN, piecePosition, pieceAlliance,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateCoordinateOffset: CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;
            while (true){
                if (    isFirstColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset) ||
                        isEigthColumnExclusion(candidateDestinationCoordinate,candidateCoordinateOffset)){
                    break;
                }
                candidateDestinationCoordinate+=candidateCoordinateOffset;
                if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                    break;
                } else {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateDestinationTile.isTileOccupied()){
                        legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));
                    } else {
                        final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance){
                            legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Queen movePiece(Move move) {
        return new Queen(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance());
    }
    @Override
    public String toString() {
        return PieceType.QUEEN.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1 ||candidateOffset==-9 || candidateOffset==7);
    }
    private static boolean isEigthColumnExclusion(final int currentPosition, final int candidateOffset){
        return BoardUtils.EIGTH_COLUMN[currentPosition] && (candidateOffset==-7 || candidateOffset == 1 || candidateOffset==9);
    }
}
