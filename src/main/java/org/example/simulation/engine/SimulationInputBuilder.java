package org.example.simulation.engine;

import org.example.*;
import org.example.simulation.model.SimulationInput;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds SimulationInput from user data (HabitService, GoalService, AnalyticsService).
 */
public class SimulationInputBuilder {
    private final AnalyticsService analyticsService;

    public SimulationInputBuilder() {
        this(new AnalyticsService());
    }

    public SimulationInputBuilder(AnalyticsService analyticsService) {
        if (analyticsService == null) {
            throw new IllegalArgumentException("AnalyticsService cannot be null");
        }
        this.analyticsService = analyticsService;
    }

    /**
     * Builds a SimulationInput from user data.
     *
     * @param userStats current user stats
     * @param habitService habit service with user's habits
     * @param goalService goal service with user's goals
     * @param yearsToSimulate number of years to simulate (1-5)
     * @return a SimulationInput ready for simulation
     */
    public SimulationInput build(UserStats userStats,
                                HabitService habitService,
                                GoalService goalService,
                                int yearsToSimulate) {
        if (userStats == null) {
            throw new IllegalArgumentException("UserStats cannot be null");
        }
        if (habitService == null) {
            throw new IllegalArgumentException("HabitService cannot be null");
        }
        if (goalService == null) {
            throw new IllegalArgumentException("GoalService cannot be null");
        }
        if (yearsToSimulate < 1 || yearsToSimulate > 5) {
            throw new IllegalArgumentException("Years to simulate must be between 1 and 5");
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate oneMonthAgo = currentDate.minusDays(30);

        // Calculate habits consistency score
        double consistencyScore = calculateHabitsConsistencyScore(habitService, oneMonthAgo, currentDate);

        // Calculate average daily effort
        double averageDailyEffort = calculateAverageDailyEffort(habitService, goalService, oneMonthAgo, currentDate);

        // Calculate difficulty distribution
        Map<Difficulty, Integer> difficultyDistribution = calculateDifficultyDistribution(habitService);

        // Get active goals
        List<Goal> activeGoals = goalService.getAllGoals().stream()
            .filter(goal -> goal.getTargetDate().isAfter(currentDate) || goal.getTargetDate().isEqual(currentDate))
            .collect(Collectors.toList());

        // Get burnout warning
        BurnoutWarning burnoutWarning = calculateBurnoutWarning(habitService, goalService, currentDate);

        // Calculate active days in last month
        int activeDaysLastMonth = calculateActiveDaysLastMonth(habitService, oneMonthAgo, currentDate);

        // Calculate average streak length
        double averageStreakLength = calculateAverageStreakLength(habitService, currentDate);

        return new SimulationInput(
            userStats,
            consistencyScore,
            averageDailyEffort,
            difficultyDistribution,
            activeGoals,
            burnoutWarning,
            activeDaysLastMonth,
            averageStreakLength,
            yearsToSimulate
        );
    }

    /**
     * Calculates habits consistency score (0-100) based on completion rate.
     */
    private double calculateHabitsConsistencyScore(HabitService habitService,
                                                  LocalDate startDate,
                                                  LocalDate endDate) {
        List<HabitCheck> checks = habitService.getAllHabitChecks().stream()
            .filter(check -> !check.date().isBefore(startDate) && !check.date().isAfter(endDate))
            .collect(Collectors.toList());

        if (checks.isEmpty()) {
            return 50.0; // Default if no data
        }

        long doneCount = checks.stream()
            .filter(check -> check.result() == HabitCheckResult.DONE)
            .count();

        return (doneCount * 100.0) / checks.size();
    }

    /**
     * Calculates average daily effort (XP per active day) in the last month.
     */
    private double calculateAverageDailyEffort(HabitService habitService,
                                             GoalService goalService,
                                             LocalDate startDate,
                                             LocalDate endDate) {
        List<XpHistoryEntry> history = analyticsService.buildXpHistory(habitService, goalService);
        
        Map<LocalDate, Integer> xpByDate = new HashMap<>();
        for (XpHistoryEntry entry : history) {
            if (!entry.date().isBefore(startDate) && !entry.date().isAfter(endDate)) {
                xpByDate.merge(entry.date(), entry.xpChange(), Integer::sum);
            }
        }

        if (xpByDate.isEmpty()) {
            return 0.0;
        }

        // Only count days with positive XP (active days)
        List<Integer> activeDayXp = xpByDate.values().stream()
            .filter(xp -> xp > 0)
            .collect(Collectors.toList());

        if (activeDayXp.isEmpty()) {
            return 0.0;
        }

        return activeDayXp.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }

    /**
     * Calculates difficulty distribution of habits.
     */
    private Map<Difficulty, Integer> calculateDifficultyDistribution(HabitService habitService) {
        List<HabitCheck> checks = habitService.getAllHabitChecks();
        Map<Difficulty, Integer> distribution = new HashMap<>();
        
        // Extract unique habits from checks
        Set<Habit> uniqueHabits = checks.stream()
            .map(HabitCheck::habit)
            .collect(Collectors.toSet());
        
        for (Habit habit : uniqueHabits) {
            distribution.merge(habit.getDifficulty(), 1, Integer::sum);
        }

        // Initialize all difficulties to 0 if not present
        for (Difficulty d : Difficulty.values()) {
            distribution.putIfAbsent(d, 0);
        }

        return distribution;
    }

    /**
     * Calculates burnout warning from analytics.
     */
    private BurnoutWarning calculateBurnoutWarning(HabitService habitService,
                                                  GoalService goalService,
                                                  LocalDate currentDate) {
        try {
            return analyticsService.detectBurnout(habitService, goalService, currentDate);
        } catch (Exception e) {
            // Return a safe default if calculation fails
            return new BurnoutWarning(false, List.of(), 0.0);
        }
    }

    /**
     * Calculates number of active days in the last month.
     */
    private int calculateActiveDaysLastMonth(HabitService habitService,
                                           LocalDate startDate,
                                           LocalDate endDate) {
        Map<LocalDate, DailyActivityLog> logs = habitService.getAllActivityLogs();
        int activeDays = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DailyActivityLog log = logs.get(date);
            if (log != null && log.getXpGained() > 0) {
                activeDays++;
            }
        }

        return activeDays;
    }

    /**
     * Calculates average streak length across all habits.
     */
    private double calculateAverageStreakLength(HabitService habitService, LocalDate currentDate) {
        List<HabitCheck> allChecks = habitService.getAllHabitChecks();
        
        // Extract unique habits from checks
        Set<Habit> uniqueHabits = allChecks.stream()
            .map(HabitCheck::habit)
            .collect(Collectors.toSet());

        if (uniqueHabits.isEmpty()) {
            return 0.0;
        }

        HabitStreakCalculator streakCalculator = new HabitStreakCalculator();
        List<HabitStreak> streaks = uniqueHabits.stream()
            .map(habit -> streakCalculator.calculateStreak(habit, allChecks, currentDate))
            .collect(Collectors.toList());

        return streaks.stream()
            .mapToInt(HabitStreak::currentStreak)
            .average()
            .orElse(0.0);
    }
}

