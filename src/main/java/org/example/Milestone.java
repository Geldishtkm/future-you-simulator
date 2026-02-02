package org.example;

import java.time.LocalDate;

/**
 * Represents a milestone achievement in the user's journey.
 */
public record Milestone(
    MilestoneType type,
    String title,
    String description,
    int targetValue,
    int currentValue,
    boolean achieved,
    LocalDate achievedDate
) {
    /**
     * Creates a milestone.
     */
    public Milestone {
        if (type == null) {
            throw new IllegalArgumentException("MilestoneType cannot be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (targetValue <= 0) {
            throw new IllegalArgumentException("Target value must be positive");
        }
        if (currentValue < 0) {
            throw new IllegalArgumentException("Current value cannot be negative");
        }
    }

    /**
     * Creates an unachieved milestone.
     */
    public static Milestone unachieved(MilestoneType type, String title, String description, 
                                      int targetValue, int currentValue) {
        return new Milestone(type, title, description, targetValue, currentValue, false, null);
    }

    /**
     * Creates an achieved milestone.
     */
    public static Milestone achieved(MilestoneType type, String title, String description,
                                    int targetValue, int currentValue, LocalDate achievedDate) {
        return new Milestone(type, title, description, targetValue, currentValue, true, achievedDate);
    }

    /**
     * Calculates progress percentage (0-100).
     */
    public double getProgressPercentage() {
        return Math.min(100.0, (currentValue * 100.0) / targetValue);
    }
}

