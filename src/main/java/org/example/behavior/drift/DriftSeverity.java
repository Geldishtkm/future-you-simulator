package org.example.behavior.drift;

/**
 * Severity levels for detected behavior drift.
 */
public enum DriftSeverity {
    /**
     * Low severity - minor drift, may be temporary.
     */
    LOW,

    /**
     * Medium severity - noticeable drift, worth monitoring.
     */
    MEDIUM,

    /**
     * High severity - significant drift, action may be needed.
     */
    HIGH
}

