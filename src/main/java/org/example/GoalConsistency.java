package org.example;

/**
 * Represents consistency metrics for a goal.
 *
 * @param goal the goal this consistency is for
 * @param consistencyScore the consistency score (0.0 to 100.0)
 * @param activeDays the number of days with goal notes
 * @param totalNotes the total number of goal notes
 * @param averageGapDays the average number of days between activity days
 */
public record GoalConsistency(Goal goal, double consistencyScore, int activeDays, 
                             int totalNotes, double averageGapDays) {
    /**
     * Creates a new goal consistency record.
     *
     * @param goal the goal (must not be null)
     * @param consistencyScore the score (0.0 to 100.0)
     * @param activeDays the active days (must be non-negative)
     * @param totalNotes the total notes (must be non-negative)
     * @param averageGapDays the average gap (must be non-negative)
     * @throws IllegalArgumentException if validation fails
     */
    public GoalConsistency {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (consistencyScore < 0.0 || consistencyScore > 100.0) {
            throw new IllegalArgumentException("Consistency score must be between 0.0 and 100.0");
        }
        if (activeDays < 0 || totalNotes < 0 || averageGapDays < 0) {
            throw new IllegalArgumentException("Metrics cannot be negative");
        }
    }
}

