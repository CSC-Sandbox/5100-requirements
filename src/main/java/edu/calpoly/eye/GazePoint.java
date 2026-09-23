package edu.calpoly.eye;

// Literally just a class to hold an (x,y) coordinate
public class GazePoint {

    public double x;
    public double y;

    GazePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Copy constructor
    GazePoint(GazePoint gazePoint) {
        x = gazePoint.x;
        y = gazePoint.y;
    }

    // Set both simultaneously
    public void setXY(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
