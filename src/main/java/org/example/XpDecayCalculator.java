package org.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Calculates XP decay based on user inactivity.
 * If a user has no activity for a specified number of days, their XP is gradually reduced.
 */
public class XpDecayCalculator {
    private static final int DEFAULT_INACTIVITY_THRESHOLD_DAYS = 3;
    private static final double DECAY_PERCENTAGE_PER_DAY = 0.05; // 5% per day

    private final int inactivityThresholdDays;
    private final double decayPercentagePerDay;

    /**
     * Creates a new XP decay calculator with default settings.
     * Default: 3 days of inactivity triggers decay, 5% XP loss per day.
     */
    public XpDecayCalculator() {
        this(DEFAULT_INACTIVITY_THRESHOLD_DAYS, DECAY_PERCENTAGE_PER_DAY);
    }

    /**
     * Creates a new XP decay calculator with custom settings.
     *
     * @param inactivityThresholdDays the number of days of inactivity before decay starts (must be positive)
     * @param decayPercentagePerDay the percentage of XP to lose per day of inactivity (0.0 to 1.0)
     * @throws IllegalArgumentException if threshold is not positive or decay percentage is invalid
     */
    public XpDecayCalculator(int inactivityThresholdDays, double decayPercentagePerDay) {
        if (inactivityThresholdDays <= 0) {
            throw new IllegalArgumentException("Inactivity threshold must be positive");
        }
        if (decayPercentagePerDay < 0.0 || decayPercentagePerDay > 1.0) {
            throw new IllegalArgumentException("Decay percentage must be between 0.0 and 1.0");
        }
        this.inactivityThresholdDays = inactivityThresholdDays;
        this.decayPercentagePerDay = decayPercentagePerDay;
    }

    /**
     * Calculates XP decay based on the last activity date and current date.
     * If the user has been inactive for more than the threshold, calculates the decay amount.
     *
     * @param lastActivityDate the date of the last activity (must not be null)
     * @param currentDate the current date (must not be null)
     * @param currentXp the current total XP
     * @return an XpTransaction representing the decay (negative amount), or null if no decay applies
     * @throws IllegalArgumentException if dates are null or lastActivityDate is after currentDate
     */
    public XpTransaction calculateDecay(LocalDate lastActivityDate, LocalDate currentDate, int currentXp) {
        if (lastActivityDate == null) {
            throw new IllegalArgumentException("Last activity date cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }
        if (lastActivityDate.isAfter(currentDate)) {
            throw new IllegalArgumentException("Last activity date cannot be after current date");
        }

        long daysInactive = ChronoUnit.DAYS.between(lastActivityDate, currentDate);

        if (daysInactive <= inactivityThresholdDays) {
            return null; // No decay
        }

        long daysOfDecay = daysInactive - inactivityThresholdDays;
        int totalDecay = 0;
        int remainingXp = currentXp;

        // Apply decay for each day beyond the threshold
        for (long day = 0; day < daysOfDecay; day++) {
            int dailyDecay = (int) (remainingXp * decayPercentagePerDay);
            if (dailyDecay > 0) {
                totalDecay += dailyDecay;
                remainingXp -= dailyDecay;
            } else {
                break; // No more XP to decay
            }
        }

        if (totalDecay == 0) {
            return null;
        }

        String reason = String.format("Inactivity decay: %d days inactive (threshold: %d days)", 
                daysInactive, inactivityThresholdDays);
        return new XpTransaction(-totalDecay, reason);
    }

    /**
     * Returns the inactivity threshold in days.
     *
     * @return the threshold
     */
    public int getInactivityThresholdDays() {
        return inactivityThresholdDays;
    }
}

