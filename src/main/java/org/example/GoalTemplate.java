package org.example;

/**
 * Represents a template for creating goals.
 */
public record GoalTemplate(
    String id,
    String title,
    String description,
    String category,
    int suggestedDurationDays,
    int suggestedImportance,
    String[] suggestedSteps
) {
    /**
     * Creates a goal template.
     */
    public GoalTemplate {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }
        if (suggestedDurationDays <= 0) {
            throw new IllegalArgumentException("Suggested duration must be positive");
        }
        if (suggestedImportance < 1 || suggestedImportance > 5) {
            throw new IllegalArgumentException("Suggested importance must be between 1 and 5");
        }
    }
}

