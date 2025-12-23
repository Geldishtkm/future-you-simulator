package org.example;

/**
 * Represents streak information for a habit.
 *
 * @param habit the habit this streak is for
 * @param currentStreak the current active streak in days (0 if no active streak)
 * @param longestStreak the longest streak ever achieved in days
 * @param streakStartDate the start date of the current streak (null if no active streak)
 */
public record HabitStreak(Habit habit, int currentStreak, int longestStreak, 
                          java.time.LocalDate streakStartDate) {
    /**
     * Creates a new habit streak record.
     *
     * @param habit the habit (must not be null)
     * @param currentStreak the current streak (must be non-negative)
     * @param longestStreak the longest streak (must be non-negative and >= currentStreak)
     * @param streakStartDate the start date (can be null if no active streak)
     * @throws IllegalArgumentException if validation fails
     */
    public HabitStreak {
        if (habit == null) {
            throw new IllegalArgumentException("Habit cannot be null");
        }
        if (currentStreak < 0) {
            throw new IllegalArgumentException("Current streak cannot be negative");
        }
        if (longestStreak < 0) {
            throw new IllegalArgumentException("Longest streak cannot be negative");
        }
        if (longestStreak < currentStreak) {
            throw new IllegalArgumentException("Longest streak cannot be less than current streak");
        }
    }

    /**
     * Returns true if there is an active streak.
     *
     * @return true if currentStreak > 0
     */
    public boolean hasActiveStreak() {
        return currentStreak > 0;
    }
}

