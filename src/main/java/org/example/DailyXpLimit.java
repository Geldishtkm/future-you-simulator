package org.example;

/**
 * Configuration for daily XP limits.
 * XP gains are capped per day, but penalties are not limited.
 */
public class DailyXpLimit {
    private final int maxXpPerDay;

    /**
     * Creates a new daily XP limit configuration.
     *
     * @param maxXpPerDay the maximum XP that can be gained per day (must be positive)
     * @throws IllegalArgumentException if maxXpPerDay is not positive
     */
    public DailyXpLimit(int maxXpPerDay) {
        if (maxXpPerDay <= 0) {
            throw new IllegalArgumentException("Daily XP limit must be positive");
        }
        this.maxXpPerDay = maxXpPerDay;
    }

    /**
     * Returns the maximum XP that can be gained per day.
     *
     * @return the daily XP limit
     */
    public int getMaxXpPerDay() {
        return maxXpPerDay;
    }

    /**
     * Creates a default daily XP limit of 100 XP per day.
     *
     * @return a DailyXpLimit with 100 XP per day
     */
    public static DailyXpLimit defaultLimit() {
        return new DailyXpLimit(100);
    }
}

