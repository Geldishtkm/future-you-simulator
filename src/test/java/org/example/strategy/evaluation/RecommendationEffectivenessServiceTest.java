package org.example.strategy.evaluation;

import org.example.*;
import org.example.simulation.engine.FutureSimulationService;
import org.example.simulation.model.*;
import org.example.strategy.Recommendation;
import org.example.strategy.RecommendationImpact;
import org.example.strategy.RecommendationType;
import org.example.strategy.scenario.GeneratedScenario;
import org.example.strategy.scenario.ScenarioImpactSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RecommendationEffectivenessService.
 */
class RecommendationEffectivenessServiceTest {
    private RecommendationEffectivenessService effectivenessService;
    private FutureSimulationService simulationService;

    @BeforeEach
    void setUp() {
        effectivenessService = new RecommendationEffectivenessService();
        simulationService = new FutureSimulationService();
    }

    @Test
    void testEffectivenessEvaluationWithGoodMatch() {
        // Arrange: Create a scenario where actual results match expected results
        Recommendation recommendation = new Recommendation(
            RecommendationType.IMPROVE_CONSISTENCY,
            "Improve consistency",
            "Low consistency detected",
            "Better growth",
            "None",
            RecommendationImpact.MEDIUM,
            75.0
        );

        // Create base and improved simulation results
        SimulationResult baseResult = createTestSimulationResult(1000, 40.0, BurnoutRisk.LOW);
        SimulationResult improvedResult = createTestSimulationResult(1300, 50.0, BurnoutRisk.LOW);
        SimulationResult actualResult = createTestSimulationResult(1280, 49.0, BurnoutRisk.LOW); // Close match

        GeneratedScenario scenario = createTestScenario(recommendation);
        ScenarioImpactSummary expectedImpact = createTestImpactSummary(
            scenario, baseResult, improvedResult, 30.0, 10.0);

        // Act
        EffectivenessEvaluation evaluation = effectivenessService.evaluateEffectiveness(
            expectedImpact, actualResult);

        // Assert
        assertNotNull(evaluation);
        assertTrue(evaluation.getOutcome().effectivenessScore() >= 50.0,
            "Effectiveness score should be reasonable for good match");
        assertNotNull(evaluation.getDeviationReport());
        assertNotNull(evaluation.getExplanation());
        assertFalse(evaluation.getExplanation().isBlank());
    }

    @Test
    void testEffectivenessEvaluationWithPoorMatch() {
        // Arrange: Actual results significantly worse than expected
        Recommendation recommendation = new Recommendation(
            RecommendationType.IMPROVE_CONSISTENCY,
            "Improve consistency",
            "Low consistency",
            "Better growth",
            "None",
            RecommendationImpact.MEDIUM,
            75.0
        );

        SimulationResult baseResult = createTestSimulationResult(1000, 40.0, BurnoutRisk.LOW);
        SimulationResult improvedResult = createTestSimulationResult(1300, 50.0, BurnoutRisk.LOW);
        SimulationResult actualResult = createTestSimulationResult(1050, 41.0, BurnoutRisk.LOW); // Poor match

        GeneratedScenario scenario = createTestScenario(recommendation);
        ScenarioImpactSummary expectedImpact = createTestImpactSummary(
            scenario, baseResult, improvedResult, 30.0, 10.0);

        // Act
        EffectivenessEvaluation evaluation = effectivenessService.evaluateEffectiveness(
            expectedImpact, actualResult);

        // Assert
        assertNotNull(evaluation);
        assertTrue(evaluation.getOutcome().effectivenessScore() < 70.0,
            "Effectiveness score should be lower for poor match");
        
        // Should detect deviation
        DeviationReport deviationReport = evaluation.getDeviationReport();
        assertTrue(deviationReport.getSeverity() != DeviationReport.DeviationSeverity.NONE,
            "Should detect deviation for poor match");
        
        // Should generate learning signals
        assertFalse(evaluation.getLearningSignals().isEmpty(),
            "Should generate learning signals for poor performance");
    }

