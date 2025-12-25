package org.example.simulation.engine;

import org.example.*;
import org.example.simulation.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FutureSimulationService.
 * Tests ensure deterministic behavior and validate core simulation logic.
 */
class FutureSimulationServiceTest {
    private FutureSimulationService simulationService;
    private LevelCalculator levelCalculator;

    @BeforeEach
    void setUp() {
        levelCalculator = new LevelCalculator();
        simulationService = new FutureSimulationService();
    }

    @Test
    void testSimulationWithHighConsistency() {
        // Arrange: User with high consistency (90%), good habits
        UserStats userStats = new UserStats(500, levelCalculator.calculateLevel(500));
        double consistencyScore = 90.0;
        double averageDailyEffort = 80.0;
        
        Map<Difficulty, Integer> difficultyDistribution = new HashMap<>();
        difficultyDistribution.put(Difficulty.THREE, 2);
        difficultyDistribution.put(Difficulty.FOUR, 1);
        
        List<Goal> activeGoals = List.of(
            new Goal("Test Goal", "Description", LocalDate.now(), LocalDate.now().plusMonths(6), 5, 100)
        );
        
        BurnoutWarning burnoutWarning = new BurnoutWarning(false, List.of(), 0.0);
        int activeDaysLastMonth = 25;
        double averageStreakLength = 15.0;
        int yearsToSimulate = 3;

        SimulationInput input = new SimulationInput(
            userStats, consistencyScore, averageDailyEffort, difficultyDistribution,
            activeGoals, burnoutWarning, activeDaysLastMonth, averageStreakLength, yearsToSimulate
        );

        // Act
        SimulationResult result = simulationService.simulate(input);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getYearlyProjections().size());
        
        // Check that XP increases over time
        YearlyProjection year1 = result.getYearlyProjections().get(0);
        YearlyProjection year3 = result.getYearlyProjections().get(2);
        assertTrue(year3.getProjectedXp() > year1.getProjectedXp(), 
            "XP should increase over time with high consistency");
        assertTrue(year3.getProjectedLevel() >= year1.getProjectedLevel(),
            "Level should increase or stay same over time");
        
        // High consistency should lead to LOW burnout risk
        assertEquals(BurnoutRisk.LOW, result.getBurnoutRisk(),
            "High consistency should result in low burnout risk");
        
