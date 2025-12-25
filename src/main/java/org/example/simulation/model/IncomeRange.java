package org.example.simulation.model;

/**
 * Represents a probabilistic income range projection.
 */
public class IncomeRange {
    private final int lowEstimate; // 25th percentile
    private final int expectedEstimate; // 50th percentile (median)
    private final int highEstimate; // 75th percentile

    /**
     * Creates a new IncomeRange.
     *
     * @param lowEstimate low estimate (25th percentile)
     * @param expectedEstimate expected estimate (50th percentile)
     * @param highEstimate high estimate (75th percentile)
     */
    public IncomeRange(int lowEstimate, int expectedEstimate, int highEstimate) {
        if (lowEstimate < 0 || expectedEstimate < 0 || highEstimate < 0) {
            throw new IllegalArgumentException("Income estimates cannot be negative");
        }
        if (lowEstimate > expectedEstimate || expectedEstimate > highEstimate) {
            throw new IllegalArgumentException("Income estimates must be in order: low <= expected <= high");
        }

        this.lowEstimate = lowEstimate;
        this.expectedEstimate = expectedEstimate;
        this.highEstimate = highEstimate;
    }

    public int getLowEstimate() {
        return lowEstimate;
    }

    public int getExpectedEstimate() {
        return expectedEstimate;
    }

    public int getHighEstimate() {
        return highEstimate;
    }
}

