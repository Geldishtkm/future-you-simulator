package org.example.strategy.scenario;

import org.example.simulation.model.SimulationInput;
import org.example.strategy.Recommendation;

import java.util.List;

/**
 * A scenario generated from recommendations, with modified simulation parameters.
 */
public record GeneratedScenario(
    String name,
    List<Recommendation> appliedRecommendations,
    SimulationInput modifiedInput,
    String rationale,
    String expectedLongTermBenefit
) {
    /**
     * Creates a new GeneratedScenario.
     *
     * @param name descriptive name for this scenario
     * @param appliedRecommendations recommendations that were applied to generate this scenario
     * @param modifiedInput the modified simulation input
     * @param rationale explanation of what was changed and why
     * @param expectedLongTermBenefit description of expected long-term benefit
     */
    public GeneratedScenario {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Scenario name cannot be null or blank");
        }
        if (appliedRecommendations == null) {
            throw new IllegalArgumentException("Applied recommendations cannot be null");
        }
        if (modifiedInput == null) {
            throw new IllegalArgumentException("Modified input cannot be null");
        }
        if (rationale == null || rationale.isBlank()) {
            throw new IllegalArgumentException("Rationale cannot be null or blank");
        }
        if (expectedLongTermBenefit == null || expectedLongTermBenefit.isBlank()) {
            throw new IllegalArgumentException("Expected long-term benefit cannot be null or blank");
        }
        appliedRecommendations = List.copyOf(appliedRecommendations);
    }
}

