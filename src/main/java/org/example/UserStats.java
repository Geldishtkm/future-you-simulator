package org.example;

/**
 * Represents a user's statistics, including their total XP and current level.
 * This class is immutable. To modify XP, create a new UserStats instance by applying XpTransactions.
 */
public class UserStats {
    private final int totalXp;
    private final int level;

    /**
     * Creates a new UserStats with the specified XP and level.
     *
     * @param totalXp the total accumulated XP (must be non-negative)
     * @param level the current level (must be positive)
     * @throws IllegalArgumentException if totalXp is negative or level is not positive
     */
    public UserStats(int totalXp, int level) {
        if (totalXp < 0) {
            throw new IllegalArgumentException("Total XP cannot be negative");
        }
        if (level < 1) {
            throw new IllegalArgumentException("Level must be at least 1");
        }
        this.totalXp = totalXp;
        this.level = level;
    }

    /**
     * Creates a new UserStats starting at level 1 with 0 XP.
     *
     * @return a new UserStats instance with 0 XP and level 1
     */
    public static UserStats createNew() {
        return new UserStats(0, 1);
    }

    /**
     * Applies an XP transaction to create a new UserStats instance.
     * The new instance will have updated XP and a recalculated level.
     *
     * @param transaction the XP transaction to apply
     * @param levelCalculator the calculator to use for determining the new level
     * @return a new UserStats instance with updated XP and level
     */
    public UserStats applyTransaction(XpTransaction transaction, LevelCalculator levelCalculator) {
        int newTotalXp = Math.max(0, this.totalXp + transaction.amount());
        int newLevel = levelCalculator.calculateLevel(newTotalXp);
        return new UserStats(newTotalXp, newLevel);
    }

    /**
     * Returns the total accumulated XP.
     *
     * @return the total XP
     */
    public int getTotalXp() {
        return totalXp;
    }

    /**
     * Returns the current level.
     *
     * @return the current level
     */
    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "UserStats{totalXp=" + totalXp + ", level=" + level + "}";
    }
}

