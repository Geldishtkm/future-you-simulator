package org.example.strategy.scenario;

import org.example.*;
import org.example.simulation.model.SimulationInput;
import org.example.strategy.Recommendation;
import org.example.strategy.RecommendationType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates concrete simulation scenarios from strategy recommendations.
 * Converts advice into testable behavior changes.
 */
public class ScenarioGeneratorService {
    
    private static final double CONSISTENCY_IMPROVEMENT = 15.0; // Moderate improvement
    private static final double EFFORT_REDUCTION = 0.75; // 25% reduction for burnout
    private static final double EFFORT_BALANCE_REDUCTION = 0.90; // 10% reduction for balance
    private static final int ACTIVE_DAYS_IMPROVEMENT = 5; // Additional active days
    private static final double STREAK_IMPROVEMENT = 5.0; // Improvement in streak length

    /**
     * Generates scenarios from recommendations.
     * Each scenario applies one or more recommendations to create a modified simulation input.
     *
     * @param baseInput the current simulation input
     * @param recommendations the recommendations to apply
     * @return list of generated scenarios
     */
    public List<GeneratedScenario> generateScenarios(SimulationInput baseInput, List<Recommendation> recommendations) {
        if (baseInput == null) {
            throw new IllegalArgumentException("Base input cannot be null");
        }
        if (recommendations == null || recommendations.isEmpty()) {
            throw new IllegalArgumentException("Recommendations cannot be null or empty");
        }

        List<GeneratedScenario> scenarios = new ArrayList<>();
        Map<RecommendationType, List<Recommendation>> groupedByType = groupRecommendationsByType(recommendations);

        // Generate scenarios for high-priority recommendation types
        for (Map.Entry<RecommendationType, List<Recommendation>> entry : groupedByType.entrySet()) {
            RecommendationType type = entry.getKey();
            List<Recommendation> typeRecommendations = entry.getValue();
            
            // Get highest priority recommendation of this type
            Recommendation topRecommendation = typeRecommendations.stream()
                .max(Comparator.comparingDouble(Recommendation::priorityScore))
                .orElse(null);

            if (topRecommendation != null) {
                GeneratedScenario scenario = generateScenarioForType(baseInput, type, topRecommendation);
                if (scenario != null) {
                    scenarios.add(scenario);
                }
            }
        }

        // If multiple high-priority recommendations exist, create a combined scenario
        if (recommendations.size() >= 2) {
            List<Recommendation> topRecommendations = recommendations.stream()
                .sorted(Comparator.comparingDouble(Recommendation::priorityScore).reversed())
                .limit(2)
                .collect(Collectors.toList());
            
            if (topRecommendations.size() == 2) {
                GeneratedScenario combined = generateCombinedScenario(baseInput, topRecommendations);
                if (combined != null) {
                    scenarios.add(combined);
                }
            }
        }

        return scenarios;
    }

    /**
     * Groups recommendations by type.
     */
    private Map<RecommendationType, List<Recommendation>> groupRecommendationsByType(List<Recommendation> recommendations) {
        return recommendations.stream()
            .collect(Collectors.groupingBy(Recommendation::type));
    }

    /**
     * Generates a scenario for a specific recommendation type.
     */
    private GeneratedScenario generateScenarioForType(SimulationInput baseInput,
                                                      RecommendationType type,
                                                      Recommendation recommendation) {
        return switch (type) {
            case REDUCE_BURNOUT_RISK -> generateBurnoutReductionScenario(baseInput, recommendation);
            case IMPROVE_CONSISTENCY -> generateConsistencyImprovementScenario(baseInput, recommendation);
            case ADD_GOAL_FOCUS -> generateGoalFocusScenario(baseInput, recommendation);
            case ADJUST_HABIT_DIFFICULTY -> generateDifficultyAdjustmentScenario(baseInput, recommendation);
            case ADD_HABITS_FOR_GROWTH -> generateHabitGrowthScenario(baseInput, recommendation);
            case BALANCE_EFFORT -> generateEffortBalanceScenario(baseInput, recommendation);
            case OPTIMIZE_STRATEGY -> generateOptimizationScenario(baseInput, recommendation);
        };
    }

