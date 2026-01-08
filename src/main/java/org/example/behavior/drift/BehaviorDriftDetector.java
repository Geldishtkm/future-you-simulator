package org.example.behavior.drift;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Detects meaningful behavior drift by comparing behavior snapshots over time.
 * Ignores short-term noise and focuses on statistically meaningful changes.
 */
@Service
public class BehaviorDriftDetector {
    private static final double MIN_CHANGE_THRESHOLD = 10.0; // Minimum 10% change to be considered meaningful
    private static final int MIN_DAYS_FOR_DRIFT = 14; // Minimum days between snapshots to detect drift

    /**
     * Detects behavior drift by comparing two snapshots.
     *
     * @param earlierSnapshot the earlier behavior snapshot
     * @param laterSnapshot the later behavior snapshot
     * @param detectionDate the date when drift detection is performed
     * @return a DriftEvent if meaningful drift is detected, null otherwise
     */
    public DriftEvent detectDrift(BehaviorSnapshot earlierSnapshot,
                                  BehaviorSnapshot laterSnapshot,
                                  LocalDate detectionDate) {
        if (earlierSnapshot == null) {
            throw new IllegalArgumentException("Earlier snapshot cannot be null");
        }
        if (laterSnapshot == null) {
            throw new IllegalArgumentException("Later snapshot cannot be null");
        }
        if (detectionDate == null) {
            throw new IllegalArgumentException("Detection date cannot be null");
        }

        // Calculate days between snapshots
        int daysBetween = (int) java.time.temporal.ChronoUnit.DAYS.between(
            earlierSnapshot.date(), laterSnapshot.date());

        // Need sufficient time between snapshots
        if (daysBetween < MIN_DAYS_FOR_DRIFT) {
            return null; // Not enough time to detect meaningful drift
        }

        // Calculate changes in all metrics
        Map<String, Double> metricChanges = calculateMetricChanges(earlierSnapshot, laterSnapshot);

        // Determine if there's meaningful drift
        if (!hasMeaningfulChange(metricChanges)) {
            return null; // No meaningful drift detected
        }

        // Classify drift type
        DriftType driftType = classifyDriftType(earlierSnapshot, laterSnapshot, metricChanges);

        // Determine severity
        DriftSeverity severity = determineSeverity(metricChanges, driftType);

        // Generate explanation
        String explanation = generateExplanation(driftType, severity, metricChanges, daysBetween);

        return new DriftEvent(
            driftType,
            severity,
            earlierSnapshot,
            laterSnapshot,
            detectionDate,
            metricChanges,
            explanation,
            daysBetween
        );
    }

    /**
     * Detects drift across multiple snapshots by comparing recent vs earlier periods.
     *
     * @param snapshots list of snapshots ordered by date (earliest first)
     * @param detectionDate the date when drift detection is performed
     * @return a list of drift events detected
     */
    public List<DriftEvent> detectDriftOverTime(List<BehaviorSnapshot> snapshots,
                                                LocalDate detectionDate) {
        if (snapshots == null || snapshots.size() < 2) {
            return List.of();
        }

        List<DriftEvent> driftEvents = new ArrayList<>();

        // Compare each pair of snapshots with sufficient time between them
        for (int i = 0; i < snapshots.size() - 1; i++) {
            BehaviorSnapshot earlier = snapshots.get(i);
            BehaviorSnapshot later = snapshots.get(i + 1);

            DriftEvent event = detectDrift(earlier, later, detectionDate);
            if (event != null) {
                driftEvents.add(event);
            }
        }

        return driftEvents;
    }

    /**
     * Calculates changes in all metrics between two snapshots.
     */
    private Map<String, Double> calculateMetricChanges(BehaviorSnapshot earlier,
                                                       BehaviorSnapshot later) {
        Map<String, Double> changes = new HashMap<>();

        // Average daily XP change (percentage)
        double xpChange = earlier.averageDailyXp() > 0 ?
            ((later.averageDailyXp() - earlier.averageDailyXp()) / earlier.averageDailyXp()) * 100.0 :
            (later.averageDailyXp() > 0 ? 100.0 : 0.0);
        changes.put("averageDailyXp", xpChange);

        // Habit completion rate change (percentage points)
        double completionRateChange = later.habitCompletionRate() - earlier.habitCompletionRate();
        changes.put("habitCompletionRate", completionRateChange);

        // Streak stability change (percentage points)
        double streakStabilityChange = later.streakStability() - earlier.streakStability();
        changes.put("streakStability", streakStabilityChange);

        // Burnout risk change (percentage points)
        double burnoutRiskChange = later.burnoutRiskScore() - earlier.burnoutRiskScore();
        changes.put("burnoutRiskScore", burnoutRiskChange);

        // Active goal count change (absolute)
        double goalCountChange = later.activeGoalCount() - earlier.activeGoalCount();
        changes.put("activeGoalCount", goalCountChange);

        // Goal engagement rate change (percentage points)
        double goalEngagementChange = later.goalEngagementRate() - earlier.goalEngagementRate();
        changes.put("goalEngagementRate", goalEngagementChange);

        return changes;
    }

