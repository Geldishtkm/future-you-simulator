package org.example.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for monthly summary data.
 */
public class MonthlySummaryDto {
    private LocalDate monthStart;
    private LocalDate monthEnd;
    private int totalXpGained;
    private int habitsCompleted;
    private int goalsProgressed;
    private int activeDays;
    private double averageConsistency;
    private int longestStreak;
    private int levelAtStart;
    private int levelAtEnd;
    private int levelGained;
    private List<String> topHabits;
    private List<String> achievementsUnlocked;
    private String summaryMessage;

    public MonthlySummaryDto() {
    }

    // Getters and setters
    public LocalDate getMonthStart() { return monthStart; }
    public void setMonthStart(LocalDate monthStart) { this.monthStart = monthStart; }
    public LocalDate getMonthEnd() { return monthEnd; }
    public void setMonthEnd(LocalDate monthEnd) { this.monthEnd = monthEnd; }
    public int getTotalXpGained() { return totalXpGained; }
    public void setTotalXpGained(int totalXpGained) { this.totalXpGained = totalXpGained; }
    public int getHabitsCompleted() { return habitsCompleted; }
    public void setHabitsCompleted(int habitsCompleted) { this.habitsCompleted = habitsCompleted; }
    public int getGoalsProgressed() { return goalsProgressed; }
    public void setGoalsProgressed(int goalsProgressed) { this.goalsProgressed = goalsProgressed; }
    public int getActiveDays() { return activeDays; }
    public void setActiveDays(int activeDays) { this.activeDays = activeDays; }
    public double getAverageConsistency() { return averageConsistency; }
    public void setAverageConsistency(double averageConsistency) { this.averageConsistency = averageConsistency; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    public int getLevelAtStart() { return levelAtStart; }
    public void setLevelAtStart(int levelAtStart) { this.levelAtStart = levelAtStart; }
    public int getLevelAtEnd() { return levelAtEnd; }
    public void setLevelAtEnd(int levelAtEnd) { this.levelAtEnd = levelAtEnd; }
    public int getLevelGained() { return levelGained; }
    public void setLevelGained(int levelGained) { this.levelGained = levelGained; }
    public List<String> getTopHabits() { return topHabits; }
    public void setTopHabits(List<String> topHabits) { this.topHabits = topHabits; }
    public List<String> getAchievementsUnlocked() { return achievementsUnlocked; }
    public void setAchievementsUnlocked(List<String> achievementsUnlocked) { this.achievementsUnlocked = achievementsUnlocked; }
    public String getSummaryMessage() { return summaryMessage; }
    public void setSummaryMessage(String summaryMessage) { this.summaryMessage = summaryMessage; }
}

