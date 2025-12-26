package org.example.strategy.scenario;

import org.example.*;
import org.example.simulation.engine.FutureSimulationService;
import org.example.simulation.model.*;
import org.example.strategy.Recommendation;
import org.example.strategy.RecommendationType;

import java.util.*;

/**
 * Service that generates improved simulation scenarios based on recommendations.
 * Converts strategic advice into concrete, testable scenarios.
 */
public class ScenarioGeneratorService {
    private final FutureSimulationService simulationService;

    public ScenarioGeneratorService() {
        this(new FutureSimulationService());
    }

    public ScenarioGeneratorService(FutureSimulationService simulationService) {
        if (simulationService == null) {
            throw new IllegalArgumentException("FutureSimulationService cannot be null");
        }
        this.simulationService = simulationService;
    }

    /**
     * Generates improved scenarios based on recommendations and evaluates their impact.
     *
     * @param baseInput the base simulation input
     * @param recommendations the recommendations to apply
     * @return a list of scenario impact summaries (one per generated scenario)
     */
    public List<ScenarioImpactSummary> generateAndEvaluateScenarios(
            SimulationInput baseInput,
            List<Recommendation> recommendations) {
        if (baseInput == null) {
            throw new IllegalArgumentException("Base input cannot be null");
        }
        if (recommendations == null || recommendations.isEmpty()) {
            throw new IllegalArgumentException("Recommendations cannot be null or empty");
        }

        // Generate scenarios from recommendations
        List<GeneratedScenario> scenarios = generateScenarios(baseInput, recommendations);

        // Run base simulation
        SimulationResult baseResult = simulationService.simulate(baseInput);

        // Evaluate each scenario
        List<ScenarioImpactSummary> summaries = new ArrayList<>();
        for (GeneratedScenario scenario : scenarios) {
            SimulationResult improvedResult = simulationService.simulate(scenario.getModifiedInput());
            ScenarioImpactSummary summary = createImpactSummary(scenario, baseResult, improvedResult);
            summaries.add(summary);
        }

        // Sort by XP improvement (highest first)
        summaries.sort(Comparator.comparingDouble(ScenarioImpactSummary::getXpImprovement).reversed());

        return summaries;
    }

    /**
     * Generates scenarios based on recommendations.
     * Groups recommendations by compatibility and creates scenarios.
     */
    private List<GeneratedScenario> generateScenarios(
            SimulationInput baseInput,
            List<Recommendation> recommendations) {
        List<GeneratedScenario> scenarios = new ArrayList<>();

        // Group recommendations by priority and compatibility
        List<Recommendation> highPriority = recommendations.stream()
            .filter(r -> r.priorityScore() >= 70.0)
            .sorted(Comparator.comparingDouble(Recommendation::priorityScore).reversed())
            .toList();

        // Generate scenarios for top recommendations (limit to 3 to avoid explosion)
        int maxScenarios = Math.min(3, highPriority.size());
        for (int i = 0; i < maxScenarios; i++) {
            Recommendation rec = highPriority.get(i);
            GeneratedScenario scenario = generateScenarioForRecommendation(baseInput, rec);
            if (scenario != null) {
                scenarios.add(scenario);
            }
        }

        return scenarios;
    }

    /**
     * Generates a single scenario based on a recommendation.
     */
    private GeneratedScenario generateScenarioForRecommendation(
            SimulationInput baseInput,
            Recommendation recommendation) {
        RecommendationType type = recommendation.type();
        Map<String, String> changes = new HashMap<>();
        SimulationInput modifiedInput = baseInput;

        switch (type) {
            case REDUCE_BURNOUT_RISK:
                modifiedInput = applyBurnoutReduction(baseInput, changes);
                break;
            case IMPROVE_CONSISTENCY:
                modifiedInput = applyConsistencyImprovement(baseInput, changes);
                break;
            case ADD_GOAL_FOCUS:
                modifiedInput = applyGoalFocus(baseInput, changes);
                break;
            case ADJUST_HABIT_DIFFICULTY:
                modifiedInput = applyDifficultyAdjustment(baseInput, changes);
                break;
            case ADD_HABITS_FOR_GROWTH:
                modifiedInput = applyHabitAddition(baseInput, changes);
                break;
            case BALANCE_EFFORT:
                modifiedInput = applyEffortBalance(baseInput, changes);
                break;
            case OPTIMIZE_STRATEGY:
                modifiedInput = applyStrategyOptimization(baseInput, changes);
                break;
            default:
                return null; // Unknown recommendation type
        }

        String scenarioName = generateScenarioName(type);
        String rationale = generateRationale(recommendation, changes);

        return new GeneratedScenario(
            scenarioName,
            List.of(recommendation),
            modifiedInput,
            rationale,
            changes
        );
    }

