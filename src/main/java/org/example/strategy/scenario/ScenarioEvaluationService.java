package org.example.strategy.scenario;

import org.example.simulation.engine.FutureSimulationService;
import org.example.simulation.model.SimulationInput;
import org.example.simulation.model.SimulationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates generated scenarios by running simulations and comparing results.
 */
public class ScenarioEvaluationService {
    private final FutureSimulationService simulationService;

    public ScenarioEvaluationService() {
        this(new FutureSimulationService());
    }

    public ScenarioEvaluationService(FutureSimulationService simulationService) {
        if (simulationService == null) {
            throw new IllegalArgumentException("Simulation service cannot be null");
        }
        this.simulationService = simulationService;
    }

    /**
     * Evaluates scenarios by simulating them and comparing to the base result.
     *
     * @param baseInput the base simulation input
     * @param baseResult the base simulation result
     * @param scenarios the generated scenarios to evaluate
     * @return list of impact summaries for each scenario
     */
    public List<ScenarioImpactSummary> evaluateScenarios(SimulationInput baseInput,
                                                         SimulationResult baseResult,
                                                         List<GeneratedScenario> scenarios) {
        if (baseInput == null) {
            throw new IllegalArgumentException("Base input cannot be null");
        }
        if (baseResult == null) {
            throw new IllegalArgumentException("Base result cannot be null");
        }
        if (scenarios == null || scenarios.isEmpty()) {
            throw new IllegalArgumentException("Scenarios cannot be null or empty");
        }

        List<ScenarioImpactSummary> summaries = new ArrayList<>();

        for (GeneratedScenario scenario : scenarios) {
            // Simulate the improved scenario
            SimulationResult improvedResult = simulationService.simulate(scenario.modifiedInput());

            // Calculate improvements
            double xpImprovement = calculateXpImprovement(baseResult, improvedResult);
            double skillGrowthImprovement = calculateSkillGrowthImprovement(baseResult, improvedResult);
            String impactDescription = generateImpactDescription(baseResult, improvedResult, xpImprovement, skillGrowthImprovement);

            summaries.add(new ScenarioImpactSummary(
                scenario,
                baseResult,
                improvedResult,
                xpImprovement,
                skillGrowthImprovement,
                impactDescription
            ));
        }

        return summaries;
    }

    /**
     * Calculates XP improvement percentage.
     */
    private double calculateXpImprovement(SimulationResult base, SimulationResult improved) {
        if (base.getYearlyProjections().isEmpty() || improved.getYearlyProjections().isEmpty()) {
            return 0.0;
        }

        int baseFinalXp = base.getYearlyProjections().get(base.getYearlyProjections().size() - 1).getProjectedXp();
        int improvedFinalXp = improved.getYearlyProjections().get(improved.getYearlyProjections().size() - 1).getProjectedXp();

        if (baseFinalXp == 0) {
            return improvedFinalXp > 0 ? 100.0 : 0.0;
        }

        return ((improvedFinalXp - baseFinalXp) / (double) baseFinalXp) * 100.0;
    }

    /**
     * Calculates skill growth improvement percentage.
     */
    private double calculateSkillGrowthImprovement(SimulationResult base, SimulationResult improved) {
        double baseGrowth = base.getAverageSkillGrowthIndex();
        double improvedGrowth = improved.getAverageSkillGrowthIndex();

        if (baseGrowth == 0.0) {
            return improvedGrowth > 0 ? 100.0 : 0.0;
        }

        return ((improvedGrowth - baseGrowth) / baseGrowth) * 100.0;
    }

    /**
     * Generates a human-readable impact description.
     */
    private String generateImpactDescription(SimulationResult base, SimulationResult improved,
                                            double xpImprovement, double skillGrowthImprovement) {
        StringBuilder description = new StringBuilder();

        // XP improvement
        if (base.getYearlyProjections().isEmpty() || improved.getYearlyProjections().isEmpty()) {
            return "Unable to calculate impact - missing projection data.";
        }

        int baseFinalXp = base.getYearlyProjections().get(base.getYearlyProjections().size() - 1).getProjectedXp();
        int improvedFinalXp = improved.getYearlyProjections().get(improved.getYearlyProjections().size() - 1).getProjectedXp();
        int baseFinalLevel = base.getYearlyProjections().get(base.getYearlyProjections().size() - 1).getProjectedLevel();
        int improvedFinalLevel = improved.getYearlyProjections().get(improved.getYearlyProjections().size() - 1).getProjectedLevel();

        description.append(String.format("Final XP: %d → %d (%.1f%% improvement). ", 
            baseFinalXp, improvedFinalXp, xpImprovement));

        if (improvedFinalLevel > baseFinalLevel) {
            description.append(String.format("Level progression: %d → %d. ", baseFinalLevel, improvedFinalLevel));
        }

        // Skill growth
        description.append(String.format("Skill growth index: %.1f → %.1f (%.1f%% improvement). ",
            base.getAverageSkillGrowthIndex(), improved.getAverageSkillGrowthIndex(), skillGrowthImprovement));

        // Burnout risk
        if (improved.getBurnoutRisk() != base.getBurnoutRisk()) {
            description.append(String.format("Burnout risk: %s → %s. ",
                base.getBurnoutRisk(), improved.getBurnoutRisk()));
        }

        // Income projection
        int baseExpectedIncome = base.getIncomeRange().getExpectedEstimate();
        int improvedExpectedIncome = improved.getIncomeRange().getExpectedEstimate();
        if (improvedExpectedIncome != baseExpectedIncome) {
            double incomeImprovement = ((improvedExpectedIncome - baseExpectedIncome) / (double) baseExpectedIncome) * 100.0;
            description.append(String.format("Expected income: $%d → $%d (%.1f%% improvement).",
                baseExpectedIncome, improvedExpectedIncome, incomeImprovement));
        }

        return description.toString();
    }
}

