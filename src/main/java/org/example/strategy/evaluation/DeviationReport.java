package org.example.strategy.evaluation;

/**
 * Reports on deviations between expected and actual outcomes.
 */
public class DeviationReport {
    private final double xpDeviation; // Percentage deviation from expected XP improvement
    private final double skillGrowthDeviation; // Deviation from expected skill growth improvement
    private final String deviationAnalysis; // Human-readable analysis
    private final DeviationSeverity severity; // Overall severity of deviations
    private final String mostAffectedMetric; // Which metric had the largest deviation

    /**
     * Creates a new DeviationReport.
     *
     * @param xpDeviation percentage deviation from expected XP improvement
     * @param skillGrowthDeviation deviation from expected skill growth improvement
     * @param deviationAnalysis human-readable analysis of deviations
     * @param severity overall severity of deviations
     * @param mostAffectedMetric the metric with largest deviation
     */
    public DeviationReport(double xpDeviation,
                          double skillGrowthDeviation,
                          String deviationAnalysis,
                          DeviationSeverity severity,
                          String mostAffectedMetric) {
        if (deviationAnalysis == null || deviationAnalysis.isBlank()) {
            throw new IllegalArgumentException("Deviation analysis cannot be null or blank");
        }
        if (severity == null) {
            throw new IllegalArgumentException("Severity cannot be null");
        }
        if (mostAffectedMetric == null || mostAffectedMetric.isBlank()) {
            throw new IllegalArgumentException("Most affected metric cannot be null or blank");
        }

        this.xpDeviation = xpDeviation;
        this.skillGrowthDeviation = skillGrowthDeviation;
        this.deviationAnalysis = deviationAnalysis;
        this.severity = severity;
        this.mostAffectedMetric = mostAffectedMetric;
    }

    public double getXpDeviation() {
        return xpDeviation;
    }

    public double getSkillGrowthDeviation() {
        return skillGrowthDeviation;
    }

    public String getDeviationAnalysis() {
        return deviationAnalysis;
    }

    public DeviationSeverity getSeverity() {
        return severity;
    }

    public String getMostAffectedMetric() {
        return mostAffectedMetric;
    }

    /**
     * Severity levels for deviations.
     */
    public enum DeviationSeverity {
        NONE,       // No significant deviation (< 5%)
        LOW,        // Minor deviation (5-15%)
        MEDIUM,     // Moderate deviation (15-30%)
        HIGH,       // Significant deviation (30-50%)
        CRITICAL    // Major deviation (> 50%)
    }
}

