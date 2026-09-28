package edu.calpoly.monitor;

import java.util.Optional;

/**
 * Supplies the current data-activity snapshot to {@link DisplayDataActivity},
 * regardless of which communication mechanism produced it. This is the seam
 * that keeps the display unaware of REST vs. MQTT transport details, and lets
 * either implementation be swapped in without changing the GUI.
 *
 * @author Aiden Rodriguez
 * @version September 27, 2026
 */
public interface ActivitySnapshotSource extends AutoCloseable {

    /**
     * Begins obtaining activity snapshots (polling or subscribing, depending
     * on the implementation). Safe to call once before first use; the display
     * should call this after it is visible and ready to receive updates.
     */
    void start();

    /**
     * Returns the most recently obtained snapshot, if any has been received
     * or fetched successfully yet. Implementations return the same value
     * repeatedly between updates rather than blocking for a fresh one.
     *
     * @return the latest known snapshot, or empty if none is available yet
     */
    Optional<ActivitySnapshot> latestSnapshot();

    /**
     * Returns a short, human-readable description of this source's current
     * connection state, suitable for display in the existing status label.
     *
     * @return current status text
     */
    String status();

    /**
     * Stops background activity (a polling thread or an MQTT subscription)
     * and releases any resources. Safe to call even if {@link #start()} was
     * never called.
     */
    @Override
    void close();
}
