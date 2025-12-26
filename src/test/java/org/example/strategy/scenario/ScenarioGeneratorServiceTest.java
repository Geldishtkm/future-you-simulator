package org.example.strategy.scenario;

import org.example.*;
import org.example.simulation.model.SimulationInput;
import org.example.strategy.Recommendation;
import org.example.strategy.RecommendationImpact;
import org.example.strategy.RecommendationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScenarioGeneratorService.
 */
class ScenarioGeneratorServiceTest {
    private ScenarioGeneratorService scenarioGenerator;
    private UserStats userStats;

    @BeforeEach
    void setUp() {
        scenarioGenerator = new ScenarioGeneratorService();
        LevelCalculator levelCalculator = new LevelCalculator();
        userStats = new UserStats(500, levelCalculator.calculateLevel(500));
    }

    @Test
    void testGenerateBurnoutReductionScenario() {
        // Arrange
        SimulationInput baseInput = createTestInput(80.0, 120.0, createHighDifficultyDistribution());
        Recommendation recommendation = new Recommendation(
            RecommendationType.REDUCE_BURNOUT_RISK,
            "Reduce effort",
            "High burnout risk",
            "Lower burnout",
            "May slow growth",
            RecommendationImpact.HIGH,
            90.0
        );

        // Act
        List<GeneratedScenario> scenarios = scenarioGenerator.generateScenarios(baseInput, List.of(recommendation));

        // Assert
        assertNotNull(scenarios);
        assertFalse(scenarios.isEmpty());
        
        GeneratedScenario scenario = scenarios.get(0);
        assertEquals("Burnout Risk Reduction", scenario.name());
        assertTrue(scenario.modifiedInput().getAverageDailyEffort() < baseInput.getAverageDailyEffort(),
            "Effort should be reduced");
        assertFalse(scenario.modifiedInput().getBurnoutWarning().isWarningActive(),
            "Burnout warning should be improved");
    }

    @Test
    void testGenerateConsistencyImprovementScenario() {
        // Arrange
        SimulationInput baseInput = createTestInput(50.0, 60.0, createMediumDifficultyDistribution());
        Recommendation recommendation = new Recommendation(
            RecommendationType.IMPROVE_CONSISTENCY,
            "Improve consistency",
            "Low consistency",
            "Better growth",
            "None",
            RecommendationImpact.MEDIUM,
            75.0
        );

        // Act
        List<GeneratedScenario> scenarios = scenarioGenerator.generateScenarios(baseInput, List.of(recommendation));

        // Assert
        assertNotNull(scenarios);
        assertFalse(scenarios.isEmpty());
        
        GeneratedScenario scenario = scenarios.get(0);
        assertEquals("Consistency Improvement", scenario.name());
        assertTrue(scenario.modifiedInput().getHabitsConsistencyScore() > baseInput.getHabitsConsistencyScore(),
            "Consistency should be improved");
        assertTrue(scenario.modifiedInput().getActiveDaysLastMonth() > baseInput.getActiveDaysLastMonth(),
            "Active days should increase");
    }

    @Test
    void testGenerateGoalFocusScenario() {
        // Arrange
        SimulationInput baseInput = createTestInput(60.0, 70.0, createMediumDifficultyDistribution());
        Recommendation recommendation = new Recommendation(
            RecommendationType.ADD_GOAL_FOCUS,
            "Add goal focus",
            "Low skill growth",
            "Better skills",
            "None",
            RecommendationImpact.HIGH,
            80.0
        );

        // Act
        List<GeneratedScenario> scenarios = scenarioGenerator.generateScenarios(baseInput, List.of(recommendation));

        // Assert
        assertNotNull(scenarios);
        assertFalse(scenarios.isEmpty());
        
        GeneratedScenario scenario = scenarios.get(0);
        assertEquals("Goal Focus Enhancement", scenario.name());
        assertTrue(scenario.modifiedInput().getAverageDailyEffort() > baseInput.getAverageDailyEffort(),
            "Effort should increase for goal focus");
    }

