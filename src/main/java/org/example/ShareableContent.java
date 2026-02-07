package org.example;

import java.time.LocalDate;

/**
 * Represents content that can be shared on social media.
 */
public record ShareableContent(
    ShareType type,
    String title,
    String message,
    String imageUrl,
    LocalDate date,
    int value,
    String achievementName
) {
    /**
     * Creates shareable content.
     */
    public ShareableContent {
        if (type == null) {
            throw new IllegalArgumentException("ShareType cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
    }
}

