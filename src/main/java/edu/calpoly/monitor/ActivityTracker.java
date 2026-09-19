package edu.calpoly.monitor;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.LongSupplier;

// Tracks message timestamps within a moving time window
public final class ActivityTracker {
    public static final Duration DEFAULT_WINDOW = Duration.ofSeconds(60);

    private final long windowNanos;
    private final LongSupplier timeSource;
    private final EnumMap<DataSource, ArrayDeque<Long>> messageTimes =
            new EnumMap<>(DataSource.class);

    // Uses the standard 60-second window.
    public ActivityTracker() {
        this(DEFAULT_WINDOW, System::nanoTime);
    }

    // Allows a custom window and clock when checking expiration behavior
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

    // Records one message for a source
    public synchronized void record(DataSource source) {
        Objects.requireNonNull(source, "source");
        long now = timeSource.getAsLong();
        pruneExpired(now);
        messageTimes.get(source).addLast(now);
    }

    // Returns the current counts after removing old messages
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
