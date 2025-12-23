package org.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculates habit streaks based on habit check history.
 * A streak continues as long as the habit is completed on consecutive days.
 * A streak breaks when a habit is missed on a scheduled day.
 */
public class HabitStreakCalculator {
    /**
     * Calculates streak information for a habit based on its check history.
     *
     * @param habit the habit to calculate streaks for
     * @param habitChecks all habit checks for this habit, ordered by date (must not be null)
     * @param currentDate the current date to calculate streaks up to
     * @return the HabitStreak information
     * @throws IllegalArgumentException if habit or habitChecks is null
     */
    public HabitStreak calculateStreak(Habit habit, List<HabitCheck> habitChecks, LocalDate currentDate) {
        if (habit == null) {
            throw new IllegalArgumentException("Habit cannot be null");
        }
        if (habitChecks == null) {
            throw new IllegalArgumentException("Habit checks list cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        // Filter checks for this specific habit and sort by date
        List<HabitCheck> relevantChecks = habitChecks.stream()
                .filter(check -> check.habit().equals(habit))
                .sorted((a, b) -> a.date().compareTo(b.date()))
                .collect(Collectors.toList());

        if (relevantChecks.isEmpty()) {
            return new HabitStreak(habit, 0, 0, null);
        }

        int currentStreak = 0;
        int longestStreak = 0;
        LocalDate streakStartDate = null;
        LocalDate currentStreakStart = null;

        // Calculate streaks by iterating through checks
        for (int i = 0; i < relevantChecks.size(); i++) {
            HabitCheck check = relevantChecks.get(i);
            LocalDate checkDate = check.date();

            if (check.isDone()) {
                // Habit was completed
                if (currentStreak == 0) {
                    // Starting a new streak
                    currentStreak = 1;
                    currentStreakStart = checkDate;
                } else {
                    // Check if this continues the streak (consecutive day)
                    HabitCheck previousCheck = relevantChecks.get(i - 1);
                    long daysBetween = ChronoUnit.DAYS.between(previousCheck.date(), checkDate);
                    
                    if (daysBetween == 1) {
                        // Consecutive day - streak continues
                        currentStreak++;
                    } else if (daysBetween > 1) {
                        // Gap detected - streak broken, start new one
                        if (currentStreak > longestStreak) {
                            longestStreak = currentStreak;
                        }
                        currentStreak = 1;
                        currentStreakStart = checkDate;
                    }
                    // daysBetween == 0 means same day, which shouldn't happen but we ignore it
                }
            } else if (check.isMissed()) {
                // Habit was missed - streak breaks
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak;
                }
                currentStreak = 0;
                currentStreakStart = null;
            }
        }

        // Update longest streak if current streak is longer
        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }

        // Check if current streak is still active (last check was recent and done)
        HabitCheck lastCheck = relevantChecks.get(relevantChecks.size() - 1);
        if (lastCheck.isMissed() || ChronoUnit.DAYS.between(lastCheck.date(), currentDate) > 1) {
            // Streak is broken (missed or too much time passed)
            streakStartDate = null;
            if (currentStreak > 0) {
                // Save current streak as longest if it's longer
                if (currentStreak > longestStreak) {
                    longestStreak = currentStreak;
                }
                currentStreak = 0;
            }
        } else {
            // Streak is still active
            streakStartDate = currentStreakStart;
        }

        return new HabitStreak(habit, currentStreak, longestStreak, streakStartDate);
    }
}

