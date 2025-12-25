package org.example.simulation.model;

import org.example.BurnoutWarning;
import org.example.Difficulty;
import org.example.Goal;
import org.example.UserStats;

import java.util.List;
import java.util.Map;

/**
 * Input data for a future simulation.
 * Contains all the necessary information to simulate a user's trajectory over 1-5 years.
 */
public class SimulationInput {
    private final UserStats currentStats;
    private final double habitsConsistencyScore; // 0.0 to 100.0
    private final double averageDailyEffort; // Average XP gained per active day
    private final Map<Difficulty, Integer> difficultyDistribution; // Count of habits per difficulty
    private final List<Goal> activeGoals;
    private final BurnoutWarning burnoutWarning;
    private final int activeDaysLastMonth; // Days with activity in last 30 days
    private final double averageStreakLength; // Average habit streak length
    private final int yearsToSimulate; // 1-5 years

    /**
     * Creates a new SimulationInput.
     *
     * @param currentStats the user's current XP and level
     * @param habitsConsistencyScore consistency score (0.0-100.0)
     * @param averageDailyEffort average XP per active day
     * @param difficultyDistribution distribution of habits by difficulty
     * @param activeGoals currently active goals
     * @param burnoutWarning current burnout warning status
     * @param activeDaysLastMonth number of active days in last 30 days
     * @param averageStreakLength average streak length across all habits
     * @param yearsToSimulate number of years to simulate (1-5)
     */
    public SimulationInput(UserStats currentStats,
                          double habitsConsistencyScore,
                          double averageDailyEffort,
                          Map<Difficulty, Integer> difficultyDistribution,
                          List<Goal> activeGoals,
                          BurnoutWarning burnoutWarning,
                          int activeDaysLastMonth,
                          double averageStreakLength,
                          int yearsToSimulate) {
        if (currentStats == null) {
            throw new IllegalArgumentException("Current stats cannot be null");
        }
        if (habitsConsistencyScore < 0.0 || habitsConsistencyScore > 100.0) {
            throw new IllegalArgumentException("Consistency score must be between 0.0 and 100.0");
        }
        if (averageDailyEffort < 0) {
            throw new IllegalArgumentException("Average daily effort cannot be negative");
        }
        if (difficultyDistribution == null) {
            throw new IllegalArgumentException("Difficulty distribution cannot be null");
        }
        if (activeGoals == null) {
            throw new IllegalArgumentException("Active goals cannot be null");
        }
        if (burnoutWarning == null) {
            throw new IllegalArgumentException("Burnout warning cannot be null");
        }
        if (activeDaysLastMonth < 0 || activeDaysLastMonth > 31) {
            throw new IllegalArgumentException("Active days last month must be between 0 and 31");
        }
        if (averageStreakLength < 0) {
            throw new IllegalArgumentException("Average streak length cannot be negative");
        }
        if (yearsToSimulate < 1 || yearsToSimulate > 5) {
            throw new IllegalArgumentException("Years to simulate must be between 1 and 5");
        }

        this.currentStats = currentStats;
        this.habitsConsistencyScore = habitsConsistencyScore;
        this.averageDailyEffort = averageDailyEffort;
        this.difficultyDistribution = Map.copyOf(difficultyDistribution);
        this.activeGoals = List.copyOf(activeGoals);
        this.burnoutWarning = burnoutWarning;
        this.activeDaysLastMonth = activeDaysLastMonth;
        this.averageStreakLength = averageStreakLength;
        this.yearsToSimulate = yearsToSimulate;
    }

    public UserStats getCurrentStats() {
        return currentStats;
    }

    public double getHabitsConsistencyScore() {
        return habitsConsistencyScore;
    }

    public double getAverageDailyEffort() {
        return averageDailyEffort;
    }

    public Map<Difficulty, Integer> getDifficultyDistribution() {
        return difficultyDistribution;
    }

    public List<Goal> getActiveGoals() {
        return activeGoals;
    }

    public BurnoutWarning getBurnoutWarning() {
        return burnoutWarning;
    }

    public int getActiveDaysLastMonth() {
        return activeDaysLastMonth;
    }

    public double getAverageStreakLength() {
        return averageStreakLength;
    }

    public int getYearsToSimulate() {
        return yearsToSimulate;
    }
}

