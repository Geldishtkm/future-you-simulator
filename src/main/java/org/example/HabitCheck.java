package org.example;

import java.time.LocalDate;

/**
 * Represents a single habit check on a specific date.
 * This is used to track which habits have been checked and prevent duplicate rewards.
 *
 * @param habit the habit that was checked
 * @param date the date when the habit was checked
 * @param result whether the habit was done or missed
 */
public record HabitCheck(Habit habit, LocalDate date, HabitCheckResult result) {
    /**
     * Creates a new habit check.
     *
     * @param habit the habit that was checked (must not be null)
     * @param date the date when the habit was checked (must not be null)
     * @param result whether the habit was done or missed (must not be null)
     * @throws IllegalArgumentException if any parameter is null
     */
    public HabitCheck {
        if (habit == null) {
            throw new IllegalArgumentException("Habit cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (result == null) {
            throw new IllegalArgumentException("HabitCheckResult cannot be null");
        }
    }

    /**
     * Returns true if this check represents a completed habit.
     *
     * @return true if result is DONE
     */
    public boolean isDone() {
        return result == HabitCheckResult.DONE;
    }

    /**
     * Returns true if this check represents a missed habit.
     *
     * @return true if result is MISSED
     */
    public boolean isMissed() {
        return result == HabitCheckResult.MISSED;
    }
}

