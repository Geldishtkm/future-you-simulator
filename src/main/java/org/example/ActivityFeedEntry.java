package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single entry in the activity feed.
 */
public record ActivityFeedEntry(
    LocalDateTime timestamp,
    ActivityType type,
    String title,
    String description,
    int xpChange,
    String source
) {
    /**
     * Creates an activity feed entry.
     */
    public ActivityFeedEntry {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("ActivityType cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
    }
}

