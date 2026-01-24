package org.example.dto;

import java.util.List;

/**
 * DTO for dashboard response containing comprehensive user overview.
 */
public class DashboardDto {
    private int totalXp;
    private int level;
    private int totalHabits;
    private int activeHabits;
    private int totalGoals;
    private int activeGoals;
    private double consistencyScore;
    private int currentStreaks;
    private int longestStreak;
    private int activeDaysLastWeek;
    private String trendDirection;
    private String trendStrength;
    private String burnoutRisk;
    private String burnoutMessage;
    private List<HabitStreakDto> topStreaks;
    private List<XpActivityDto> recentActivity;

    public DashboardDto() {
    }

    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotalHabits() {
        return totalHabits;
    }

    public void setTotalHabits(int totalHabits) {
        this.totalHabits = totalHabits;
    }

    public int getActiveHabits() {
        return activeHabits;
    }

    public void setActiveHabits(int activeHabits) {
        this.activeHabits = activeHabits;
    }

    public int getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(int totalGoals) {
        this.totalGoals = totalGoals;
    }

    public int getActiveGoals() {
        return activeGoals;
    }

    public void setActiveGoals(int activeGoals) {
        this.activeGoals = activeGoals;
    }

    public double getConsistencyScore() {
        return consistencyScore;
    }

    public void setConsistencyScore(double consistencyScore) {
        this.consistencyScore = consistencyScore;
    }

    public int getCurrentStreaks() {
        return currentStreaks;
    }

    public void setCurrentStreaks(int currentStreaks) {
        this.currentStreaks = currentStreaks;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public int getActiveDaysLastWeek() {
        return activeDaysLastWeek;
    }

    public void setActiveDaysLastWeek(int activeDaysLastWeek) {
        this.activeDaysLastWeek = activeDaysLastWeek;
    }

    public String getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(String trendDirection) {
        this.trendDirection = trendDirection;
    }

    public String getTrendStrength() {
        return trendStrength;
    }

    public void setTrendStrength(String trendStrength) {
        this.trendStrength = trendStrength;
    }

    public String getBurnoutRisk() {
        return burnoutRisk;
    }

    public void setBurnoutRisk(String burnoutRisk) {
        this.burnoutRisk = burnoutRisk;
    }

    public String getBurnoutMessage() {
        return burnoutMessage;
    }

    public void setBurnoutMessage(String burnoutMessage) {
        this.burnoutMessage = burnoutMessage;
    }

    public List<HabitStreakDto> getTopStreaks() {
        return topStreaks;
    }

    public void setTopStreaks(List<HabitStreakDto> topStreaks) {
        this.topStreaks = topStreaks;
    }

    public List<XpActivityDto> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<XpActivityDto> recentActivity) {
        this.recentActivity = recentActivity;
    }
}

