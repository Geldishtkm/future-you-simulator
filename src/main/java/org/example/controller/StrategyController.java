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

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for strategy and recommendation endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/strategy")
public class StrategyController {
    private final UserService userService;
    private final SimulationInputBuilder inputBuilder;
    private final FutureSimulationService simulationService;
    private final StrategyRecommendationService recommendationService;
    private final ScenarioGeneratorService scenarioGeneratorService;

    @Autowired
    public StrategyController(UserService userService,
                             SimulationInputBuilder inputBuilder,
                             FutureSimulationService simulationService,
                             StrategyRecommendationService recommendationService,
                             ScenarioGeneratorService scenarioGeneratorService) {
        this.userService = userService;
        this.inputBuilder = inputBuilder;
        this.simulationService = simulationService;
        this.recommendationService = recommendationService;
        this.scenarioGeneratorService = scenarioGeneratorService;
    }

    /**
     * Generates strategic recommendations based on user's current trajectory.
     *
     * GET /api/users/{userId}/strategy/recommendations?years=3
     */
    @GetMapping("/recommendations")
    public ResponseEntity<List<RecommendationDto>> getRecommendations(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "3") int years) {
        
        // Validate years
        if (years < 1 || years > 5) {
            throw new IllegalArgumentException("Years must be between 1 and 5");
        }

        // Get user data and run simulation
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);

        // Build simulation input and run simulation
        SimulationInput input = inputBuilder.build(userStats, habitService, goalService, years);
        SimulationResult result = simulationService.simulate(input);

        // Generate recommendations
        List<Recommendation> recommendations = recommendationService.generateRecommendations(result);

        // Convert to DTOs
        List<RecommendationDto> dtos = recommendations.stream()
            .map(this::toRecommendationDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Generates and evaluates improved scenarios based on recommendations.
     *
     * POST /api/users/{userId}/strategy/scenarios?years=3
     */
    @PostMapping("/scenarios")
    public ResponseEntity<List<ScenarioImpactSummaryDto>> generateScenarios(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "3") int years) {
        
        // Validate years
        if (years < 1 || years > 5) {
            throw new IllegalArgumentException("Years must be between 1 and 5");
        }

        // Get user data
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);

        // Build base simulation input
        SimulationInput baseInput = inputBuilder.build(userStats, habitService, goalService, years);

        // Run base simulation
        SimulationResult baseResult = simulationService.simulate(baseInput);

        // Generate recommendations
        List<Recommendation> recommendations = recommendationService.generateRecommendations(baseResult);

        // Generate and evaluate scenarios
        List<ScenarioImpactSummary> summaries = scenarioGeneratorService.generateAndEvaluateScenarios(
            baseInput, recommendations);

        // Convert to DTOs
        List<ScenarioImpactSummaryDto> dtos = summaries.stream()
            .map(this::toScenarioImpactSummaryDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }


    /**
     * Converts Recommendation to DTO.
     * Made package-private for use by PlanController.
     */
    RecommendationDto toRecommendationDto(Recommendation recommendation) {
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
     * Made package-private for use by PlanController.
     */
    ScenarioImpactSummaryDto toScenarioImpactSummaryDto(ScenarioImpactSummary summary) {
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
     * Made package-private for use by PlanController.
     */
    SimulationResultDto toSimulationResultDto(SimulationResult result) {
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

