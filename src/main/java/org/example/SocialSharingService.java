package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for generating shareable content for social media.
 */
@Service
public class SocialSharingService {

    /**
     * Generates shareable content for achievements.
     */
    public ShareableContent generateAchievementShare(Achievement achievement) {
        return new ShareableContent(
            ShareType.ACHIEVEMENT,
            "Achievement Unlocked!",
            String.format("I just unlocked the '%s' achievement! %s", 
                achievement.getName(), achievement.getDescription()),
            null,
            achievement.getUnlockedDate() != null ? achievement.getUnlockedDate() : LocalDate.now(),
            0,
            achievement.getName()
        );
    }

    /**
     * Generates shareable content for milestones.
     */
    public ShareableContent generateMilestoneShare(Milestone milestone) {
        return new ShareableContent(
            ShareType.MILESTONE,
            "Milestone Reached!",
            String.format("I reached the '%s' milestone! %s (%d/%d)", 
                milestone.title(), milestone.description(), 
                milestone.currentValue(), milestone.targetValue()),
            null,
            milestone.achievedDate() != null ? milestone.achievedDate() : LocalDate.now(),
            milestone.currentValue(),
            milestone.title()
        );
    }

    /**
     * Generates shareable content for level ups.
     */
    public ShareableContent generateLevelUpShare(UserStats userStats) {
        return new ShareableContent(
            ShareType.LEVEL_UP,
            "Level Up!",
            String.format("I just reached Level %d with %d total XP! 🎉", 
                userStats.getLevel(), userStats.getTotalXp()),
            null,
            LocalDate.now(),
            userStats.getLevel(),
            null
        );
    }

    /**
     * Generates shareable content for streaks.
     */
    public ShareableContent generateStreakShare(Habit habit, HabitStreak streak) {
        return new ShareableContent(
            ShareType.STREAK,
            "Streak Achievement!",
            String.format("I've maintained a %d-day streak for '%s'! 🔥", 
                streak.currentStreak(), habit.getName()),
            null,
            LocalDate.now(),
            streak.currentStreak(),
            habit.getName()
        );
    }

    /**
     * Generates shareable content for weekly summary.
     */
    public ShareableContent generateWeeklySummaryShare(WeeklySummary summary) {
        return new ShareableContent(
            ShareType.WEEKLY_SUMMARY,
            "Weekly Progress!",
            String.format("This week I gained %d XP, completed %d habits, and was active %d days! 💪", 
                summary.totalXpGained(), summary.habitsCompleted(), summary.activeDays()),
            null,
            summary.weekEnd(),
            summary.totalXpGained(),
            null
        );
    }

    /**
     * Generates shareable content for monthly summary.
     */
    public ShareableContent generateMonthlySummaryShare(MonthlySummary summary) {
        return new ShareableContent(
            ShareType.MONTHLY_SUMMARY,
            "Monthly Progress!",
            String.format("This month I gained %d XP, leveled up %d times, and was active %d days! 🚀", 
                summary.totalXpGained(), summary.levelGained(), summary.activeDays()),
            null,
            summary.monthEnd(),
            summary.totalXpGained(),
            null
        );
    }

    /**
     * Generates shareable content for completed goals.
     */
    public ShareableContent generateGoalCompletedShare(Goal goal) {
        return new ShareableContent(
            ShareType.GOAL_COMPLETED,
            "Goal Completed!",
            String.format("I just completed my goal: '%s'! 🎯", goal.getTitle()),
            null,
            LocalDate.now(),
            goal.getTotalProgressPoints(),
            goal.getTitle()
        );
    }

    /**
     * Gets all shareable content for a user.
     */
    public List<ShareableContent> getAllShareableContent(UserStats userStats,
                                                         List<Habit> habits,
                                                         List<Goal> goals,
                                                         HabitService habitService,
                                                         GoalService goalService,
                                                         AchievementService achievementService,
                                                         MilestoneService milestoneService,
                                                         AnalyticsService analyticsService,
                                                         LocalDate currentDate) {
        List<ShareableContent> content = new ArrayList<>();

        // Level up
        if (userStats.getLevel() > 1) {
            content.add(generateLevelUpShare(userStats));
        }

        // Achievements
        List<Achievement> achievements = achievementService.calculateAchievements(
            userStats, habits, goals, habitService, goalService, analyticsService, currentDate);
        achievements.stream()
            .filter(Achievement::isUnlocked)
            .forEach(ach -> content.add(generateAchievementShare(ach)));

        // Milestones
        List<Milestone> milestones = milestoneService.getMilestones(
            userStats, habits, goals, habitService, goalService, analyticsService, currentDate);
        milestones.stream()
            .filter(Milestone::achieved)
            .forEach(mil -> content.add(generateMilestoneShare(mil)));

        // Streaks
        Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
            habits, habitService, currentDate);
        streaks.entrySet().stream()
            .filter(e -> e.getValue().currentStreak() >= 7)
            .forEach(e -> content.add(generateStreakShare(e.getKey(), e.getValue())));

        return content;
    }
}

