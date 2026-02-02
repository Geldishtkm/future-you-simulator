package org.example.controller;

import org.example.*;
import org.example.dto.ActivityFeedEntryDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for activity feed endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/activity-feed")
public class ActivityFeedController {
    private final UserService userService;
    private final ActivityFeedService activityFeedService;
    private final AnalyticsService analyticsService;

    @Autowired
    public ActivityFeedController(UserService userService, ActivityFeedService activityFeedService,
                                 AnalyticsService analyticsService) {
        this.userService = userService;
        this.activityFeedService = activityFeedService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get activity feed for a user.
     *
     * GET /api/users/{userId}/activity-feed?limit=50
     */
    @GetMapping
    public ResponseEntity<List<ActivityFeedEntryDto>> getActivityFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "50") Integer limit) {
        // Validate user exists
        userService.getUser(userId);

        // Get user data
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);

        // Get unique habits from habit checks
        List<Habit> habits = habitService.getAllHabitChecks().stream()
            .map(HabitCheck::habit)
            .distinct()
            .toList();

        // Get goals
        List<Goal> goals = goalService.getAllGoals();

        // Generate activity feed
        List<ActivityFeedEntry> feed = activityFeedService.generateActivityFeed(
            userStats, habits, goals, habitService, goalService, analyticsService, limit);

        // Convert to DTOs
        List<ActivityFeedEntryDto> feedDtos = feed.stream()
            .map(this::toActivityFeedEntryDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(feedDtos);
    }

    private ActivityFeedEntryDto toActivityFeedEntryDto(ActivityFeedEntry entry) {
        ActivityFeedEntryDto dto = new ActivityFeedEntryDto();
        dto.setTimestamp(entry.timestamp());
        dto.setType(entry.type().name());
        dto.setTitle(entry.title());
        dto.setDescription(entry.description());
        dto.setXpChange(entry.xpChange());
        dto.setSource(entry.source());
        return dto;
    }
}

