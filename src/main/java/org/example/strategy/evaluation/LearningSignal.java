package org.example.strategy.evaluation;

/**
 * Signals that can be used to improve future recommendations.
 */
public enum LearningSignal {
    /**
     * Recommendation was over-optimistic; reduce expected improvements.
     */
    OVER_OPTIMISTIC_RECOMMENDATION,

    /**
     * User compliance with recommendation was low.
     */
    LOW_USER_COMPLIANCE,

    /**
     * Model assumptions were incorrect (e.g., consistency improvement didn't materialize).
     */
    INCORRECT_MODEL_ASSUMPTION,

    /**
     * Recommendation worked better than expected; can be more ambitious.
     */
    RECOMMENDATION_WAS_CONSERVATIVE,

    /**
     * External factors affected outcomes (can't be directly attributed to recommendation).
     */
    EXTERNAL_FACTORS_INFLUENCE,

    /**
     * Recommendation type needs adjustment (e.g., different approach needed).
     */
    RECOMMENDATION_TYPE_MISMATCH
}

