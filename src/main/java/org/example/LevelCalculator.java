package org.example;

/**
 * Calculates a user's level based on their total XP.
 * Uses a progressive XP requirement system where each level requires more XP than the previous.
 */
public class LevelCalculator {
    private static final int BASE_XP_PER_LEVEL = 100;
    private static final double XP_MULTIPLIER = 1.5;

    /**
     * Calculates the level based on total XP.
     * Level 1: 0-99 XP
     * Level 2: 100-249 XP
     * Level 3: 250-474 XP
     * And so on, with each level requiring progressively more XP.
     *
     * @param totalXp the total accumulated XP (must be non-negative)
     * @return the calculated level (minimum 1)
     * @throws IllegalArgumentException if totalXp is negative
     */
    public int calculateLevel(int totalXp) {
        if (totalXp < 0) {
            throw new IllegalArgumentException("Total XP cannot be negative");
        }

        if (totalXp == 0) {
            return 1;
        }

        int level = 1;
        int xpRequired = 0;
        int xpForNextLevel = BASE_XP_PER_LEVEL;

        while (totalXp >= xpRequired + xpForNextLevel) {
            xpRequired += xpForNextLevel;
            level++;
            xpForNextLevel = (int) (xpForNextLevel * XP_MULTIPLIER);
        }

        return level;
    }

    /**
     * Calculates the minimum XP required to reach a specific level.
     *
     * @param level the target level (must be at least 1)
     * @return the minimum XP required for that level
     * @throws IllegalArgumentException if level is less than 1
     */
    public int getXpRequiredForLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be at least 1");
        }

        if (level == 1) {
            return 0;
        }

        int totalXpRequired = 0;
        int xpForNextLevel = BASE_XP_PER_LEVEL;

        for (int i = 2; i <= level; i++) {
            totalXpRequired += xpForNextLevel;
            xpForNextLevel = (int) (xpForNextLevel * XP_MULTIPLIER);
        }

        return totalXpRequired;
    }
}

