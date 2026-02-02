package org.example.controller;

import org.example.*;
import org.example.dto.MonthlySummaryDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for monthly summary endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/monthly-summary")
public class MonthlySummaryController {
    private final UserService userService;
    private final MonthlySummaryService monthlySummaryService;
    private final AnalyticsService analyticsService;

    @Autowired
    public MonthlySummaryController(UserService userService, MonthlySummaryService monthlySummaryService,
                                   AnalyticsService analyticsService) {
        this.userService = userService;
        this.monthlySummaryService = monthlySummaryService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get monthly summary for a user.
     *
     * GET /api/users/{userId}/monthly-summary?monthEnd=2025-01-31
     */
    @GetMapping
    public ResponseEntity<MonthlySummaryDto> getMonthlySummary(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate monthEnd) {
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

        // Generate monthly summary
        MonthlySummary summary = monthlySummaryService.generateMonthlySummary(
            userStats, habits, goals, habitService, goalService, analyticsService, monthEnd);

        // Convert to DTO
        MonthlySummaryDto dto = toMonthlySummaryDto(summary);

        return ResponseEntity.ok(dto);
    }

    private MonthlySummaryDto toMonthlySummaryDto(MonthlySummary summary) {
        MonthlySummaryDto dto = new MonthlySummaryDto();
        dto.setMonthStart(summary.monthStart());
        dto.setMonthEnd(summary.monthEnd());
        dto.setTotalXpGained(summary.totalXpGained());
        dto.setHabitsCompleted(summary.habitsCompleted());
        dto.setGoalsProgressed(summary.goalsProgressed());
        dto.setActiveDays(summary.activeDays());
        dto.setAverageConsistency(summary.averageConsistency());
        dto.setLongestStreak(summary.longestStreak());
        dto.setLevelAtStart(summary.levelAtStart());
        dto.setLevelAtEnd(summary.levelAtEnd());
        dto.setLevelGained(summary.levelGained());
        dto.setTopHabits(summary.topHabits());
        dto.setAchievementsUnlocked(summary.achievementsUnlocked());
        dto.setSummaryMessage(summary.summaryMessage());
        return dto;
    }
}

