package edu.calpoly.robot;

import java.util.function.Consumer;

/**
 * RobotBlackBoard is a simple storage for a RobotMessage.
 * 
 * Typical usage is create RobotBlackBoard, optionally add CallbackOnPost, and then post whenever necessary.
 *
 * @author Paul Motter (PaulMotter)
 * @version 1.0.0 (9/24/2026)
 */
public class RobotBlackBoard {
    // The most recently posted message, or null if nothing has been posted yet.
    private RobotMessage latest;
    // Optional callback invoked when a new message is posted.
    private Consumer<RobotMessage> onUpdate;

    /**
     * Creates an empty RobotBlackBoard with no data posted yet.
     */
    public RobotBlackBoard() {
        latest = null;
        onUpdate = null;
    }

    /**
     * Registers a callback that is called on every post.
     * Passing null removes any existing callback.
     * @param callback The callback to run on each post, or null to clear it.
     */
    public synchronized void callbackOnPost(Consumer<RobotMessage> callback) {
        onUpdate = callback;
    }

    /**
     * Posts a new message to the blackboard, replacing any previous value.
     * A defensive copy is stored. If a callback is registered, it is
     * invoked with a copy of the newly posted message.
     * @param message The RobotMessage to store. Must not be null.
     */
    public synchronized void post(RobotMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null.");
        }
        latest = message.clone();
        if (onUpdate != null) {
            onUpdate.accept(latest.clone());
        }
    }

    /**
     * Returns a copy of the most recently posted message.
     * @return A copy of the latest RobotMessage, or null if none has been posted.
     */
    public synchronized RobotMessage read() {
        return latest == null ? null : latest.clone();
    }

    /**
     * Reports whether any message has been posted.
     * @return true if a message is available, false otherwise.
     */
    public synchronized boolean hasData() {
        return latest != null;
    }

    /**
     * Clears the blackboard, discarding any stored message.
     */
    public synchronized void clear() {
        latest = null;
    }
}
