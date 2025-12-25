package org.example.dto;

/**
 * DTO for income range projection.
 */
public class IncomeRangeDto {
    private int lowEstimate;
    private int expectedEstimate;
    private int highEstimate;

    public IncomeRangeDto() {
    }

    public IncomeRangeDto(int lowEstimate, int expectedEstimate, int highEstimate) {
        this.lowEstimate = lowEstimate;
        this.expectedEstimate = expectedEstimate;
        this.highEstimate = highEstimate;
    }

    public int getLowEstimate() {
        return lowEstimate;
    }

    public void setLowEstimate(int lowEstimate) {
        this.lowEstimate = lowEstimate;
    }

    public int getExpectedEstimate() {
        return expectedEstimate;
    }

    public void setExpectedEstimate(int expectedEstimate) {
        this.expectedEstimate = expectedEstimate;
    }

    public int getHighEstimate() {
        return highEstimate;
    }

    public void setHighEstimate(int highEstimate) {
        this.highEstimate = highEstimate;
    }
}

