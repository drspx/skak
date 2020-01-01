package engine.pieces;

import engine.Alliance;
import engine.board.Board;
import engine.board.BoardUtils;
import engine.board.Move;
import engine.board.Move.AttackMove;
import engine.board.Move.MajorMove;
import com.google.common.collect.ImmutableList;
import engine.board.Move.PawnAttackMove;
import engine.board.Move.PawnJump;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {

    private final static int[] CANDIDATE_MOVE_COORDINATES = {8, 16, 7, 9};

    public Pawn(final int piecePosition, final Alliance pieceAlliance) {
        super(PieceType.PAWN,piecePosition, pieceAlliance, true);
    }
    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(PieceType.PAWN,piecePosition,pieceAlliance,isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
            final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * currentCandidateOffset);

            System.out.println(candidateDestinationCoordinate);

            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                //TODO deal with promotions
                legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
            } else if ( (currentCandidateOffset == 16 && this.isFirstMove() ) &&
                    ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SECOND_RANK[this.piecePosition] && this.getPieceAlliance().isWhite())) ) {
                final int behindCandidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * 8 );
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                        !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGTH_COLUMN[this.piecePosition] && this.getPieceAlliance().isWhite()) ||
                            (BoardUtils.FIRST_COLUMN[this.piecePosition] && this.getPieceAlliance().isBlack()))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        // TODO more to do here
                        legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceOnCandidate));
                    }
                }
            } else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePosition] && this.getPieceAlliance().isWhite()) ||
                            (BoardUtils.EIGTH_COLUMN[this.piecePosition] && this.getPieceAlliance().isBlack()))) {
                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.getPieceAlliance() != pieceOnCandidate.getPieceAlliance()) {
                        // TODO more to do here
                        legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceOnCandidate));
                    }
                }
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(),move.getMovedPiece().getPieceAlliance());
    }
    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

}