    /**
     * Applies burnout reduction changes.
     */
    private SimulationInput applyBurnoutReduction(SimulationInput input, Map<String, String> changes) {
        // Reduce average daily effort by 15-20%
        double newEffort = input.getAverageDailyEffort() * 0.82;
        changes.put("averageDailyEffort", String.format("Reduced from %.1f to %.1f (18%% reduction)", 
            input.getAverageDailyEffort(), newEffort));

        // Reduce active days slightly
        int newActiveDays = Math.max(18, (int) (input.getActiveDaysLastMonth() * 0.9));
        changes.put("activeDaysLastMonth", String.format("Reduced from %d to %d days", 
            input.getActiveDaysLastMonth(), newActiveDays));

        // Shift difficulty distribution to lower difficulties
        Map<Difficulty, Integer> newDistribution = new HashMap<>(input.getDifficultyDistribution());
        // Ensure all difficulty levels are present in the map
        for (Difficulty d : Difficulty.values()) {
            newDistribution.putIfAbsent(d, 0);
        }
        
        int highDifficultyCount = newDistribution.getOrDefault(Difficulty.FOUR, 0) + 
                                 newDistribution.getOrDefault(Difficulty.FIVE, 0);
        if (highDifficultyCount > 0) {
            // Reduce one high-difficulty habit, add one medium
            if (newDistribution.getOrDefault(Difficulty.FIVE, 0) > 0) {
                newDistribution.put(Difficulty.FIVE, newDistribution.get(Difficulty.FIVE) - 1);
                newDistribution.put(Difficulty.THREE, newDistribution.getOrDefault(Difficulty.THREE, 0) + 1);
                changes.put("difficultyDistribution", "Reduced one difficulty-5 habit, added one difficulty-3 habit");
            } else if (newDistribution.getOrDefault(Difficulty.FOUR, 0) > 0) {
                newDistribution.put(Difficulty.FOUR, newDistribution.get(Difficulty.FOUR) - 1);
                newDistribution.put(Difficulty.THREE, newDistribution.getOrDefault(Difficulty.THREE, 0) + 1);
                changes.put("difficultyDistribution", "Reduced one difficulty-4 habit, added one difficulty-3 habit");
            }
        }

        // Improve burnout warning (assume improvement after changes)
        BurnoutWarning improvedWarning = new BurnoutWarning(false, List.of(), 0.0);
        changes.put("burnoutWarning", "Assumed improvement after reducing effort");

        return new SimulationInput(
            input.getCurrentStats(),
            input.getHabitsConsistencyScore(),
            newEffort,
            newDistribution,
            input.getActiveGoals(),
            improvedWarning,
            newActiveDays,
            input.getAverageStreakLength(),
            input.getYearsToSimulate()
        );
    }

    /**
     * Applies consistency improvement changes.
     */
    private SimulationInput applyConsistencyImprovement(SimulationInput input, Map<String, String> changes) {
        // Improve consistency score by 15-20%
        double newConsistency = Math.min(100.0, input.getHabitsConsistencyScore() * 1.18);
        changes.put("habitsConsistencyScore", String.format("Improved from %.1f%% to %.1f%%", 
            input.getHabitsConsistencyScore(), newConsistency));

        // Increase active days
        int newActiveDays = Math.min(30, input.getActiveDaysLastMonth() + 3);
        changes.put("activeDaysLastMonth", String.format("Increased from %d to %d days", 
            input.getActiveDaysLastMonth(), newActiveDays));

        // Improve streak length
        double newStreakLength = input.getAverageStreakLength() * 1.25;
        changes.put("averageStreakLength", String.format("Improved from %.1f to %.1f days", 
            input.getAverageStreakLength(), newStreakLength));

        return new SimulationInput(
            input.getCurrentStats(),
            newConsistency,
            input.getAverageDailyEffort(),
            input.getDifficultyDistribution(),
            input.getActiveGoals(),
            input.getBurnoutWarning(),
            newActiveDays,
            newStreakLength,
            input.getYearsToSimulate()
        );
    }

