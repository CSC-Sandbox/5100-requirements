package edu.calpoly.monitor;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the shared JSON structure produced by the activity provider and
 * consumed through either REST or MQTT. Provider and consumer implementations
 * should both use this record so their communication contracts remain aligned.
 *
 * @param windowSeconds length of the activity window in seconds
 * @param generatedAt ISO-8601 UTC time at which the snapshot was created
 * @param counts number of active messages for every known data source
 * @author Dylan Gururajan
 * @version September 27, 2026
 */
public record ActivitySnapshot(
        long windowSeconds,
        String generatedAt,
        Map<DataSource, Integer> counts) {

    /**
     * Validates and defensively copies snapshot data.
     */
    public ActivitySnapshot {
        if (windowSeconds <= 0) {
            throw new IllegalArgumentException("windowSeconds must be positive");
        }
        if (generatedAt == null || generatedAt.isBlank()) {
            throw new IllegalArgumentException("generatedAt is required");
        }
        Objects.requireNonNull(counts, "counts");

        EnumMap<DataSource, Integer> validatedCounts = new EnumMap<>(DataSource.class);
        for (DataSource source : DataSource.values()) {
            Integer count = counts.get(source);
            if (count == null || count < 0) {
                throw new IllegalArgumentException(
                        "A nonnegative count is required for " + source);
            }
            validatedCounts.put(source, count);
        }
        counts = Collections.unmodifiableMap(validatedCounts);
    }
}
