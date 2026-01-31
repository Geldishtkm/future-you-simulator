package org.example;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a weekly summary of user activity and progress.
 */
public record WeeklySummary(
    LocalDate weekStart,
    LocalDate weekEnd,
    int totalXpGained,
    int habitsCompleted,
    int goalsProgressed,
    int activeDays,
    double averageConsistency,
    int longestStreak,
    List<String> topHabits,
    List<String> achievementsUnlocked,
    String summaryMessage
) {
    /**
     * Creates a weekly summary with all metrics.
     */
    public WeeklySummary {
        if (weekStart == null || weekEnd == null) {
            throw new IllegalArgumentException("Week dates cannot be null");
        }
        if (weekStart.isAfter(weekEnd)) {
            throw new IllegalArgumentException("Week start must be before or equal to week end");
        }
    }
}

