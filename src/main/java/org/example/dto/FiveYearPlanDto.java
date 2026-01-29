package org.example.dto;

import java.util.List;

/**
 * DTO for comprehensive five-year plan response.
 * Combines simulation, recommendations, scenarios, analytics, and health metrics.
 */
public class FiveYearPlanDto {
    private DashboardDto currentStatus;
    private HealthScoreDto healthScore;
    private SimulationResultDto baseSimulation;
    private List<RecommendationDto> topRecommendations;
    private List<ScenarioImpactSummaryDto> bestScenarios;
    private String summary;
    private String actionPlan;
    private List<String> keyMilestones;
    private double projectedImprovement;

    public FiveYearPlanDto() {
    }

    public DashboardDto getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(DashboardDto currentStatus) {
        this.currentStatus = currentStatus;
    }

    public HealthScoreDto getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(HealthScoreDto healthScore) {
        this.healthScore = healthScore;
    }

    public SimulationResultDto getBaseSimulation() {
        return baseSimulation;
    }

    public void setBaseSimulation(SimulationResultDto baseSimulation) {
        this.baseSimulation = baseSimulation;
    }

    public List<RecommendationDto> getTopRecommendations() {
        return topRecommendations;
    }

    public void setTopRecommendations(List<RecommendationDto> topRecommendations) {
        this.topRecommendations = topRecommendations;
    }

    public List<ScenarioImpactSummaryDto> getBestScenarios() {
        return bestScenarios;
    }

    public void setBestScenarios(List<ScenarioImpactSummaryDto> bestScenarios) {
        this.bestScenarios = bestScenarios;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActionPlan() {
        return actionPlan;
    }

    public void setActionPlan(String actionPlan) {
        this.actionPlan = actionPlan;
    }

    public List<String> getKeyMilestones() {
        return keyMilestones;
    }

    public void setKeyMilestones(List<String> keyMilestones) {
        this.keyMilestones = keyMilestones;
    }

    public double getProjectedImprovement() {
        return projectedImprovement;
    }

    public void setProjectedImprovement(double projectedImprovement) {
        this.projectedImprovement = projectedImprovement;
    }
}

