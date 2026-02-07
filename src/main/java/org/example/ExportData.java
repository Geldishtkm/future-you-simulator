package org.example;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents exported user data.
 */
public record ExportData(
    LocalDate exportDate,
    UserStats userStats,
    List<Habit> habits,
    List<Goal> goals,
    List<HabitCheck> habitChecks,
    List<GoalNote> goalNotes,
    List<Achievement> achievements,
    List<Milestone> milestones
) {
    /**
     * Creates export data.
     */
    public ExportData {
        if (exportDate == null) {
            throw new IllegalArgumentException("Export date cannot be null");
        }
        if (userStats == null) {
            throw new IllegalArgumentException("User stats cannot be null");
        }
        if (habits == null || goals == null || habitChecks == null || 
            goalNotes == null || achievements == null || milestones == null) {
            throw new IllegalArgumentException("Lists cannot be null");
        }
    }
}

