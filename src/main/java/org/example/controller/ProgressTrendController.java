package org.example.controller;

import org.example.*;
import org.example.dto.ProgressTrendDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for progress trend endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/trends")
public class ProgressTrendController {
    private final UserService userService;
    private final ProgressTrendService progressTrendService;
    private final AnalyticsService analyticsService;

    @Autowired
    public ProgressTrendController(UserService userService, ProgressTrendService progressTrendService,
                                   AnalyticsService analyticsService) {
        this.userService = userService;
        this.progressTrendService = progressTrendService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get XP trend for a user.
     *
     * GET /api/users/{userId}/trends/xp?days=30
     */
    @GetMapping("/xp")
    public ResponseEntity<ProgressTrendDto> getXpTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {
        // Validate user exists
        userService.getUser(userId);

        // Get user data
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);

        // Generate trend
        ProgressTrend trend = progressTrendService.generateXpTrend(
            habitService, goalService, analyticsService, days);

        return ResponseEntity.ok(toProgressTrendDto(trend));
    }

    /**
     * Get consistency trend for a user.
     *
     * GET /api/users/{userId}/trends/consistency?days=30
     */
    @GetMapping("/consistency")
    public ResponseEntity<ProgressTrendDto> getConsistencyTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {
        // Validate user exists
        userService.getUser(userId);

        // Get user data
        GoalService goalService = userService.getGoalService(userId);
        List<Goal> goals = goalService.getAllGoals();

        // Generate trend
        ProgressTrend trend = progressTrendService.generateConsistencyTrend(
            goals, goalService, analyticsService, days);

        return ResponseEntity.ok(toProgressTrendDto(trend));
    }

    private ProgressTrendDto toProgressTrendDto(ProgressTrend trend) {
        ProgressTrendDto dto = new ProgressTrendDto();
        dto.setStartDate(trend.startDate());
        dto.setEndDate(trend.endDate());
        dto.setMetric(trend.metric());
        dto.setDataPoints(trend.dataPoints().stream()
            .map(dp -> {
                ProgressTrendDto.DataPointDto dpDto = new ProgressTrendDto.DataPointDto();
                dpDto.setDate(dp.date());
                dpDto.setValue(dp.value());
                return dpDto;
            })
            .collect(Collectors.toList()));
        dto.setAverageValue(trend.averageValue());
        dto.setGrowthRate(trend.growthRate());
        dto.setDirection(trend.direction().name());
        return dto;
    }
}

