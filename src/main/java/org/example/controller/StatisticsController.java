package org.example.controller;

import org.example.*;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for statistics endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/statistics")
public class StatisticsController {
    private final UserService userService;
    private final StatisticsService statisticsService;
    private final AnalyticsService analyticsService;

    @Autowired
    public StatisticsController(UserService userService, StatisticsService statisticsService,
                               AnalyticsService analyticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get comprehensive statistics for a user.
     *
     * GET /api/users/{userId}/statistics
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStatistics(@PathVariable Long userId) {
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

        // Generate statistics
        Map<String, Object> stats = statisticsService.generateStatistics(
            userStats, habits, goals, habitService, goalService, analyticsService);

        return ResponseEntity.ok(stats);
    }
}

