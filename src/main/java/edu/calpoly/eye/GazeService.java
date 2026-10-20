package edu.calpoly.eye;

/**
 * Stores the current gaze point for the eye system.
 *
 * Both REST and MQTT communication will use this same service.
 */
public class GazeService {

    private GazePoint gazePoint = new GazePoint(0.5, 0.5);

    public synchronized GazePoint getGazePoint() {
        return new GazePoint(gazePoint);
    }

    public synchronized void setGazePoint(GazePoint gazePoint) {
        if (gazePoint == null) {
            throw new IllegalArgumentException("Gaze point is required");
        }

        if (gazePoint.x < 0.0 || gazePoint.x > 1.0
                || gazePoint.y < 0.0 || gazePoint.y > 1.0) {
            throw new IllegalArgumentException(
                    "Gaze coordinates must be between 0.0 and 1.0");
        }

        this.gazePoint = new GazePoint(gazePoint);
    }
}