    @Test
    void testGenerateMultipleScenarios() {
        // Arrange
        SimulationInput baseInput = createTestInput(70.0, 100.0, createHighDifficultyDistribution());
        List<Recommendation> recommendations = List.of(
            new Recommendation(
                RecommendationType.REDUCE_BURNOUT_RISK,
                "Reduce burnout",
                "High risk",
                "Sustainability",
                "None",
                RecommendationImpact.HIGH,
                90.0
            ),
            new Recommendation(
                RecommendationType.IMPROVE_CONSISTENCY,
                "Improve consistency",
                "Can improve",
                "Better growth",
                "None",
                RecommendationImpact.MEDIUM,
                70.0
            )
        );

        // Act
        List<GeneratedScenario> scenarios = scenarioGenerator.generateScenarios(baseInput, recommendations);

        // Assert
        assertNotNull(scenarios);
        assertTrue(scenarios.size() >= 2, "Should generate multiple scenarios");
        
        // Should have individual scenarios
        boolean hasBurnoutScenario = scenarios.stream()
            .anyMatch(s -> s.name().equals("Burnout Risk Reduction"));
        assertTrue(hasBurnoutScenario, "Should have burnout reduction scenario");
        
        boolean hasConsistencyScenario = scenarios.stream()
            .anyMatch(s -> s.name().equals("Consistency Improvement"));
        assertTrue(hasConsistencyScenario, "Should have consistency improvement scenario");
        
        // May also have combined scenario
        boolean hasCombined = scenarios.stream()
            .anyMatch(s -> s.name().equals("Combined Improvements"));
        // Combined scenario is optional, so we just check if scenarios exist
    }

    @Test
    void testGeneratedScenarioHasAllRequiredFields() {
        // Arrange
        SimulationInput baseInput = createTestInput(60.0, 80.0, createMediumDifficultyDistribution());
        Recommendation recommendation = new Recommendation(
            RecommendationType.IMPROVE_CONSISTENCY,
            "Test",
            "Test reason",
            "Test benefit",
            "Test risk",
            RecommendationImpact.MEDIUM,
            70.0
        );

        // Act
        List<GeneratedScenario> scenarios = scenarioGenerator.generateScenarios(baseInput, List.of(recommendation));

        // Assert
        assertFalse(scenarios.isEmpty());
        GeneratedScenario scenario = scenarios.get(0);
        
        assertNotNull(scenario.name());
        assertFalse(scenario.name().isBlank());
        assertNotNull(scenario.appliedRecommendations());
        assertFalse(scenario.appliedRecommendations().isEmpty());
        assertNotNull(scenario.modifiedInput());
        assertNotNull(scenario.rationale());
        assertFalse(scenario.rationale().isBlank());
        assertNotNull(scenario.expectedLongTermBenefit());
        assertFalse(scenario.expectedLongTermBenefit().isBlank());
    }

    @Test
    void testNullInputThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            scenarioGenerator.generateScenarios(null, List.of());
        });
    }

    @Test
    void testEmptyRecommendationsThrowsException() {
        // Arrange
        SimulationInput baseInput = createTestInput(60.0, 80.0, createMediumDifficultyDistribution());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            scenarioGenerator.generateScenarios(baseInput, List.of());
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

    private Map<Difficulty, Integer> createHighDifficultyDistribution() {
        Map<Difficulty, Integer> dist = new HashMap<>();
        dist.put(Difficulty.ONE, 1);
        dist.put(Difficulty.TWO, 1);
        dist.put(Difficulty.THREE, 1);
        dist.put(Difficulty.FOUR, 2);
        dist.put(Difficulty.FIVE, 1);
        return dist;
    }

    private Map<Difficulty, Integer> createMediumDifficultyDistribution() {
        Map<Difficulty, Integer> dist = new HashMap<>();
        dist.put(Difficulty.ONE, 2);
        dist.put(Difficulty.TWO, 2);
        dist.put(Difficulty.THREE, 2);
        dist.put(Difficulty.FOUR, 0);
        dist.put(Difficulty.FIVE, 0);
        return dist;
    }
}

