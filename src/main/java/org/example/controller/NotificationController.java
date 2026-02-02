package org.example.controller;

import org.example.*;
import org.example.dto.NotificationDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for notification endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/notifications")
public class NotificationController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final AnalyticsService analyticsService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService,
                                AnalyticsService analyticsService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get all notifications for a user.
     *
     * GET /api/users/{userId}/notifications
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
        // Validate user exists
        userService.getUser(userId);

        // Get user data
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);
        UserPreferences preferences = userService.getUserPreferences(userId);

        // Get unique habits from habit checks
        List<Habit> habits = habitService.getAllHabitChecks().stream()
            .map(HabitCheck::habit)
            .distinct()
            .toList();

        // Get goals
        List<Goal> goals = goalService.getAllGoals();

        // Generate notifications
        LocalDate currentDate = LocalDate.now();
        List<Notification> notifications = notificationService.generateNotifications(
            userStats, habits, goals, habitService, goalService, analyticsService, preferences, currentDate);

        // Convert to DTOs
        List<NotificationDto> notificationDtos = notifications.stream()
            .map(this::toNotificationDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(notificationDtos);
    }

    /**
     * Get unread notifications count.
     *
     * GET /api/users/{userId}/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        // Validate user exists
        userService.getUser(userId);

        // Get user data
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);
        UserPreferences preferences = userService.getUserPreferences(userId);

        // Get unique habits from habit checks
        List<Habit> habits = habitService.getAllHabitChecks().stream()
            .map(HabitCheck::habit)
            .distinct()
            .toList();

        // Get goals
        List<Goal> goals = goalService.getAllGoals();

        // Generate notifications
        LocalDate currentDate = LocalDate.now();
        List<Notification> notifications = notificationService.generateNotifications(
            userStats, habits, goals, habitService, goalService, analyticsService, preferences, currentDate);

        long unreadCount = notificationService.getUnreadCount(notifications);

        return ResponseEntity.ok(unreadCount);
    }

    /**
     * Mark notification as read.
     *
     * PUT /api/users/{userId}/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long userId, @PathVariable Long notificationId) {
        // Validate user exists
        userService.getUser(userId);

        notificationService.markAsRead(notificationId);

        return ResponseEntity.ok().build();
    }

    private NotificationDto toNotificationDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.id());
        dto.setType(notification.type().name());
        dto.setTitle(notification.title());
        dto.setMessage(notification.message());
        dto.setCreatedAt(notification.createdAt());
        dto.setScheduledFor(notification.scheduledFor());
        dto.setRead(notification.read());
        dto.setActionUrl(notification.actionUrl());
        return dto;
    }
}

