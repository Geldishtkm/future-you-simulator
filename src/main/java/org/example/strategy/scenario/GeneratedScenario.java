package org.example.strategy.scenario;

import org.example.simulation.model.SimulationInput;
import org.example.strategy.Recommendation;

import java.util.List;
import java.util.Map;

/**
 * A generated simulation scenario based on recommendations.
 * Contains modified parameters and rationale for the changes.
 */
public class GeneratedScenario {
    private final String scenarioName;
    private final List<Recommendation> appliedRecommendations;
    private final SimulationInput modifiedInput;
    private final String rationale; // Why this scenario was generated
    private final Map<String, String> parameterChanges; // What changed and how

    /**
     * Creates a new GeneratedScenario.
     *
     * @param scenarioName a descriptive name for this scenario
     * @param appliedRecommendations the recommendations that were applied
     * @param modifiedInput the modified simulation input
     * @param rationale explanation of why this scenario was generated
     * @param parameterChanges description of what parameters changed
     */
    public GeneratedScenario(String scenarioName,
                           List<Recommendation> appliedRecommendations,
                           SimulationInput modifiedInput,
                           String rationale,
                           Map<String, String> parameterChanges) {
        if (scenarioName == null || scenarioName.isBlank()) {
            throw new IllegalArgumentException("Scenario name cannot be null or blank");
        }
        if (appliedRecommendations == null || appliedRecommendations.isEmpty()) {
            throw new IllegalArgumentException("Applied recommendations cannot be null or empty");
        }
        if (modifiedInput == null) {
            throw new IllegalArgumentException("Modified input cannot be null");
        }
        if (rationale == null || rationale.isBlank()) {
            throw new IllegalArgumentException("Rationale cannot be null or blank");
        }
        if (parameterChanges == null) {
            throw new IllegalArgumentException("Parameter changes cannot be null");
        }

        this.scenarioName = scenarioName;
        this.appliedRecommendations = List.copyOf(appliedRecommendations);
        this.modifiedInput = modifiedInput;
        this.rationale = rationale;
        this.parameterChanges = Map.copyOf(parameterChanges);
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public List<Recommendation> getAppliedRecommendations() {
        return appliedRecommendations;
    }

    public SimulationInput getModifiedInput() {
        return modifiedInput;
    }

    public String getRationale() {
        return rationale;
    }

    public Map<String, String> getParameterChanges() {
        return parameterChanges;
    }
}

