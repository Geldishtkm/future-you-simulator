package org.example;

import java.util.List;

/**
 * Represents a burnout warning with details about potential risk factors.
 *
 * @param isWarningActive true if burnout warning should be shown
 * @param riskFactors list of risk factors detected
 * @param severityScore severity score from 0.0 (low) to 100.0 (high)
 */
public record BurnoutWarning(boolean isWarningActive, List<String> riskFactors, double severityScore) {
    /**
     * Creates a new burnout warning.
     *
     * @param isWarningActive whether warning is active
     * @param riskFactors the list of risk factors (must not be null)
     * @param severityScore the severity (0.0 to 100.0)
     * @throws IllegalArgumentException if riskFactors is null or severityScore is invalid
     */
    public BurnoutWarning {
        if (riskFactors == null) {
            throw new IllegalArgumentException("Risk factors list cannot be null");
        }
        if (severityScore < 0.0 || severityScore > 100.0) {
            throw new IllegalArgumentException("Severity score must be between 0.0 and 100.0");
        }
    }

    /**
     * Returns true if there are any risk factors.
     *
     * @return true if riskFactors is not empty
     */
    public boolean hasRiskFactors() {
        return !riskFactors.isEmpty();
    }
}

