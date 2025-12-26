package org.example.strategy.scenario;

import org.example.*;
import org.example.simulation.engine.FutureSimulationService;
import org.example.simulation.model.*;
import org.example.strategy.Recommendation;
import org.example.strategy.RecommendationImpact;
import org.example.strategy.RecommendationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScenarioEvaluationService.
 */
class ScenarioEvaluationServiceTest {
    private ScenarioEvaluationService evaluationService;
    private FutureSimulationService simulationService;
    private UserStats userStats;
    private LevelCalculator levelCalculator;

    @BeforeEach
    void setUp() {
        levelCalculator = new LevelCalculator();
        simulationService = new FutureSimulationService();
        evaluationService = new ScenarioEvaluationService(simulationService);
        userStats = new UserStats(500, levelCalculator.calculateLevel(500));
    }

    @Test
    void testEvaluateScenario() {
        // Arrange
        SimulationInput baseInput = createTestInput(60.0, 70.0, createMediumDistribution());
        SimulationResult baseResult = simulationService.simulate(baseInput);

        // Create improved scenario (higher consistency)
        SimulationInput improvedInput = new SimulationInput(
            baseInput.getCurrentStats(),
            80.0, // Improved consistency
            baseInput.getAverageDailyEffort(),
            baseInput.getDifficultyDistribution(),
            baseInput.getActiveGoals(),
            baseInput.getBurnoutWarning(),
            baseInput.getActiveDaysLastMonth() + 5,
            baseInput.getAverageStreakLength() + 5.0,
            baseInput.getYearsToSimulate()
        );

        GeneratedScenario scenario = new GeneratedScenario(
            "Test Scenario",
            List.of(new Recommendation(
                RecommendationType.IMPROVE_CONSISTENCY,
                "Test",
                "Test reason",
                "Test benefit",
                "",
                RecommendationImpact.MEDIUM,
                70.0
            )),
            improvedInput,
            "Improved consistency",
            "Better growth"
        );

        // Act
        List<ScenarioImpactSummary> summaries = evaluationService.evaluateScenarios(
            baseInput, baseResult, List.of(scenario));

        // Assert
        assertNotNull(summaries);
        assertFalse(summaries.isEmpty());
        
        ScenarioImpactSummary summary = summaries.get(0);
        assertEquals(scenario, summary.scenario());
        assertEquals(baseResult, summary.baseResult());
        assertNotNull(summary.improvedResult());
        
        // Improved result should have better or equal metrics
        assertTrue(summary.improvedResult().getAverageSkillGrowthIndex() >= 
                  summary.baseResult().getAverageSkillGrowthIndex() - 5.0,
            "Improved scenario should have similar or better skill growth");
        
        assertNotNull(summary.impactDescription());
        assertFalse(summary.impactDescription().isBlank());
    }

    @Test
    void testCalculateXpImprovement() {
        // Arrange
        SimulationInput baseInput = createTestInput(50.0, 60.0, createMediumDistribution());
        SimulationResult baseResult = simulationService.simulate(baseInput);

        // Create scenario with higher effort
        SimulationInput improvedInput = new SimulationInput(
            baseInput.getCurrentStats(),
            baseInput.getHabitsConsistencyScore(),
            90.0, // Higher effort
            baseInput.getDifficultyDistribution(),
            baseInput.getActiveGoals(),
            baseInput.getBurnoutWarning(),
            baseInput.getActiveDaysLastMonth(),
            baseInput.getAverageStreakLength(),
            baseInput.getYearsToSimulate()
        );

        GeneratedScenario scenario = new GeneratedScenario(
            "Higher Effort",
            List.of(),
            improvedInput,
            "Increased effort",
            "More XP"
        );

        // Act
        List<ScenarioImpactSummary> summaries = evaluationService.evaluateScenarios(
            baseInput, baseResult, List.of(scenario));

        // Assert
        assertFalse(summaries.isEmpty());
        ScenarioImpactSummary summary = summaries.get(0);
        
        // Should calculate improvement percentage
        assertNotNull(summary.xpImprovementPercentage());
        // Improvement might be positive (if sustainable) or negative (if burnout increases)
        // So we just check it's calculated
    }

    @Test
    void testEvaluateMultipleScenarios() {
        // Arrange
        SimulationInput baseInput = createTestInput(60.0, 70.0, createMediumDistribution());
        SimulationResult baseResult = simulationService.simulate(baseInput);

        GeneratedScenario scenario1 = new GeneratedScenario(
            "Scenario 1",
            List.of(),
            baseInput, // Same as base for simplicity
            "Test 1",
            "Benefit 1"
        );

        GeneratedScenario scenario2 = new GeneratedScenario(
            "Scenario 2",
            List.of(),
            baseInput, // Same as base for simplicity
            "Test 2",
            "Benefit 2"
        );

        // Act
        List<ScenarioImpactSummary> summaries = evaluationService.evaluateScenarios(
            baseInput, baseResult, List.of(scenario1, scenario2));

        // Assert
        assertNotNull(summaries);
        assertEquals(2, summaries.size());
        assertNotNull(summaries.get(0));
        assertNotNull(summaries.get(1));
    }

    @Test
    void testNullInputThrowsException() {
        // Arrange
        SimulationInput baseInput = createTestInput(60.0, 70.0, createMediumDistribution());
        SimulationResult baseResult = simulationService.simulate(baseInput);
        GeneratedScenario scenario = new GeneratedScenario(
            "Test",
            List.of(),
            baseInput,
            "Test",
            "Test"
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            evaluationService.evaluateScenarios(null, baseResult, List.of(scenario));
        });
    }

    // Helper methods
    private SimulationInput createTestInput(double consistency, double dailyEffort, Map<Difficulty, Integer> distribution) {
        return new SimulationInput(
            userStats,
            consistency,
            dailyEffort,
            distribution,
            List.of(),
            new BurnoutWarning(false, List.of(), 0.0),
            20,
            10.0,
            3
        );
    }

    private Map<Difficulty, Integer> createMediumDistribution() {
        Map<Difficulty, Integer> dist = new HashMap<>();
        dist.put(Difficulty.ONE, 1);
        dist.put(Difficulty.TWO, 1);
        dist.put(Difficulty.THREE, 2);
        dist.put(Difficulty.FOUR, 0);
        dist.put(Difficulty.FIVE, 0);
        return dist;
    }
}

