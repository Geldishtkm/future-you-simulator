package org.example.strategy;

import org.example.simulation.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service that analyzes simulation results and generates strategic recommendations.
 * Transforms predictions into actionable advice.
 */
@Service
public class StrategyRecommendationService {

    /**
     * Generates ranked recommendations based on simulation results.
     *
     * @param simulationResult the simulation result to analyze
     * @return a ranked list of recommendations (highest priority first)
     */
    public List<Recommendation> generateRecommendations(SimulationResult simulationResult) {
        if (simulationResult == null) {
            throw new IllegalArgumentException("Simulation result cannot be null");
        }

        List<Recommendation> recommendations = new ArrayList<>();

        // Analyze different aspects and generate recommendations
        analyzeBurnoutRisk(simulationResult, recommendations);
        analyzeSkillGrowth(simulationResult, recommendations);
        analyzeXpGrowth(simulationResult, recommendations);
        analyzeConsistencyOpportunities(simulationResult, recommendations);
        analyzeGoalEngagement(simulationResult, recommendations);

        // Sort by priority score (highest first)
        recommendations.sort(Comparator.comparingDouble(Recommendation::priorityScore).reversed());

        return recommendations;
    }

    /**
     * Analyzes burnout risk and generates recommendations.
     */
    private void analyzeBurnoutRisk(SimulationResult result, List<Recommendation> recommendations) {
        BurnoutRisk risk = result.getBurnoutRisk();
        List<YearlyProjection> projections = result.getYearlyProjections();

        if (risk == BurnoutRisk.HIGH) {
            // High burnout risk - recommend reducing effort
            double priority = 90.0; // High priority
            
            String description = "Reduce daily effort intensity and habit difficulty to prevent burnout";
            String reason = String.format(
                "Your simulation shows HIGH burnout risk. Current trajectory suggests unsustainable effort levels.");
            String expectedBenefit = "Lower burnout risk, improved sustainability, and better long-term growth potential";
            String riskNote = "May temporarily slow XP growth, but prevents future decline from burnout";

            recommendations.add(new Recommendation(
                RecommendationType.REDUCE_BURNOUT_RISK,
                description,
                reason,
                expectedBenefit,
                riskNote,
                RecommendationImpact.HIGH,
                priority
            ));

            // Additional recommendation: adjust habit difficulty
            if (projections.size() > 0) {
                YearlyProjection firstYear = projections.get(0);
                if (firstYear.getSkillGrowthIndex() > 60) {
                    // High skill growth but burnout - suggest reducing difficulty
                    recommendations.add(new Recommendation(
                        RecommendationType.ADJUST_HABIT_DIFFICULTY,
                        "Consider reducing difficulty of 1-2 habits from high (4-5) to medium (3) difficulty",
                        "High skill growth combined with burnout risk suggests too much intensity",
                        "Maintains growth momentum while reducing stress and burnout risk",
                        "May slightly reduce XP per habit, but improves consistency and sustainability",
                        RecommendationImpact.MEDIUM,
                        75.0
                    ));
                }
            }
        } else if (risk == BurnoutRisk.MEDIUM) {
            // Medium burnout risk - recommend balancing effort
            recommendations.add(new Recommendation(
                RecommendationType.BALANCE_EFFORT,
                "Balance your effort levels - consider taking 1-2 rest days per week",
                "Your simulation indicates MEDIUM burnout risk. Current patterns may lead to increased risk over time",
                "Prevents escalation to high burnout risk while maintaining growth",
                "Requires discipline to balance activity and rest",
                RecommendationImpact.MEDIUM,
                60.0
            ));
        }
    }

    /**
     * Analyzes skill growth and generates recommendations.
     */
    private void analyzeSkillGrowth(SimulationResult result, List<Recommendation> recommendations) {
        double avgSkillGrowth = result.getAverageSkillGrowthIndex();
        List<YearlyProjection> projections = result.getYearlyProjections();

        if (avgSkillGrowth < 30.0) {
            // Low skill growth - recommend goal focus
            double priority = 85.0;

            String description = "Focus on goal completion and add 1-2 medium-difficulty goals";
            String reason = String.format(
                "Your average skill growth index is %.1f/100, indicating slow skill development. " +
                "Goals drive deeper skill growth than habits alone.", avgSkillGrowth);
            String expectedBenefit = "Significant increase in skill growth index, better career trajectory, higher income potential";
            String riskNote = "Requires dedicated time and focus on goal completion";

            recommendations.add(new Recommendation(
                RecommendationType.ADD_GOAL_FOCUS,
                description,
                reason,
                expectedBenefit,
                riskNote,
                RecommendationImpact.HIGH,
                priority
            ));

            // Also suggest adding habits if growth is very low
            if (avgSkillGrowth < 20.0) {
                recommendations.add(new Recommendation(
                    RecommendationType.ADD_HABITS_FOR_GROWTH,
                    "Add 1-2 medium-difficulty (3) habits to increase daily skill practice",
                    "Very low skill growth suggests insufficient daily practice",
                    "Increases daily XP gain and skill development rate",
                    "Be careful not to add too many habits at once - start with 1-2",
                    RecommendationImpact.MEDIUM,
                    70.0
                ));
            }
        } else if (avgSkillGrowth >= 30.0 && avgSkillGrowth < 50.0) {
            // Moderate skill growth - suggest optimization
            recommendations.add(new Recommendation(
                RecommendationType.OPTIMIZE_STRATEGY,
                "Your skill growth is moderate. Consider balancing habits and goals better",
                String.format("Skill growth index of %.1f/100 suggests room for optimization", avgSkillGrowth),
                "Potential to increase skill growth by 20-30% with better habit-goal balance",
                "May require adjusting current routines",
                RecommendationImpact.MEDIUM,
                50.0
            ));
        }

        // Check for skill growth plateau (declining growth over time)
        if (projections.size() >= 2) {
            YearlyProjection first = projections.get(0);
            YearlyProjection last = projections.get(projections.size() - 1);
            
            if (last.getSkillGrowthIndex() < first.getSkillGrowthIndex() - 10) {
                // Skill growth declining
                recommendations.add(new Recommendation(
                    RecommendationType.ADD_GOAL_FOCUS,
                    "Add new goals or increase goal complexity to prevent skill plateau",
                    "Skill growth is declining over time, indicating a plateau risk",
                    "New challenging goals can re-energize growth trajectory",
                    "Requires setting and committing to new objectives",
                    RecommendationImpact.HIGH,
                    80.0
                ));
            }
        }
    }

