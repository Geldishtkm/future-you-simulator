package org.example.simulation.explanation;

import org.example.simulation.model.BurnoutRisk;
import org.example.simulation.model.IncomeRange;
import org.example.simulation.model.SimulationResult;
import org.example.simulation.model.YearlyProjection;

import java.util.List;

/**
 * Generates human-readable explanations for simulation results.
 */
public class SimulationExplanationGenerator {

    /**
     * Generates a textual explanation for a simulation result.
     *
     * @param result the simulation result to explain
     * @param inputConsistencyScore the input consistency score for context
     * @return a detailed explanation string
     */
    public String generateExplanation(SimulationResult result, double inputConsistencyScore) {
        if (result == null) {
            throw new IllegalArgumentException("Simulation result cannot be null");
        }

        StringBuilder explanation = new StringBuilder();

        // Overview
        explanation.append(generateOverview(result, inputConsistencyScore));

        // XP and Level Trajectory
        explanation.append("\n\n");
        explanation.append(generateXpTrajectory(result.getYearlyProjections()));

        // Skill Growth Analysis
        explanation.append("\n\n");
        explanation.append(generateSkillGrowthAnalysis(result));

        // Burnout Risk Analysis
        explanation.append("\n\n");
        explanation.append(generateBurnoutAnalysis(result));

        // Income Projection
        explanation.append("\n\n");
        explanation.append(generateIncomeProjection(result));

        // Emigration Probability
        explanation.append("\n\n");
        explanation.append(generateEmigrationAnalysis(result));

        return explanation.toString();
    }

    private String generateOverview(SimulationResult result, double consistencyScore) {
        List<YearlyProjection> projections = result.getYearlyProjections();
        YearlyProjection finalYear = projections.get(projections.size() - 1);
        
        int startLevel = projections.get(0).getProjectedLevel();
        int endLevel = finalYear.getProjectedLevel();
        int levelIncrease = endLevel - startLevel;

        StringBuilder overview = new StringBuilder();
        overview.append("=== Simulation Overview ===\n");
        overview.append(String.format("Based on your current consistency score of %.1f%%, ", consistencyScore));
        overview.append(String.format("you're projected to reach Level %d over the next %d years ", 
                endLevel, projections.size()));
        overview.append(String.format("(from Level %d, a +%d level increase).", startLevel, levelIncrease));
        
        if (levelIncrease > 5) {
            overview.append(" This indicates strong growth potential.");
        } else if (levelIncrease < 2) {
            overview.append(" Growth is modest; consider increasing activity consistency.");
        } else {
            overview.append(" Growth is steady and sustainable.");
        }

        return overview.toString();
    }

    private String generateXpTrajectory(List<YearlyProjection> projections) {
        StringBuilder trajectory = new StringBuilder();
        trajectory.append("=== XP Trajectory ===\n");

        for (YearlyProjection projection : projections) {
            trajectory.append(String.format("Year %d: %d XP (Level %d)", 
                    projection.getYear(), projection.getProjectedXp(), projection.getProjectedLevel()));
            
            if (projection.getXpGrowthRate() < -10) {
                trajectory.append(" [Declining growth - burnout risk increasing]");
            } else if (projection.getXpGrowthRate() < 0) {
                trajectory.append(" [Growth slowing]");
            } else if (projection.getXpGrowthRate() > 50) {
                trajectory.append(" [Strong growth]");
            } else if (projection.getXpGrowthRate() > 20) {
                trajectory.append(" [Healthy growth]");
            }
            
            trajectory.append("\n");
        }

        return trajectory.toString();
    }

    private String generateSkillGrowthAnalysis(SimulationResult result) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== Skill Growth Analysis ===\n");
        
        double avgGrowth = result.getAverageSkillGrowthIndex();
        
        if (avgGrowth >= 70) {
            analysis.append(String.format("Your skill growth index is %.1f/100, indicating exceptional growth potential. ", avgGrowth));
            analysis.append("You're on track to develop strong expertise in your areas of focus.");
        } else if (avgGrowth >= 50) {
            analysis.append(String.format("Your skill growth index is %.1f/100, showing solid progress. ", avgGrowth));
            analysis.append("Continued consistency will lead to significant skill development.");
        } else if (avgGrowth >= 30) {
            analysis.append(String.format("Your skill growth index is %.1f/100, indicating moderate growth. ", avgGrowth));
            analysis.append("Consider increasing goal engagement to accelerate skill development.");
        } else {
            analysis.append(String.format("Your skill growth index is %.1f/100, suggesting slow growth. ", avgGrowth));
            analysis.append("Focus on completing more challenging goals and maintaining better consistency.");
        }

        return analysis.toString();
    }

    private String generateBurnoutAnalysis(SimulationResult result) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== Burnout Risk Assessment ===\n");
        
        BurnoutRisk risk = result.getBurnoutRisk();
        
        switch (risk) {
            case LOW:
                analysis.append("Burnout risk is LOW. Your current activity patterns are sustainable. ");
                analysis.append("You're maintaining a healthy balance between effort and rest.");
                break;
            case MEDIUM:
                analysis.append("Burnout risk is MEDIUM. Monitor your activity levels and ensure adequate rest. ");
                analysis.append("Consider slightly reducing intensity or frequency if you feel overwhelmed.");
                break;
            case HIGH:
                analysis.append("Burnout risk is HIGH. Your current trajectory suggests unsustainable effort levels. ");
                analysis.append("Strongly consider: (1) Reducing daily activity intensity, (2) Taking regular breaks, ");
                analysis.append("(3) Focusing on consistency over intensity.");
                break;
        }

        return analysis.toString();
    }

    private String generateIncomeProjection(SimulationResult result) {
        StringBuilder projection = new StringBuilder();
        projection.append("=== Income Projection ===\n");
        
        IncomeRange range = result.getIncomeRange();
        projection.append(String.format("Based on skill growth and level progression:\n"));
        projection.append(String.format("  Low estimate: $%d/year (25th percentile)\n", range.getLowEstimate()));
        projection.append(String.format("  Expected: $%d/year (50th percentile)\n", range.getExpectedEstimate()));
        projection.append(String.format("  High estimate: $%d/year (75th percentile)\n", range.getHighEstimate()));
        projection.append("\nNote: These are probabilistic estimates based on skill level progression. ");
        projection.append("Actual income depends on many factors including location, industry, and market conditions.");

        return projection.toString();
    }

    private String generateEmigrationAnalysis(SimulationResult result) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== Emigration Probability ===\n");
        
        double probability = result.getEmigrationProbability();
        
        if (probability >= 70) {
            analysis.append(String.format("Emigration probability: %.1f%% (HIGH)\n", probability));
            analysis.append("High skill growth combined with income potential suggests strong motivation ");
            analysis.append("to seek opportunities in markets with better compensation or opportunities.");
        } else if (probability >= 40) {
            analysis.append(String.format("Emigration probability: %.1f%% (MODERATE)\n", probability));
            analysis.append("Moderate likelihood of seeking opportunities abroad, especially if local market ");
            analysis.append("conditions don't align with skill growth.");
        } else {
            analysis.append(String.format("Emigration probability: %.1f%% (LOW)\n", probability));
            analysis.append("Lower probability suggests current trajectory may be sustainable locally, ");
            analysis.append("or skill growth may not yet be at levels that typically drive emigration decisions.");
        }

        return analysis.toString();
    }
}

