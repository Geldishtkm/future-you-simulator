package org.example.strategy;

/**
 * A strategic recommendation for improving future trajectory.
 * Immutable record-based model for modern Java.
 */
public record Recommendation(
    RecommendationType type,
    String description,
    String reason,
    String expectedBenefit,
    String riskNote,
    RecommendationImpact impact,
    double priorityScore // 0.0 to 100.0 for ranking
) {
    /**
     * Creates a new Recommendation.
     *
     * @param type the type of recommendation
     * @param description what action to take
     * @param reason why this recommendation was generated
     * @param expectedBenefit what improvement is expected
     * @param riskNote any risks or considerations
     * @param impact expected impact level
     * @param priorityScore priority score for ranking (0.0-100.0)
     */
    public Recommendation {
        if (type == null) {
            throw new IllegalArgumentException("Recommendation type cannot be null");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or blank");
        }
        if (expectedBenefit == null || expectedBenefit.isBlank()) {
            throw new IllegalArgumentException("Expected benefit cannot be null or blank");
        }
        if (riskNote == null) {
            riskNote = "";
        }
        if (impact == null) {
            throw new IllegalArgumentException("Impact cannot be null");
        }
        if (priorityScore < 0.0 || priorityScore > 100.0) {
            throw new IllegalArgumentException("Priority score must be between 0.0 and 100.0");
        }
    }
}