    /**
     * Determines if there are meaningful changes in the metrics.
     */
    private boolean hasMeaningfulChange(Map<String, Double> metricChanges) {
        // Check if any metric has a change above the threshold
        for (double change : metricChanges.values()) {
            if (Math.abs(change) >= MIN_CHANGE_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    /**
     * Classifies the type of drift based on metric changes.
     */
    private DriftType classifyDriftType(BehaviorSnapshot earlier,
                                       BehaviorSnapshot later,
                                       Map<String, Double> metricChanges) {
        double xpChange = metricChanges.get("averageDailyXp");
        double burnoutChange = metricChanges.get("burnoutRiskScore");
        double completionRateChange = metricChanges.get("habitCompletionRate");

        // Burnout drift takes priority if burnout risk increased significantly
        if (burnoutChange >= 15.0 && burnoutChange > Math.abs(xpChange)) {
            return DriftType.BURNOUT;
        }

        // Improvement: XP and completion rate improving
        if (xpChange > MIN_CHANGE_THRESHOLD && completionRateChange > 5.0) {
            return DriftType.IMPROVEMENT;
        }

        // Decline: XP and completion rate declining
        if (xpChange < -MIN_CHANGE_THRESHOLD && completionRateChange < -5.0) {
            return DriftType.DECLINE;
        }

        // Stagnation: minimal changes overall
        double maxChange = metricChanges.values().stream()
            .mapToDouble(Math::abs)
            .max()
            .orElse(0.0);

        if (maxChange < MIN_CHANGE_THRESHOLD * 1.5) {
            return DriftType.STAGNATION;
        }

        // Default to decline if negative trends dominate
        if (xpChange < 0 && completionRateChange < 0) {
            return DriftType.DECLINE;
        }

        // Default to improvement if positive trends dominate
        return DriftType.IMPROVEMENT;
    }

    /**
     * Determines the severity of the drift.
     */
    private DriftSeverity determineSeverity(Map<String, Double> metricChanges, DriftType driftType) {
        // Calculate maximum absolute change
        double maxChange = metricChanges.values().stream()
            .mapToDouble(Math::abs)
            .max()
            .orElse(0.0);

        // Severity thresholds
        if (maxChange >= 30.0) {
            return DriftSeverity.HIGH;
        } else if (maxChange >= 15.0) {
            return DriftSeverity.MEDIUM;
        } else {
            return DriftSeverity.LOW;
        }
    }

    /**
     * Generates a human-readable explanation of the drift.
     */
    private String generateExplanation(DriftType driftType,
                                      DriftSeverity severity,
                                      Map<String, Double> metricChanges,
                                      int daysBetween) {
        StringBuilder explanation = new StringBuilder();

        explanation.append(String.format("Detected %s drift (%s severity) over %d days. ", 
            driftType.name().toLowerCase(), severity.name().toLowerCase(), daysBetween));

        // Describe key changes
        List<String> keyChanges = new ArrayList<>();
        
        if (Math.abs(metricChanges.get("averageDailyXp")) >= MIN_CHANGE_THRESHOLD) {
            double xpChange = metricChanges.get("averageDailyXp");
            if (xpChange > 0) {
                keyChanges.add(String.format("average daily XP increased by %.1f%%", xpChange));
            } else {
                keyChanges.add(String.format("average daily XP decreased by %.1f%%", Math.abs(xpChange)));
            }
        }

        if (Math.abs(metricChanges.get("habitCompletionRate")) >= 5.0) {
            double completionChange = metricChanges.get("habitCompletionRate");
            if (completionChange > 0) {
                keyChanges.add(String.format("habit completion rate improved by +%.1f points", completionChange));
            } else {
                keyChanges.add(String.format("habit completion rate declined by %.1f points", Math.abs(completionChange)));
            }
        }

        if (Math.abs(metricChanges.get("burnoutRiskScore")) >= 10.0) {
            double burnoutChange = metricChanges.get("burnoutRiskScore");
            if (burnoutChange > 0) {
                keyChanges.add(String.format("burnout risk increased by +%.1f points", burnoutChange));
            } else {
                keyChanges.add(String.format("burnout risk decreased by %.1f points", Math.abs(burnoutChange)));
            }
        }

        if (!keyChanges.isEmpty()) {
            explanation.append("Key changes: ").append(String.join("; ", keyChanges)).append(". ");
        }

        // Add long-term risk or opportunity assessment
        switch (driftType) {
            case IMPROVEMENT:
                explanation.append("This suggests positive behavioral momentum. ");
                break;
            case DECLINE:
                explanation.append("This may indicate a need for intervention to prevent further decline. ");
                break;
            case BURNOUT:
                explanation.append("Burnout indicators suggest the need to reduce effort intensity. ");
                break;
            case STAGNATION:
                explanation.append("Stagnation suggests the need for new challenges or goals to re-energize progress. ");
                break;
        }

        return explanation.toString().trim();
    }
}

