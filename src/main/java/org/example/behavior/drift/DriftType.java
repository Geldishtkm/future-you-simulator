package org.example.behavior.drift;

/**
 * Types of behavior drift that can be detected.
 */
public enum DriftType {
    /**
     * Positive drift - behavior improving over time.
     */
    IMPROVEMENT,

    /**
     * Negative drift - behavior declining over time.
     */
    DECLINE,

    /**
     * Burnout drift - increasing burnout indicators.
     */
    BURNOUT,

    /**
     * Stagnation drift - behavior plateauing, no meaningful change.
     */
    STAGNATION
}

