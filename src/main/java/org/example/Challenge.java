package org.example;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a challenge that users can participate in.
 */
public record Challenge(
    String id,
    String title,
    String description,
    ChallengeType type,
    int targetValue,
    int currentValue,
    LocalDate startDate,
    LocalDate endDate,
    boolean active,
    List<String> participants,
    String reward
) {
    /**
     * Creates a challenge.
     */
    public Challenge {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("ChallengeType cannot be null");
        }
        if (targetValue <= 0) {
            throw new IllegalArgumentException("Target value must be positive");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    /**
     * Calculates progress percentage (0-100).
     */
    public double getProgressPercentage() {
        return Math.min(100.0, (currentValue * 100.0) / targetValue);
    }

    /**
     * Checks if challenge is currently active.
     */
    public boolean isCurrentlyActive(LocalDate currentDate) {
        return active && !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate);
    }
}

