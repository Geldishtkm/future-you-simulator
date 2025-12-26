package org.example.strategy.scenario;

import org.example.simulation.model.SimulationResult;

/**
 * Summary of the impact of a generated scenario compared to the base scenario.
 */
public record ScenarioImpactSummary(
    GeneratedScenario scenario,
    SimulationResult baseResult,
    SimulationResult improvedResult,
    double xpImprovementPercentage,
    double skillGrowthImprovementPercentage,
    String impactDescription
) {
    /**
     * Creates a new ScenarioImpactSummary.
     *
     * @param scenario the generated scenario
     * @param baseResult the base simulation result
     * @param improvedResult the improved simulation result
     * @param xpImprovementPercentage percentage improvement in final XP
     * @param skillGrowthImprovementPercentage percentage improvement in skill growth index
     * @param impactDescription human-readable description of the impact
     */
    public ScenarioImpactSummary {
        if (scenario == null) {
            throw new IllegalArgumentException("Scenario cannot be null");
        }
        if (baseResult == null) {
            throw new IllegalArgumentException("Base result cannot be null");
        }
        if (improvedResult == null) {
            throw new IllegalArgumentException("Improved result cannot be null");
        }
        if (impactDescription == null || impactDescription.isBlank()) {
            throw new IllegalArgumentException("Impact description cannot be null or blank");
        }
    }
}