    @Test
    void testDeviationReportCalculation() {
        // Arrange: Large deviation between expected and actual
        Recommendation recommendation = new Recommendation(
            RecommendationType.ADD_GOAL_FOCUS,
            "Add goal focus",
            "Low skill growth",
            "Better skill development",
            "Commitment required",
            RecommendationImpact.HIGH,
            80.0
        );

        SimulationResult baseResult = createTestSimulationResult(1000, 30.0, BurnoutRisk.LOW);
        SimulationResult improvedResult = createTestSimulationResult(1500, 60.0, BurnoutRisk.LOW);
        SimulationResult actualResult = createTestSimulationResult(1100, 35.0, BurnoutRisk.LOW); // Large deviation

        GeneratedScenario scenario = createTestScenario(recommendation);
        ScenarioImpactSummary expectedImpact = createTestImpactSummary(
            scenario, baseResult, improvedResult, 50.0, 30.0);

        // Act
        EffectivenessEvaluation evaluation = effectivenessService.evaluateEffectiveness(
            expectedImpact, actualResult);

        // Assert
        DeviationReport deviationReport = evaluation.getDeviationReport();
        assertNotNull(deviationReport);
        assertNotNull(deviationReport.getDeviationAnalysis());
        assertFalse(deviationReport.getDeviationAnalysis().isBlank());
        assertNotNull(deviationReport.getMostAffectedMetric());
    }

    @Test
    void testLearningSignalsGeneration() {
        // Arrange: Over-optimistic recommendation scenario
        Recommendation recommendation = new Recommendation(
            RecommendationType.REDUCE_BURNOUT_RISK,
            "Reduce burnout risk",
            "High burnout risk",
            "Sustainability",
            "May slow growth",
            RecommendationImpact.HIGH,
            90.0
        );

        SimulationResult baseResult = createTestSimulationResult(1000, 50.0, BurnoutRisk.HIGH);
        SimulationResult improvedResult = createTestSimulationResult(1200, 55.0, BurnoutRisk.LOW);
        SimulationResult actualResult = createTestSimulationResult(1050, 48.0, BurnoutRisk.MEDIUM); // Underperformed

        GeneratedScenario scenario = createTestScenario(recommendation);
        ScenarioImpactSummary expectedImpact = createTestImpactSummary(
            scenario, baseResult, improvedResult, 20.0, 5.0);

        // Act
        EffectivenessEvaluation evaluation = effectivenessService.evaluateEffectiveness(
            expectedImpact, actualResult);

        // Assert
        List<LearningSignal> signals = evaluation.getLearningSignals();
        assertFalse(signals.isEmpty(), "Should generate learning signals");
        
        // Should include signals about poor performance
        boolean hasRelevantSignal = signals.stream()
            .anyMatch(s -> s == LearningSignal.OVER_OPTIMISTIC_RECOMMENDATION ||
                          s == LearningSignal.LOW_USER_COMPLIANCE ||
                          s == LearningSignal.INCORRECT_MODEL_ASSUMPTION);
        assertTrue(hasRelevantSignal, "Should include signals about poor performance");
    }

    @Test
    void testConfidenceLevelCalculation() {
        // Arrange
        Recommendation recommendation = new Recommendation(
            RecommendationType.IMPROVE_CONSISTENCY,
            "Improve consistency",
            "Test",
            "Test",
            "Test",
            RecommendationImpact.MEDIUM,
            70.0
        );

        SimulationResult baseResult = createTestSimulationResult(1000, 40.0, BurnoutRisk.LOW);
        SimulationResult improvedResult = createTestSimulationResult(1200, 50.0, BurnoutRisk.LOW);
        SimulationResult actualResult = createTestSimulationResult(1180, 48.0, BurnoutRisk.LOW); // Good match

        GeneratedScenario scenario = createTestScenario(recommendation);
        ScenarioImpactSummary expectedImpact = createTestImpactSummary(
            scenario, baseResult, improvedResult, 20.0, 10.0);

        // Act
        EffectivenessEvaluation evaluation = effectivenessService.evaluateEffectiveness(
            expectedImpact, actualResult);

        // Assert
        double confidenceLevel = evaluation.getConfidenceLevel();
        assertTrue(confidenceLevel >= 0.0 && confidenceLevel <= 100.0,
            "Confidence level should be between 0 and 100");
        assertTrue(confidenceLevel >= 50.0,
            "Confidence should be reasonable for good match");
    }

