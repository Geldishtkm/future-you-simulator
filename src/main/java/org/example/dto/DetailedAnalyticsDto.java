package org.example.dto;

import java.util.List;

/**
 * DTO for detailed analytics response.
 */
public class DetailedAnalyticsDto {
    private double consistencyScore;
    private List<HabitStreakDto> habitStreaks;
    private List<TrendDto> trends;
    private BurnoutWarningDto burnoutWarning;
    private List<XpHistoryEntryDto> xpHistory;

    public DetailedAnalyticsDto() {
    }

    public double getConsistencyScore() {
        return consistencyScore;
    }

    public void setConsistencyScore(double consistencyScore) {
        this.consistencyScore = consistencyScore;
    }

    public List<HabitStreakDto> getHabitStreaks() {
        return habitStreaks;
    }

    public void setHabitStreaks(List<HabitStreakDto> habitStreaks) {
        this.habitStreaks = habitStreaks;
    }

    public List<TrendDto> getTrends() {
        return trends;
    }

    public void setTrends(List<TrendDto> trends) {
        this.trends = trends;
    }

    public BurnoutWarningDto getBurnoutWarning() {
        return burnoutWarning;
    }

    public void setBurnoutWarning(BurnoutWarningDto burnoutWarning) {
        this.burnoutWarning = burnoutWarning;
    }

    public List<XpHistoryEntryDto> getXpHistory() {
        return xpHistory;
    }

    public void setXpHistory(List<XpHistoryEntryDto> xpHistory) {
        this.xpHistory = xpHistory;
    }
}

