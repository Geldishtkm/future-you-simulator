package org.example.behavior.drift;

import java.time.LocalDate;
import java.util.Map;

/**
 * Represents a detected behavior drift event.
 * Contains drift classification, affected metrics, and explanation.
 */
public class DriftEvent {
    private final DriftType driftType;
    private final DriftSeverity severity;
    private final BehaviorSnapshot earlierSnapshot;
    private final BehaviorSnapshot laterSnapshot;
    private final LocalDate detectionDate;
    private final Map<String, Double> affectedMetrics; // Metric name -> change amount
    private final String explanation; // Human-readable explanation
    private final int daysBetweenSnapshots; // Timeframe of the drift

    /**
     * Creates a new DriftEvent.
     *
     * @param driftType the type of drift detected
     * @param severity the severity of the drift
     * @param earlierSnapshot the earlier behavior snapshot
     * @param laterSnapshot the later behavior snapshot
     * @param detectionDate the date when drift was detected
     * @param affectedMetrics map of affected metrics and their changes
     * @param explanation human-readable explanation of the drift
     * @param daysBetweenSnapshots number of days between snapshots
     */
    public DriftEvent(DriftType driftType,
                     DriftSeverity severity,
                     BehaviorSnapshot earlierSnapshot,
                     BehaviorSnapshot laterSnapshot,
                     LocalDate detectionDate,
                     Map<String, Double> affectedMetrics,
                     String explanation,
                     int daysBetweenSnapshots) {
        if (driftType == null) {
            throw new IllegalArgumentException("Drift type cannot be null");
        }
        if (severity == null) {
            throw new IllegalArgumentException("Severity cannot be null");
        }
        if (earlierSnapshot == null) {
            throw new IllegalArgumentException("Earlier snapshot cannot be null");
        }
        if (laterSnapshot == null) {
            throw new IllegalArgumentException("Later snapshot cannot be null");
        }
        if (detectionDate == null) {
            throw new IllegalArgumentException("Detection date cannot be null");
        }
        if (affectedMetrics == null || affectedMetrics.isEmpty()) {
            throw new IllegalArgumentException("Affected metrics cannot be null or empty");
        }
        if (explanation == null || explanation.isBlank()) {
            throw new IllegalArgumentException("Explanation cannot be null or blank");
        }
        if (daysBetweenSnapshots < 1) {
            throw new IllegalArgumentException("Days between snapshots must be at least 1");
        }

        this.driftType = driftType;
        this.severity = severity;
        this.earlierSnapshot = earlierSnapshot;
        this.laterSnapshot = laterSnapshot;
        this.detectionDate = detectionDate;
        this.affectedMetrics = Map.copyOf(affectedMetrics);
        this.explanation = explanation;
        this.daysBetweenSnapshots = daysBetweenSnapshots;
    }

    public DriftType getDriftType() {
        return driftType;
    }

    public DriftSeverity getSeverity() {
        return severity;
    }

    public BehaviorSnapshot getEarlierSnapshot() {
        return earlierSnapshot;
    }

    public BehaviorSnapshot getLaterSnapshot() {
        return laterSnapshot;
    }

    public LocalDate getDetectionDate() {
        return detectionDate;
    }

    public Map<String, Double> getAffectedMetrics() {
        return affectedMetrics;
    }

    public String getExplanation() {
        return explanation;
    }

    public int getDaysBetweenSnapshots() {
        return daysBetweenSnapshots;
    }
}

