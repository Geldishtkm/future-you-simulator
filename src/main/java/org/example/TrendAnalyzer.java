package org.example;

import java.time.LocalDate;
import java.util.List;

/**
 * Analyzes XP trends over time to detect improving, stable, or declining patterns.
 */
public class TrendAnalyzer {
    private static final int MIN_DATA_POINTS = 3; // Minimum points needed for trend analysis

    /**
     * Analyzes XP trend over a period of time.
     *
     * @param xpHistory the XP history entries, ordered by date (must not be null)
     * @param lookbackDays the number of days to look back for trend analysis
     * @param currentDate the current date
     * @return the detected Trend
     * @throws IllegalArgumentException if xpHistory is null or lookbackDays is not positive
     */
    public Trend analyzeTrend(List<XpHistoryEntry> xpHistory, int lookbackDays, LocalDate currentDate) {
        if (xpHistory == null) {
            throw new IllegalArgumentException("XP history cannot be null");
        }
        if (lookbackDays <= 0) {
            throw new IllegalArgumentException("Lookback days must be positive");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        LocalDate startDate = currentDate.minusDays(lookbackDays);

        // Filter entries within the lookback period
        List<XpHistoryEntry> recentEntries = xpHistory.stream()
                .filter(entry -> !entry.date().isBefore(startDate) && !entry.date().isAfter(currentDate))
                .sorted((a, b) -> a.date().compareTo(b.date()))
                .toList();

        if (recentEntries.size() < MIN_DATA_POINTS) {
            return Trend.STABLE; // Not enough data
        }

        // Calculate daily XP totals
        int[] dailyXp = new int[lookbackDays];
        for (XpHistoryEntry entry : recentEntries) {
            long daysAgo = java.time.temporal.ChronoUnit.DAYS.between(entry.date(), currentDate);
            int index = (int) (lookbackDays - 1 - daysAgo);
            if (index >= 0 && index < lookbackDays) {
                dailyXp[index] += entry.xpChange();
            }
        }

        // Split into two halves and compare
        int firstHalfSize = lookbackDays / 2;
        int secondHalfSize = lookbackDays - firstHalfSize;

        double firstHalfAverage = calculateAverage(dailyXp, 0, firstHalfSize);
        double secondHalfAverage = calculateAverage(dailyXp, firstHalfSize, secondHalfSize);

        // Determine trend based on comparison
        double difference = secondHalfAverage - firstHalfAverage;
        double threshold = Math.abs(firstHalfAverage) * 0.1; // 10% threshold for stability

        if (difference > threshold) {
            return Trend.IMPROVING;
        } else if (difference < -threshold) {
            return Trend.DECLINING;
        } else {
            return Trend.STABLE;
        }
    }

    /**
     * Calculates the average of a portion of an array.
     */
    private double calculateAverage(int[] array, int start, int length) {
        if (length == 0) {
            return 0.0;
        }
        long sum = 0;
        for (int i = start; i < start + length && i < array.length; i++) {
            sum += array[i];
        }
        return (double) sum / length;
    }
}

