package org.example.simulation.model;

/**
 * Represents a single year's projection in the simulation.
 */
public class YearlyProjection {
    private final int year; // 1, 2, 3, etc.
    private final int projectedXp;
    private final int projectedLevel;
    private final double skillGrowthIndex; // 0.0 to 100.0
    private final double xpGrowthRate; // Percentage change from previous year

    /**
     * Creates a new YearlyProjection.
     *
     * @param year the year number (1-based)
     * @param projectedXp the projected XP at the end of this year
     * @param projectedLevel the projected level at the end of this year
     * @param skillGrowthIndex skill growth index (0.0-100.0)
     * @param xpGrowthRate percentage growth rate from previous year
     */
    public YearlyProjection(int year, int projectedXp, int projectedLevel,
                           double skillGrowthIndex, double xpGrowthRate) {
        if (year < 1) {
            throw new IllegalArgumentException("Year must be at least 1");
        }
        if (projectedXp < 0) {
            throw new IllegalArgumentException("Projected XP cannot be negative");
        }
        if (projectedLevel < 1) {
            throw new IllegalArgumentException("Projected level must be at least 1");
        }
        if (skillGrowthIndex < 0.0 || skillGrowthIndex > 100.0) {
            throw new IllegalArgumentException("Skill growth index must be between 0.0 and 100.0");
        }

        this.year = year;
        this.projectedXp = projectedXp;
        this.projectedLevel = projectedLevel;
        this.skillGrowthIndex = skillGrowthIndex;
        this.xpGrowthRate = xpGrowthRate;
    }

    public int getYear() {
        return year;
    }

    public int getProjectedXp() {
        return projectedXp;
    }

    public int getProjectedLevel() {
        return projectedLevel;
    }

    public double getSkillGrowthIndex() {
        return skillGrowthIndex;
    }

    public double getXpGrowthRate() {
        return xpGrowthRate;
    }
}

