package org.example.strategy.scenario;

import org.example.*;
import org.example.simulation.engine.FutureSimulationService;
import org.example.simulation.model.*;
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
 * Tests ensure scenarios are generated correctly and evaluated properly.
 */
class ScenarioGeneratorServiceTest {
    private ScenarioGeneratorService scenarioService;
    private SimulationInput baseInput;

    @BeforeEach
    void setUp() {
        scenarioService = new ScenarioGeneratorService();
        
        // Create a base input for testing
        UserStats userStats = new UserStats(500, 5);
        double consistencyScore = 70.0;
        double averageDailyEffort = 80.0;
        
        Map<Difficulty, Integer> difficultyDistribution = new HashMap<>();
        difficultyDistribution.put(Difficulty.ONE, 1);
        difficultyDistribution.put(Difficulty.TWO, 2);
        difficultyDistribution.put(Difficulty.THREE, 2);
        difficultyDistribution.put(Difficulty.FOUR, 1);
        difficultyDistribution.put(Difficulty.FIVE, 0);
        
        List<Goal> activeGoals = List.of(
            new Goal("Test Goal", "Description", LocalDate.now(), LocalDate.now().plusMonths(6), 4, 100)
        );
        
        BurnoutWarning burnoutWarning = new BurnoutWarning(false, List.of(), 0.0);
        int activeDaysLastMonth = 25;
        double averageStreakLength = 10.0;
        int yearsToSimulate = 3;

        baseInput = new SimulationInput(
            userStats, consistencyScore, averageDailyEffort, difficultyDistribution,
            activeGoals, burnoutWarning, activeDaysLastMonth, averageStreakLength, yearsToSimulate
        );
    }

    @Test
    void testGenerateScenarioForBurnoutReduction() {
        // Arrange
        Recommendation recommendation = new Recommendation(
            RecommendationType.REDUCE_BURNOUT_RISK,
            "Reduce effort to prevent burnout",
            "High burnout risk detected",
            "Lower burnout risk",
            "May slow growth",
            RecommendationImpact.HIGH,
            90.0
        );

        // Act
        List<ScenarioImpactSummary> summaries = scenarioService.generateAndEvaluateScenarios(
            baseInput, List.of(recommendation)
        );

        // Assert
        assertNotNull(summaries);
        assertFalse(summaries.isEmpty(), "Should generate at least one scenario");
        
        ScenarioImpactSummary summary = summaries.get(0);
        assertNotNull(summary.getScenario());
        assertEquals("Reduced Burnout Risk Scenario", summary.getScenario().getScenarioName());
        assertTrue(summary.getScenario().getParameterChanges().containsKey("averageDailyEffort"),
            "Should reduce daily effort");
    }

    @Test
    void testGenerateScenarioForConsistencyImprovement() {
        // Arrange
        Recommendation recommendation = new Recommendation(
            RecommendationType.IMPROVE_CONSISTENCY,
            "Improve consistency",
            "Low consistency detected",
            "Better growth",
            "None",
            RecommendationImpact.MEDIUM,
            75.0
        );

        // Act
        List<ScenarioImpactSummary> summaries = scenarioService.generateAndEvaluateScenarios(
            baseInput, List.of(recommendation)
        );

        // Assert
        assertNotNull(summaries);
        assertFalse(summaries.isEmpty());
        
        ScenarioImpactSummary summary = summaries.get(0);
        GeneratedScenario scenario = summary.getScenario();
        assertTrue(scenario.getParameterChanges().containsKey("habitsConsistencyScore"),
            "Should improve consistency score");
        
        // Verify consistency was improved
        double originalConsistency = baseInput.getHabitsConsistencyScore();
        double newConsistency = scenario.getModifiedInput().getHabitsConsistencyScore();
        assertTrue(newConsistency > originalConsistency,
            "Consistency should be improved");
    }

    @Test
    void testGenerateScenarioForGoalFocus() {
        // Arrange
        Recommendation recommendation = new Recommendation(
            RecommendationType.ADD_GOAL_FOCUS,
            "Add goal focus",
            "Low skill growth",
            "Better skill development",
            "Requires commitment",
            RecommendationImpact.HIGH,
            80.0
        );

        // Act
        List<ScenarioImpactSummary> summaries = scenarioService.generateAndEvaluateScenarios(
            baseInput, List.of(recommendation)
        );

        // Assert
        assertNotNull(summaries);
        assertFalse(summaries.isEmpty());
        
        ScenarioImpactSummary summary = summaries.get(0);
        GeneratedScenario scenario = summary.getScenario();
        assertTrue(scenario.getParameterChanges().containsKey("activeGoals"),
            "Should indicate goal-related changes");
    }

