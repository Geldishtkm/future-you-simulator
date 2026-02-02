package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing notifications and reminders.
 */
@Service
public class NotificationService {
    private final List<Notification> notifications = new ArrayList<>();
    private long nextId = 1;

    /**
     * Generates notifications for a user based on their activity and preferences.
     *
     * @param userStats the user's stats
     * @param habits the user's habits
     * @param goals the user's goals
     * @param habitService the habit service
     * @param goalService the goal service
     * @param analyticsService the analytics service
     * @param preferences the user's preferences
     * @param currentDate the current date
     * @return a list of notifications
     */
    public List<Notification> generateNotifications(UserStats userStats,
                                                    List<Habit> habits,
                                                    List<Goal> goals,
                                                    HabitService habitService,
                                                    GoalService goalService,
                                                    AnalyticsService analyticsService,
                                                    UserPreferences preferences,
                                                    LocalDate currentDate) {
        List<Notification> notifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Habit reminders (if enabled)
        if (preferences.isEmailNotifications() && preferences.getDailyReminderTime() != null) {
            LocalTime reminderTime = preferences.getDailyReminderTime();
            LocalDateTime reminderDateTime = currentDate.atTime(reminderTime);
            
            if (reminderDateTime.isAfter(now) || reminderDateTime.isBefore(now.minusHours(1))) {
                for (Habit habit : habits) {
                    notifications.add(new Notification(
                        nextId++,
                        NotificationType.HABIT_REMINDER,
                        "Habit Reminder",
                        String.format("Don't forget to complete '%s' today!", habit.getName()),
                        now,
                        reminderDateTime,
                        false,
                        "/habits"
                    ));
                }
            }
        }

        // Goal deadline warnings
        for (Goal goal : goals) {
            if (goal.getTargetDate() != null) {
                long daysUntilDeadline = java.time.temporal.ChronoUnit.DAYS.between(currentDate, goal.getTargetDate());
                if (daysUntilDeadline <= 7 && daysUntilDeadline > 0) {
                    notifications.add(new Notification(
                        nextId++,
                        NotificationType.GOAL_DEADLINE,
                        "Goal Deadline Approaching",
                        String.format("'%s' deadline is in %d day%s", goal.getTitle(), daysUntilDeadline, daysUntilDeadline > 1 ? "s" : ""),
                        now,
                        now,
                        false,
                        "/goals"
                    ));
                }
            }
        }

        // Streak warnings
        Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
            habits, habitService, currentDate);
        for (Map.Entry<Habit, HabitStreak> entry : streaks.entrySet()) {
            HabitStreak streak = entry.getValue();
            if (streak.currentStreak() > 0) {
                LocalDate lastActivity = streak.streakStartDate();
                if (lastActivity.isBefore(currentDate.minusDays(1))) {
                    notifications.add(new Notification(
                        nextId++,
                        NotificationType.STREAK_WARNING,
                        "Streak Warning",
                        String.format("Your '%s' streak is at risk! Complete it today to maintain your %d-day streak.",
                            entry.getKey().getName(), streak.currentStreak()),
                        now,
                        now,
                        false,
                        "/habits"
                    ));
                }
            }
        }

        // Level up notification (if recently leveled up)
        // This would need historical tracking - for now, we'll check if level is high
        if (userStats.getLevel() >= 5) {
            notifications.add(new Notification(
                nextId++,
                NotificationType.LEVEL_UP,
                "Level Up!",
                String.format("Congratulations! You've reached Level %d!", userStats.getLevel()),
                now,
                now,
                false,
                "/dashboard"
            ));
        }

        // Motivational messages
        double consistency = analyticsService.calculateAllGoalConsistency(goals, goalService, currentDate)
            .values().stream()
            .mapToDouble(GoalConsistency::consistencyScore)
            .average()
            .orElse(0.0);
        
        if (consistency >= 0.8) {
            notifications.add(new Notification(
                nextId++,
                NotificationType.MOTIVATIONAL,
                "Great Job!",
                "You're maintaining excellent consistency! Keep up the amazing work!",
                now,
                now,
                false,
                "/dashboard"
            ));
        }

        return notifications.stream()
            .sorted(Comparator.comparing(Notification::scheduledFor))
            .collect(Collectors.toList());
    }

    /**
     * Marks a notification as read.
     */
    public void markAsRead(Long notificationId) {
        // In production, this would update the database
        // For now, we'll just track it in memory
    }

    /**
     * Gets unread notifications count.
     */
    public long getUnreadCount(List<Notification> notifications) {
        return notifications.stream()
            .filter(n -> !n.read())
            .count();
    }
}

