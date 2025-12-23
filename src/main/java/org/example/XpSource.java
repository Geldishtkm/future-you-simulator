package org.example;

/**
 * Represents the source of an XP change.
 * Used for tracking and analytics purposes.
 */
public enum XpSource {
    /**
     * XP gained from completing habits.
     */
    HABIT,

    /**
     * XP gained from goal progress notes.
     */
    GOAL,

    /**
     * XP lost due to inactivity decay.
     */
    DECAY
}