        // Check explanation is generated
        assertNotNull(result.getExplanation());
        assertFalse(result.getExplanation().isBlank());
    }

    @Test
    void testSimulationWithLowConsistency() {
        // Arrange: User with low consistency (30%), few active days
        UserStats userStats = new UserStats(200, levelCalculator.calculateLevel(200));
        double consistencyScore = 30.0;
        double averageDailyEffort = 40.0;
        
        Map<Difficulty, Integer> difficultyDistribution = new HashMap<>();
        difficultyDistribution.put(Difficulty.ONE, 1);
        difficultyDistribution.put(Difficulty.TWO, 1);
        
        List<Goal> activeGoals = List.of();
        BurnoutWarning burnoutWarning = new BurnoutWarning(false, List.of(), 0.0);
        int activeDaysLastMonth = 8;
        double averageStreakLength = 3.0;
        int yearsToSimulate = 2;

        SimulationInput input = new SimulationInput(
            userStats, consistencyScore, averageDailyEffort, difficultyDistribution,
            activeGoals, burnoutWarning, activeDaysLastMonth, averageStreakLength, yearsToSimulate
        );

        // Act
        SimulationResult result = simulationService.simulate(input);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getYearlyProjections().size());
        
        // Low consistency should result in slower growth
        YearlyProjection year1 = result.getYearlyProjections().get(0);
        YearlyProjection year2 = result.getYearlyProjections().get(1);
        assertTrue(year2.getProjectedXp() > year1.getProjectedXp(),
            "XP should still increase but more slowly");
        
        // Skill growth index should be lower
        assertTrue(result.getAverageSkillGrowthIndex() < 50.0,
            "Low consistency should result in lower skill growth index");
    }

    @Test
    void testSimulationWithBurnoutWarning() {
        // Arrange: User with existing burnout warning
        UserStats userStats = new UserStats(800, levelCalculator.calculateLevel(800));
        double consistencyScore = 95.0; // High consistency but burnout risk
        double averageDailyEffort = 150.0; // Very high effort
        
        Map<Difficulty, Integer> difficultyDistribution = new HashMap<>();
        difficultyDistribution.put(Difficulty.FIVE, 3); // Many high-difficulty habits
        
        List<Goal> activeGoals = List.of(
            new Goal("Goal 1", "Desc", LocalDate.now(), LocalDate.now().plusMonths(3), 5, 100),
            new Goal("Goal 2", "Desc", LocalDate.now(), LocalDate.now().plusMonths(4), 5, 100)
        );
        
        BurnoutWarning burnoutWarning = new BurnoutWarning(true, 
            List.of("High activity", "Frequent cap hits"), 75.0);
        int activeDaysLastMonth = 30;
        double averageStreakLength = 25.0;
        int yearsToSimulate = 2;

        SimulationInput input = new SimulationInput(
            userStats, consistencyScore, averageDailyEffort, difficultyDistribution,
            activeGoals, burnoutWarning, activeDaysLastMonth, averageStreakLength, yearsToSimulate
        );

        // Act
        SimulationResult result = simulationService.simulate(input);

        // Assert
        assertNotNull(result);
        // Burnout warning should result in MEDIUM or HIGH burnout risk
        assertTrue(result.getBurnoutRisk() == BurnoutRisk.MEDIUM || 
                  result.getBurnoutRisk() == BurnoutRisk.HIGH,
            "Existing burnout warning should increase burnout risk");
    }

    @Test
    void testSimulationDeterministic() {
        // Arrange: Same input should produce same output
        UserStats userStats = new UserStats(300, levelCalculator.calculateLevel(300));
        double consistencyScore = 70.0;
        double averageDailyEffort = 60.0;
        
        Map<Difficulty, Integer> difficultyDistribution = new HashMap<>();
        difficultyDistribution.put(Difficulty.THREE, 2);
        
        List<Goal> activeGoals = List.of();
        BurnoutWarning burnoutWarning = new BurnoutWarning(false, List.of(), 0.0);
        int activeDaysLastMonth = 20;
        double averageStreakLength = 10.0;
        int yearsToSimulate = 2;

        SimulationInput input = new SimulationInput(
            userStats, consistencyScore, averageDailyEffort, difficultyDistribution,
            activeGoals, burnoutWarning, activeDaysLastMonth, averageStreakLength, yearsToSimulate
        );

        // Act: Run simulation twice
        SimulationResult result1 = simulationService.simulate(input);
        SimulationResult result2 = simulationService.simulate(input);

        // Assert: Results should be identical (deterministic)
        assertEquals(result1.getYearlyProjections().size(), result2.getYearlyProjections().size());
        for (int i = 0; i < result1.getYearlyProjections().size(); i++) {
            YearlyProjection p1 = result1.getYearlyProjections().get(i);
            YearlyProjection p2 = result2.getYearlyProjections().get(i);
            assertEquals(p1.getProjectedXp(), p2.getProjectedXp(),
                "Projected XP should be deterministic");
            assertEquals(p1.getProjectedLevel(), p2.getProjectedLevel(),
                "Projected level should be deterministic");
        }
        assertEquals(result1.getBurnoutRisk(), result2.getBurnoutRisk());
        assertEquals(result1.getIncomeRange().getExpectedEstimate(), 
                    result2.getIncomeRange().getExpectedEstimate());
    }

    @Test
    void testSimulationIncomeProjection() {
        // Arrange: User with good progression
        UserStats userStats = new UserStats(1000, levelCalculator.calculateLevel(1000));
        double consistencyScore = 85.0;
        double averageDailyEffort = 100.0;
        
        Map<Difficulty, Integer> difficultyDistribution = new HashMap<>();
        difficultyDistribution.put(Difficulty.FOUR, 2);
        
        List<Goal> activeGoals = List.of();
        BurnoutWarning burnoutWarning = new BurnoutWarning(false, List.of(), 0.0);
        int activeDaysLastMonth = 28;
        double averageStreakLength = 20.0;
        int yearsToSimulate = 1;

        SimulationInput input = new SimulationInput(
            userStats, consistencyScore, averageDailyEffort, difficultyDistribution,
            activeGoals, burnoutWarning, activeDaysLastMonth, averageStreakLength, yearsToSimulate
        );

        // Act
        SimulationResult result = simulationService.simulate(input);

        // Assert: Income range should have valid values
        IncomeRange income = result.getIncomeRange();
        assertTrue(income.getLowEstimate() > 0, "Low estimate should be positive");
        assertTrue(income.getExpectedEstimate() >= income.getLowEstimate(),
            "Expected should be >= low");
        assertTrue(income.getHighEstimate() >= income.getExpectedEstimate(),
            "High should be >= expected");
    }

    @Test
    void testSimulationEmigrationProbability() {
        // Arrange: User with high skill growth potential
        UserStats userStats = new UserStats(1500, levelCalculator.calculateLevel(1500));
        double consistencyScore = 90.0;
        double averageDailyEffort = 120.0;
        
        Map<Difficulty, Integer> difficultyDistribution = new HashMap<>();
        difficultyDistribution.put(Difficulty.FIVE, 2);
        
        List<Goal> activeGoals = List.of(
            new Goal("Important Goal", "Desc", LocalDate.now(), LocalDate.now().plusMonths(6), 5, 200)
        );
        
        BurnoutWarning burnoutWarning = new BurnoutWarning(false, List.of(), 0.0);
        int activeDaysLastMonth = 30;
        double averageStreakLength = 25.0;
        int yearsToSimulate = 1;

        SimulationInput input = new SimulationInput(
            userStats, consistencyScore, averageDailyEffort, difficultyDistribution,
            activeGoals, burnoutWarning, activeDaysLastMonth, averageStreakLength, yearsToSimulate
        );

        // Act
        SimulationResult result = simulationService.simulate(input);

        // Assert: Emigration probability should be between 0 and 100
        assertTrue(result.getEmigrationProbability() >= 0.0 && 
                  result.getEmigrationProbability() <= 100.0,
            "Emigration probability should be between 0 and 100");
    }
}

