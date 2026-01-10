package org.example.dto;

import java.util.Map;

/**
 * DTO for scenario impact summary response.
 */
public class ScenarioImpactSummaryDto {
    private String scenarioName;
    private String rationale;
    private Map<String, String> parameterChanges;
    private double xpImprovement;
    private double skillGrowthImprovement;
    private String burnoutRiskChange;
    private int incomeProjectionDelta;
    private double emigrationProbabilityChange;
    private String improvementDescription;
    private SimulationResultDto baseResult;
    private SimulationResultDto improvedResult;

    public ScenarioImpactSummaryDto() {
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public Map<String, String> getParameterChanges() {
        return parameterChanges;
    }

    public void setParameterChanges(Map<String, String> parameterChanges) {
        this.parameterChanges = parameterChanges;
    }

    public double getXpImprovement() {
        return xpImprovement;
    }

    public void setXpImprovement(double xpImprovement) {
        this.xpImprovement = xpImprovement;
    }

    public double getSkillGrowthImprovement() {
        return skillGrowthImprovement;
    }

    public void setSkillGrowthImprovement(double skillGrowthImprovement) {
        this.skillGrowthImprovement = skillGrowthImprovement;
    }

    public String getBurnoutRiskChange() {
        return burnoutRiskChange;
    }

    public void setBurnoutRiskChange(String burnoutRiskChange) {
        this.burnoutRiskChange = burnoutRiskChange;
    }

    public int getIncomeProjectionDelta() {
        return incomeProjectionDelta;
    }

    public void setIncomeProjectionDelta(int incomeProjectionDelta) {
        this.incomeProjectionDelta = incomeProjectionDelta;
    }

    public double getEmigrationProbabilityChange() {
        return emigrationProbabilityChange;
    }

    public void setEmigrationProbabilityChange(double emigrationProbabilityChange) {
        this.emigrationProbabilityChange = emigrationProbabilityChange;
    }

    public String getImprovementDescription() {
        return improvementDescription;
    }

    public void setImprovementDescription(String improvementDescription) {
        this.improvementDescription = improvementDescription;
    }

    public SimulationResultDto getBaseResult() {
        return baseResult;
    }

    public void setBaseResult(SimulationResultDto baseResult) {
        this.baseResult = baseResult;
    }

    public SimulationResultDto getImprovedResult() {
        return improvedResult;
    }

    public void setImprovedResult(SimulationResultDto improvedResult) {
        this.improvedResult = improvedResult;
    }
}