    /**
     * Applies goal focus changes.
     */
    private SimulationInput applyGoalFocus(SimulationInput input, Map<String, String> changes) {
        // Add a virtual goal (we simulate this by increasing active goals count)
        // Since we can't create actual Goal objects without context, we simulate impact
        // by improving consistency and effort slightly (goals drive behavior)
        double newConsistency = Math.min(100.0, input.getHabitsConsistencyScore() * 1.12);
        double newEffort = input.getAverageDailyEffort() * 1.10;
        
        changes.put("activeGoals", "Added 1-2 new goals (simulated by improved engagement)");
        changes.put("habitsConsistencyScore", String.format("Improved from %.1f%% to %.1f%% (goal-driven)", 
            input.getHabitsConsistencyScore(), newConsistency));
        changes.put("averageDailyEffort", String.format("Increased from %.1f to %.1f (goal-driven)", 
            input.getAverageDailyEffort(), newEffort));

        // Keep existing goals, just simulate impact
        return new SimulationInput(
            input.getCurrentStats(),
            newConsistency,
            newEffort,
            input.getDifficultyDistribution(),
            input.getActiveGoals(),
            input.getBurnoutWarning(),
            input.getActiveDaysLastMonth(),
            input.getAverageStreakLength(),
            input.getYearsToSimulate()
        );
    }

    /**
     * Applies difficulty adjustment changes.
     */
    private SimulationInput applyDifficultyAdjustment(SimulationInput input, Map<String, String> changes) {
        Map<Difficulty, Integer> newDistribution = new HashMap<>(input.getDifficultyDistribution());
        // Ensure all difficulty levels are present in the map
        for (Difficulty d : Difficulty.values()) {
            newDistribution.putIfAbsent(d, 0);
        }
        
        // Reduce one high-difficulty habit, add one medium
        if (newDistribution.getOrDefault(Difficulty.FOUR, 0) > 0 || 
            newDistribution.getOrDefault(Difficulty.FIVE, 0) > 0) {
            if (newDistribution.getOrDefault(Difficulty.FIVE, 0) > 0) {
                newDistribution.put(Difficulty.FIVE, newDistribution.get(Difficulty.FIVE) - 1);
                newDistribution.put(Difficulty.THREE, newDistribution.getOrDefault(Difficulty.THREE, 0) + 1);
                changes.put("difficultyDistribution", "Reduced one difficulty-5, added one difficulty-3");
            } else {
                newDistribution.put(Difficulty.FOUR, newDistribution.get(Difficulty.FOUR) - 1);
                newDistribution.put(Difficulty.THREE, newDistribution.getOrDefault(Difficulty.THREE, 0) + 1);
                changes.put("difficultyDistribution", "Reduced one difficulty-4, added one difficulty-3");
            }
        }

        return new SimulationInput(
            input.getCurrentStats(),
            input.getHabitsConsistencyScore(),
            input.getAverageDailyEffort(),
            newDistribution,
            input.getActiveGoals(),
            input.getBurnoutWarning(),
            input.getActiveDaysLastMonth(),
            input.getAverageStreakLength(),
            input.getYearsToSimulate()
        );
    }

