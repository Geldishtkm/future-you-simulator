package org.example.dto;

import java.util.List;

/**
 * DTO for health score response.
 * Provides a comprehensive health score based on multiple factors.
 */
public class HealthScoreDto {
    private double overallScore; // 0-100
    private double consistencyScore;
    private double streakScore;
    private double trendScore;
    private double burnoutScore; // Lower is better, inverted for health
    private String healthLevel; // EXCELLENT, GOOD, FAIR, POOR
    private List<String> strengths;
    private List<String> areasForImprovement;
    private String recommendation;

    public HealthScoreDto() {
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    public double getConsistencyScore() {
        return consistencyScore;
    }

    public void setConsistencyScore(double consistencyScore) {
        this.consistencyScore = consistencyScore;
    }

    public double getStreakScore() {
        return streakScore;
    }

    public void setStreakScore(double streakScore) {
        this.streakScore = streakScore;
    }

    public double getTrendScore() {
        return trendScore;
    }

    public void setTrendScore(double trendScore) {
        this.trendScore = trendScore;
    }

    public double getBurnoutScore() {
        return burnoutScore;
    }

    public void setBurnoutScore(double burnoutScore) {
        this.burnoutScore = burnoutScore;
    }

    public String getHealthLevel() {
        return healthLevel;
    }

    public void setHealthLevel(String healthLevel) {
        this.healthLevel = healthLevel;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getAreasForImprovement() {
        return areasForImprovement;
    }

    public void setAreasForImprovement(List<String> areasForImprovement) {
        this.areasForImprovement = areasForImprovement;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}

