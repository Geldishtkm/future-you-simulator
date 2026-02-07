package org.example.dto;

/**
 * DTO for habit recommendation.
 */
public class HabitRecommendationDto {
    private String name;
    private String description;
    private String suggestedDifficulty;
    private String category;
    private String reason;
    private double matchScore;

    public HabitRecommendationDto() {
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSuggestedDifficulty() { return suggestedDifficulty; }
    public void setSuggestedDifficulty(String suggestedDifficulty) { this.suggestedDifficulty = suggestedDifficulty; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }
}

