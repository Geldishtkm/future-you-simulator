package org.example.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for weekly summary data.
 */
public class WeeklySummaryDto {
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private int totalXpGained;
    private int habitsCompleted;
    private int goalsProgressed;
    private int activeDays;
    private double averageConsistency;
    private int longestStreak;
    private List<String> topHabits;
    private List<String> achievementsUnlocked;
    private String summaryMessage;

    public WeeklySummaryDto() {
    }

    public LocalDate getWeekStart() {
        return weekStart;
    }

    public void setWeekStart(LocalDate weekStart) {
        this.weekStart = weekStart;
    }

    public LocalDate getWeekEnd() {
        return weekEnd;
    }

    public void setWeekEnd(LocalDate weekEnd) {
        this.weekEnd = weekEnd;
    }

    public int getTotalXpGained() {
        return totalXpGained;
    }

    public void setTotalXpGained(int totalXpGained) {
        this.totalXpGained = totalXpGained;
    }

    public int getHabitsCompleted() {
        return habitsCompleted;
    }

    public void setHabitsCompleted(int habitsCompleted) {
        this.habitsCompleted = habitsCompleted;
    }

    public int getGoalsProgressed() {
        return goalsProgressed;
    }

    public void setGoalsProgressed(int goalsProgressed) {
        this.goalsProgressed = goalsProgressed;
    }

    public int getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(int activeDays) {
        this.activeDays = activeDays;
    }

    public double getAverageConsistency() {
        return averageConsistency;
    }

    public void setAverageConsistency(double averageConsistency) {
        this.averageConsistency = averageConsistency;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public List<String> getTopHabits() {
        return topHabits;
    }

    public void setTopHabits(List<String> topHabits) {
        this.topHabits = topHabits;
    }

    public List<String> getAchievementsUnlocked() {
        return achievementsUnlocked;
    }

    public void setAchievementsUnlocked(List<String> achievementsUnlocked) {
        this.achievementsUnlocked = achievementsUnlocked;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public void setSummaryMessage(String summaryMessage) {
        this.summaryMessage = summaryMessage;
    }
}

