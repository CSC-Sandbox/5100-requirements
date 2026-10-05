package edu.calpoly.monitor;

import java.util.Optional;

/**
 * Supplies the current data-activity snapshot to DisplayDataActivity
 *
 * @author Aiden Rodriguez
 * @version September 27, 2026
 */
public interface ActivitySnapshotSource extends AutoCloseable {

    /**
     * Begins obtaining activity snapshots
     */
    void start();

    /**
     * Returns the most recently obtained snapshot
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

    @Override
    void close();
}