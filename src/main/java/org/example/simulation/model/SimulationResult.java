package org.example.simulation.model;

import java.util.List;

/**
 * Result of a future simulation, containing projections and predictions.
 */
public class SimulationResult {
    private final List<YearlyProjection> yearlyProjections;
    private final double averageSkillGrowthIndex; // Average across all years
    private final BurnoutRisk burnoutRisk;
    private final IncomeRange incomeRange;
    private final double emigrationProbability; // 0.0 to 100.0
    private final String explanation; // Textual explanation of the results

    /**
     * Creates a new SimulationResult.
     *
     * @param yearlyProjections projections for each year
     * @param averageSkillGrowthIndex average skill growth across all years
     * @param burnoutRisk predicted burnout risk
     * @param incomeRange projected income range
     * @param emigrationProbability probability of emigration (0.0-100.0)
     * @param explanation textual explanation of the results
     */
    public SimulationResult(List<YearlyProjection> yearlyProjections,
                           double averageSkillGrowthIndex,
                           BurnoutRisk burnoutRisk,
                           IncomeRange incomeRange,
                           double emigrationProbability,
                           String explanation) {
        if (yearlyProjections == null || yearlyProjections.isEmpty()) {
            throw new IllegalArgumentException("Yearly projections cannot be null or empty");
        }
        if (averageSkillGrowthIndex < 0.0 || averageSkillGrowthIndex > 100.0) {
            throw new IllegalArgumentException("Average skill growth index must be between 0.0 and 100.0");
        }
        if (burnoutRisk == null) {
            throw new IllegalArgumentException("Burnout risk cannot be null");
        }
        if (incomeRange == null) {
            throw new IllegalArgumentException("Income range cannot be null");
        }
        if (emigrationProbability < 0.0 || emigrationProbability > 100.0) {
            throw new IllegalArgumentException("Emigration probability must be between 0.0 and 100.0");
        }
        if (explanation == null || explanation.isBlank()) {
            throw new IllegalArgumentException("Explanation cannot be null or blank");
        }

        this.yearlyProjections = List.copyOf(yearlyProjections);
        this.averageSkillGrowthIndex = averageSkillGrowthIndex;
        this.burnoutRisk = burnoutRisk;
        this.incomeRange = incomeRange;
        this.emigrationProbability = emigrationProbability;
        this.explanation = explanation;
    }

    public List<YearlyProjection> getYearlyProjections() {
        return yearlyProjections;
    }

    public double getAverageSkillGrowthIndex() {
        return averageSkillGrowthIndex;
    }

    public BurnoutRisk getBurnoutRisk() {
        return burnoutRisk;
    }

    public IncomeRange getIncomeRange() {
        return incomeRange;
    }

    public double getEmigrationProbability() {
        return emigrationProbability;
    }

    public String getExplanation() {
        return explanation;
    }
}

