package org.example.strategy.evaluation;

import java.util.List;

/**
 * Complete effectiveness evaluation result.
 * Contains outcome, deviation report, learning signals, and explanation.
 */
public class EffectivenessEvaluation {
    private final RecommendationOutcome outcome;
    private final DeviationReport deviationReport;
    private final List<LearningSignal> learningSignals;
    private final String explanation; // Human-readable explanation
    private final double confidenceLevel; // 0.0 to 100.0

    /**
     * Creates a new EffectivenessEvaluation.
     *
     * @param outcome the recommendation outcome
     * @param deviationReport the deviation analysis
     * @param learningSignals signals for improving future recommendations
     * @param explanation human-readable explanation
     * @param confidenceLevel confidence in the evaluation (0.0-100.0)
     */
    public EffectivenessEvaluation(RecommendationOutcome outcome,
                                  DeviationReport deviationReport,
                                  List<LearningSignal> learningSignals,
                                  String explanation,
                                  double confidenceLevel) {
        if (outcome == null) {
            throw new IllegalArgumentException("Outcome cannot be null");
        }
        if (deviationReport == null) {
            throw new IllegalArgumentException("Deviation report cannot be null");
        }
        if (learningSignals == null) {
            throw new IllegalArgumentException("Learning signals cannot be null");
        }
        if (explanation == null || explanation.isBlank()) {
            throw new IllegalArgumentException("Explanation cannot be null or blank");
        }
        if (confidenceLevel < 0.0 || confidenceLevel > 100.0) {
            throw new IllegalArgumentException("Confidence level must be between 0.0 and 100.0");
        }

        this.outcome = outcome;
        this.deviationReport = deviationReport;
        this.learningSignals = List.copyOf(learningSignals);
        this.explanation = explanation;
        this.confidenceLevel = confidenceLevel;
    }

    public RecommendationOutcome getOutcome() {
        return outcome;
    }

    public DeviationReport getDeviationReport() {
        return deviationReport;
    }

    public List<LearningSignal> getLearningSignals() {
        return learningSignals;
    }

    public String getExplanation() {
        return explanation;
    }

    public double getConfidenceLevel() {
        return confidenceLevel;
    }
}

