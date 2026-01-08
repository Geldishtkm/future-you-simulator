package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main service for analytics and insights.
 * Provides read-only analysis of user activity, streaks, trends, and burnout warnings.
 * 
 * This service does not modify any data - it only analyzes existing data.
 */
@Service
public class AnalyticsService {
    private final HabitStreakCalculator streakCalculator;
    private final GoalConsistencyCalculator consistencyCalculator;
    private final TrendAnalyzer trendAnalyzer;
    private final BurnoutDetector burnoutDetector;

    /**
     * Creates a new AnalyticsService with default calculators.
     */
    public AnalyticsService() {
        this(new HabitStreakCalculator(),
             new GoalConsistencyCalculator(),
             new TrendAnalyzer(),
             new BurnoutDetector());
    }

    /**
     * Creates a new AnalyticsService with custom calculators.
     *
     * @param streakCalculator the streak calculator
     * @param consistencyCalculator the consistency calculator
     * @param trendAnalyzer the trend analyzer
     * @param burnoutDetector the burnout detector
     * @throws IllegalArgumentException if any parameter is null
     */
    public AnalyticsService(HabitStreakCalculator streakCalculator,
                           GoalConsistencyCalculator consistencyCalculator,
                           TrendAnalyzer trendAnalyzer,
                           BurnoutDetector burnoutDetector) {
        if (streakCalculator == null) {
            throw new IllegalArgumentException("HabitStreakCalculator cannot be null");
        }
        if (consistencyCalculator == null) {
            throw new IllegalArgumentException("GoalConsistencyCalculator cannot be null");
        }
        if (trendAnalyzer == null) {
            throw new IllegalArgumentException("TrendAnalyzer cannot be null");
        }
        if (burnoutDetector == null) {
            throw new IllegalArgumentException("BurnoutDetector cannot be null");
        }
        this.streakCalculator = streakCalculator;
        this.consistencyCalculator = consistencyCalculator;
        this.trendAnalyzer = trendAnalyzer;
        this.burnoutDetector = burnoutDetector;
    }

    /**
     * Builds XP history from habit service and goal service data.
     *
     * @param habitService the habit service
     * @param goalService the goal service
     * @return a list of XP history entries, ordered by date
     */
    public List<XpHistoryEntry> buildXpHistory(HabitService habitService, GoalService goalService) {
        if (habitService == null) {
            throw new IllegalArgumentException("HabitService cannot be null");
        }
        if (goalService == null) {
            throw new IllegalArgumentException("GoalService cannot be null");
        }

        List<XpHistoryEntry> history = new ArrayList<>();
        XpCalculator xpCalculator = new XpCalculator();

        // Add habit XP entries from habit checks
        List<HabitCheck> habitChecks = habitService.getAllHabitChecks();
        
        // Group by date to aggregate XP per day
        Map<LocalDate, Integer> habitXpByDate = new HashMap<>();
        for (HabitCheck check : habitChecks) {
            XpTransaction transaction = xpCalculator.calculateTransaction(
                check.habit(), check.result());
            LocalDate date = check.date();
            habitXpByDate.merge(date, transaction.amount(), Integer::sum);
        }

        // Add habit XP entries
        for (Map.Entry<LocalDate, Integer> entry : habitXpByDate.entrySet()) {
            if (entry.getValue() != 0) {
                history.add(new XpHistoryEntry(entry.getKey(), entry.getValue(), XpSource.HABIT));
            }
        }

        // Add goal XP entries
        List<GoalNote> goalNotes = goalService.getAllGoalNotes();
        for (GoalNote note : goalNotes) {
            if (note.points() > 0) {
                history.add(new XpHistoryEntry(note.date(), note.points(), XpSource.GOAL));
            }
        }

        // Note: Decay tracking would require storing decay transactions explicitly.
        // For now, decay is not included in history as it's calculated on-the-fly.
        // In production, we'd track all XP transactions including decay.

        // Sort by date
        history.sort((a, b) -> a.date().compareTo(b.date()));

        return history;
    }