    /**
     * Applies habit addition changes.
     */
    private SimulationInput applyHabitAddition(SimulationInput input, Map<String, String> changes) {
        Map<Difficulty, Integer> newDistribution = new HashMap<>(input.getDifficultyDistribution());
        // Ensure all difficulty levels are present in the map
        for (Difficulty d : Difficulty.values()) {
            newDistribution.putIfAbsent(d, 0);
        }
        
        // Add one medium-difficulty habit
        newDistribution.put(Difficulty.THREE, newDistribution.getOrDefault(Difficulty.THREE, 0) + 1);
        changes.put("difficultyDistribution", "Added one difficulty-3 habit");

        // Slight increase in daily effort (new habit = more XP)
        double newEffort = input.getAverageDailyEffort() * 1.15;
        changes.put("averageDailyEffort", String.format("Increased from %.1f to %.1f (new habit)", 
            input.getAverageDailyEffort(), newEffort));

        return new SimulationInput(
            input.getCurrentStats(),
            input.getHabitsConsistencyScore(),
            newEffort,
            newDistribution,
            input.getActiveGoals(),
            input.getBurnoutWarning(),
            input.getActiveDaysLastMonth(),
            input.getAverageStreakLength(),
            input.getYearsToSimulate()
        );
    }

    /**
     * Applies effort balance changes.
     */
    private SimulationInput applyEffortBalance(SimulationInput input, Map<String, String> changes) {
        // Slightly reduce effort and increase consistency (balance)
        double newEffort = input.getAverageDailyEffort() * 0.92;
        double newConsistency = Math.min(100.0, input.getHabitsConsistencyScore() * 1.10);
        
        changes.put("averageDailyEffort", String.format("Reduced from %.1f to %.1f (balanced)", 
            input.getAverageDailyEffort(), newEffort));
        changes.put("habitsConsistencyScore", String.format("Improved from %.1f%% to %.1f%% (balanced)", 
            input.getHabitsConsistencyScore(), newConsistency));

        // Reduce active days slightly
        int newActiveDays = Math.max(20, input.getActiveDaysLastMonth() - 2);
        changes.put("activeDaysLastMonth", String.format("Adjusted from %d to %d days (balanced)", 
            input.getActiveDaysLastMonth(), newActiveDays));

        return new SimulationInput(
            input.getCurrentStats(),
            newConsistency,
            newEffort,
            input.getDifficultyDistribution(),
            input.getActiveGoals(),
            input.getBurnoutWarning(),
            newActiveDays,
            input.getAverageStreakLength(),
            input.getYearsToSimulate()
        );
    }

    /**
     * Applies strategy optimization changes.
     */
    private SimulationInput applyStrategyOptimization(SimulationInput input, Map<String, String> changes) {
        // Combine consistency improvement with slight effort adjustment
        double newConsistency = Math.min(100.0, input.getHabitsConsistencyScore() * 1.10);
        double newEffort = input.getAverageDailyEffort() * 1.05;
        
        changes.put("habitsConsistencyScore", String.format("Improved from %.1f%% to %.1f%%", 
            input.getHabitsConsistencyScore(), newConsistency));
        changes.put("averageDailyEffort", String.format("Optimized from %.1f to %.1f", 
            input.getAverageDailyEffort(), newEffort));

        return new SimulationInput(
            input.getCurrentStats(),
            newConsistency,
            newEffort,
            input.getDifficultyDistribution(),
            input.getActiveGoals(),
            input.getBurnoutWarning(),
            input.getActiveDaysLastMonth(),
            input.getAverageStreakLength(),
            input.getYearsToSimulate()
        );
    }

    /**
     * Generates a scenario name based on recommendation type.
     */
    private String generateScenarioName(RecommendationType type) {
        return switch (type) {
            case REDUCE_BURNOUT_RISK -> "Reduced Burnout Risk Scenario";
            case IMPROVE_CONSISTENCY -> "Improved Consistency Scenario";
            case ADD_GOAL_FOCUS -> "Goal-Focused Scenario";
            case ADJUST_HABIT_DIFFICULTY -> "Balanced Difficulty Scenario";
            case ADD_HABITS_FOR_GROWTH -> "Growth-Oriented Scenario";
            case BALANCE_EFFORT -> "Balanced Effort Scenario";
            case OPTIMIZE_STRATEGY -> "Optimized Strategy Scenario";
        };
    }