    @Test
    void testScenarioImpactSummaryContainsAllFields() {
        // Arrange
        Recommendation recommendation = new Recommendation(
            RecommendationType.IMPROVE_CONSISTENCY,
            "Improve consistency",
            "Test reason",
            "Test benefit",
            "Test risk",
            RecommendationImpact.MEDIUM,
            70.0
        );

        // Act
        List<ScenarioImpactSummary> summaries = scenarioService.generateAndEvaluateScenarios(
            baseInput, List.of(recommendation)
        );

        // Assert
        assertFalse(summaries.isEmpty());
        ScenarioImpactSummary summary = summaries.get(0);
        
        assertNotNull(summary.getScenario(), "Scenario should not be null");
        assertNotNull(summary.getBaseResult(), "Base result should not be null");
        assertNotNull(summary.getImprovedResult(), "Improved result should not be null");
        assertNotNull(summary.getBurnoutRiskChange(), "Burnout risk change should not be null");
        assertNotNull(summary.getImprovementDescription(), "Improvement description should not be null");
        assertFalse(summary.getImprovementDescription().isBlank(),
            "Improvement description should not be blank");
    }

    @Test
    void testScenariosAreSortedByImprovement() {
        // Arrange: Multiple recommendations with different priorities
        List<Recommendation> recommendations = List.of(
            new Recommendation(
                RecommendationType.IMPROVE_CONSISTENCY,
                "Improve consistency",
                "Low consistency",
                "Better growth",
                "None",
                RecommendationImpact.MEDIUM,
                65.0
            ),
            new Recommendation(
                RecommendationType.REDUCE_BURNOUT_RISK,
                "Reduce burnout",
                "High burnout risk",
                "Sustainability",
                "May slow growth",
                RecommendationImpact.HIGH,
                90.0
            ),
            new Recommendation(
                RecommendationType.ADD_GOAL_FOCUS,
                "Add goals",
                "Low skill growth",
                "Skill development",
                "Commitment required",
                RecommendationImpact.HIGH,
                85.0
            )
        );

        // Act
        List<ScenarioImpactSummary> summaries = scenarioService.generateAndEvaluateScenarios(
            baseInput, recommendations
        );

        // Assert: Should be sorted by XP improvement (highest first)
        assertTrue(summaries.size() >= 2, "Should generate multiple scenarios");
        
        for (int i = 0; i < summaries.size() - 1; i++) {
            double currentImprovement = summaries.get(i).getXpImprovement();
            double nextImprovement = summaries.get(i + 1).getXpImprovement();
            assertTrue(currentImprovement >= nextImprovement,
                String.format("Scenarios should be sorted by improvement. " +
                    "Position %d has %.1f%%, position %d has %.1f%%",
                    i, currentImprovement, i + 1, nextImprovement));
        }
    }

    @Test
    void testScenarioRationaleIsGenerated() {
        // Arrange
        Recommendation recommendation = new Recommendation(
            RecommendationType.IMPROVE_CONSISTENCY,
            "Improve consistency",
            "Test reason for recommendation",
            "Expected benefit",
            "Risk note",
            RecommendationImpact.MEDIUM,
            70.0
        );

        // Act
        List<ScenarioImpactSummary> summaries = scenarioService.generateAndEvaluateScenarios(
            baseInput, List.of(recommendation)
        );

        // Assert
        assertFalse(summaries.isEmpty());
        GeneratedScenario scenario = summaries.get(0).getScenario();
        
        assertNotNull(scenario.getRationale());
        assertFalse(scenario.getRationale().isBlank());
        assertTrue(scenario.getRationale().contains("Test reason"),
            "Rationale should include recommendation reason");
    }

    @Test
    void testNullInputThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            scenarioService.generateAndEvaluateScenarios(null, List.of());
        }, "Should throw exception for null input");
    }

    @Test
    void testEmptyRecommendationsThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            scenarioService.generateAndEvaluateScenarios(baseInput, List.of());
        }, "Should throw exception for empty recommendations");
    }

    @Test
    void testHabitAdditionScenario() {
        // Arrange
        Recommendation recommendation = new Recommendation(
            RecommendationType.ADD_HABITS_FOR_GROWTH,
            "Add habits for growth",
            "Low skill growth",
            "Increased XP",
            "More commitment needed",
            RecommendationImpact.MEDIUM,
            75.0
        );

        // Act
        List<ScenarioImpactSummary> summaries = scenarioService.generateAndEvaluateScenarios(
            baseInput, List.of(recommendation)
        );

        // Assert
        assertFalse(summaries.isEmpty());
        GeneratedScenario scenario = summaries.get(0).getScenario();
        
        // Check that a habit was added
        assertTrue(scenario.getParameterChanges().containsKey("difficultyDistribution"),
            "Should indicate habit addition");
        
        // Check that effort increased (new habit = more XP)
        double originalEffort = baseInput.getAverageDailyEffort();
        double newEffort = scenario.getModifiedInput().getAverageDailyEffort();
        assertTrue(newEffort > originalEffort,
            "Adding a habit should increase daily effort");
    }
}

