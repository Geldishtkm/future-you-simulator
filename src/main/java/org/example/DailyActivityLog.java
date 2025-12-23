package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Tracks daily activity including XP gained and habits checked for a specific date.
 * This class is immutable and used for anti-cheat validation.
 */
public class DailyActivityLog {
    private final LocalDate date;
    private final int xpGained;
    private final List<HabitCheck> habitChecks;

    /**
     * Creates a new daily activity log.
     *
     * @param date the date this log represents (must not be null)
     * @param xpGained the total XP gained on this date (must be non-negative)
     * @param habitChecks the list of habit checks for this date (must not be null)
     * @throws IllegalArgumentException if date is null, xpGained is negative, or habitChecks is null
     */
    public DailyActivityLog(LocalDate date, int xpGained, List<HabitCheck> habitChecks) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (xpGained < 0) {
            throw new IllegalArgumentException("XP gained cannot be negative");
        }
        if (habitChecks == null) {
            throw new IllegalArgumentException("Habit checks list cannot be null");
        }
        this.date = date;
        this.xpGained = xpGained;
        this.habitChecks = new ArrayList<>(habitChecks);
    }

    /**
     * Creates an empty daily activity log for a specific date.
     *
     * @param date the date for this log
     * @return a new DailyActivityLog with 0 XP and no habit checks
     */
    public static DailyActivityLog empty(LocalDate date) {
        return new DailyActivityLog(date, 0, new ArrayList<>());
    }

    /**
     * Adds a habit check and updates XP gained.
     * Returns a new DailyActivityLog instance (immutable).
     *
     * @param habitCheck the habit check to add
     * @param xpFromCheck the XP gained from this check (0 or positive for gains, negative for penalties)
     * @return a new DailyActivityLog with the added check and updated XP
     */
    public DailyActivityLog addHabitCheck(HabitCheck habitCheck, int xpFromCheck) {
        if (habitCheck == null) {
            throw new IllegalArgumentException("Habit check cannot be null");
        }
        if (!habitCheck.date().equals(this.date)) {
            throw new IllegalArgumentException("Habit check date must match log date");
        }

        List<HabitCheck> newChecks = new ArrayList<>(this.habitChecks);
        newChecks.add(habitCheck);

        int newXpGained = this.xpGained;
        if (xpFromCheck > 0) {
            newXpGained += xpFromCheck;
        }

        return new DailyActivityLog(this.date, newXpGained, newChecks);
    }

    /**
     * Adds XP directly to this log (e.g., from goals).
     * Returns a new DailyActivityLog instance (immutable).
     *
     * @param xpToAdd the XP to add (must be non-negative)
     * @return a new DailyActivityLog with updated XP
     * @throws IllegalArgumentException if xpToAdd is negative
     */
    public DailyActivityLog addXp(int xpToAdd) {
        if (xpToAdd < 0) {
            throw new IllegalArgumentException("XP to add cannot be negative");
        }
        if (xpToAdd == 0) {
            return this; // No change needed
        }
        return new DailyActivityLog(this.date, this.xpGained + xpToAdd, this.habitChecks);
    }

    /**
     * Checks if a specific habit has already been checked on this date.
     *
     * @param habit the habit to check
     * @return true if the habit has already been checked on this date
     */
    public boolean hasHabitBeenChecked(Habit habit) {
        if (habit == null) {
            return false;
        }
        return habitChecks.stream()
                .anyMatch(check -> check.habit().equals(habit) && check.isDone());
    }

    /**
     * Returns the date this log represents.
     *
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns the total XP gained on this date (penalties are not counted in this total).
     *
     * @return the XP gained
     */
    public int getXpGained() {
        return xpGained;
    }

    /**
     * Returns an immutable list of habit checks for this date.
     *
     * @return the list of habit checks
     */
    public List<HabitCheck> getHabitChecks() {
        return Collections.unmodifiableList(habitChecks);
    }

    /**
     * Returns the number of habits checked on this date.
     *
     * @return the count of habit checks
     */
    public int getHabitCheckCount() {
        return habitChecks.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DailyActivityLog that = (DailyActivityLog) o;
        return xpGained == that.xpGained &&
                Objects.equals(date, that.date) &&
                Objects.equals(habitChecks, that.habitChecks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, xpGained, habitChecks);
    }

    @Override
    public String toString() {
        return "DailyActivityLog{date=" + date + ", xpGained=" + xpGained + ", habitChecks=" + habitChecks.size() + "}";
    }
}