    /**
     * Generates rationale for the scenario.
     */
    private String generateRationale(Recommendation recommendation, Map<String, String> changes) {
        StringBuilder rationale = new StringBuilder();
        rationale.append("This scenario applies the recommendation: ").append(recommendation.description()).append(". ");
        rationale.append(recommendation.reason()).append(" ");
        
        if (!changes.isEmpty()) {
            rationale.append("The following changes were made: ");
            changes.values().forEach(change -> rationale.append(change).append("; "));
        }
        
        rationale.append("Expected benefit: ").append(recommendation.expectedBenefit()).append(".");
        
        return rationale.toString();
    }

    /**
     * Creates an impact summary comparing base and improved results.
     */
    private ScenarioImpactSummary createImpactSummary(
            GeneratedScenario scenario,
            SimulationResult baseResult,
            SimulationResult improvedResult) {
        
        // Calculate XP improvement
        List<YearlyProjection> baseProjections = baseResult.getYearlyProjections();
        List<YearlyProjection> improvedProjections = improvedResult.getYearlyProjections();
        
        int baseFinalXp = baseProjections.get(baseProjections.size() - 1).getProjectedXp();
        int improvedFinalXp = improvedProjections.get(improvedProjections.size() - 1).getProjectedXp();
        double xpImprovement = baseFinalXp > 0 ? 
            ((double) (improvedFinalXp - baseFinalXp) / baseFinalXp) * 100.0 : 0.0;
        
        // Calculate skill growth improvement
        double skillGrowthImprovement = improvedResult.getAverageSkillGrowthIndex() - 
                                       baseResult.getAverageSkillGrowthIndex();
        
        // Generate improvement description
        String description = generateImprovementDescription(
            baseResult, improvedResult, xpImprovement, skillGrowthImprovement);
        
        return new ScenarioImpactSummary(
            scenario,
            baseResult,
            improvedResult,
            xpImprovement,
            skillGrowthImprovement,
            description
        );
    }

    /**
     * Generates a human-readable improvement description.
     */
    private String generateImprovementDescription(
            SimulationResult baseResult,
            SimulationResult improvedResult,
            double xpImprovement,
            double skillGrowthImprovement) {
        
        StringBuilder desc = new StringBuilder();
        
        // XP improvement
        if (xpImprovement > 5.0) {
            desc.append(String.format("Significant XP improvement (+%.1f%%). ", xpImprovement));
        } else if (xpImprovement > 0) {
            desc.append(String.format("Modest XP improvement (+%.1f%%). ", xpImprovement));
        } else if (xpImprovement < -5.0) {
            desc.append(String.format("XP reduced (%.1f%%) - may be due to sustainability focus. ", xpImprovement));
        }
        
        // Skill growth improvement
        if (skillGrowthImprovement > 5.0) {
            desc.append(String.format("Skill growth index improved by +%.1f points. ", skillGrowthImprovement));
        } else if (skillGrowthImprovement > 0) {
            desc.append(String.format("Skill growth improved slightly (+%.1f points). ", skillGrowthImprovement));
        }
        
        // Burnout risk change
        if (baseResult.getBurnoutRisk() == BurnoutRisk.HIGH && 
            improvedResult.getBurnoutRisk() != BurnoutRisk.HIGH) {
            desc.append("Burnout risk reduced significantly. ");
        } else if (baseResult.getBurnoutRisk() == BurnoutRisk.MEDIUM && 
                   improvedResult.getBurnoutRisk() == BurnoutRisk.LOW) {
            desc.append("Burnout risk improved. ");
        }
        
        // Level improvement
        int baseFinalLevel = baseResult.getYearlyProjections()
            .get(baseResult.getYearlyProjections().size() - 1).getProjectedLevel();
        int improvedFinalLevel = improvedResult.getYearlyProjections()
            .get(improvedResult.getYearlyProjections().size() - 1).getProjectedLevel();
        if (improvedFinalLevel > baseFinalLevel) {
            desc.append(String.format("Projected level increases from %d to %d. ", 
                baseFinalLevel, improvedFinalLevel));
        }
        
        return desc.toString().trim();
    }
}

