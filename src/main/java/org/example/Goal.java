package org.example;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a long-term goal that a user is working towards.
 * Goals have a start date, target date, importance level, and track progress through accumulated points.
 */
public class Goal {
    private final String title;
    private final String description;
    private final LocalDate startDate;
    private final LocalDate targetDate;
    private final int importance; // 1-5 scale
    private final int totalProgressPoints; // Target points to complete the goal

    /**
     * Creates a new goal.
     *
     * @param title the goal title (must not be null or blank)
     * @param description the goal description (can be null or empty)
     * @param startDate the date when work on this goal started (must not be null)
     * @param targetDate the target completion date (must not be null, must be after startDate)
     * @param importance the importance level from 1 (lowest) to 5 (highest)
     * @param totalProgressPoints the total points needed to complete this goal (must be positive)
     * @throws IllegalArgumentException if validation fails
     */
    public Goal(String title, String description, LocalDate startDate, LocalDate targetDate, 
                int importance, int totalProgressPoints) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Goal title cannot be null or blank");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (targetDate == null) {
            throw new IllegalArgumentException("Target date cannot be null");
        }
        if (targetDate.isBefore(startDate) || targetDate.isEqual(startDate)) {
            throw new IllegalArgumentException("Target date must be after start date");
        }
        if (importance < 1 || importance > 5) {
            throw new IllegalArgumentException("Importance must be between 1 and 5");
        }
        if (totalProgressPoints <= 0) {
            throw new IllegalArgumentException("Total progress points must be positive");
        }

        this.title = title;
        this.description = description != null ? description : "";
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.importance = importance;
        this.totalProgressPoints = totalProgressPoints;
    }

    /**
     * Returns the goal title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the goal description.
     *
     * @return the description (may be empty)
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the start date.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the target completion date.
     *
     * @return the target date
     */
    public LocalDate getTargetDate() {
        return targetDate;
    }

    /**
     * Returns the importance level (1-5).
     *
     * @return the importance
     */
    public int getImportance() {
        return importance;
    }

    /**
     * Returns the total progress points needed to complete this goal.
     *
     * @return the total progress points
     */
    public int getTotalProgressPoints() {
        return totalProgressPoints;
    }

    /**
     * Returns true if the target date has passed.
     *
     * @param currentDate the current date to check against
     * @return true if target date is in the past
     */
    public boolean isOverdue(LocalDate currentDate) {
        return currentDate.isAfter(targetDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return importance == goal.importance &&
                totalProgressPoints == goal.totalProgressPoints &&
                Objects.equals(title, goal.title) &&
                Objects.equals(description, goal.description) &&
                Objects.equals(startDate, goal.startDate) &&
                Objects.equals(targetDate, goal.targetDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, startDate, targetDate, importance, totalProgressPoints);
    }

    @Override
    public String toString() {
        return "Goal{title='" + title + "', importance=" + importance + 
               ", progressPoints=" + totalProgressPoints + ", targetDate=" + targetDate + "}";
    }
}

