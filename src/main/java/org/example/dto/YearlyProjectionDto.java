package org.example.dto;

/**
 * DTO for a single year's projection.
 */
public class YearlyProjectionDto {
    private int year;
    private int projectedXp;
    private int projectedLevel;
    private double skillGrowthIndex;
    private double xpGrowthRate;

    public YearlyProjectionDto() {
    }

    public YearlyProjectionDto(int year, int projectedXp, int projectedLevel,
                               double skillGrowthIndex, double xpGrowthRate) {
        this.year = year;
        this.projectedXp = projectedXp;
        this.projectedLevel = projectedLevel;
        this.skillGrowthIndex = skillGrowthIndex;
        this.xpGrowthRate = xpGrowthRate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getProjectedXp() {
        return projectedXp;
    }

    public void setProjectedXp(int projectedXp) {
        this.projectedXp = projectedXp;
    }

    public int getProjectedLevel() {
        return projectedLevel;
    }

    public void setProjectedLevel(int projectedLevel) {
        this.projectedLevel = projectedLevel;
    }

    public double getSkillGrowthIndex() {
        return skillGrowthIndex;
    }

    public void setSkillGrowthIndex(double skillGrowthIndex) {
        this.skillGrowthIndex = skillGrowthIndex;
    }

    public double getXpGrowthRate() {
        return xpGrowthRate;
    }

    public void setXpGrowthRate(double xpGrowthRate) {
        this.xpGrowthRate = xpGrowthRate;
    }
}

