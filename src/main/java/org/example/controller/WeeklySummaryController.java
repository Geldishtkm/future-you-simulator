package org.example.controller;

import org.example.*;
import org.example.dto.WeeklySummaryDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for weekly summary endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/weekly-summary")
public class WeeklySummaryController {
    private final UserService userService;
    private final WeeklySummaryService weeklySummaryService;
    private final AnalyticsService analyticsService;

    @Autowired
    public WeeklySummaryController(UserService userService, WeeklySummaryService weeklySummaryService,
                                  AnalyticsService analyticsService) {
        this.userService = userService;
        this.weeklySummaryService = weeklySummaryService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get weekly summary for a user.
     *
     * GET /api/users/{userId}/weekly-summary?weekEnd=2025-01-15
     */
    @GetMapping
    public ResponseEntity<WeeklySummaryDto> getWeeklySummary(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekEnd) {
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

        // Generate weekly summary
        WeeklySummary summary = weeklySummaryService.generateWeeklySummary(
            userStats, habits, goals, habitService, goalService, analyticsService, weekEnd);

        // Convert to DTO
        WeeklySummaryDto dto = toWeeklySummaryDto(summary);

        return ResponseEntity.ok(dto);
    }

    private WeeklySummaryDto toWeeklySummaryDto(WeeklySummary summary) {
        WeeklySummaryDto dto = new WeeklySummaryDto();
        dto.setWeekStart(summary.weekStart());
        dto.setWeekEnd(summary.weekEnd());
        dto.setTotalXpGained(summary.totalXpGained());
        dto.setHabitsCompleted(summary.habitsCompleted());
        dto.setGoalsProgressed(summary.goalsProgressed());
        dto.setActiveDays(summary.activeDays());
        dto.setAverageConsistency(summary.averageConsistency());
        dto.setLongestStreak(summary.longestStreak());
        dto.setTopHabits(summary.topHabits());
        dto.setAchievementsUnlocked(summary.achievementsUnlocked());
        dto.setSummaryMessage(summary.summaryMessage());
        return dto;
    }
}

