package org.example.controller;

import org.example.*;
import org.example.dto.*;
import org.example.service.UserService;
import org.example.simulation.engine.FutureSimulationService;
import org.example.simulation.engine.SimulationInputBuilder;
import org.example.simulation.model.*;
import org.example.strategy.Recommendation;
import org.example.strategy.StrategyRecommendationService;
import org.example.strategy.scenario.ScenarioGeneratorService;
import org.example.strategy.scenario.ScenarioImpactSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for comprehensive planning endpoints.
 * Orchestrates multiple services to provide unified planning insights.
 */
@RestController
@RequestMapping("/api/users/{userId}/plan")
public class PlanController {
    private final UserService userService;
    private final SimulationInputBuilder inputBuilder;
    private final FutureSimulationService simulationService;
    private final StrategyRecommendationService recommendationService;
    private final ScenarioGeneratorService scenarioGeneratorService;
    private final AnalyticsController analyticsController;

    @Autowired
    public PlanController(UserService userService,
                          SimulationInputBuilder inputBuilder,
                          FutureSimulationService simulationService,
                          StrategyRecommendationService recommendationService,
                          ScenarioGeneratorService scenarioGeneratorService,
                          AnalyticsController analyticsController) {
        this.userService = userService;
        this.inputBuilder = inputBuilder;
        this.simulationService = simulationService;
        this.recommendationService = recommendationService;
        this.scenarioGeneratorService = scenarioGeneratorService;
        this.analyticsController = analyticsController;
    }

    /**
     * Generate comprehensive five-year plan.
     * Combines current status, health score, simulation, recommendations, and scenarios.
     *
     * GET /api/users/{userId}/plan/five-year?years=5
     */
    @GetMapping("/five-year")
    public ResponseEntity<FiveYearPlanDto> getFiveYearPlan(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") int years) {
        
        // Validate years
        if (years < 1 || years > 5) {
            throw new IllegalArgumentException("Years must be between 1 and 5");
        }

        // Validate user exists
        userService.getUser(userId);

        // Get all data components
        DashboardDto dashboard = analyticsController.getDashboard(userId).getBody();
        HealthScoreDto healthScore = analyticsController.getHealthScore(userId).getBody();
        
        // Get user data for simulation
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);

        // Build and run simulation
        SimulationInput baseInput = inputBuilder.build(userStats, habitService, goalService, years);
        SimulationResult baseResult = simulationService.simulate(baseInput);
        SimulationResultDto baseSimulation = toSimulationResultDto(baseResult);

        // Get recommendations
        List<Recommendation> recommendations = recommendationService.generateRecommendations(baseResult);
        List<RecommendationDto> topRecommendations = recommendations.stream()
            .sorted(Comparator.comparingDouble(Recommendation::priorityScore).reversed())
            .limit(5)
            .map(this::toRecommendationDto)
            .collect(Collectors.toList());

        // Generate and evaluate scenarios
        List<ScenarioImpactSummary> scenarios = scenarioGeneratorService.generateAndEvaluateScenarios(
            baseInput, recommendations);
        List<ScenarioImpactSummaryDto> bestScenarios = scenarios.stream()
            .sorted(Comparator.comparingDouble(ScenarioImpactSummary::getXpImprovement).reversed())
            .limit(3)
            .map(this::toScenarioImpactSummaryDto)
            .collect(Collectors.toList());

        // Calculate projected improvement
        double projectedImprovement = bestScenarios.isEmpty() ? 0.0 :
            bestScenarios.stream()
                .mapToDouble(ScenarioImpactSummaryDto::getXpImprovement)
                .average()
                .orElse(0.0);

        // Build comprehensive plan
        FiveYearPlanDto plan = new FiveYearPlanDto();
        plan.setCurrentStatus(dashboard);
        plan.setHealthScore(healthScore);
        plan.setBaseSimulation(baseSimulation);
        plan.setTopRecommendations(topRecommendations);
        plan.setBestScenarios(bestScenarios);
        plan.setProjectedImprovement(projectedImprovement);

        // Generate summary
        String summary = generateSummary(dashboard, healthScore, baseResult, recommendations.size());
        plan.setSummary(summary);

        // Generate action plan
        String actionPlan = generateActionPlan(topRecommendations, bestScenarios);
        plan.setActionPlan(actionPlan);

        // Generate key milestones
        List<String> milestones = generateMilestones(baseResult, years);
        plan.setKeyMilestones(milestones);

