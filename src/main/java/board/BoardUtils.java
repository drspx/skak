package board;

public class BoardUtils {

    public static final boolean[] FIRST_COLUMN = null;
    public static final boolean[] SECOND_COLUMN = null;
    public static final boolean[] SEVENTH_COLUMN = null;
    public static final boolean[] EIGTH_COLUMN = null;

    private BoardUtils(){
        throw new RuntimeException("cannot be instantionated");
    }

    public static boolean isValidTileCoordinate(int candidateDestinationCoordinate) {
        return candidateDestinationCoordinate>=0&&candidateDestinationCoordinate<64;
    }

}
