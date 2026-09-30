package edu.calpoly.eye;

/**
 * Simple test for invalid gaze data.
 */
public class TestGazeService {

    public static void main(String[] args) {
        GazeService service = new GazeService();

        try {
            service.setGazePoint(new GazePoint(1.5, 0.5));
            System.out.println("Invalid gaze data was accepted.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid gaze data was rejected.");
        }
    }
}