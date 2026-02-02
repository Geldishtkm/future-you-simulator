package org.example;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a monthly summary of user activity and progress.
 */
public record MonthlySummary(
    LocalDate monthStart,
    LocalDate monthEnd,
    int totalXpGained,
    int habitsCompleted,
    int goalsProgressed,
    int activeDays,
    double averageConsistency,
    int longestStreak,
    int levelAtStart,
    int levelAtEnd,
    int levelGained,
    List<String> topHabits,
    List<String> achievementsUnlocked,
    String summaryMessage
) {
    /**
     * Creates a monthly summary with all metrics.
     */
    public MonthlySummary {
        if (monthStart == null || monthEnd == null) {
            throw new IllegalArgumentException("Month dates cannot be null");
        }
        if (monthStart.isAfter(monthEnd)) {
            throw new IllegalArgumentException("Month start must be before or equal to month end");
        }
    }
}

