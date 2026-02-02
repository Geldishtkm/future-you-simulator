package org.example;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a progress trend over time.
 */
public record ProgressTrend(
    LocalDate startDate,
    LocalDate endDate,
    String metric,
    List<DataPoint> dataPoints,
    double averageValue,
    double growthRate,
    TrendDirection direction
) {
    /**
     * Creates a progress trend.
     */
    public ProgressTrend {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (metric == null || metric.isBlank()) {
            throw new IllegalArgumentException("Metric cannot be null or blank");
        }
        if (dataPoints == null) {
            throw new IllegalArgumentException("Data points cannot be null");
        }
        if (direction == null) {
            throw new IllegalArgumentException("TrendDirection cannot be null");
        }
    }

    /**
     * Represents a single data point in the trend.
     */
    public record DataPoint(
        LocalDate date,
        double value
    ) {
    }
}

