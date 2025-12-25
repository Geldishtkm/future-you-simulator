package org.example.dto;

import java.util.List;

/**
 * DTO for simulation result response.
 */
public class SimulationResultDto {
    private List<YearlyProjectionDto> yearlyProjections;
    private double averageSkillGrowthIndex;
    private String burnoutRisk; // "LOW", "MEDIUM", "HIGH"
    private IncomeRangeDto incomeRange;
    private double emigrationProbability;
    private String explanation;

    public SimulationResultDto() {
    }

    public List<YearlyProjectionDto> getYearlyProjections() {
        return yearlyProjections;
    }

    public void setYearlyProjections(List<YearlyProjectionDto> yearlyProjections) {
        this.yearlyProjections = yearlyProjections;
    }

    public double getAverageSkillGrowthIndex() {
        return averageSkillGrowthIndex;
    }

    public void setAverageSkillGrowthIndex(double averageSkillGrowthIndex) {
        this.averageSkillGrowthIndex = averageSkillGrowthIndex;
    }

    public String getBurnoutRisk() {
        return burnoutRisk;
    }

    public void setBurnoutRisk(String burnoutRisk) {
        this.burnoutRisk = burnoutRisk;
    }

    public IncomeRangeDto getIncomeRange() {
        return incomeRange;
    }

    public void setIncomeRange(IncomeRangeDto incomeRange) {
        this.incomeRange = incomeRange;
    }

    public double getEmigrationProbability() {
        return emigrationProbability;
    }

    public void setEmigrationProbability(double emigrationProbability) {
        this.emigrationProbability = emigrationProbability;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}

