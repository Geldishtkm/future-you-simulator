package org.example;

/**
 * Represents a habit recommendation for a user.
 */
public record HabitRecommendation(
    String name,
    String description,
    Difficulty suggestedDifficulty,
    String category,
    String reason,
    double matchScore
) {
    /**
     * Creates a habit recommendation.
     */
    public HabitRecommendation {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (suggestedDifficulty == null) {
            throw new IllegalArgumentException("Suggested difficulty cannot be null");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason cannot be null or blank");
        }
        if (matchScore < 0 || matchScore > 1) {
            throw new IllegalArgumentException("Match score must be between 0 and 1");
        }
    }
}

