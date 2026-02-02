package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for tracking and calculating user milestones.
 */
@Service
public class MilestoneService {

    /**
     * Gets all milestones for a user.
     *
     * @param userStats the user's current stats
     * @param habits the user's habits
     * @param goals the user's goals
     * @param habitService the habit service
     * @param goalService the goal service
     * @param analyticsService the analytics service
     * @param currentDate the current date
     * @return a list of milestones
     */
    public List<Milestone> getMilestones(UserStats userStats,
                                        List<Habit> habits,
                                        List<Goal> goals,
                                        HabitService habitService,
                                        GoalService goalService,
                                        AnalyticsService analyticsService,
                                        LocalDate currentDate) {
        List<Milestone> milestones = new ArrayList<>();

        // XP milestones
        milestones.add(createXpMilestone(userStats, 1000, "First Thousand", "Earn 1,000 total XP"));
        milestones.add(createXpMilestone(userStats, 5000, "Five K Club", "Earn 5,000 total XP"));
        milestones.add(createXpMilestone(userStats, 10000, "Ten K Champion", "Earn 10,000 total XP"));
        milestones.add(createXpMilestone(userStats, 25000, "Quarter Century", "Earn 25,000 total XP"));
        milestones.add(createXpMilestone(userStats, 50000, "Half Century", "Earn 50,000 total XP"));
        milestones.add(createXpMilestone(userStats, 100000, "Century Master", "Earn 100,000 total XP"));

        // Level milestones
        milestones.add(createLevelMilestone(userStats, 5, "Rising Star", "Reach Level 5"));
        milestones.add(createLevelMilestone(userStats, 10, "Experienced", "Reach Level 10"));
        milestones.add(createLevelMilestone(userStats, 20, "Veteran", "Reach Level 20"));
        milestones.add(createLevelMilestone(userStats, 50, "Master", "Reach Level 50"));
        milestones.add(createLevelMilestone(userStats, 100, "Legend", "Reach Level 100"));

        // Streak milestones
        Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
            habits, habitService, currentDate);
        int maxStreak = streaks.values().stream()
            .mapToInt(s -> Math.max(s.currentStreak(), s.longestStreak()))
            .max()
            .orElse(0);
        
        milestones.add(createStreakMilestone(maxStreak, 7, "Week Warrior", "Maintain a 7-day streak"));
        milestones.add(createStreakMilestone(maxStreak, 30, "Month Master", "Maintain a 30-day streak"));
        milestones.add(createStreakMilestone(maxStreak, 100, "Century Club", "Maintain a 100-day streak"));
        milestones.add(createStreakMilestone(maxStreak, 365, "Year Champion", "Maintain a 365-day streak"));

        // Habits completed milestones
        long totalHabitCompletions = habitService.getAllHabitChecks().stream()
            .filter(check -> check.result() == HabitCheckResult.DONE)
            .count();
        milestones.add(createHabitsMilestone(totalHabitCompletions, 100, "Habit Starter", "Complete 100 habits"));
        milestones.add(createHabitsMilestone(totalHabitCompletions, 500, "Habit Enthusiast", "Complete 500 habits"));
        milestones.add(createHabitsMilestone(totalHabitCompletions, 1000, "Habit Master", "Complete 1,000 habits"));

        // Days active milestones
        List<XpHistoryEntry> history = analyticsService.buildXpHistory(habitService, goalService);
        long uniqueActiveDays = history.stream()
            .map(XpHistoryEntry::date)
            .distinct()
            .count();
        milestones.add(createDaysActiveMilestone(uniqueActiveDays, 30, "Month Active", "Be active for 30 days"));
        milestones.add(createDaysActiveMilestone(uniqueActiveDays, 100, "Century Active", "Be active for 100 days"));
        milestones.add(createDaysActiveMilestone(uniqueActiveDays, 365, "Year Active", "Be active for 365 days"));

        return milestones;
    }

    private Milestone createXpMilestone(UserStats stats, int targetXp, String title, String description) {
        int currentXp = stats.getTotalXp();
        boolean achieved = currentXp >= targetXp;
        return achieved 
            ? Milestone.achieved(MilestoneType.TOTAL_XP, title, description, targetXp, currentXp, LocalDate.now())
            : Milestone.unachieved(MilestoneType.TOTAL_XP, title, description, targetXp, currentXp);
    }

    private Milestone createLevelMilestone(UserStats stats, int targetLevel, String title, String description) {
        int currentLevel = stats.getLevel();
        boolean achieved = currentLevel >= targetLevel;
        return achieved
            ? Milestone.achieved(MilestoneType.LEVEL, title, description, targetLevel, currentLevel, LocalDate.now())
            : Milestone.unachieved(MilestoneType.LEVEL, title, description, targetLevel, currentLevel);
    }

    private Milestone createStreakMilestone(int currentStreak, int targetStreak, String title, String description) {
        boolean achieved = currentStreak >= targetStreak;
        return achieved
            ? Milestone.achieved(MilestoneType.STREAK, title, description, targetStreak, currentStreak, LocalDate.now())
            : Milestone.unachieved(MilestoneType.STREAK, title, description, targetStreak, currentStreak);
    }

    private Milestone createHabitsMilestone(long currentCount, int targetCount, String title, String description) {
        boolean achieved = currentCount >= targetCount;
        return achieved
            ? Milestone.achieved(MilestoneType.HABITS_COMPLETED, title, description, targetCount, (int) currentCount, LocalDate.now())
            : Milestone.unachieved(MilestoneType.HABITS_COMPLETED, title, description, targetCount, (int) currentCount);
    }

    private Milestone createDaysActiveMilestone(long currentDays, int targetDays, String title, String description) {
        boolean achieved = currentDays >= targetDays;
        return achieved
            ? Milestone.achieved(MilestoneType.DAYS_ACTIVE, title, description, targetDays, (int) currentDays, LocalDate.now())
            : Milestone.unachieved(MilestoneType.DAYS_ACTIVE, title, description, targetDays, (int) currentDays);
    }
}