    /**
     * Analyzes XP growth patterns and generates recommendations.
     */
    private void analyzeXpGrowth(SimulationResult result, List<Recommendation> recommendations) {
        List<YearlyProjection> projections = result.getYearlyProjections();

        if (projections.size() < 2) {
            return;
        }

        // Check for declining XP growth rate
        YearlyProjection first = projections.get(0);
        YearlyProjection last = projections.get(projections.size() - 1);

        if (last.getXpGrowthRate() < -10.0) {
            // Declining growth
            recommendations.add(new Recommendation(
                RecommendationType.IMPROVE_CONSISTENCY,
                "Focus on consistency rather than intensity. Complete habits more regularly, even if it means lower difficulty",
                String.format("XP growth rate is declining (%.1f%%), suggesting inconsistent effort", last.getXpGrowthRate()),
                "Improved consistency leads to more sustainable and predictable growth",
                "May require discipline to maintain daily routines",
                RecommendationImpact.HIGH,
                85.0
            ));
        }

        // Check for very slow growth
        if (first.getXpGrowthRate() < 5.0 && last.getXpGrowthRate() < 5.0) {
            recommendations.add(new Recommendation(
                RecommendationType.IMPROVE_CONSISTENCY,
                "Increase consistency by committing to 1-2 core habits daily",
                "XP growth is very slow, likely due to low consistency or infrequent activity",
                "Higher consistency will accelerate XP growth and skill development",
                "Start small - focus on 1-2 habits you can complete daily",
                RecommendationImpact.HIGH,
                75.0
            ));
        }
    }

    /**
     * Analyzes consistency opportunities and generates recommendations.
     */
    private void analyzeConsistencyOpportunities(SimulationResult result, List<Recommendation> recommendations) {
        // If burnout risk is low but growth is moderate, consistency is the key
        if (result.getBurnoutRisk() == BurnoutRisk.LOW && result.getAverageSkillGrowthIndex() < 60.0) {
            recommendations.add(new Recommendation(
                RecommendationType.IMPROVE_CONSISTENCY,
                "Focus on maintaining consistent daily activity - even small daily actions compound over time",
                "Low burnout risk with moderate growth suggests consistency is the limiting factor",
                "Consistency will accelerate growth without increasing burnout risk",
                "Requires building strong daily routines",
                RecommendationImpact.MEDIUM,
                55.0
            ));
        }
    }

    /**
     * Analyzes goal engagement and generates recommendations.
     */
    private void analyzeGoalEngagement(SimulationResult result, List<Recommendation> recommendations) {
        // If skill growth is low relative to projections, goals may be missing
        List<YearlyProjection> projections = result.getYearlyProjections();

        if (projections.isEmpty()) {
            return;
        }
        
        // If initial skill growth is low, suggest goal focus
        YearlyProjection firstYear = projections.get(0);
        if (firstYear.getSkillGrowthIndex() < 40.0 && result.getAverageSkillGrowthIndex() < 40.0) {
            recommendations.add(new Recommendation(
                RecommendationType.ADD_GOAL_FOCUS,
                "Set 1-2 challenging long-term goals with clear milestones",
                "Low skill growth suggests insufficient goal engagement. Goals provide direction and deeper learning",
                "Goals drive focused skill development and higher XP gains from meaningful progress",
                "Goals require commitment and regular progress tracking",
                RecommendationImpact.HIGH,
                70.0
            ));
        }

        // Check if growth is plateauing - goals can help
        if (projections.size() >= 2) {
            YearlyProjection first = projections.get(0);
            YearlyProjection second = projections.get(1);
            
            // If growth is declining from year 1 to year 2, suggest new goals
            if (second.getSkillGrowthIndex() < first.getSkillGrowthIndex() - 5) {
                recommendations.add(new Recommendation(
                    RecommendationType.ADD_GOAL_FOCUS,
                    "Refresh your goals regularly - add new challenging goals as you complete existing ones",
                    "Skill growth is declining over time, suggesting need for new objectives and challenges",
                    "New goals provide fresh motivation and prevent skill plateau",
                    "Requires ongoing goal-setting and commitment",
                    RecommendationImpact.MEDIUM,
                    60.0
                ));
            }
        }
    }
}

