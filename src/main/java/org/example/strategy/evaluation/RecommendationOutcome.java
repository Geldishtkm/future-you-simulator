package org.example.strategy.evaluation;

import org.example.simulation.model.SimulationResult;
import org.example.strategy.Recommendation;
import org.example.strategy.scenario.ScenarioImpactSummary;

/**
 * Represents the outcome of following a recommendation.
 * Compares expected impact with actual results.
 */
public record RecommendationOutcome(
    Recommendation recommendation,
    ScenarioImpactSummary expectedImpact,
    SimulationResult actualResult,
    double effectivenessScore // 0.0 to 100.0
) {
    /**
     * Creates a new RecommendationOutcome.
     *
     * @param recommendation the recommendation that was followed
     * @param expectedImpact the expected impact from scenario generation
     * @param actualResult the actual simulation result after following the recommendation
     * @param effectivenessScore the effectiveness score (0.0-100.0)
     */
    public RecommendationOutcome {
        if (recommendation == null) {
            throw new IllegalArgumentException("Recommendation cannot be null");
        }
        if (expectedImpact == null) {
            throw new IllegalArgumentException("Expected impact cannot be null");
        }
        if (actualResult == null) {
            throw new IllegalArgumentException("Actual result cannot be null");
        }
        if (effectivenessScore < 0.0 || effectivenessScore > 100.0) {
            throw new IllegalArgumentException("Effectiveness score must be between 0.0 and 100.0");
        }
    }
}

