package org.example;

/**
 * Represents the difficulty level of a habit, ranging from 1 (easiest) to 5 (hardest).
 * Higher difficulty habits typically reward more XP when completed.
 */
public enum Difficulty {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    Difficulty(int value) {
        this.value = value;
    }

    /**
     * Returns the numeric value of this difficulty level.
     *
     * @return the difficulty value (1-5)
     */
    public int getValue() {
        return value;
    }
}

