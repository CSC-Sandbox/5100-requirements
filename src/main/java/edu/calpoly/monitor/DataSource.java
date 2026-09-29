package edu.calpoly.monitor;

import java.util.Locale;
import java.util.Optional;

/**
 * Identifies the message-producing systems represented in the activity monitor.
 * Each value supplies a user-facing label and can be resolved from the source
 * field at the beginning of a broker message.
 *
 * @author Dylan Gururajan
 * @version September 25, 2026
 */
public enum DataSource {
    ROBOT("Robot"),
    GAZE("Gaze"),
    AFFECT("Affect"),
    LIDAR("LiDAR");

    private final String displayName;

    DataSource(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the human-readable source name intended for display in the user
     * interface.
     *
     * @return display label for this source
     */
    public String displayName() {
        return displayName;
    }

    /**
     * Resolves a data source from the first comma-delimited field of a broker
     * message.
     *
     * @param message broker message whose first field identifies its source
     * @return the matching source, or an empty optional when the message is
     *         blank, {@code null}, or begins with an unknown source
     */
    public static Optional<DataSource> fromMessage(String message) {
        if (message == null || message.isBlank()) {
            return Optional.empty();
        }

        int delimiter = message.indexOf(',');
        String sourceField = delimiter >= 0 ? message.substring(0, delimiter) : message;
        String normalizedSource = sourceField.trim().toUpperCase(Locale.ROOT);

        try {
            return Optional.of(DataSource.valueOf(normalizedSource));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
