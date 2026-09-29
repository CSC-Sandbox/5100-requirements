package edu.calpoly.eye;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Blackboard for the current gaze data
 * Supports both getting the current GazePoint and running methods upon an update
 *
 * @author Jaems Yaguma
 * @version 1.0 (2026-09-29)
 */
public class GazeBlackboard {
    private static GazeBlackboard instance;
    private final GazePoint gazePoint = new GazePoint();
    private final ArrayList<Consumer<GazePoint>> callbacks = new ArrayList<>();

    /**
     * Get the current instance of the singleton
     * Creates a new instance if one does not exist
     *
     * @return The current GazeBlackboard instance
     */
    public synchronized static GazeBlackboard getInstance() {
        if (instance == null) {
            instance = new GazeBlackboard();
        }
        return instance;
    }

    /**
     * Update the currently stored GazePoint
     * Also call any update callbacks that were added
     *
     * @param newGaze The new GazePoint to store
     */
    public synchronized void updateGazePoint(GazePoint newGaze) {
        gazePoint.setXY(newGaze.x, newGaze.y);
        for (Consumer<GazePoint> callback : callbacks) {
            callback.accept(gazePoint);
        }
    }

    /**
     * Get the most recent GazePoint
     *
     * @return A GazePoint with the most recent (x,y) coordinates
     */
    public GazePoint getGazePoint() {
        return gazePoint;
    }

    /**
     * Add a callback function to be called upon an update
     * Must be a Consumer that accepts a GazePoint as a parameter
     *
     * @param callback The callback function to call
     */
    public synchronized void addCallback(Consumer<GazePoint> callback) {
        callbacks.add(callback);
    }

    /**
     * Remove all callback functions queued up
     */
    public synchronized void clearCallbacks() {
        callbacks.clear();
    }
}
