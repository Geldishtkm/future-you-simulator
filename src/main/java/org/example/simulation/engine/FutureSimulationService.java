package org.example.simulation.engine;

import org.example.*;
import org.example.simulation.explanation.SimulationExplanationGenerator;
import org.example.simulation.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Core engine for simulating a user's future trajectory.
 * Uses probabilistic modeling with weighted probabilities, diminishing returns, and consistency penalties.
 */
public class FutureSimulationService {
    private static final double BASE_DAYS_PER_YEAR = 365.0;
    private static final double CONSISTENCY_MULTIPLIER_MIN = 0.5; // Minimum multiplier for low consistency
    private static final double CONSISTENCY_MULTIPLIER_MAX = 1.2; // Maximum multiplier for high consistency
    private static final double DIMINISHING_RETURNS_FACTOR = 0.85; // XP growth slows each year
    private static final double BURNOUT_REDUCTION_FACTOR = 0.6; // XP reduction when burnout risk is high
    
    private final LevelCalculator levelCalculator;
    private final SimulationExplanationGenerator explanationGenerator;

    public FutureSimulationService() {
        this(new LevelCalculator(), new SimulationExplanationGenerator());
    }

    public FutureSimulationService(LevelCalculator levelCalculator,
                                   SimulationExplanationGenerator explanationGenerator) {
        if (levelCalculator == null) {
            throw new IllegalArgumentException("LevelCalculator cannot be null");
        }
        if (explanationGenerator == null) {
            throw new IllegalArgumentException("SimulationExplanationGenerator cannot be null");
        }
        this.levelCalculator = levelCalculator;
        this.explanationGenerator = explanationGenerator;
    }

    /**
     * Runs a future simulation based on the provided input.
     *
     * @param input the simulation input data
     * @return the simulation result with projections and predictions
     */
    public SimulationResult simulate(SimulationInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Simulation input cannot be null");
        }

        // Calculate base yearly XP gain
        double baseYearlyXpGain = calculateBaseYearlyXpGain(input);
        
        // Generate yearly projections
        List<YearlyProjection> projections = generateYearlyProjections(input, baseYearlyXpGain);
        
        // Calculate average skill growth index
        double avgSkillGrowth = calculateAverageSkillGrowth(projections, input);
        
        // Determine burnout risk
        BurnoutRisk burnoutRisk = calculateBurnoutRisk(input, projections);
        
        // Project income range
        IncomeRange incomeRange = projectIncomeRange(projections, input);
        
        // Calculate emigration probability
        double emigrationProbability = calculateEmigrationProbability(projections, incomeRange, input);
        
        // Generate explanation
        String explanation = explanationGenerator.generateExplanation(
            new SimulationResult(projections, avgSkillGrowth, burnoutRisk, incomeRange,
                              emigrationProbability, ""), // Temporary, will be replaced
            input.getHabitsConsistencyScore()
        );

