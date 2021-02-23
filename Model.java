package game2048;

import java.security.KeyStore;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author TODO: Brandon Nguyen
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    public void addTile(Tile tile) { //
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     *
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     *
     * @param */

    // Moving Tiles when called on
    public boolean movingT( int col,int row_start,int row_below) {
        Tile curr = board.tile(col,row_start);
        Tile below = board.tile(col,row_below);
        if (curr.value() == below.value()) {
        board.move(col, row_start, below); // moving the row_below to row_start ??
        score += board.tile(col,row_start).value();
        return true;
    }
        else{
            return false;
        }
    }
    // A helper to see if the tiles under me are a null or and int, if an int, use movingT method
    public boolean NullorInt(int col){
        int tracker = 0; // check if moves are actually moving
        for (int row = board.size()-1;row >=1;row = row - 1){
            Tile curr = board.tile(col,row);
             if (curr == null){
                continue; // if current tile is null we want to keep going down
            } else if (curr !=null) {
                int under = row -1;
                while (under >= 0){//
                    Tile below = board.tile(col,under);
                    if (below != null) { //
                        if (board.tile(col, under).value() == curr.value()) {
                            if (movingT(col, row, under) == true) {
                                tracker += 1;
                                break;
                            } else { //
                                under = under -1;
                            }
                        } else { //
                            under = under -1;
                        }

                    }
                        else if (below == null){
                            under -= 1;
                            }
                    }
                }

            }
        if (tracker > 0){
            return true;
        }else{
            return false;
        }
        }

    // Moving the values up using movingT
    public boolean nullA(int col) {
        int tracker = 0;
        for (int row = 3; row>0;row-=1) {
            if (row == 1){//
              if (board.tile(col,0) ==null) {
                  break;
              }
            } //
            Tile curr = board.tile(col, row);
            int under = row - 1;
            if (curr != null) {
                continue;
            } else {
                while (under >= 0) { // take out = from >=
                    Tile below = board.tile(col, under);
                    if (below == null) {
                    under -= 1;
                } else  {
                    board.move(col, row, below);//
                    tracker += 1;
                    break;}
                }
                
            }

        }
           if (tracker > 0) {
                return true;
            } else {
                return false;
            }
    }

    public boolean tilt(Side side) {
        boolean changed;
        changed = false;
        board.setViewingPerspective(side);
        // TODO: Modify this.board (and perhaps this.score) to account
        // for the tilt to the Side SIDE. If the board changed, set the
        // changed local variable to true.
        for (int col = 0; col < board.size(); col +=1 ) {
            if (NullorInt(col) != false) {
                changed = true;
            }
            if (nullA(col)!=false) {
                changed = true;
            }
        }

        board.setViewingPerspective(Side.NORTH);
        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }


    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // TODO: Fill in this function.
        for (int col = 0; col < b.size(); col = col+1) {
            for (int row = 0;row < b.size();row = row +1) {
                if (b.tile(col,row) == null) {
                    return true;}
                }
            }
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // TODO: Fill in this function.
        for (int x = 0; x<b.size();x+=1){
            for (int z = 0;z<b.size();z+=1){
                if (b.tile(x,z)!=null) {
                    if (b.tile(x, z).value() == MAX_PIECE) {
                        return true;
                    }
                }

            }
        }

        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // TODO: Fill in this function.

        if (emptySpaceExists(b) == true){
            return true;}
        for (int x = 0; x<b.size();x+=1){
            for (int y = 0;y<b.size();y+=1){
                /** check if the value to the right when column is the same */
                if (x!=b.size()-1){ /** make sure doesnt go out of bounds right*/
                    if (b.tile(x, y).value() == b.tile(x+1,y).value()) {
                        return true;}
                    /** if the value next to column is empty */
                    if (b.tile(x+1, y) == null) {
                        return true;}
                }
                if (y!=b.size()-1) {
                    /** if the value on top is the same */
                    if (b.tile(x, y).value() == b.tile(x, y + 1).value()) {
                        return true;
                    }
                    /** if the value on top of column is empty */
                    if (b.tile(x, y + 1) == null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
