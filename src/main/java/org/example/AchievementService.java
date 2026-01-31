package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for calculating and managing user achievements.
 */
@Service
public class AchievementService {

    /**
     * Calculates all achievements for a user based on their stats and activity.
     *
     * @param userStats the user's current stats
     * @param habits the user's habits
     * @param goals the user's goals
     * @param habitService the habit service
     * @param goalService the goal service
     * @param analyticsService the analytics service
     * @param currentDate the current date
     * @return a list of achievements (both locked and unlocked)
     */
    public List<Achievement> calculateAchievements(UserStats userStats,
                                                   List<Habit> habits,
                                                   List<Goal> goals,
                                                   HabitService habitService,
                                                   GoalService goalService,
                                                   AnalyticsService analyticsService,
                                                   LocalDate currentDate) {
        List<Achievement> achievements = new ArrayList<>();

        // Level milestones
        achievements.add(calculateLevelAchievement(userStats, 5, "Rising Star", "Reached Level 5"));
        achievements.add(calculateLevelAchievement(userStats, 10, "Experienced", "Reached Level 10"));
        achievements.add(calculateLevelAchievement(userStats, 20, "Veteran", "Reached Level 20"));
        achievements.add(calculateLevelAchievement(userStats, 50, "Master", "Reached Level 50"));

        // Streak achievements
        Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
            habits, habitService, currentDate);
        achievements.add(calculateStreakAchievement(streaks, 7, "Week Warrior", "Maintained a 7-day streak"));
        achievements.add(calculateStreakAchievement(streaks, 30, "Month Master", "Maintained a 30-day streak"));
        achievements.add(calculateStreakAchievement(streaks, 100, "Century Club", "Maintained a 100-day streak"));

        // Consistency achievements
        Map<Goal, GoalConsistency> consistency = analyticsService.calculateAllGoalConsistency(
            goals, goalService, currentDate);
        double avgConsistency = consistency.values().stream()
            .mapToDouble(GoalConsistency::consistencyScore)
            .average()
            .orElse(0.0);
        achievements.add(calculateConsistencyAchievement(avgConsistency, 0.8, 
            "Consistency King", "Achieved 80% consistency"));
        achievements.add(calculateConsistencyAchievement(avgConsistency, 0.9, 
            "Consistency Master", "Achieved 90% consistency"));

        // XP achievements
        achievements.add(calculateXpAchievement(userStats, 1000, "XP Novice", "Earned 1,000 XP"));
        achievements.add(calculateXpAchievement(userStats, 5000, "XP Collector", "Earned 5,000 XP"));
        achievements.add(calculateXpAchievement(userStats, 10000, "XP Master", "Earned 10,000 XP"));
        achievements.add(calculateXpAchievement(userStats, 50000, "XP Legend", "Earned 50,000 XP"));

        // Activity achievements
        List<XpHistoryEntry> history = analyticsService.buildXpHistory(habitService, goalService);
        achievements.add(calculateWeeklyActivityAchievement(history, currentDate));
        achievements.add(calculateMonthlyActivityAchievement(history, currentDate));

        // First steps achievement
        if (!history.isEmpty()) {
            achievements.add(Achievement.unlocked(AchievementType.FIRST_STEPS, 
                "First Steps", "Completed your first activity", history.get(0).date()));
        } else {
            achievements.add(Achievement.locked(AchievementType.FIRST_STEPS, 
                "First Steps", "Complete your first activity"));
        }

        // Habit master achievement
        if (habits.size() >= 5) {
            achievements.add(Achievement.unlocked(AchievementType.HABIT_MASTER, 
                "Habit Master", "Created 5 or more habits", currentDate));
        } else {
            achievements.add(Achievement.locked(AchievementType.HABIT_MASTER, 
                "Habit Master", "Create 5 or more habits"));
        }

        return achievements;
    }

    private Achievement calculateLevelAchievement(UserStats stats, int targetLevel, 
                                                  String name, String description) {
        if (stats.getLevel() >= targetLevel) {
            return Achievement.unlocked(AchievementType.LEVEL_MILESTONE, name, description, 
                LocalDate.now()); // Approximate - in production, track when level was reached
        } else {
            return Achievement.locked(AchievementType.LEVEL_MILESTONE, name, description);
        }
    }

    private Achievement calculateStreakAchievement(Map<Habit, HabitStreak> streaks, 
                                                  int targetStreak, String name, String description) {
        boolean hasStreak = streaks.values().stream()
            .anyMatch(s -> s.currentStreak() >= targetStreak || s.longestStreak() >= targetStreak);
        
        if (hasStreak) {
            LocalDate unlockDate = streaks.values().stream()
                .filter(s -> s.currentStreak() >= targetStreak || s.longestStreak() >= targetStreak)
                .map(HabitStreak::streakStartDate)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now());
            return Achievement.unlocked(AchievementType.STREAK_MASTER, name, description, unlockDate);
        } else {
            return Achievement.locked(AchievementType.STREAK_MASTER, name, description);
        }
    }

    private Achievement calculateConsistencyAchievement(double consistency, double target, 
                                                       String name, String description) {
        if (consistency >= target) {
            return Achievement.unlocked(AchievementType.CONSISTENCY_KING, name, description, LocalDate.now());
        } else {
            return Achievement.locked(AchievementType.CONSISTENCY_KING, name, description);
        }
    }

    private Achievement calculateXpAchievement(UserStats stats, int targetXp, 
                                              String name, String description) {
        if (stats.getTotalXp() >= targetXp) {
            return Achievement.unlocked(AchievementType.XP_HARVESTER, name, description, LocalDate.now());
        } else {
            return Achievement.locked(AchievementType.XP_HARVESTER, name, description);
        }
    }

    private Achievement calculateWeeklyActivityAchievement(List<XpHistoryEntry> history, LocalDate currentDate) {
        LocalDate weekStart = currentDate.minusDays(6);
        long activeDays = history.stream()
            .filter(entry -> !entry.date().isBefore(weekStart) && !entry.date().isAfter(currentDate))
            .map(XpHistoryEntry::date)
            .distinct()
            .count();
        
        if (activeDays >= 7) {
            return Achievement.unlocked(AchievementType.WEEK_WARRIOR, 
                "Perfect Week", "Active every day for a week", currentDate);
        } else {
            return Achievement.locked(AchievementType.WEEK_WARRIOR, 
                "Perfect Week", "Be active every day for a week");
        }
    }

    private Achievement calculateMonthlyActivityAchievement(List<XpHistoryEntry> history, LocalDate currentDate) {
        LocalDate monthStart = currentDate.minusDays(29);
        long activeDays = history.stream()
            .filter(entry -> !entry.date().isBefore(monthStart) && !entry.date().isAfter(currentDate))
            .map(XpHistoryEntry::date)
            .distinct()
            .count();
        
        if (activeDays >= 30) {
            return Achievement.unlocked(AchievementType.MONTH_CHAMPION, 
                "Perfect Month", "Active every day for a month", currentDate);
        } else {
            return Achievement.locked(AchievementType.MONTH_CHAMPION, 
                "Perfect Month", "Be active every day for a month");
        }
    }
}