    /**
     * Calculates streaks for all habits.
     *
     * @param habits the list of habits to analyze
     * @param habitService the habit service
     * @param currentDate the current date
     * @return a map of habit to HabitStreak
     */
    public Map<Habit, HabitStreak> calculateAllHabitStreaks(List<Habit> habits, 
                                                             HabitService habitService,
                                                             LocalDate currentDate) {
        if (habits == null) {
            throw new IllegalArgumentException("Habits list cannot be null");
        }
        if (habitService == null) {
            throw new IllegalArgumentException("HabitService cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        List<HabitCheck> allChecks = habitService.getAllHabitChecks();
        Map<Habit, HabitStreak> streaks = new HashMap<>();

        for (Habit habit : habits) {
            HabitStreak streak = streakCalculator.calculateStreak(habit, allChecks, currentDate);
            streaks.put(habit, streak);
        }

        return streaks;
    }

    /**
     * Calculates consistency for all goals.
     *
     * @param goals the list of goals to analyze
     * @param goalService the goal service
     * @param currentDate the current date
     * @return a map of goal to GoalConsistency
     */
    public Map<Goal, GoalConsistency> calculateAllGoalConsistency(List<Goal> goals,
                                                                 GoalService goalService,
                                                                 LocalDate currentDate) {
        if (goals == null) {
            throw new IllegalArgumentException("Goals list cannot be null");
        }
        if (goalService == null) {
            throw new IllegalArgumentException("GoalService cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        List<GoalNote> allNotes = goalService.getAllGoalNotes();
        Map<Goal, GoalConsistency> consistency = new HashMap<>();

        for (Goal goal : goals) {
            GoalConsistency goalConsistency = consistencyCalculator.calculateConsistency(goal, allNotes, currentDate);
            consistency.put(goal, goalConsistency);
        }

        return consistency;
    }

    /**
     * Analyzes XP trend over a period.
     *
     * @param habitService the habit service
     * @param goalService the goal service
     * @param lookbackDays the number of days to look back
     * @param currentDate the current date
     * @return the detected Trend
     */
    public Trend analyzeXpTrend(HabitService habitService, GoalService goalService,
                                int lookbackDays, LocalDate currentDate) {
        if (habitService == null || goalService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        if (lookbackDays <= 0) {
            throw new IllegalArgumentException("Lookback days must be positive");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        List<XpHistoryEntry> history = buildXpHistory(habitService, goalService);
        return trendAnalyzer.analyzeTrend(history, lookbackDays, currentDate);
    }

    /**
     * Detects burnout warning signals.
     *
     * @param habitService the habit service
     * @param goalService the goal service
     * @param currentDate the current date
     * @return a BurnoutWarning with detected risk factors
     */
    public BurnoutWarning detectBurnout(HabitService habitService, GoalService goalService,
                                        LocalDate currentDate) {
        if (habitService == null || goalService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        // Build XP history and analyze trend
        List<XpHistoryEntry> history = buildXpHistory(habitService, goalService);
        Trend trend = trendAnalyzer.analyzeTrend(history, 14, currentDate);

        // Get activity logs
        Map<LocalDate, DailyActivityLog> activityLogs = habitService.getAllActivityLogs();
        DailyXpLimit dailyXpLimit = habitService.getDailyXpLimit();

        return burnoutDetector.detectBurnout(trend, history, activityLogs, dailyXpLimit, currentDate);
    }

    /**
     * Generates a comprehensive analytics summary.
     *
     * @param habits the list of habits
     * @param goals the list of goals
     * @param habitService the habit service
     * @param goalService the goal service
     * @param currentDate the current date
     * @return an AnalyticsSummary with all insights
     */
    public AnalyticsSummary generateSummary(List<Habit> habits, List<Goal> goals,
                                           HabitService habitService, GoalService goalService,
                                           LocalDate currentDate) {
        if (habits == null || goals == null) {
            throw new IllegalArgumentException("Habits and goals lists cannot be null");
        }
        if (habitService == null || goalService == null) {
            throw new IllegalArgumentException("Services cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        // Calculate all metrics
        Map<Habit, HabitStreak> streaks = calculateAllHabitStreaks(habits, habitService, currentDate);
        Map<Goal, GoalConsistency> consistency = calculateAllGoalConsistency(goals, goalService, currentDate);
        Trend trend = analyzeXpTrend(habitService, goalService, 14, currentDate);
        BurnoutWarning burnout = detectBurnout(habitService, goalService, currentDate);
        List<XpHistoryEntry> history = buildXpHistory(habitService, goalService);

        return new AnalyticsSummary(streaks, consistency, trend, burnout, history);
    }

    /**
     * Comprehensive analytics summary containing all insights.
     *
     * @param habitStreaks map of habit to streak information
     * @param goalConsistency map of goal to consistency information
     * @param xpTrend the detected XP trend
     * @param burnoutWarning the burnout warning
     * @param xpHistory the complete XP history
     */
    public record AnalyticsSummary(
            Map<Habit, HabitStreak> habitStreaks,
            Map<Goal, GoalConsistency> goalConsistency,
            Trend xpTrend,
            BurnoutWarning burnoutWarning,
            List<XpHistoryEntry> xpHistory
    ) {
    }
}

