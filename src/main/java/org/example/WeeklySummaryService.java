package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for generating weekly summary reports.
 */
@Service
public class WeeklySummaryService {

    private final AchievementService achievementService;

    public WeeklySummaryService() {
        this.achievementService = new AchievementService();
    }

    public WeeklySummaryService(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    /**
     * Generates a weekly summary for a user.
     *
     * @param userStats the user's current stats
     * @param habits the user's habits
     * @param goals the user's goals
     * @param habitService the habit service
     * @param goalService the goal service
     * @param analyticsService the analytics service
     * @param weekEndDate the end date of the week (defaults to today if null)
     * @return a WeeklySummary with all weekly metrics
     */
    public WeeklySummary generateWeeklySummary(UserStats userStats,
                                               List<Habit> habits,
                                               List<Goal> goals,
                                               HabitService habitService,
                                               GoalService goalService,
                                               AnalyticsService analyticsService,
                                               LocalDate weekEndDate) {
        final LocalDate weekEnd = weekEndDate != null ? weekEndDate : LocalDate.now();
        LocalDate weekStart = weekEnd.minusDays(6);

        // Get XP history for the week
        List<XpHistoryEntry> allHistory = analyticsService.buildXpHistory(habitService, goalService);
        List<XpHistoryEntry> weekHistory = allHistory.stream()
            .filter(entry -> !entry.date().isBefore(weekStart) && !entry.date().isAfter(weekEnd))
            .sorted(Comparator.comparing(XpHistoryEntry::date))
            .collect(Collectors.toList());

        // Calculate total XP gained
        int totalXpGained = weekHistory.stream()
            .filter(entry -> entry.xpChange() > 0)
            .mapToInt(XpHistoryEntry::xpChange)
            .sum();

        // Count habits completed
        List<HabitCheck> weekHabitChecks = habitService.getAllHabitChecks().stream()
            .filter(check -> !check.date().isBefore(weekStart) && !check.date().isAfter(weekEnd))
            .filter(check -> check.result() == HabitCheckResult.DONE)
            .collect(Collectors.toList());
        int habitsCompleted = weekHabitChecks.size();

        // Count goals progressed
        List<GoalNote> weekGoalNotes = goalService.getAllGoalNotes().stream()
            .filter(note -> !note.date().isBefore(weekStart) && !note.date().isAfter(weekEnd))
            .collect(Collectors.toList());
        int goalsProgressed = weekGoalNotes.size();

        // Count active days
        long activeDays = weekHistory.stream()
            .map(XpHistoryEntry::date)
            .distinct()
            .count();

        // Calculate average consistency
        Map<Goal, GoalConsistency> consistency = analyticsService.calculateAllGoalConsistency(
            goals, goalService, weekEnd);
        double averageConsistency = consistency.values().stream()
            .mapToDouble(GoalConsistency::consistencyScore)
            .average()
            .orElse(0.0);

        // Get longest streak
        Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
            habits, habitService, weekEnd);
        int longestStreak = streaks.values().stream()
            .mapToInt(HabitStreak::currentStreak)
            .max()
            .orElse(0);

        // Get top habits by completion count
        List<String> topHabits = weekHabitChecks.stream()
            .collect(Collectors.groupingBy(
                check -> check.habit().getName(),
                Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        // Get achievements unlocked this week
        List<Achievement> allAchievements = achievementService.calculateAchievements(
            userStats, habits, goals, habitService, goalService, analyticsService, weekEnd);
        List<String> achievementsUnlocked = allAchievements.stream()
            .filter(Achievement::isUnlocked)
            .filter(ach -> ach.getUnlockedDate() != null && 
                          !ach.getUnlockedDate().isBefore(weekStart) && 
                          !ach.getUnlockedDate().isAfter(weekEnd))
            .map(Achievement::getName)
            .collect(Collectors.toList());

        // Generate summary message
        int activeDaysInt = (int) activeDays;
        String summaryMessage = generateSummaryMessage(
            totalXpGained, habitsCompleted, goalsProgressed, activeDaysInt, 
            averageConsistency, longestStreak);

        return new WeeklySummary(
            weekStart,
            weekEnd,
            totalXpGained,
            habitsCompleted,
            goalsProgressed,
            activeDaysInt,
            averageConsistency,
            longestStreak,
            topHabits,
            achievementsUnlocked,
            summaryMessage
        );
    }

    private String generateSummaryMessage(int xpGained, int habitsCompleted, int goalsProgressed,
                                         int activeDays, double consistency, int longestStreak) {
        List<String> highlights = new ArrayList<>();

        if (xpGained > 0) {
            highlights.add(String.format("gained %d XP", xpGained));
        }
        if (habitsCompleted > 0) {
            highlights.add(String.format("completed %d habit check%s", 
                habitsCompleted, habitsCompleted > 1 ? "s" : ""));
        }
        if (goalsProgressed > 0) {
            highlights.add(String.format("made progress on %d goal%s", 
                goalsProgressed, goalsProgressed > 1 ? "s" : ""));
        }
        if (activeDays == 7) {
            highlights.add("maintained perfect activity all week");
        } else if (activeDays >= 5) {
            highlights.add(String.format("was active %d out of 7 days", activeDays));
        }
        if (consistency >= 0.8) {
            highlights.add("maintained excellent consistency");
        }
        if (longestStreak >= 7) {
            highlights.add(String.format("maintained a %d-day streak", longestStreak));
        }

        if (highlights.isEmpty()) {
            return "This week was quiet. Time to get back on track!";
        }

        return "This week you " + String.join(", ", highlights) + "!";
    }
}

