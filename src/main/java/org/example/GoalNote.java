package org.example;

import java.time.LocalDate;

/**
 * Represents a daily note and progress update for a goal.
 * Each note includes the date, text content, and manually assigned progress points.
 *
 * @param goal the goal this note belongs to
 * @param date the date of this note
 * @param textNote the text content of the note
 * @param points the progress points assigned (must be non-negative)
 */
public record GoalNote(Goal goal, LocalDate date, String textNote, int points) {
    /**
     * Creates a new goal note.
     *
     * @param goal the goal this note belongs to (must not be null)
     * @param date the date of this note (must not be null)
     * @param textNote the text content (can be null or empty)
     * @param points the progress points assigned (must be non-negative)
     * @throws IllegalArgumentException if goal or date is null, or points is negative
     */
    public GoalNote {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (points < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }
    }

    /**
     * Returns true if this note has any text content.
     *
     * @return true if textNote is not null and not blank
     */
    public boolean hasText() {
        return textNote != null && !textNote.isBlank();
    }
}