        return new SimulationResult(projections, avgSkillGrowth, burnoutRisk, incomeRange,
                                  emigrationProbability, explanation);
    }

    /**
     * Calculates the base yearly XP gain based on input metrics.
     */
    private double calculateBaseYearlyXpGain(SimulationInput input) {
        // Start with average daily effort
        double baseDailyXp = input.getAverageDailyEffort();
        
        // Apply consistency multiplier
        double consistencyMultiplier = calculateConsistencyMultiplier(input.getHabitsConsistencyScore());
        baseDailyXp *= consistencyMultiplier;
        
        // Calculate active days per year (based on last month's activity)
        double activeDaysRatio = input.getActiveDaysLastMonth() / 30.0;
        double activeDaysPerYear = BASE_DAYS_PER_YEAR * Math.min(activeDaysRatio, 1.0) * 0.85; // 85% of projected
        
        // Apply difficulty distribution bonus (more high-difficulty habits = bonus)
        double difficultyBonus = calculateDifficultyBonus(input.getDifficultyDistribution());
        baseDailyXp *= (1.0 + difficultyBonus);
        
        // Apply streak bonus
        double streakBonus = Math.min(input.getAverageStreakLength() / 30.0, 0.3); // Max 30% bonus
        baseDailyXp *= (1.0 + streakBonus);
        
        // Apply goal engagement bonus
        double goalBonus = calculateGoalEngagementBonus(input.getActiveGoals());
        baseDailyXp *= (1.0 + goalBonus);
        
        return baseDailyXp * activeDaysPerYear;
    }

    /**
     * Maps consistency score (0-100) to a multiplier (0.5-1.2).
     */
    private double calculateConsistencyMultiplier(double consistencyScore) {
        double normalized = consistencyScore / 100.0; // 0.0 to 1.0
        return CONSISTENCY_MULTIPLIER_MIN + 
               (CONSISTENCY_MULTIPLIER_MAX - CONSISTENCY_MULTIPLIER_MIN) * normalized;
    }

    /**
     * Calculates bonus based on difficulty distribution.
     * More high-difficulty habits = higher bonus (max 15%).
     */
    private double calculateDifficultyBonus(Map<Difficulty, Integer> distribution) {
        if (distribution.isEmpty()) {
            return 0.0;
        }
        
        int totalHabits = distribution.values().stream().mapToInt(Integer::intValue).sum();
        if (totalHabits == 0) {
            return 0.0;
        }
        
        // Weighted average difficulty
        double weightedDifficulty = 0.0;
        for (Map.Entry<Difficulty, Integer> entry : distribution.entrySet()) {
            weightedDifficulty += entry.getKey().getValue() * entry.getValue();
        }
        weightedDifficulty /= totalHabits;
        
        // Bonus: 0% for difficulty 1-2, up to 15% for difficulty 5
        return Math.max(0, (weightedDifficulty - 2) / 3.0) * 0.15;
    }

    /**
     * Calculates bonus based on active goals.
     */
    private double calculateGoalEngagementBonus(List<Goal> activeGoals) {
        if (activeGoals.isEmpty()) {
            return 0.0;
        }
        
        // Average importance of goals (1-5 scale)
        double avgImportance = activeGoals.stream()
            .mapToInt(Goal::getImportance)
            .average()
            .orElse(0.0);
        
        // Bonus based on number and importance of goals (max 10%)
        double countBonus = Math.min(activeGoals.size() / 10.0, 0.05); // 5% max from count
        double importanceBonus = (avgImportance - 1) / 4.0 * 0.05; // 5% max from importance
        
        return countBonus + importanceBonus;
    }

    /**
     * Generates yearly projections with diminishing returns and burnout effects.
     */
    private List<YearlyProjection> generateYearlyProjections(SimulationInput input, double baseYearlyXpGain) {
        List<YearlyProjection> projections = new ArrayList<>();
        int currentXp = input.getCurrentStats().getTotalXp();
        int currentLevel = input.getCurrentStats().getLevel();
        
        double yearlyXpGain = baseYearlyXpGain;
        double diminishingReturns = 1.0;
        
        // Check initial burnout warning
        boolean hasBurnoutWarning = input.getBurnoutWarning().isWarningActive();
        
        for (int year = 1; year <= input.getYearsToSimulate(); year++) {
            // Apply diminishing returns (growth slows over time)
            yearlyXpGain *= diminishingReturns;
            diminishingReturns *= DIMINISHING_RETURNS_FACTOR;
            
            // Apply burnout penalty if risk increases
            if (hasBurnoutWarning || calculateYearlyBurnoutRisk(input, year) > 0.6) {
                yearlyXpGain *= BURNOUT_REDUCTION_FACTOR;
                hasBurnoutWarning = true; // Burnout persists
            }
            
            // Calculate XP growth rate
            double xpGrowthRate = ((yearlyXpGain / baseYearlyXpGain) - 1.0) * 100.0;
            
            // Apply yearly XP gain
            currentXp += (int) yearlyXpGain;
            currentLevel = levelCalculator.calculateLevel(currentXp);
            
            // Calculate skill growth index for this year
            double skillGrowthIndex = calculateYearlySkillGrowthIndex(
                yearlyXpGain, input.getHabitsConsistencyScore(), input.getActiveGoals().size());
            
            projections.add(new YearlyProjection(year, currentXp, currentLevel,
                                                skillGrowthIndex, xpGrowthRate));
        }
        
        return projections;
    }

    /**
     * Calculates burnout risk for a specific year in the simulation.
     */
    private double calculateYearlyBurnoutRisk(SimulationInput input, int year) {
        double baseRisk = 0.0;
        
        // High consistency with high daily effort = burnout risk
        if (input.getHabitsConsistencyScore() > 80 && input.getAverageDailyEffort() > 100) {
            baseRisk += 0.3;
        }
        
        // Many high-difficulty habits = burnout risk
        int totalHighDifficulty = input.getDifficultyDistribution().entrySet().stream()
            .filter(e -> e.getKey().getValue() >= 4)
            .mapToInt(Map.Entry::getValue)
            .sum();
        if (totalHighDifficulty > 3) {
            baseRisk += 0.2;
        }
        
        // Existing burnout warning increases risk
        if (input.getBurnoutWarning().isWarningActive()) {
            baseRisk += 0.3;
        }
        
        // Risk increases over time if patterns continue
        baseRisk += (year - 1) * 0.1;
        
        return Math.min(baseRisk, 1.0);
    }

    /**
     * Calculates skill growth index for a single year.
     */
    private double calculateYearlySkillGrowthIndex(double yearlyXpGain, double consistency, int goalCount) {
        // Base index from XP gain (normalized to 0-100)
        double baseIndex = Math.min(yearlyXpGain / 50.0, 50.0); // Max 50 from XP
        
        // Consistency contribution (0-30)
        double consistencyContribution = (consistency / 100.0) * 30.0;
        
        // Goal engagement contribution (0-20)
        double goalContribution = Math.min(goalCount * 5.0, 20.0);
        
        return baseIndex + consistencyContribution + goalContribution;
    }

    /**
     * Calculates average skill growth index across all years.
     */
    private double calculateAverageSkillGrowth(List<YearlyProjection> projections, SimulationInput input) {
        return projections.stream()
            .mapToDouble(YearlyProjection::getSkillGrowthIndex)
            .average()
            .orElse(0.0);
    }

    /**
     * Determines overall burnout risk level.
     */
    private BurnoutRisk calculateBurnoutRisk(SimulationInput input, List<YearlyProjection> projections) {
        // Check final year burnout risk
        double finalYearRisk = calculateYearlyBurnoutRisk(input, input.getYearsToSimulate());
        
        // Check if XP growth is declining (burnout indicator)
        boolean decliningGrowth = false;
        if (projections.size() >= 2) {
            YearlyProjection last = projections.get(projections.size() - 1);
            YearlyProjection secondLast = projections.get(projections.size() - 2);
            decliningGrowth = last.getXpGrowthRate() < secondLast.getXpGrowthRate() - 10;
        }
        
        // Existing burnout warning
        if (input.getBurnoutWarning().isWarningActive()) {
            finalYearRisk += 0.2;
        }
        
        if (finalYearRisk >= 0.7 || decliningGrowth) {
            return BurnoutRisk.HIGH;
        } else if (finalYearRisk >= 0.4) {
            return BurnoutRisk.MEDIUM;
        } else {
            return BurnoutRisk.LOW;
        }
    }

    /**
     * Projects income range based on level progression and skill growth.
     */
    private IncomeRange projectIncomeRange(List<YearlyProjection> projections, SimulationInput input) {
        YearlyProjection finalYear = projections.get(projections.size() - 1);
        int finalLevel = finalYear.getProjectedLevel();
        double skillGrowth = finalYear.getSkillGrowthIndex();
        
        // Base income scales with level (rough approximation: $30k per level starting from level 1)
        int baseIncome = 30000 * finalLevel;
        
        // Apply skill growth multiplier (0.8 to 1.5)
        double skillMultiplier = 0.8 + (skillGrowth / 100.0) * 0.7;
        int expectedIncome = (int) (baseIncome * skillMultiplier);
        
        // Low estimate: 25th percentile (80% of expected)
        int lowEstimate = (int) (expectedIncome * 0.8);
        
        // High estimate: 75th percentile (130% of expected)
        int highEstimate = (int) (expectedIncome * 1.3);
        
        return new IncomeRange(lowEstimate, expectedIncome, highEstimate);
    }

    /**
     * Calculates emigration probability based on skill growth and income gap.
     */
    private double calculateEmigrationProbability(List<YearlyProjection> projections,
                                                 IncomeRange incomeRange,
                                                 SimulationInput input) {
        YearlyProjection finalYear = projections.get(projections.size() - 1);
        double skillGrowth = finalYear.getSkillGrowthIndex();
        int expectedIncome = incomeRange.getExpectedEstimate();
        
        // Base probability from skill growth (high skill = more opportunities abroad)
        double baseProbability = (skillGrowth / 100.0) * 40.0; // Max 40% from skill
        
        // Income gap factor (higher income = more likely to seek better markets)
        // If expected income > $80k, emigration probability increases
        if (expectedIncome > 80000) {
            double incomeFactor = ((expectedIncome - 80000) / 50000.0) * 30.0; // Up to 30% from income
            baseProbability += Math.min(incomeFactor, 30.0);
        }
        
        // Consistency factor (high consistency = higher mobility)
        double consistencyFactor = (input.getHabitsConsistencyScore() / 100.0) * 20.0; // Up to 20%
        baseProbability += consistencyFactor;
        
        return Math.min(baseProbability, 100.0);
    }
}

