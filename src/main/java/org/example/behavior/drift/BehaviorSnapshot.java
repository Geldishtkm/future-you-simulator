package org.example.behavior.drift;

import java.time.LocalDate;

/**
 * A snapshot of user behavior at a specific point in time.
 * Immutable model for capturing behavioral metrics.
 */
public record BehaviorSnapshot(
    LocalDate date,
    double averageDailyXp,
    double habitCompletionRate, // 0.0 to 100.0
    double streakStability, // 0.0 to 100.0 (how consistent streaks are)
    double burnoutRiskScore, // 0.0 to 100.0
    int activeGoalCount,
    double goalEngagementRate // 0.0 to 100.0
) {
    /**
     * Creates a new BehaviorSnapshot.
     *
     * @param date the date of this snapshot
     * @param averageDailyXp average XP per day in the period
     * @param habitCompletionRate habit completion rate (0.0-100.0)
     * @param streakStability streak stability score (0.0-100.0)
     * @param burnoutRiskScore burnout risk score (0.0-100.0)
     * @param activeGoalCount number of active goals
     * @param goalEngagementRate goal engagement rate (0.0-100.0)
     */
    public BehaviorSnapshot {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (averageDailyXp < 0) {
            throw new IllegalArgumentException("Average daily XP cannot be negative");
        }
        if (habitCompletionRate < 0.0 || habitCompletionRate > 100.0) {
            throw new IllegalArgumentException("Habit completion rate must be between 0.0 and 100.0");
        }
        if (streakStability < 0.0 || streakStability > 100.0) {
            throw new IllegalArgumentException("Streak stability must be between 0.0 and 100.0");
        }
        if (burnoutRiskScore < 0.0 || burnoutRiskScore > 100.0) {
            throw new IllegalArgumentException("Burnout risk score must be between 0.0 and 100.0");
        }
        if (activeGoalCount < 0) {
            throw new IllegalArgumentException("Active goal count cannot be negative");
        }
        if (goalEngagementRate < 0.0 || goalEngagementRate > 100.0) {
            throw new IllegalArgumentException("Goal engagement rate must be between 0.0 and 100.0");
        }
    }
}

