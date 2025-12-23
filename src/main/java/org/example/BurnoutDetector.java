package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Detects potential burnout warning signals based on activity patterns.
 * Lightweight detection based on:
 * - XP trend (declining)
 * - Inactivity decay triggered recently
 * - Daily XP cap frequently reached (potential overwork)
 */
public class BurnoutDetector {
    private static final int LOOKBACK_DAYS = 14; // Analyze last 2 weeks
    private static final double CAP_FREQUENCY_THRESHOLD = 0.7; // 70% of days hitting cap
    private static final int RECENT_DECAY_DAYS = 7; // Decay in last 7 days

    /**
     * Detects burnout warning signals.
     *
     * @param xpTrend the current XP trend
     * @param xpHistory the XP history entries
     * @param activityLogs map of date to activity log
     * @param dailyXpLimit the daily XP limit
     * @param currentDate the current date
     * @return a BurnoutWarning with detected risk factors
     * @throws IllegalArgumentException if any parameter is null
     */
    public BurnoutWarning detectBurnout(Trend xpTrend, List<XpHistoryEntry> xpHistory,
                                       java.util.Map<LocalDate, DailyActivityLog> activityLogs,
                                       DailyXpLimit dailyXpLimit, LocalDate currentDate) {
        if (xpTrend == null) {
            throw new IllegalArgumentException("XP trend cannot be null");
        }
        if (xpHistory == null) {
            throw new IllegalArgumentException("XP history cannot be null");
        }
        if (activityLogs == null) {
            throw new IllegalArgumentException("Activity logs cannot be null");
        }
        if (dailyXpLimit == null) {
            throw new IllegalArgumentException("Daily XP limit cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        List<String> riskFactors = new ArrayList<>();
        double severityScore = 0.0;

        // Risk factor 1: Declining XP trend
        if (xpTrend == Trend.DECLINING) {
            riskFactors.add("XP trend is declining over the last " + LOOKBACK_DAYS + " days");
            severityScore += 30.0;
        }

        // Risk factor 2: Recent inactivity decay
        LocalDate decayCheckStart = currentDate.minusDays(RECENT_DECAY_DAYS);
        boolean hasRecentDecay = xpHistory.stream()
                .anyMatch(entry -> entry.source() == XpSource.DECAY &&
                        !entry.date().isBefore(decayCheckStart) &&
                        !entry.date().isAfter(currentDate));
        
        if (hasRecentDecay) {
            riskFactors.add("Inactivity decay triggered in the last " + RECENT_DECAY_DAYS + " days");
            severityScore += 25.0;
        }

        // Risk factor 3: Frequently hitting daily XP cap (potential overwork)
        LocalDate lookbackStart = currentDate.minusDays(LOOKBACK_DAYS);
        int daysWithActivity = 0;
        int daysAtCap = 0;

        for (LocalDate date = lookbackStart; !date.isAfter(currentDate); date = date.plusDays(1)) {
            DailyActivityLog log = activityLogs.get(date);
            if (log != null && log.getXpGained() > 0) {
                daysWithActivity++;
                if (log.getXpGained() >= dailyXpLimit.getMaxXpPerDay()) {
                    daysAtCap++;
                }
            }
        }

        if (daysWithActivity > 0) {
            double capFrequency = (double) daysAtCap / daysWithActivity;
            if (capFrequency >= CAP_FREQUENCY_THRESHOLD) {
                riskFactors.add(String.format("Daily XP cap reached on %.0f%% of active days (potential overwork)",
                        capFrequency * 100));
                severityScore += 20.0;
            }
        }

        // Risk factor 4: Long period of high activity followed by decline
        if (xpTrend == Trend.DECLINING && daysAtCap > LOOKBACK_DAYS / 2) {
            riskFactors.add("High activity period followed by decline (possible burnout)");
            severityScore += 25.0;
        }

        // Cap severity score at 100
        severityScore = Math.min(100.0, severityScore);

        boolean isWarningActive = severityScore >= 30.0; // Threshold for showing warning

        return new BurnoutWarning(isWarningActive, riskFactors, severityScore);
    }
}