    @Test
    void testEffectivenessEvaluationContainsAllFields() {
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

        SimulationResult baseResult = createTestSimulationResult(1000, 40.0, BurnoutRisk.LOW);
        SimulationResult improvedResult = createTestSimulationResult(1200, 50.0, BurnoutRisk.LOW);
        SimulationResult actualResult = createTestSimulationResult(1150, 48.0, BurnoutRisk.LOW);

        GeneratedScenario scenario = createTestScenario(recommendation);
        ScenarioImpactSummary expectedImpact = createTestImpactSummary(
            scenario, baseResult, improvedResult, 20.0, 10.0);

        // Act
        EffectivenessEvaluation evaluation = effectivenessService.evaluateEffectiveness(
            expectedImpact, actualResult);

        // Assert: All fields should be populated
        assertNotNull(evaluation.getOutcome());
        assertNotNull(evaluation.getDeviationReport());
        assertNotNull(evaluation.getLearningSignals());
        assertNotNull(evaluation.getExplanation());
        assertFalse(evaluation.getExplanation().isBlank());
        assertTrue(evaluation.getConfidenceLevel() >= 0.0 && evaluation.getConfidenceLevel() <= 100.0);
        
        RecommendationOutcome outcome = evaluation.getOutcome();
        assertEquals(recommendation, outcome.recommendation());
        assertEquals(expectedImpact, outcome.expectedImpact());
        assertEquals(actualResult, outcome.actualResult());
        assertTrue(outcome.effectivenessScore() >= 0.0 && outcome.effectivenessScore() <= 100.0);
    }

    @Test
    void testNullInputThrowsException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            effectivenessService.evaluateEffectiveness(null, createTestSimulationResult(1000, 40.0, BurnoutRisk.LOW));
        }, "Should throw exception for null expected impact");

        assertThrows(IllegalArgumentException.class, () -> {
            Recommendation rec = new Recommendation(
                RecommendationType.IMPROVE_CONSISTENCY, "Test", "Test", "Test", "", RecommendationImpact.MEDIUM, 70.0);
            GeneratedScenario scenario = createTestScenario(rec);
            SimulationResult base = createTestSimulationResult(1000, 40.0, BurnoutRisk.LOW);
            SimulationResult improved = createTestSimulationResult(1200, 50.0, BurnoutRisk.LOW);
            ScenarioImpactSummary expected = createTestImpactSummary(scenario, base, improved, 20.0, 10.0);
            effectivenessService.evaluateEffectiveness(expected, null);
        }, "Should throw exception for null actual result");
    }

    // Helper methods

    private SimulationResult createTestSimulationResult(int finalXp, double skillGrowth, BurnoutRisk burnoutRisk) {
        List<YearlyProjection> projections = List.of(
            new YearlyProjection(1, finalXp, 5, skillGrowth, 20.0)
        );
        IncomeRange incomeRange = new IncomeRange(50000, 60000, 70000);
        return new SimulationResult(
            projections, skillGrowth, burnoutRisk, incomeRange, 30.0, "Test explanation"
        );
    }

    private GeneratedScenario createTestScenario(Recommendation recommendation) {
        // Create a minimal simulation input for the scenario
        UserStats userStats = new UserStats(500, 5);
        Map<Difficulty, Integer> distribution = new HashMap<>();
        for (Difficulty d : Difficulty.values()) {
            distribution.put(d, 0);
        }
        distribution.put(Difficulty.THREE, 2);

        SimulationInput input = new SimulationInput(
            userStats, 70.0, 80.0, distribution, List.of(),
            new BurnoutWarning(false, List.of(), 0.0), 25, 10.0, 1
        );

        return new GeneratedScenario(
            "Test Scenario",
            List.of(recommendation),
            input,
            "Test rationale",
            Map.of("test", "change")
        );
    }

    private ScenarioImpactSummary createTestImpactSummary(
            GeneratedScenario scenario,
            SimulationResult baseResult,
            SimulationResult improvedResult,
            double xpImprovement,
            double skillGrowthImprovement) {
        return new ScenarioImpactSummary(
            scenario,
            baseResult,
            improvedResult,
            xpImprovement,
            skillGrowthImprovement,
            ScenarioImpactSummary.BurnoutRiskChange.UNCHANGED,
            5000,
            5.0,
            "Test improvement description"
        );
    }
}