    /**
     * Generates a scenario for reducing burnout risk.
     */
    private GeneratedScenario generateBurnoutReductionScenario(SimulationInput baseInput, Recommendation recommendation) {
        // Reduce average daily effort
        double newEffort = baseInput.getAverageDailyEffort() * EFFORT_REDUCTION;
        
        // Reduce difficulty distribution (shift high difficulty habits to medium)
        Map<Difficulty, Integer> newDistribution = reduceDifficultyDistribution(baseInput.getDifficultyDistribution());
        
        // Improve burnout warning (assume it gets better)
        BurnoutWarning newWarning = new BurnoutWarning(false, List.of(), 0.0);
        
        SimulationInput modified = new SimulationInput(
            baseInput.getCurrentStats(),
            baseInput.getHabitsConsistencyScore(), // Keep consistency
            newEffort,
            newDistribution,
            baseInput.getActiveGoals(),
            newWarning,
            baseInput.getActiveDaysLastMonth(),
            baseInput.getAverageStreakLength(),
            baseInput.getYearsToSimulate()
        );

        String rationale = String.format(
            "Reduced average daily effort from %.1f to %.1f XP/day (25%% reduction) and " +
            "adjusted habit difficulty distribution to reduce high-difficulty habits. " +
            "This addresses the high burnout risk identified in the simulation.",
            baseInput.getAverageDailyEffort(), newEffort);

        String benefit = "Lower burnout risk, improved sustainability, and better long-term growth potential " +
                        "despite temporary reduction in daily XP gains.";

        return new GeneratedScenario(
            "Burnout Risk Reduction",
            List.of(recommendation),
            modified,
            rationale,
            benefit
        );
    }

    /**
     * Generates a scenario for improving consistency.
     */
    private GeneratedScenario generateConsistencyImprovementScenario(SimulationInput baseInput, Recommendation recommendation) {
        // Improve consistency score
        double newConsistency = Math.min(100.0, baseInput.getHabitsConsistencyScore() + CONSISTENCY_IMPROVEMENT);
        
        // Increase active days
        int newActiveDays = Math.min(31, baseInput.getActiveDaysLastMonth() + ACTIVE_DAYS_IMPROVEMENT);
        
        // Improve streak length
        double newStreak = baseInput.getAverageStreakLength() + STREAK_IMPROVEMENT;

        SimulationInput modified = new SimulationInput(
            baseInput.getCurrentStats(),
            newConsistency,
            baseInput.getAverageDailyEffort(), // Keep effort same
            baseInput.getDifficultyDistribution(),
            baseInput.getActiveGoals(),
            baseInput.getBurnoutWarning(),
            newActiveDays,
            newStreak,
            baseInput.getYearsToSimulate()
        );

        String rationale = String.format(
            "Improved consistency score from %.1f%% to %.1f%%, increased active days from %d to %d, " +
            "and improved average streak length from %.1f to %.1f days.",
            baseInput.getHabitsConsistencyScore(), newConsistency,
            baseInput.getActiveDaysLastMonth(), newActiveDays,
            baseInput.getAverageStreakLength(), newStreak);

        String benefit = "More sustainable and predictable growth through consistent daily activity, " +
                        "leading to better long-term outcomes.";

        return new GeneratedScenario(
            "Consistency Improvement",
            List.of(recommendation),
            modified,
            rationale,
            benefit
        );
    }

    /**
     * Generates a scenario for adding goal focus.
     */
    private GeneratedScenario generateGoalFocusScenario(SimulationInput baseInput, Recommendation recommendation) {
        // Goals themselves can't be easily modified in SimulationInput, but we can improve
        // consistency and active days which support goal completion
        double newConsistency = Math.min(100.0, baseInput.getHabitsConsistencyScore() + CONSISTENCY_IMPROVEMENT);
        int newActiveDays = Math.min(31, baseInput.getActiveDaysLastMonth() + ACTIVE_DAYS_IMPROVEMENT);
        
        // Slightly increase daily effort (more focused goal work)
        double newEffort = baseInput.getAverageDailyEffort() * 1.1; // 10% increase for goal focus

        SimulationInput modified = new SimulationInput(
            baseInput.getCurrentStats(),
            newConsistency,
            newEffort,
            baseInput.getDifficultyDistribution(),
            baseInput.getActiveGoals(), // Keep existing goals
            baseInput.getBurnoutWarning(),
            newActiveDays,
            baseInput.getAverageStreakLength() + STREAK_IMPROVEMENT,
            baseInput.getYearsToSimulate()
        );

        String rationale = String.format(
            "Improved consistency to %.1f%% and active days to %d to support goal completion. " +
            "Slightly increased daily effort to %.1f XP/day to reflect focused goal work.",
            newConsistency, newActiveDays, newEffort);

        String benefit = "Better skill growth through goal-focused activities, leading to deeper learning " +
                        "and higher income potential over time.";

        return new GeneratedScenario(
            "Goal Focus Enhancement",
            List.of(recommendation),
            modified,
            rationale,
            benefit
        );
    }

