package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for generating monthly summary reports.
 */
@Service
public class MonthlySummaryService {

    private final AchievementService achievementService;

    public MonthlySummaryService() {
        this.achievementService = new AchievementService();
    }

    public MonthlySummaryService(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    /**
     * Generates a monthly summary for a user.
     *
     * @param userStats the user's current stats
     * @param habits the user's habits
     * @param goals the user's goals
     * @param habitService the habit service
     * @param goalService the goal service
     * @param analyticsService the analytics service
     * @param monthEndDate the end date of the month (defaults to today if null)
     * @return a MonthlySummary with all monthly metrics
     */
    public MonthlySummary generateMonthlySummary(UserStats userStats,
                                                 List<Habit> habits,
                                                 List<Goal> goals,
                                                 HabitService habitService,
                                                 GoalService goalService,
                                                 AnalyticsService analyticsService,
                                                 LocalDate monthEndDate) {
        final LocalDate monthEnd = monthEndDate != null ? monthEndDate : LocalDate.now();
        LocalDate monthStart = monthEnd.withDayOfMonth(1);

        // Get XP history for the month
        List<XpHistoryEntry> allHistory = analyticsService.buildXpHistory(habitService, goalService);
        List<XpHistoryEntry> monthHistory = allHistory.stream()
            .filter(entry -> !entry.date().isBefore(monthStart) && !entry.date().isAfter(monthEnd))
            .sorted(Comparator.comparing(XpHistoryEntry::date))
            .collect(Collectors.toList());

        // Calculate total XP gained
        int totalXpGained = monthHistory.stream()
            .filter(entry -> entry.xpChange() > 0)
            .mapToInt(XpHistoryEntry::xpChange)
            .sum();

        // Count habits completed
        List<HabitCheck> monthHabitChecks = habitService.getAllHabitChecks().stream()
            .filter(check -> !check.date().isBefore(monthStart) && !check.date().isAfter(monthEnd))
            .filter(check -> check.result() == HabitCheckResult.DONE)
            .collect(Collectors.toList());
        int habitsCompleted = monthHabitChecks.size();

        // Count goals progressed
        List<GoalNote> monthGoalNotes = goalService.getAllGoalNotes().stream()
            .filter(note -> !note.date().isBefore(monthStart) && !note.date().isAfter(monthEnd))
            .collect(Collectors.toList());
        int goalsProgressed = monthGoalNotes.size();

        // Count active days
        long activeDays = monthHistory.stream()
            .map(XpHistoryEntry::date)
            .distinct()
            .count();

        // Calculate average consistency
        Map<Goal, GoalConsistency> consistency = analyticsService.calculateAllGoalConsistency(
            goals, goalService, monthEnd);
        double averageConsistency = consistency.values().stream()
            .mapToDouble(GoalConsistency::consistencyScore)
            .average()
            .orElse(0.0);

        // Get longest streak
        Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
            habits, habitService, monthEnd);
        int longestStreak = streaks.values().stream()
            .mapToInt(HabitStreak::currentStreak)
            .max()
            .orElse(0);

        // Estimate level at start (approximate - in production, track historical levels)
        int levelAtStart = Math.max(1, userStats.getLevel() - (totalXpGained / 1000)); // Rough estimate
        int levelAtEnd = userStats.getLevel();
        int levelGained = Math.max(0, levelAtEnd - levelAtStart);

        // Get top habits by completion count
        List<String> topHabits = monthHabitChecks.stream()
            .collect(Collectors.groupingBy(
                check -> check.habit().getName(),
                Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // Get achievements unlocked this month
        List<Achievement> allAchievements = achievementService.calculateAchievements(
            userStats, habits, goals, habitService, goalService, analyticsService, monthEnd);
        List<String> achievementsUnlocked = allAchievements.stream()
            .filter(Achievement::isUnlocked)
            .filter(ach -> ach.getUnlockedDate() != null && 
                          !ach.getUnlockedDate().isBefore(monthStart) && 
                          !ach.getUnlockedDate().isAfter(monthEnd))
            .map(Achievement::getName)
            .collect(Collectors.toList());

        // Generate summary message
        String summaryMessage = generateSummaryMessage(
            totalXpGained, habitsCompleted, goalsProgressed, (int) activeDays, 
            averageConsistency, longestStreak, levelGained);

        return new MonthlySummary(
            monthStart,
            monthEnd,
            totalXpGained,
            habitsCompleted,
            goalsProgressed,
            (int) activeDays,
            averageConsistency,
            longestStreak,
            levelAtStart,
            levelAtEnd,
            levelGained,
            topHabits,
            achievementsUnlocked,
            summaryMessage
        );
    }

    private String generateSummaryMessage(int xpGained, int habitsCompleted, int goalsProgressed,
                                         int activeDays, double consistency, int longestStreak, int levelGained) {
        List<String> highlights = new ArrayList<>();

        if (xpGained > 0) {
            highlights.add(String.format("gained %d XP", xpGained));
        }
        if (levelGained > 0) {
            highlights.add(String.format("leveled up %d time%s", levelGained, levelGained > 1 ? "s" : ""));
        }
        if (habitsCompleted > 0) {
            highlights.add(String.format("completed %d habit check%s", 
                habitsCompleted, habitsCompleted > 1 ? "s" : ""));
        }
        if (goalsProgressed > 0) {
            highlights.add(String.format("made progress on %d goal%s", 
                goalsProgressed, goalsProgressed > 1 ? "s" : ""));
        }
        if (activeDays >= 25) {
            highlights.add("maintained excellent activity throughout the month");
        } else if (activeDays >= 15) {
            highlights.add(String.format("was active %d days this month", activeDays));
        }
        if (consistency >= 0.8) {
            highlights.add("maintained excellent consistency");
        }
        if (longestStreak >= 30) {
            highlights.add(String.format("maintained a %d-day streak", longestStreak));
        }

        if (highlights.isEmpty()) {
            return "This month was quiet. Time to get back on track!";
        }

        return "This month you " + String.join(", ", highlights) + "!";
    }
}

