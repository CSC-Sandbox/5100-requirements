package edu.calpoly.monitor;

import java.util.Locale;
import java.util.Optional;

// The data sources shown in the activity monitor
public enum DataSource {
    ROBOT("Robot"),
    GAZE("Gaze"),
    AFFECT("Affect"),
    LIDAR("LiDAR");

    private final String displayName;

    DataSource(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    // Reads the source type from the first field of a message
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