    /**
     * Generates a scenario for adjusting habit difficulty.
     */
    private GeneratedScenario generateDifficultyAdjustmentScenario(SimulationInput baseInput, Recommendation recommendation) {
        // Reduce high difficulty habits (4-5) and add medium difficulty (3)
        Map<Difficulty, Integer> newDistribution = adjustDifficultyDistribution(baseInput.getDifficultyDistribution());

        SimulationInput modified = new SimulationInput(
            baseInput.getCurrentStats(),
            baseInput.getHabitsConsistencyScore(),
            baseInput.getAverageDailyEffort(),
            newDistribution,
            baseInput.getActiveGoals(),
            baseInput.getBurnoutWarning(),
            baseInput.getActiveDaysLastMonth(),
            baseInput.getAverageStreakLength(),
            baseInput.getYearsToSimulate()
        );

        String rationale = "Adjusted habit difficulty distribution by reducing high-difficulty habits (4-5) " +
                          "and balancing with medium-difficulty habits (3) for better sustainability.";

        String benefit = "More sustainable habit completion rates, reduced burnout risk, " +
                        "while maintaining good skill growth momentum.";

        return new GeneratedScenario(
            "Habit Difficulty Adjustment",
            List.of(recommendation),
            modified,
            rationale,
            benefit
        );
    }

    /**
     * Generates a scenario for adding habits for growth.
     */
    private GeneratedScenario generateHabitGrowthScenario(SimulationInput baseInput, Recommendation recommendation) {
        // Add medium difficulty habits
        Map<Difficulty, Integer> newDistribution = addMediumDifficultyHabits(baseInput.getDifficultyDistribution());
        
        // Slightly increase daily effort
        double newEffort = baseInput.getAverageDailyEffort() * 1.15; // 15% increase

        SimulationInput modified = new SimulationInput(
            baseInput.getCurrentStats(),
            baseInput.getHabitsConsistencyScore(),
            newEffort,
            newDistribution,
            baseInput.getActiveGoals(),
            baseInput.getBurnoutWarning(),
            baseInput.getActiveDaysLastMonth(),
            baseInput.getAverageStreakLength(),
            baseInput.getYearsToSimulate()
        );

        String rationale = String.format(
            "Added 1-2 medium-difficulty habits to the distribution and increased daily effort to %.1f XP/day " +
            "to reflect additional practice time.",
            newEffort);

        String benefit = "Faster skill development through increased daily practice, " +
                        "leading to accelerated XP growth and level progression.";

        return new GeneratedScenario(
            "Habit Growth Addition",
            List.of(recommendation),
            modified,
            rationale,
            benefit
        );
    }

    /**
     * Generates a scenario for balancing effort.
     */
    private GeneratedScenario generateEffortBalanceScenario(SimulationInput baseInput, Recommendation recommendation) {
        // Slight reduction in effort with improved consistency
        double newEffort = baseInput.getAverageDailyEffort() * EFFORT_BALANCE_REDUCTION;
        double newConsistency = Math.min(100.0, baseInput.getHabitsConsistencyScore() + 10.0);

        SimulationInput modified = new SimulationInput(
            baseInput.getCurrentStats(),
            newConsistency,
            newEffort,
            baseInput.getDifficultyDistribution(),
            baseInput.getActiveGoals(),
            new BurnoutWarning(false, List.of(), 0.0), // Assume burnout improves
            baseInput.getActiveDaysLastMonth(),
            baseInput.getAverageStreakLength() + 3.0,
            baseInput.getYearsToSimulate()
        );

        String rationale = String.format(
            "Reduced daily effort by 10%% (from %.1f to %.1f XP/day) while improving consistency " +
            "to %.1f%% for better balance.",
            baseInput.getAverageDailyEffort(), newEffort, newConsistency);

        String benefit = "Prevents escalation to high burnout risk while maintaining steady growth " +
                        "through improved consistency.";

        return new GeneratedScenario(
            "Effort Balance",
            List.of(recommendation),
            modified,
            rationale,
            benefit
        );
    }

