package org.example;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Main service for managing habit checks and XP tracking.
 * Provides anti-cheat validation, daily XP capping, and activity tracking.
 * 
 * This is the primary API for interacting with the habit system.
 */
public class HabitService {
    private final XpCalculator xpCalculator;
    private final LevelCalculator levelCalculator;
    private final XpDecayCalculator decayCalculator;
    private final DailyXpLimit dailyXpLimit;
    private final Map<LocalDate, DailyActivityLog> activityLogs;
    private LocalDate lastActivityDate;

    /**
     * Creates a new HabitService with default settings.
     * Default: 100 XP per day limit, 3 days inactivity threshold.
     */
    public HabitService() {
        this(new XpCalculator(), 
             new LevelCalculator(), 
             new XpDecayCalculator(),
             DailyXpLimit.defaultLimit());
    }

    /**
     * Creates a new HabitService with custom settings.
     *
     * @param xpCalculator the XP calculator to use
     * @param levelCalculator the level calculator to use
     * @param decayCalculator the decay calculator to use
     * @param dailyXpLimit the daily XP limit configuration
     * @throws IllegalArgumentException if any parameter is null
     */
    public HabitService(XpCalculator xpCalculator, 
                       LevelCalculator levelCalculator,
                       XpDecayCalculator decayCalculator,
                       DailyXpLimit dailyXpLimit) {
        if (xpCalculator == null) {
            throw new IllegalArgumentException("XpCalculator cannot be null");
        }
        if (levelCalculator == null) {
            throw new IllegalArgumentException("LevelCalculator cannot be null");
        }
        if (decayCalculator == null) {
            throw new IllegalArgumentException("XpDecayCalculator cannot be null");
        }
        if (dailyXpLimit == null) {
            throw new IllegalArgumentException("DailyXpLimit cannot be null");
        }
        this.xpCalculator = xpCalculator;
        this.levelCalculator = levelCalculator;
        this.decayCalculator = decayCalculator;
        this.dailyXpLimit = dailyXpLimit;
        this.activityLogs = new HashMap<>();
    }

