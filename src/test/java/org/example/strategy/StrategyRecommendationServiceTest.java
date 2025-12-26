package org.example.strategy;

import org.example.simulation.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StrategyRecommendationService.
 * Tests ensure recommendations are generated correctly based on simulation results.
 */
class StrategyRecommendationServiceTest {
    private StrategyRecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new StrategyRecommendationService();
    }

    @Test
    void testRecommendationsForHighBurnoutRisk() {
        // Arrange: Simulation result with high burnout risk
        List<YearlyProjection> projections = List.of(
            new YearlyProjection(1, 1000, 5, 70.0, 25.0),
            new YearlyProjection(2, 1500, 6, 65.0, 15.0)
        );
        
        SimulationResult result = new SimulationResult(
            projections,
            67.5,
            BurnoutRisk.HIGH,
            new IncomeRange(50000, 70000, 90000),
            45.0,
            "Test explanation"
        );

        // Act
        List<Recommendation> recommendations = recommendationService.generateRecommendations(result);

        // Assert
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty(), "Should generate recommendations for high burnout risk");
        
        // Should have at least one burnout-related recommendation
        boolean hasBurnoutRecommendation = recommendations.stream()
            .anyMatch(r -> r.type() == RecommendationType.REDUCE_BURNOUT_RISK);
        assertTrue(hasBurnoutRecommendation, "Should recommend reducing burnout risk");
        
        // Recommendations should be sorted by priority (highest first)
        for (int i = 0; i < recommendations.size() - 1; i++) {
            assertTrue(recommendations.get(i).priorityScore() >= recommendations.get(i + 1).priorityScore(),
                "Recommendations should be sorted by priority score (highest first)");
        }
    }

    @Test
    void testRecommendationsForLowSkillGrowth() {
        // Arrange: Simulation result with low skill growth
        List<YearlyProjection> projections = List.of(
            new YearlyProjection(1, 500, 3, 25.0, 10.0),
            new YearlyProjection(2, 700, 4, 22.0, 8.0)
        );
        
        SimulationResult result = new SimulationResult(
            projections,
            23.5, // Low average skill growth
            BurnoutRisk.LOW,
            new IncomeRange(30000, 40000, 50000),
            20.0,
            "Test explanation"
        );

        // Act
        List<Recommendation> recommendations = recommendationService.generateRecommendations(result);

        // Assert
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        
        // Should recommend adding goal focus for low skill growth
        boolean hasGoalRecommendation = recommendations.stream()
            .anyMatch(r -> r.type() == RecommendationType.ADD_GOAL_FOCUS);
        assertTrue(hasGoalRecommendation, "Should recommend adding goal focus for low skill growth");
        
        // Very low skill growth should also suggest adding habits
        boolean hasHabitRecommendation = recommendations.stream()
            .anyMatch(r -> r.type() == RecommendationType.ADD_HABITS_FOR_GROWTH);
        assertTrue(hasHabitRecommendation, "Very low skill growth should suggest adding habits");
    }

    @Test
    void testRecommendationsForDecliningXpGrowth() {
        // Arrange: Simulation with declining XP growth
        List<YearlyProjection> projections = List.of(
            new YearlyProjection(1, 800, 5, 50.0, 20.0),
            new YearlyProjection(2, 900, 5, 45.0, -15.0) // Declining growth
        );
        
        SimulationResult result = new SimulationResult(
            projections,
            47.5,
            BurnoutRisk.MEDIUM,
            new IncomeRange(50000, 60000, 70000),
            35.0,
            "Test explanation"
        );

        // Act
        List<Recommendation> recommendations = recommendationService.generateRecommendations(result);

        // Assert
        assertNotNull(recommendations);
        
        // Should recommend improving consistency for declining growth
        boolean hasConsistencyRecommendation = recommendations.stream()
            .anyMatch(r -> r.type() == RecommendationType.IMPROVE_CONSISTENCY && 
                         r.description().toLowerCase().contains("consistency"));
        assertTrue(hasConsistencyRecommendation, "Should recommend improving consistency for declining growth");
    }

    @Test
    void testRecommendationsForSkillPlateau() {
        // Arrange: Skill growth declining over time
        List<YearlyProjection> projections = List.of(
            new YearlyProjection(1, 1000, 6, 65.0, 25.0),
            new YearlyProjection(2, 1300, 7, 55.0, 20.0),
            new YearlyProjection(3, 1500, 7, 45.0, 10.0) // Declining skill growth
        );
        
        SimulationResult result = new SimulationResult(
            projections,
            55.0,
            BurnoutRisk.LOW,
            new IncomeRange(60000, 75000, 90000),
            40.0,
            "Test explanation"
        );

        // Act
        List<Recommendation> recommendations = recommendationService.generateRecommendations(result);

        // Assert
        assertNotNull(recommendations);
        
        // Should recommend adding new goals to prevent plateau
        boolean hasPlateauRecommendation = recommendations.stream()
            .anyMatch(r -> r.type() == RecommendationType.ADD_GOAL_FOCUS && 
                         (r.reason().toLowerCase().contains("plateau") || 
                          r.reason().toLowerCase().contains("declining")));
        assertTrue(hasPlateauRecommendation, "Should recommend adding goals for skill plateau");
    }

    @Test
    void testRecommendationsIncludeAllRequiredFields() {
        // Arrange
        List<YearlyProjection> projections = List.of(
            new YearlyProjection(1, 600, 4, 40.0, 15.0)
        );
        
        SimulationResult result = new SimulationResult(
            projections,
            40.0,
            BurnoutRisk.LOW,
            new IncomeRange(40000, 50000, 60000),
            30.0,
            "Test explanation"
        );

        // Act
        List<Recommendation> recommendations = recommendationService.generateRecommendations(result);

        // Assert: All recommendations should have all required fields
        for (Recommendation rec : recommendations) {
            assertNotNull(rec.type(), "Recommendation type should not be null");
            assertNotNull(rec.description(), "Description should not be null");
            assertFalse(rec.description().isBlank(), "Description should not be blank");
            assertNotNull(rec.reason(), "Reason should not be null");
            assertFalse(rec.reason().isBlank(), "Reason should not be blank");
            assertNotNull(rec.expectedBenefit(), "Expected benefit should not be null");
            assertFalse(rec.expectedBenefit().isBlank(), "Expected benefit should not be blank");
            assertNotNull(rec.riskNote(), "Risk note should not be null");
            assertNotNull(rec.impact(), "Impact should not be null");
            assertTrue(rec.priorityScore() >= 0.0 && rec.priorityScore() <= 100.0,
                "Priority score should be between 0 and 100");
        }
    }

    @Test
    void testRecommendationsAreRankedByPriority() {
        // Arrange: Complex scenario with multiple issues
        List<YearlyProjection> projections = List.of(
            new YearlyProjection(1, 500, 3, 20.0, 5.0),
            new YearlyProjection(2, 550, 3, 18.0, -10.0)
        );
        
        SimulationResult result = new SimulationResult(
            projections,
            19.0,
            BurnoutRisk.HIGH,
            new IncomeRange(30000, 40000, 50000),
            25.0,
            "Test explanation"
        );

        // Act
        List<Recommendation> recommendations = recommendationService.generateRecommendations(result);

        // Assert: Should be sorted by priority (highest first)
        assertTrue(recommendations.size() > 1, "Should generate multiple recommendations");
        
        for (int i = 0; i < recommendations.size() - 1; i++) {
            double currentPriority = recommendations.get(i).priorityScore();
            double nextPriority = recommendations.get(i + 1).priorityScore();
            assertTrue(currentPriority >= nextPriority,
                String.format("Recommendations should be sorted by priority. " +
                    "Position %d has priority %.1f, position %d has priority %.1f",
                    i, currentPriority, i + 1, nextPriority));
        }
    }

    @Test
    void testRecommendationsForMediumBurnoutRisk() {
        // Arrange
        List<YearlyProjection> projections = List.of(
            new YearlyProjection(1, 800, 5, 55.0, 20.0)
        );
        
        SimulationResult result = new SimulationResult(
            projections,
            55.0,
            BurnoutRisk.MEDIUM,
            new IncomeRange(50000, 60000, 70000),
            35.0,
            "Test explanation"
        );

        // Act
        List<Recommendation> recommendations = recommendationService.generateRecommendations(result);

        // Assert
        assertNotNull(recommendations);
        
        // Should recommend balancing effort for medium burnout risk
        boolean hasBalanceRecommendation = recommendations.stream()
            .anyMatch(r -> r.type() == RecommendationType.BALANCE_EFFORT);
        assertTrue(hasBalanceRecommendation, "Should recommend balancing effort for medium burnout risk");
    }

    @Test
    void testRecommendationsForNullInput() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            recommendationService.generateRecommendations(null);
        }, "Should throw exception for null input");
    }
}