    /**
     * Generates an optimization scenario.
     */
    private GeneratedScenario generateOptimizationScenario(SimulationInput baseInput, Recommendation recommendation) {
        // Balanced improvements
        double newConsistency = Math.min(100.0, baseInput.getHabitsConsistencyScore() + 10.0);
        int newActiveDays = Math.min(31, baseInput.getActiveDaysLastMonth() + 3);

        SimulationInput modified = new SimulationInput(
            baseInput.getCurrentStats(),
            newConsistency,
            baseInput.getAverageDailyEffort(),
            baseInput.getDifficultyDistribution(),
            baseInput.getActiveGoals(),
            baseInput.getBurnoutWarning(),
            newActiveDays,
            baseInput.getAverageStreakLength() + 2.0,
            baseInput.getYearsToSimulate()
        );

        String rationale = String.format(
            "Optimized consistency (%.1f%%) and active days (%d) for better habit-goal balance.",
            newConsistency, newActiveDays);

        String benefit = "Potential 20-30%% increase in skill growth through better optimization " +
                        "of existing routines.";

        return new GeneratedScenario(
            "Strategy Optimization",
            List.of(recommendation),
            modified,
            rationale,
            benefit
        );
    }

    /**
     * Generates a combined scenario from multiple recommendations.
     */
    private GeneratedScenario generateCombinedScenario(SimulationInput baseInput, List<Recommendation> recommendations) {
        // Apply moderate versions of both recommendations
        SimulationInput modified = baseInput;
        
        for (Recommendation rec : recommendations) {
            if (rec.type() == RecommendationType.IMPROVE_CONSISTENCY) {
                double newConsistency = Math.min(100.0, modified.getHabitsConsistencyScore() + 10.0);
                int newActiveDays = Math.min(31, modified.getActiveDaysLastMonth() + 3);
                double newStreak = modified.getAverageStreakLength() + 3.0;
                
                modified = new SimulationInput(
                    modified.getCurrentStats(),
                    newConsistency,
                    modified.getAverageDailyEffort(),
                    modified.getDifficultyDistribution(),
                    modified.getActiveGoals(),
                    modified.getBurnoutWarning(),
                    newActiveDays,
                    newStreak,
                    modified.getYearsToSimulate()
                );
            } else if (rec.type() == RecommendationType.ADD_GOAL_FOCUS) {
                double newEffort = modified.getAverageDailyEffort() * 1.05;
                double newConsistency = Math.min(100.0, modified.getHabitsConsistencyScore() + 5.0);
                
                modified = new SimulationInput(
                    modified.getCurrentStats(),
                    newConsistency,
                    newEffort,
                    modified.getDifficultyDistribution(),
                    modified.getActiveGoals(),
                    modified.getBurnoutWarning(),
                    modified.getActiveDaysLastMonth(),
                    modified.getAverageStreakLength(),
                    modified.getYearsToSimulate()
                );
            }
        }

        String rationale = "Combined improvements from multiple recommendations for balanced, sustainable progress.";
        String benefit = "Synergistic improvements from addressing multiple areas simultaneously.";

        return new GeneratedScenario(
            "Combined Improvements",
            recommendations,
            modified,
            rationale,
            benefit
        );
    }

    /**
     * Reduces difficulty distribution (shifts high difficulty to medium).
     */
    private Map<Difficulty, Integer> reduceDifficultyDistribution(Map<Difficulty, Integer> original) {
        Map<Difficulty, Integer> adjusted = new HashMap<>(original);
        
        // Reduce high difficulty (4-5) habits
        int highCount = adjusted.getOrDefault(Difficulty.FOUR, 0) + adjusted.getOrDefault(Difficulty.FIVE, 0);
        if (highCount > 0) {
            adjusted.put(Difficulty.FOUR, Math.max(0, adjusted.getOrDefault(Difficulty.FOUR, 0) - 1));
            adjusted.put(Difficulty.FIVE, Math.max(0, adjusted.getOrDefault(Difficulty.FIVE, 0) - 1));
            adjusted.put(Difficulty.THREE, adjusted.getOrDefault(Difficulty.THREE, 0) + 1);
        }
        
        // Ensure all difficulties are present
        for (Difficulty d : Difficulty.values()) {
            adjusted.putIfAbsent(d, 0);
        }
        
        return Map.copyOf(adjusted);
    }

    /**
     * Adjusts difficulty distribution (reduces high, adds medium).
     */
    private Map<Difficulty, Integer> adjustDifficultyDistribution(Map<Difficulty, Integer> original) {
        return reduceDifficultyDistribution(original); // Same logic
    }

    /**
     * Adds medium difficulty habits to the distribution.
     */
    private Map<Difficulty, Integer> addMediumDifficultyHabits(Map<Difficulty, Integer> original) {
        Map<Difficulty, Integer> adjusted = new HashMap<>(original);
        adjusted.put(Difficulty.THREE, adjusted.getOrDefault(Difficulty.THREE, 0) + 1);
        
        // Ensure all difficulties are present
        for (Difficulty d : Difficulty.values()) {
            adjusted.putIfAbsent(d, 0);
        }
        
        return Map.copyOf(adjusted);
    }
}

