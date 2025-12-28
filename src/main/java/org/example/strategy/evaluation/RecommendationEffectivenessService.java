package org.example.strategy.evaluation;

import org.example.simulation.model.SimulationResult;
import org.example.strategy.Recommendation;
import org.example.strategy.scenario.ScenarioImpactSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that evaluates the effectiveness of recommendations by comparing
 * expected outcomes with actual results.
 */
public class RecommendationEffectivenessService {

    /**
     * Evaluates the effectiveness of a recommendation by comparing expected vs actual impact.
     *
     * @param expectedImpact the expected impact from scenario generation
     * @param actualResult the actual simulation result after following the recommendation
     * @return an EffectivenessEvaluation with scores, deviations, and learning signals
     */
    public EffectivenessEvaluation evaluateEffectiveness(
            ScenarioImpactSummary expectedImpact,
            SimulationResult actualResult) {
        if (expectedImpact == null) {
            throw new IllegalArgumentException("Expected impact cannot be null");
        }
        if (actualResult == null) {
            throw new IllegalArgumentException("Actual result cannot be null");
        }

        // Get the recommendation from the scenario
        Recommendation recommendation = expectedImpact.getScenario().getAppliedRecommendations().get(0);

        // Calculate effectiveness score
        double effectivenessScore = calculateEffectivenessScore(expectedImpact, actualResult);

        // Create outcome
        RecommendationOutcome outcome = new RecommendationOutcome(
            recommendation,
            expectedImpact,
            actualResult,
            effectivenessScore
        );

        // Analyze deviations
        DeviationReport deviationReport = analyzeDeviations(expectedImpact, actualResult);

        // Generate learning signals
        List<LearningSignal> learningSignals = generateLearningSignals(
            expectedImpact, actualResult, deviationReport, effectivenessScore);

        // Generate explanation
        String explanation = generateExplanation(outcome, deviationReport, learningSignals);

        // Calculate confidence level
        double confidenceLevel = calculateConfidenceLevel(deviationReport, actualResult);

        return new EffectivenessEvaluation(
            outcome,
            deviationReport,
            learningSignals,
            explanation,
            confidenceLevel
        );
    }

    /**
     * Calculates effectiveness score (0-100) based on how well actual results match expectations.
     */
    private double calculateEffectivenessScore(
            ScenarioImpactSummary expectedImpact,
            SimulationResult actualResult) {
        
        SimulationResult expectedResult = expectedImpact.getImprovedResult();
        
        // Compare final XP
        List<org.example.simulation.model.YearlyProjection> expectedProjections = expectedResult.getYearlyProjections();
        List<org.example.simulation.model.YearlyProjection> actualProjections = actualResult.getYearlyProjections();
        
        if (expectedProjections.isEmpty() || actualProjections.isEmpty()) {
            return 50.0; // Neutral score if no projections
        }

        int actualFinalXp = actualProjections.get(actualProjections.size() - 1).getProjectedXp();
        int baseFinalXp = expectedImpact.getBaseResult().getYearlyProjections()
            .get(expectedImpact.getBaseResult().getYearlyProjections().size() - 1).getProjectedXp();

        // Calculate XP improvement match (expected vs actual)
        double expectedXpImprovement = expectedImpact.getXpImprovement();
        double actualXpImprovement = baseFinalXp > 0 ? 
            ((double) (actualFinalXp - baseFinalXp) / baseFinalXp) * 100.0 : 0.0;
        
        double xpMatchScore = 100.0 - Math.abs(expectedXpImprovement - actualXpImprovement);
        xpMatchScore = Math.max(0.0, Math.min(100.0, xpMatchScore)); // Clamp to 0-100

        // Compare skill growth
        double expectedSkillGrowth = expectedResult.getAverageSkillGrowthIndex();
        double actualSkillGrowth = actualResult.getAverageSkillGrowthIndex();
        double skillGrowthMatch = 100.0 - Math.abs(expectedSkillGrowth - actualSkillGrowth);
        skillGrowthMatch = Math.max(0.0, Math.min(100.0, skillGrowthMatch));

        // Weighted average (XP improvement is more important)
        double effectivenessScore = (xpMatchScore * 0.6) + (skillGrowthMatch * 0.4);

        return Math.max(0.0, Math.min(100.0, effectivenessScore));
    }

