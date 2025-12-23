package org.example;

import java.util.List;

/**
 * Calculates normalized progress (0-100%) for goals based on accumulated progress points.
 */
public class GoalProgressCalculator {
    /**
     * Calculates the progress percentage for a goal based on accumulated points.
     * Progress is normalized to 0-100%, capped at 100%.
     *
     * @param goal the goal to calculate progress for
     * @param accumulatedPoints the total points accumulated towards this goal
     * @return the progress percentage (0.0 to 100.0)
     * @throws IllegalArgumentException if goal is null or accumulatedPoints is negative
     */
    public double calculateProgress(Goal goal, int accumulatedPoints) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (accumulatedPoints < 0) {
            throw new IllegalArgumentException("Accumulated points cannot be negative");
        }

        if (goal.getTotalProgressPoints() == 0) {
            return 0.0;
        }

        double progress = (double) accumulatedPoints / goal.getTotalProgressPoints() * 100.0;
        return Math.min(100.0, Math.max(0.0, progress)); // Clamp between 0 and 100
    }

    /**
     * Calculates accumulated points from a list of goal notes.
     *
     * @param goalNotes the list of goal notes (must not be null)
     * @return the total accumulated points
     * @throws IllegalArgumentException if goalNotes is null
     */
    public int calculateAccumulatedPoints(List<GoalNote> goalNotes) {
        if (goalNotes == null) {
            throw new IllegalArgumentException("Goal notes list cannot be null");
        }
        return goalNotes.stream()
                .mapToInt(GoalNote::points)
                .sum();
    }

    /**
     * Calculates progress for a goal based on its notes.
     *
     * @param goal the goal
     * @param goalNotes the list of notes for this goal
     * @return the progress percentage (0.0 to 100.0)
     */
    public double calculateProgressFromNotes(Goal goal, List<GoalNote> goalNotes) {
        int accumulatedPoints = calculateAccumulatedPoints(goalNotes);
        return calculateProgress(goal, accumulatedPoints);
    }
}

