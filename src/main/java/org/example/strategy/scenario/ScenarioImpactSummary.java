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
    private final String improvementDescription; // Human-readable improvement summary

    /**
     * Creates a new ScenarioImpactSummary.
     *
     * @param scenario the generated scenario
     * @param baseResult the base simulation result
     * @param improvedResult the improved simulation result
     * @param xpImprovement percentage improvement in final XP
     * @param skillGrowthImprovement improvement in skill growth index
     * @param improvementDescription human-readable improvement summary
     */
    public ScenarioImpactSummary(GeneratedScenario scenario,
                                SimulationResult baseResult,
                                SimulationResult improvedResult,
                                double xpImprovement,
                                double skillGrowthImprovement,
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
        if (improvementDescription == null || improvementDescription.isBlank()) {
            throw new IllegalArgumentException("Improvement description cannot be null or blank");
        }

        this.scenario = scenario;
        this.baseResult = baseResult;
        this.improvedResult = improvedResult;
        this.xpImprovement = xpImprovement;
        this.skillGrowthImprovement = skillGrowthImprovement;
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

    public String getImprovementDescription() {
        return improvementDescription;
    }
}