    /**
     * Analyzes deviations between expected and actual results.
     */
    private DeviationReport analyzeDeviations(
            ScenarioImpactSummary expectedImpact,
            SimulationResult actualResult) {
        
        SimulationResult expectedResult = expectedImpact.getImprovedResult();

        // Calculate XP deviation
        double expectedXpImprovement = expectedImpact.getXpImprovement();
        List<org.example.simulation.model.YearlyProjection> baseProjections = 
            expectedImpact.getBaseResult().getYearlyProjections();
        List<org.example.simulation.model.YearlyProjection> actualProjections = actualResult.getYearlyProjections();
        
        int baseFinalXp = baseProjections.get(baseProjections.size() - 1).getProjectedXp();
        int actualFinalXp = actualProjections.get(actualProjections.size() - 1).getProjectedXp();
        double actualXpImprovement = baseFinalXp > 0 ? 
            ((double) (actualFinalXp - baseFinalXp) / baseFinalXp) * 100.0 : 0.0;
        
        double xpDeviation = actualXpImprovement - expectedXpImprovement;

        // Calculate skill growth deviation
        double expectedSkillGrowth = expectedResult.getAverageSkillGrowthIndex();
        double actualSkillGrowth = actualResult.getAverageSkillGrowthIndex();
        double skillGrowthDeviation = actualSkillGrowth - expectedSkillGrowth;

        // Determine most affected metric
        String mostAffectedMetric;
        if (Math.abs(xpDeviation) > Math.abs(skillGrowthDeviation * 10)) {
            mostAffectedMetric = "XP Growth";
        } else {
            mostAffectedMetric = "Skill Growth";
        }

        // Determine severity
        double maxDeviation = Math.max(Math.abs(xpDeviation), Math.abs(skillGrowthDeviation * 10));
        DeviationReport.DeviationSeverity severity;
        if (maxDeviation < 5.0) {
            severity = DeviationReport.DeviationSeverity.NONE;
        } else if (maxDeviation < 15.0) {
            severity = DeviationReport.DeviationSeverity.LOW;
        } else if (maxDeviation < 30.0) {
            severity = DeviationReport.DeviationSeverity.MEDIUM;
        } else if (maxDeviation < 50.0) {
            severity = DeviationReport.DeviationSeverity.HIGH;
        } else {
            severity = DeviationReport.DeviationSeverity.CRITICAL;
        }

        // Generate analysis
        String analysis = generateDeviationAnalysis(xpDeviation, skillGrowthDeviation, severity, mostAffectedMetric);

        return new DeviationReport(
            xpDeviation,
            skillGrowthDeviation,
            analysis,
            severity,
            mostAffectedMetric
        );
    }

    /**
     * Generates human-readable deviation analysis.
     */
    private String generateDeviationAnalysis(
            double xpDeviation,
            double skillGrowthDeviation,
            DeviationReport.DeviationSeverity severity,
            String mostAffectedMetric) {
        
        StringBuilder analysis = new StringBuilder();

        analysis.append(String.format("Deviation severity: %s. ", severity));

        if (Math.abs(xpDeviation) > 5.0) {
            if (xpDeviation > 0) {
                analysis.append(String.format("XP improvement exceeded expectations by +%.1f%%. ", xpDeviation));
            } else {
                analysis.append(String.format("XP improvement fell short by %.1f%%. ", Math.abs(xpDeviation)));
            }
        }

        if (Math.abs(skillGrowthDeviation) > 2.0) {
            if (skillGrowthDeviation > 0) {
                analysis.append(String.format("Skill growth exceeded expectations by +%.1f points. ", skillGrowthDeviation));
            } else {
                analysis.append(String.format("Skill growth fell short by %.1f points. ", Math.abs(skillGrowthDeviation)));
            }
        }

        analysis.append(String.format("Most affected metric: %s.", mostAffectedMetric));

        return analysis.toString();
    }