        return ResponseEntity.ok(plan);
    }

    /**
     * Generate executive summary of the plan.
     */
    private String generateSummary(DashboardDto dashboard, HealthScoreDto healthScore,
                                   SimulationResult baseResult, int recommendationCount) {
        StringBuilder summary = new StringBuilder();
        
        summary.append(String.format("Current Status: Level %d with %d XP. ", 
            dashboard.getLevel(), dashboard.getTotalXp()));
        summary.append(String.format("Health Score: %.1f (%s). ", 
            healthScore.getOverallScore(), healthScore.getHealthLevel()));
        
        if (!baseResult.getYearlyProjections().isEmpty()) {
            YearlyProjection finalYear = baseResult.getYearlyProjections().get(
                baseResult.getYearlyProjections().size() - 1);
            summary.append(String.format("Projected: Level %d with %d XP in %d years. ",
                finalYear.getProjectedLevel(), finalYear.getProjectedXp(),
                baseResult.getYearlyProjections().size()));
        }
        
        summary.append(String.format("Generated %d strategic recommendations. ", recommendationCount));
        summary.append(String.format("Burnout Risk: %s. ", baseResult.getBurnoutRisk().name()));
        
        return summary.toString();
    }

    /**
     * Generate actionable plan based on recommendations and scenarios.
     */
    private String generateActionPlan(List<RecommendationDto> recommendations,
                                     List<ScenarioImpactSummaryDto> scenarios) {
        StringBuilder plan = new StringBuilder();
        plan.append("Action Plan:\n\n");
        
        if (!recommendations.isEmpty()) {
            plan.append("Priority Actions:\n");
            for (int i = 0; i < Math.min(3, recommendations.size()); i++) {
                RecommendationDto rec = recommendations.get(i);
                plan.append(String.format("%d. %s\n", i + 1, rec.getDescription()));
                plan.append(String.format("   Reason: %s\n", rec.getReason()));
            }
            plan.append("\n");
        }
        
        if (!scenarios.isEmpty()) {
            plan.append("Best Improvement Scenarios:\n");
            for (int i = 0; i < Math.min(2, scenarios.size()); i++) {
                ScenarioImpactSummaryDto scenario = scenarios.get(i);
                plan.append(String.format("%d. %s\n", i + 1, scenario.getScenarioName()));
                plan.append(String.format("   Expected XP Improvement: %.0f\n", scenario.getXpImprovement()));
                plan.append(String.format("   Rationale: %s\n", scenario.getRationale()));
            }
        }
        
        return plan.toString();
    }

    /**
     * Generate key milestones based on simulation projections.
     */
    private List<String> generateMilestones(SimulationResult result, int years) {
        List<String> milestones = new ArrayList<>();
        List<YearlyProjection> projections = result.getYearlyProjections();
        
        for (int i = 0; i < projections.size(); i++) {
            YearlyProjection projection = projections.get(i);
            int year = i + 1;
            milestones.add(String.format("Year %d: Reach Level %d with %d XP (Skill Growth: %.1f%%)",
                year, projection.getProjectedLevel(), projection.getProjectedXp(),
                projection.getSkillGrowthIndex() * 100));
        }
        
        // Add income milestone if available
        IncomeRange income = result.getIncomeRange();
        if (income != null && income.getExpectedEstimate() > 0) {
            milestones.add(String.format("Income Projection: $%d - $%d (Expected: $%d)",
                income.getLowEstimate(), income.getHighEstimate(), income.getExpectedEstimate()));
        }
        
        return milestones;
    }

    /**
     * Converts Recommendation to DTO.
     */
    private RecommendationDto toRecommendationDto(Recommendation recommendation) {
        RecommendationDto dto = new RecommendationDto();
        dto.setType(recommendation.type().name());
        dto.setDescription(recommendation.description());
        dto.setReason(recommendation.reason());
        dto.setExpectedBenefit(recommendation.expectedBenefit());
        dto.setRiskNote(recommendation.riskNote());
        dto.setImpact(recommendation.impact().name());
        dto.setPriorityScore(recommendation.priorityScore());
        return dto;
    }

    /**
     * Converts ScenarioImpactSummary to DTO.
     */
    private ScenarioImpactSummaryDto toScenarioImpactSummaryDto(ScenarioImpactSummary summary) {
        ScenarioImpactSummaryDto dto = new ScenarioImpactSummaryDto();
        
        // Scenario info
        dto.setScenarioName(summary.getScenario().getScenarioName());
        dto.setRationale(summary.getScenario().getRationale());
        dto.setParameterChanges(summary.getScenario().getParameterChanges());
        
        // Impact metrics
        dto.setXpImprovement(summary.getXpImprovement());
        dto.setSkillGrowthImprovement(summary.getSkillGrowthImprovement());
        dto.setBurnoutRiskChange(summary.getBurnoutRiskChange().name());
        dto.setIncomeProjectionDelta(summary.getIncomeProjectionDelta());
        dto.setEmigrationProbabilityChange(summary.getEmigrationProbabilityChange());
        dto.setImprovementDescription(summary.getImprovementDescription());
        
        // Results
        dto.setBaseResult(toSimulationResultDto(summary.getBaseResult()));
        dto.setImprovedResult(toSimulationResultDto(summary.getImprovedResult()));
        
        return dto;
    }

    /**
     * Converts SimulationResult to DTO.
     */
    private SimulationResultDto toSimulationResultDto(SimulationResult result) {
        SimulationResultDto dto = new SimulationResultDto();
        dto.setYearlyProjections(result.getYearlyProjections().stream()
            .map(p -> new YearlyProjectionDto(
                p.getYear(), p.getProjectedXp(), p.getProjectedLevel(),
                p.getSkillGrowthIndex(), p.getXpGrowthRate()))
            .collect(Collectors.toList()));
        dto.setAverageSkillGrowthIndex(result.getAverageSkillGrowthIndex());
        dto.setBurnoutRisk(result.getBurnoutRisk().name());
        dto.setIncomeRange(new IncomeRangeDto(
            result.getIncomeRange().getLowEstimate(),
            result.getIncomeRange().getExpectedEstimate(),
            result.getIncomeRange().getHighEstimate()));
        dto.setEmigrationProbability(result.getEmigrationProbability());
        dto.setExplanation(result.getExplanation());
        return dto;
    }
}

