package org.example;

import java.time.LocalDateTime;

/**
 * Represents a notification or reminder for the user.
 */
public record Notification(
    Long id,
    NotificationType type,
    String title,
    String message,
    LocalDateTime createdAt,
    LocalDateTime scheduledFor,
    boolean read,
    String actionUrl
) {
    /**
     * Creates a notification.
     */
    public Notification {
        if (type == null) {
            throw new IllegalArgumentException("NotificationType cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        if (scheduledFor == null) {
            throw new IllegalArgumentException("ScheduledFor cannot be null");
        }
    }
}

