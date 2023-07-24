package model;

import java.io.Serializable;

/** A representation of a tetris point
 * A Tetris Piece is defined by a collection of TetrisPoints
 *
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class TetrisPoint implements Serializable, Comparable<TetrisPoint> {
    public int x;
    public int y;

    /**
     * Constructor
     *
     * @param x position of point
     * @param y position of point
     */
    public TetrisPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor
     *
     * @param point point to use to initialize
     */
    public TetrisPoint(TetrisPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    /**
     * Returns true if two pieces are the same --
     * their bodies contain the same points.
     * Interestingly, this is not the same as having exactly the
     * same body arrays, since the points may not be
     * in the same order in the bodies. Used internally to detect
     * if two rotations are effectively the same.
     *
     * @param other the object to compare to this
     *
     * @return true if objects are the same
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TetrisPoint)) return false;
        TetrisPoint pt = (TetrisPoint)other;
        return(x==pt.x && y==pt.y);
    }

    /**
     * Print the point
     *
     * @return a string representation of the point
     */
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * Compare points, for sorting. Sort by x coordinates, then sort by y coordinates
     *
     * @return 0 if equals, 1 if greater than, else -1
     */
    @Override
    public int compareTo(TetrisPoint o) {
        if(o.x == this.x && o.y == this.y) return 0;
        if(this.x > o.x || this.x == o.x && this.y > o.y) return 1;
        return -1;
    }
}
