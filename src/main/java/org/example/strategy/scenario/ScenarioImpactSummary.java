package org.example.strategy.scenario;

import org.example.simulation.model.SimulationResult;

/**
 * Summary of the impact of a generated scenario compared to the base scenario.
 */
public class ScenarioImpactSummary {
    private final GeneratedScenario scenario;
    private final SimulationResult baseResult;
    private final SimulationResult improvedResult;
    private final double xpImprovement; // Percentage improvement in final XP
    private final double skillGrowthImprovement; // Improvement in skill growth index
    private final BurnoutRiskChange burnoutRiskChange; // Change in burnout risk
    private final int incomeProjectionDelta; // Change in expected income
    private final double emigrationProbabilityChange; // Change in emigration probability
    private final String improvementDescription; // Human-readable improvement summary

    /**
     * Creates a new ScenarioImpactSummary.
     *
     * @param scenario the generated scenario
     * @param baseResult the base simulation result
     * @param improvedResult the improved simulation result
     * @param xpImprovement percentage improvement in final XP
     * @param skillGrowthImprovement improvement in skill growth index
     * @param burnoutRiskChange change in burnout risk
     * @param incomeProjectionDelta change in expected income
     * @param emigrationProbabilityChange change in emigration probability
     * @param improvementDescription human-readable improvement summary
     */
    public ScenarioImpactSummary(GeneratedScenario scenario,
                                SimulationResult baseResult,
                                SimulationResult improvedResult,
                                double xpImprovement,
                                double skillGrowthImprovement,
                                BurnoutRiskChange burnoutRiskChange,
                                int incomeProjectionDelta,
                                double emigrationProbabilityChange,
                                String improvementDescription) {
        if (scenario == null) {
            throw new IllegalArgumentException("Scenario cannot be null");
        }
        if (baseResult == null) {
            throw new IllegalArgumentException("Base result cannot be null");
        }
        if (improvedResult == null) {
            throw new IllegalArgumentException("Improved result cannot be null");
        }
        if (burnoutRiskChange == null) {
            throw new IllegalArgumentException("Burnout risk change cannot be null");
        }
        if (improvementDescription == null || improvementDescription.isBlank()) {
            throw new IllegalArgumentException("Improvement description cannot be null or blank");
        }

        this.scenario = scenario;
        this.baseResult = baseResult;
        this.improvedResult = improvedResult;
        this.xpImprovement = xpImprovement;
        this.skillGrowthImprovement = skillGrowthImprovement;
        this.burnoutRiskChange = burnoutRiskChange;
        this.incomeProjectionDelta = incomeProjectionDelta;
        this.emigrationProbabilityChange = emigrationProbabilityChange;
        this.improvementDescription = improvementDescription;
    }

    public GeneratedScenario getScenario() {
        return scenario;
    }

    public SimulationResult getBaseResult() {
        return baseResult;
    }

    public SimulationResult getImprovedResult() {
        return improvedResult;
    }

    public double getXpImprovement() {
        return xpImprovement;
    }

    public double getSkillGrowthImprovement() {
        return skillGrowthImprovement;
    }

    public BurnoutRiskChange getBurnoutRiskChange() {
        return burnoutRiskChange;
    }

    public int getIncomeProjectionDelta() {
        return incomeProjectionDelta;
    }

    public double getEmigrationProbabilityChange() {
        return emigrationProbabilityChange;
    }

    public String getImprovementDescription() {
        return improvementDescription;
    }

    /**
     * Represents the change in burnout risk between base and improved scenarios.
     */
    public enum BurnoutRiskChange {
        IMPROVED,      // Risk reduced (e.g., HIGH -> MEDIUM, MEDIUM -> LOW)
        WORSENED,      // Risk increased (e.g., LOW -> MEDIUM, MEDIUM -> HIGH)
        UNCHANGED      // Risk stayed the same
    }
}

