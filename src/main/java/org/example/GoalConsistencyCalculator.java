package org.example;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Calculates consistency metrics for goals based on goal note history.
 * Consistency is based on:
 * - Number of active days (days with notes)
 * - Total number of notes
 * - Gaps between activity days
 */
public class GoalConsistencyCalculator {
    /**
     * Calculates consistency metrics for a goal.
     *
     * @param goal the goal to calculate consistency for
     * @param goalNotes all goal notes for this goal, ordered by date (must not be null)
     * @param currentDate the current date for calculations
     * @return the GoalConsistency information
     * @throws IllegalArgumentException if goal or goalNotes is null
     */
    public GoalConsistency calculateConsistency(Goal goal, List<GoalNote> goalNotes, LocalDate currentDate) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (goalNotes == null) {
            throw new IllegalArgumentException("Goal notes list cannot be null");
        }
        if (currentDate == null) {
            throw new IllegalArgumentException("Current date cannot be null");
        }

        // Filter notes for this specific goal and sort by date
        List<GoalNote> relevantNotes = goalNotes.stream()
                .filter(note -> note.goal().equals(goal))
                .sorted((a, b) -> a.date().compareTo(b.date()))
                .collect(Collectors.toList());

        if (relevantNotes.isEmpty()) {
            return new GoalConsistency(goal, 0.0, 0, 0, 0.0);
        }

        int activeDays = relevantNotes.size();
        int totalNotes = relevantNotes.size();

        // Calculate gaps between activity days
        double totalGapDays = 0.0;
        int gapCount = 0;

        for (int i = 1; i < relevantNotes.size(); i++) {
            LocalDate previousDate = relevantNotes.get(i - 1).date();
            LocalDate currentNoteDate = relevantNotes.get(i).date();
            long daysBetween = ChronoUnit.DAYS.between(previousDate, currentNoteDate);
            
            if (daysBetween > 1) {
                // There's a gap
                totalGapDays += (daysBetween - 1); // Subtract 1 because consecutive days = 0 gap
                gapCount++;
            }
        }

        // Calculate average gap days
        double averageGapDays = gapCount > 0 ? totalGapDays / gapCount : 0.0;

        // Calculate consistency score (0-100)
        // Factors:
        // - More active days = higher score
        // - Fewer gaps = higher score
        // - Smaller average gap = higher score
        
        long daysSinceStart = ChronoUnit.DAYS.between(goal.getStartDate(), currentDate);
        if (daysSinceStart <= 0) {
            daysSinceStart = 1; // Avoid division by zero
        }

        // Activity frequency score (0-50 points)
        double activityFrequency = (double) activeDays / daysSinceStart;
        double activityScore = Math.min(50.0, activityFrequency * 50.0);

        // Consistency score (0-50 points) - based on gaps
        // Perfect consistency (no gaps) = 50 points
        // More gaps = lower score
        double consistencyScore = 50.0;
        if (averageGapDays > 0) {
            // Penalize for gaps: 1 day gap = -5 points, 2 days = -10, etc.
            consistencyScore = Math.max(0.0, 50.0 - (averageGapDays * 5.0));
        }

        double totalScore = activityScore + consistencyScore;

        return new GoalConsistency(goal, totalScore, activeDays, totalNotes, averageGapDays);
    }
}

