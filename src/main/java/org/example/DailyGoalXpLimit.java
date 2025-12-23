package org.example;

/**
 * Configuration for daily XP limits per goal.
 * Limits how much XP can be assigned to a single goal per day.
 */
public class DailyGoalXpLimit {
    private final int maxXpPerGoalPerDay;

    /**
     * Creates a new daily goal XP limit configuration.
     *
     * @param maxXpPerGoalPerDay the maximum XP that can be assigned to a single goal per day (must be positive)
     * @throws IllegalArgumentException if maxXpPerGoalPerDay is not positive
     */
    public DailyGoalXpLimit(int maxXpPerGoalPerDay) {
        if (maxXpPerGoalPerDay <= 0) {
            throw new IllegalArgumentException("Daily goal XP limit must be positive");
        }
        this.maxXpPerGoalPerDay = maxXpPerGoalPerDay;
    }

    /**
     * Returns the maximum XP that can be assigned to a single goal per day.
     *
     * @return the daily goal XP limit
     */
    public int getMaxXpPerGoalPerDay() {
        return maxXpPerGoalPerDay;
    }

    /**
     * Creates a default daily goal XP limit of 10 XP per goal per day.
     *
     * @return a DailyGoalXpLimit with 10 XP per goal per day
     */
    public static DailyGoalXpLimit defaultLimit() {
        return new DailyGoalXpLimit(10);
    }
}