    /**
     * Generates learning signals based on deviations and effectiveness.
     */
    private List<LearningSignal> generateLearningSignals(
            ScenarioImpactSummary expectedImpact,
            SimulationResult actualResult,
            DeviationReport deviationReport,
            double effectivenessScore) {
        
        List<LearningSignal> signals = new ArrayList<>();
        
        double expectedXpImprovement = expectedImpact.getXpImprovement();

        // Calculate actual XP improvement
        List<org.example.simulation.model.YearlyProjection> baseProjections = 
            expectedImpact.getBaseResult().getYearlyProjections();
        List<org.example.simulation.model.YearlyProjection> actualProjections = actualResult.getYearlyProjections();
        int baseFinalXp = baseProjections.get(baseProjections.size() - 1).getProjectedXp();
        int actualFinalXp = actualProjections.get(actualProjections.size() - 1).getProjectedXp();
        double actualXpImprovement = baseFinalXp > 0 ? 
            ((double) (actualFinalXp - baseFinalXp) / baseFinalXp) * 100.0 : 0.0;

        // Low effectiveness score suggests issues
        if (effectivenessScore < 50.0) {
            if (actualXpImprovement < expectedXpImprovement - 10.0) {
                // Significantly underperformed
                if (deviationReport.getSeverity() == DeviationReport.DeviationSeverity.CRITICAL ||
                    deviationReport.getSeverity() == DeviationReport.DeviationSeverity.HIGH) {
                    signals.add(LearningSignal.OVER_OPTIMISTIC_RECOMMENDATION);
                    signals.add(LearningSignal.LOW_USER_COMPLIANCE);
                }
            }
            signals.add(LearningSignal.INCORRECT_MODEL_ASSUMPTION);
        } else if (effectivenessScore > 80.0) {
            // High effectiveness - recommendation worked well
            if (actualXpImprovement > expectedXpImprovement + 10.0) {
                signals.add(LearningSignal.RECOMMENDATION_WAS_CONSERVATIVE);
            }
        } else {
            // Moderate effectiveness - mixed signals
            if (deviationReport.getSeverity() == DeviationReport.DeviationSeverity.MEDIUM) {
                signals.add(LearningSignal.EXTERNAL_FACTORS_INFLUENCE);
            }
        }

        // If burnout risk changed unexpectedly
        if (expectedImpact.getBurnoutRiskChange() != 
            org.example.strategy.scenario.ScenarioImpactSummary.BurnoutRiskChange.UNCHANGED &&
            actualResult.getBurnoutRisk() == expectedImpact.getBaseResult().getBurnoutRisk()) {
            signals.add(LearningSignal.INCORRECT_MODEL_ASSUMPTION);
        }

        return signals;
    }

    /**
     * Generates human-readable explanation of the evaluation.
     */
    private String generateExplanation(
            RecommendationOutcome outcome,
            DeviationReport deviationReport,
            List<LearningSignal> learningSignals) {
        
        StringBuilder explanation = new StringBuilder();

        explanation.append(String.format("Effectiveness Score: %.1f/100. ", outcome.effectivenessScore()));

        Recommendation recommendation = outcome.recommendation();
        explanation.append(String.format("The recommendation '%s' ", recommendation.description()));

        if (outcome.effectivenessScore() >= 70.0) {
            explanation.append("performed well, with actual results closely matching expectations. ");
        } else if (outcome.effectivenessScore() >= 50.0) {
            explanation.append("performed moderately, with some deviation from expected outcomes. ");
        } else {
            explanation.append("performed below expectations, with significant deviation from predicted results. ");
        }

        if (deviationReport.getSeverity() != DeviationReport.DeviationSeverity.NONE) {
            explanation.append(deviationReport.getDeviationAnalysis()).append(" ");
        }

        if (!learningSignals.isEmpty()) {
            explanation.append("Key insights: ");
            for (LearningSignal signal : learningSignals) {
                explanation.append(signal.name().toLowerCase().replace("_", " ")).append("; ");
            }
        }

        return explanation.toString().trim();
    }

    /**
     * Calculates confidence level in the evaluation (based on data quality and deviation severity).
     */
    private double calculateConfidenceLevel(
            DeviationReport deviationReport,
            SimulationResult actualResult) {
        
        double baseConfidence = 70.0; // Base confidence

        // Adjust based on deviation severity (smaller deviations = higher confidence)
        switch (deviationReport.getSeverity()) {
            case NONE:
                baseConfidence += 20.0;
                break;
            case LOW:
                baseConfidence += 10.0;
                break;
            case MEDIUM:
                // No change
                break;
            case HIGH:
                baseConfidence -= 10.0;
                break;
            case CRITICAL:
                baseConfidence -= 20.0;
                break;
        }

        // Ensure we have sufficient data (at least 1 year of projections)
        if (actualResult.getYearlyProjections().size() < 1) {
            baseConfidence -= 20.0;
        }

        return Math.max(0.0, Math.min(100.0, baseConfidence));
    }
}

