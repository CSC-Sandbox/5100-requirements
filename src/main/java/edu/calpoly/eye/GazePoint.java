package edu.calpoly.eye;

/**
 * GazePoint class to store an (x,y) coordinate as a single object
 *
 * @author James Yaguma
 * @version 1.0 (2026-09-25)
 */
public class GazePoint {

    public double x;
    public double y;

    /**
     * GazePoint constructor
     *
     * @param x Initial x value
     * @param y Initial y value
     */
    GazePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Copy constructor for GazePoint
     *
     * @param gazePoint The GazePoint to copy
     */
    GazePoint(GazePoint gazePoint) {
        x = gazePoint.x;
        y = gazePoint.y;
    }

    /**
     * Setter for both x and y simultaneously
     *
     * @param x New x value
     * @param y New y value
     */
    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
