package edu.calpoly.monitor;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;

/**
 * Maintains per-source message activity over a moving time window.
 * Callers record newly received messages and request immutable snapshots for
 * presentation without needing to manage timestamp expiration themselves.
 *
 * @author Dylan Gururajan
 * @version September 25, 2026
 */
public final class ActivityTracker {
    public static final Duration DEFAULT_WINDOW = Duration.ofSeconds(60);

    private final long windowNanos;
    private final LongSupplier timeSource;
    private final EnumMap<DataSource, ArrayDeque<Long>> messageTimes = new EnumMap<>(DataSource.class);

    /**
     * Creates a tracker that retains activity for the default 60-second window
     * using the system's monotonic clock.
     */
    public ActivityTracker() {
        this(DEFAULT_WINDOW, System::nanoTime);
    }

    /**
     * Creates a tracker with a caller-defined activity window and time source.
     * Supplying the clock makes expiration behavior deterministic in tests and
     * allows integration with another monotonic time source.
     *
     * @param window length of time that recorded messages remain active
     * @param timeSource source of monotonic time values in nanoseconds
     */
    public ActivityTracker(Duration window, LongSupplier timeSource) {
        Objects.requireNonNull(window, "window");
        this.timeSource = Objects.requireNonNull(timeSource, "timeSource");

        if (window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("window must be positive");
        }

        this.windowNanos = window.toNanos();
        for (DataSource source : DataSource.values()) {
            messageTimes.put(source, new ArrayDeque<>());
        }
    }

    /**
     * Records a newly received message for the given data source.
     *
     * @param source source that produced the message
     */
    public synchronized void record(DataSource source) {
        Objects.requireNonNull(source, "source");
        long now = timeSource.getAsLong();
        pruneExpired(now);
        messageTimes.get(source).addLast(now);
    }

    /**
     * Returns the number of currently active messages for every known source.
     *
     * @return immutable mapping from each data source to its active message count
     */
    public synchronized Map<DataSource, Integer> snapshot() {
        pruneExpired(timeSource.getAsLong());

        EnumMap<DataSource, Integer> counts = new EnumMap<>(DataSource.class);
        for (DataSource source : DataSource.values()) {
            counts.put(source, messageTimes.get(source).size());
        }
        return Collections.unmodifiableMap(counts);
    }

    private void pruneExpired(long now) {
        for (ArrayDeque<Long> times : messageTimes.values()) {
            while (!times.isEmpty() && now - times.peekFirst() > windowNanos) {
                times.removeFirst();
            }
        }
    }
}