    /**
     * Checks a habit for a specific date and updates user stats.
     * This method enforces all business rules:
     * - Prevents duplicate rewards for the same habit on the same day
     * - Applies daily XP cap to gains (not penalties)
     * - Tracks daily activity
     * - Applies inactivity decay if applicable
     *
     * @param userStats the current user stats
     * @param habit the habit to check
     * @param date the date for this habit check
     * @param result whether the habit was done or missed
     * @return a CheckResult containing updated stats and activity log
     * @throws IllegalArgumentException if any parameter is null
     * @throws IllegalStateException if attempting to reward the same habit twice on the same day
     */
    public CheckResult checkHabit(UserStats userStats, Habit habit, LocalDate date, HabitCheckResult result) {
        if (userStats == null) {
            throw new IllegalArgumentException("UserStats cannot be null");
        }
        if (habit == null) {
            throw new IllegalArgumentException("Habit cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (result == null) {
            throw new IllegalArgumentException("HabitCheckResult cannot be null");
        }

        // Anti-cheat: Prevent duplicate rewards for the same habit on the same day
        DailyActivityLog todayLog = activityLogs.getOrDefault(date, DailyActivityLog.empty(date));
        if (result == HabitCheckResult.DONE && todayLog.hasHabitBeenChecked(habit)) {
            throw new IllegalStateException(
                String.format("Habit '%s' has already been checked as DONE on %s. Cannot reward twice.", 
                    habit.getName(), date));
        }

        // Apply inactivity decay if needed
        UserStats statsAfterDecay = userStats;
        if (lastActivityDate != null && lastActivityDate.isBefore(date)) {
            XpTransaction decayTransaction = decayCalculator.calculateDecay(lastActivityDate, date, userStats.getTotalXp());
            if (decayTransaction != null) {
                statsAfterDecay = statsAfterDecay.applyTransaction(decayTransaction, levelCalculator);
            }
        }

        // Calculate base XP transaction
        XpTransaction baseTransaction = xpCalculator.calculateTransaction(habit, result);

        // Apply daily XP cap to gains only (penalties are not capped)
        XpTransaction finalTransaction = applyDailyXpCap(baseTransaction, todayLog, date);

        // Create habit check record
        HabitCheck habitCheck = new HabitCheck(habit, date, result);

        // Update daily activity log
        int xpFromTransaction = finalTransaction.isGain() ? finalTransaction.amount() : 0;
        DailyActivityLog updatedLog = todayLog.addHabitCheck(habitCheck, xpFromTransaction);
        activityLogs.put(date, updatedLog);

        // Update user stats (only if transaction has effect)
        UserStats updatedStats = statsAfterDecay;
        if (!finalTransaction.isNoOp()) {
            updatedStats = statsAfterDecay.applyTransaction(finalTransaction, levelCalculator);
        }

        // Update last activity date
        if (lastActivityDate == null || date.isAfter(lastActivityDate)) {
            lastActivityDate = date;
        }

        return new CheckResult(updatedStats, updatedLog, finalTransaction);
    }

    /**
     * Applies the daily XP cap to a transaction.
     * Only gains are capped; penalties are not limited.
     *
     * @param transaction the base transaction
     * @param todayLog the activity log for today
     * @param date the date of the transaction
     * @return the transaction with cap applied (may be modified if it's a gain)
     */
    private XpTransaction applyDailyXpCap(XpTransaction transaction, DailyActivityLog todayLog, LocalDate date) {
        if (!transaction.isGain()) {
            return transaction; // Penalties are not capped
        }

        int xpAlreadyGained = todayLog.getXpGained();
        int remainingXpCapacity = dailyXpLimit.getMaxXpPerDay() - xpAlreadyGained;

        if (remainingXpCapacity <= 0) {
            // Daily cap already reached
            String reason = String.format("Daily XP cap reached (%d XP). Habit '%s' would give %d XP but is capped at 0.",
                    dailyXpLimit.getMaxXpPerDay(), 
                    extractHabitNameFromReason(transaction.reason()),
                    transaction.amount());
            return new XpTransaction(0, reason);
        }

        if (transaction.amount() > remainingXpCapacity) {
            // Cap the transaction to remaining capacity
            String reason = String.format("%s (capped from %d to %d due to daily limit)",
                    transaction.reason(),
                    transaction.amount(),
                    remainingXpCapacity);
            return new XpTransaction(remainingXpCapacity, reason);
        }

        return transaction; // No capping needed
    }

    /**
     * Extracts habit name from transaction reason string.
     * Helper method for creating better capped transaction reasons.
     */
    private String extractHabitNameFromReason(String reason) {
        // Simple extraction - assumes format "Completed habit 'Name'..."
        int start = reason.indexOf("'");
        if (start >= 0) {
            int end = reason.indexOf("'", start + 1);
            if (end > start) {
                return reason.substring(start + 1, end);
            }
        }
        return "habit";
    }

    /**
     * Gets the activity log for a specific date.
     *
     * @param date the date to query
     * @return the activity log for that date, or an empty log if no activity
     */
    public DailyActivityLog getActivityLog(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return activityLogs.getOrDefault(date, DailyActivityLog.empty(date));
    }

    /**
     * Gets the last activity date.
     *
     * @return the last activity date, or null if no activity has been recorded
     */
    public LocalDate getLastActivityDate() {
        return lastActivityDate;
    }

    /**
     * Sets the last activity date (useful for initialization or testing).
     *
     * @param date the last activity date
     */
    public void setLastActivityDate(LocalDate date) {
        this.lastActivityDate = date;
    }

    /**
     * Gets the daily XP limit configuration.
     *
     * @return the daily XP limit
     */
    public DailyXpLimit getDailyXpLimit() {
        return dailyXpLimit;
    }

    /**
     * Result of a habit check operation.
     * Contains the updated user stats, activity log, and the XP transaction that was applied.
     *
     * @param userStats the updated user stats after the check
     * @param activityLog the updated activity log for the date
     * @param transaction the XP transaction that was applied (may be capped)
     */
    public record CheckResult(UserStats userStats, DailyActivityLog activityLog, XpTransaction transaction) {
    }
}

