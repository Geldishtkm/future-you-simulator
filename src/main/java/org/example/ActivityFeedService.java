package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for generating activity feed entries.
 */
@Service
public class ActivityFeedService {

    /**
     * Generates activity feed for a user.
     *
     * @param userStats the user's current stats
     * @param habits the user's habits
     * @param goals the user's goals
     * @param habitService the habit service
     * @param goalService the goal service
     * @param analyticsService the analytics service
     * @param limit maximum number of entries to return (default 50)
     * @return a list of activity feed entries, sorted by timestamp (newest first)
     */
    public List<ActivityFeedEntry> generateActivityFeed(UserStats userStats,
                                                        List<Habit> habits,
                                                        List<Goal> goals,
                                                        HabitService habitService,
                                                        GoalService goalService,
                                                        AnalyticsService analyticsService,
                                                        Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 50;
        }
        if (limit > 200) {
            limit = 200; // Cap at 200
        }

        List<ActivityFeedEntry> feed = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        // Add habit activities
        List<HabitCheck> habitChecks = habitService.getAllHabitChecks();
        for (HabitCheck check : habitChecks) {
            LocalDateTime timestamp = check.date().atStartOfDay();
            if (check.result() == HabitCheckResult.DONE) {
                feed.add(new ActivityFeedEntry(
                    timestamp,
                    ActivityType.HABIT_COMPLETED,
                    "Habit Completed",
                    String.format("Completed '%s'", check.habit().getName()),
                    check.habit().getDifficulty().getValue() * 10,
                    "HABIT"
                ));
            } else {
                feed.add(new ActivityFeedEntry(
                    timestamp,
                    ActivityType.HABIT_MISSED,
                    "Habit Missed",
                    String.format("Missed '%s'", check.habit().getName()),
                    -15,
                    "HABIT"
                ));
            }
        }

        // Add goal activities
        List<GoalNote> goalNotes = goalService.getAllGoalNotes();
        for (GoalNote note : goalNotes) {
            feed.add(new ActivityFeedEntry(
                note.date().atStartOfDay(),
                ActivityType.GOAL_PROGRESS,
                "Goal Progress",
                String.format("Made progress on goal: %d XP", note.points()),
                note.points(),
                "GOAL"
            ));
        }

        // Add XP history entries
        List<XpHistoryEntry> xpHistory = analyticsService.buildXpHistory(habitService, goalService);
        for (XpHistoryEntry entry : xpHistory) {
            if (entry.xpChange() > 0) {
                feed.add(new ActivityFeedEntry(
                    entry.date().atStartOfDay(),
                    ActivityType.GOAL_PROGRESS,
                    "XP Gained",
                    String.format("Gained %d XP from %s", entry.xpChange(), entry.source().name()),
                    entry.xpChange(),
                    entry.source().name()
                ));
            }
        }

        // Add achievements (if we had achievement unlock dates, we'd add them here)
        // For now, we'll add a placeholder for recent achievements

        // Sort by timestamp (newest first) and limit
        return feed.stream()
            .sorted(Comparator.comparing(ActivityFeedEntry::timestamp).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
}

