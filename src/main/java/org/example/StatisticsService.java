package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating detailed statistics and insights.
 */
@Service
public class StatisticsService {

    /**
     * Generates comprehensive statistics for a user.
     *
     * @param userStats the user's current stats
     * @param habits the user's habits
     * @param goals the user's goals
     * @param habitService the habit service
     * @param goalService the goal service
     * @param analyticsService the analytics service
     * @return a map of statistics
     */
    public Map<String, Object> generateStatistics(UserStats userStats,
                                                 List<Habit> habits,
                                                 List<Goal> goals,
                                                 HabitService habitService,
                                                 GoalService goalService,
                                                 AnalyticsService analyticsService) {
        Map<String, Object> stats = new HashMap<>();
        LocalDate currentDate = LocalDate.now();

        // Basic stats
        stats.put("totalXp", userStats.getTotalXp());
        stats.put("currentLevel", userStats.getLevel());
        stats.put("totalHabits", habits.size());
        stats.put("totalGoals", goals.size());

        // Habit statistics
        List<HabitCheck> allChecks = habitService.getAllHabitChecks();
        long totalCompletions = allChecks.stream()
            .filter(check -> check.result() == HabitCheckResult.DONE)
            .count();
        long totalMisses = allChecks.stream()
            .filter(check -> check.result() == HabitCheckResult.MISSED)
            .count();
        
        Map<String, Object> habitStats = new HashMap<>();
        habitStats.put("totalCompletions", totalCompletions);
        habitStats.put("totalMisses", totalMisses);
        habitStats.put("completionRate", totalCompletions + totalMisses > 0 
            ? (double) totalCompletions / (totalCompletions + totalMisses) 
            : 0.0);
        
        // Most completed habit
        Map<String, Long> habitCounts = allChecks.stream()
            .filter(check -> check.result() == HabitCheckResult.DONE)
            .collect(java.util.stream.Collectors.groupingBy(
                check -> check.habit().getName(),
                java.util.stream.Collectors.counting()));
        
        if (!habitCounts.isEmpty()) {
            String mostCompleted = habitCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
            habitStats.put("mostCompletedHabit", mostCompleted);
            habitStats.put("mostCompletedCount", habitCounts.get(mostCompleted));
        }
        
        stats.put("habitStatistics", habitStats);

        // Goal statistics
        List<GoalNote> allNotes = goalService.getAllGoalNotes();
        int totalGoalXp = allNotes.stream()
            .mapToInt(GoalNote::points)
            .sum();
        
        Map<String, Object> goalStats = new HashMap<>();
        goalStats.put("totalProgressNotes", allNotes.size());
        goalStats.put("totalGoalXp", totalGoalXp);
        goalStats.put("averageXpPerNote", allNotes.isEmpty() ? 0.0 : (double) totalGoalXp / allNotes.size());
        
        stats.put("goalStatistics", goalStats);

        // Consistency statistics
        Map<Goal, GoalConsistency> consistency = analyticsService.calculateAllGoalConsistency(
            goals, goalService, currentDate);
        double avgConsistency = consistency.values().stream()
            .mapToDouble(GoalConsistency::consistencyScore)
            .average()
            .orElse(0.0);
        
        stats.put("averageConsistency", avgConsistency);

        // Streak statistics
        Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
            habits, habitService, currentDate);
        int maxStreak = streaks.values().stream()
            .mapToInt(s -> Math.max(s.currentStreak(), s.longestStreak()))
            .max()
            .orElse(0);
        double avgStreak = streaks.values().stream()
            .mapToInt(HabitStreak::currentStreak)
            .average()
            .orElse(0.0);
        
        Map<String, Object> streakStats = new HashMap<>();
        streakStats.put("maxStreak", maxStreak);
        streakStats.put("averageStreak", avgStreak);
        streakStats.put("activeStreaks", streaks.values().stream()
            .mapToInt(HabitStreak::currentStreak)
            .filter(s -> s > 0)
            .count());
        
        stats.put("streakStatistics", streakStats);

        // Activity statistics
        List<XpHistoryEntry> history = analyticsService.buildXpHistory(habitService, goalService);
        long uniqueActiveDays = history.stream()
            .map(XpHistoryEntry::date)
            .distinct()
            .count();
        
        LocalDate firstActivity = history.stream()
            .map(XpHistoryEntry::date)
            .min(LocalDate::compareTo)
            .orElse(currentDate);
        
        long daysSinceFirstActivity = java.time.temporal.ChronoUnit.DAYS.between(firstActivity, currentDate);
        
        Map<String, Object> activityStats = new HashMap<>();
        activityStats.put("uniqueActiveDays", uniqueActiveDays);
        activityStats.put("firstActivityDate", firstActivity);
        activityStats.put("daysSinceFirstActivity", daysSinceFirstActivity);
        activityStats.put("activityRate", daysSinceFirstActivity > 0 
            ? (double) uniqueActiveDays / daysSinceFirstActivity 
            : 0.0);
        
        stats.put("activityStatistics", activityStats);

        // XP statistics
        int totalXpGained = history.stream()
            .filter(entry -> entry.xpChange() > 0)
            .mapToInt(XpHistoryEntry::xpChange)
            .sum();
        int totalXpLost = Math.abs(history.stream()
            .filter(entry -> entry.xpChange() < 0)
            .mapToInt(XpHistoryEntry::xpChange)
            .sum());
        
        Map<String, Object> xpStats = new HashMap<>();
        xpStats.put("totalXpGained", totalXpGained);
        xpStats.put("totalXpLost", totalXpLost);
        xpStats.put("netXp", totalXpGained - totalXpLost);
        xpStats.put("averageXpPerDay", uniqueActiveDays > 0 
            ? (double) totalXpGained / uniqueActiveDays 
            : 0.0);
        
        stats.put("xpStatistics", xpStats);

        return stats;
    }
}

