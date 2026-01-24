package org.example.controller;

import org.example.*;
import org.example.dto.*;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for analytics and dashboard endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/analytics")
public class AnalyticsController {
    private final UserService userService;
    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(UserService userService, AnalyticsService analyticsService) {
        this.userService = userService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get comprehensive dashboard data for a user.
     * Includes stats, analytics, streaks, trends, and burnout indicators.
     *
     * GET /api/users/{userId}/analytics/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard(@PathVariable Long userId) {
        // Validate user exists
        userService.getUser(userId);

        // Get user data
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);

        // Build analytics
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        List<XpHistoryEntry> xpHistory = analyticsService.buildXpHistory(habitService, goalService);
        
        // Get unique habits from habit checks
        List<Habit> habits = habitService.getAllHabitChecks().stream()
            .map(HabitCheck::habit)
            .distinct()
            .toList();
        
        // Calculate streaks
        Map<Habit, HabitStreak> habitStreaksMap = analyticsService.calculateAllHabitStreaks(
            habits, habitService, currentDate);
        List<HabitStreak> habitStreaks = new ArrayList<>(habitStreaksMap.values());
        
        // Get goals
        List<Goal> goals = goalService.getAllGoals();
        
        // Calculate consistency (average of all goal consistencies)
        Map<Goal, GoalConsistency> goalConsistencyMap = analyticsService.calculateAllGoalConsistency(
            goals, goalService, currentDate);
        double consistency = goalConsistencyMap.values().stream()
            .mapToDouble(GoalConsistency::consistencyScore)
            .average()
            .orElse(0.0);
        
        // Analyze trend
        Trend trend = analyticsService.analyzeXpTrend(habitService, goalService, 14, currentDate);
        
        // Detect burnout
        BurnoutWarning burnoutWarning = analyticsService.detectBurnout(habitService, goalService, currentDate);

        // Build dashboard DTO
        DashboardDto dashboard = new DashboardDto();
        
        // User stats
        dashboard.setTotalXp(userStats.getTotalXp());
        dashboard.setLevel(userStats.getLevel());
        
        // Habits and goals summary
        dashboard.setTotalHabits(habits.size());
        dashboard.setActiveHabits(habits.size());
        dashboard.setTotalGoals(goals.size());
        dashboard.setActiveGoals(goals.size()); // All goals are considered active
        
        // Analytics metrics
        dashboard.setConsistencyScore(consistency);
        dashboard.setCurrentStreaks(habitStreaks.stream()
            .mapToInt(HabitStreak::currentStreak)
            .sum());
        dashboard.setLongestStreak(habitStreaks.stream()
            .mapToInt(HabitStreak::longestStreak)
            .max()
            .orElse(0));
        
        // Recent activity (last 7 days)
        long recentActivityDays = xpHistory.stream()
            .filter(entry -> entry.date().isAfter(java.time.LocalDate.now().minusDays(7)))
            .count();
        dashboard.setActiveDaysLastWeek((int) recentActivityDays);
        
        // XP trends
        dashboard.setTrendDirection(trend.name());
        dashboard.setTrendStrength(burnoutWarning.severityScore() > 50 ? "HIGH" : "LOW");
        
        // Burnout indicators
        String burnoutRisk = burnoutWarning.severityScore() > 70 ? "HIGH" :
                            burnoutWarning.severityScore() > 40 ? "MEDIUM" : "LOW";
        dashboard.setBurnoutRisk(burnoutRisk);
        dashboard.setBurnoutMessage(burnoutWarning.isWarningActive() ?
            String.join("; ", burnoutWarning.riskFactors()) :
            "No burnout indicators detected");
        
        // Top habits by streak
        List<HabitStreakDto> topStreaks = habitStreaks.stream()
            .sorted((a, b) -> Integer.compare(b.currentStreak(), a.currentStreak()))
            .limit(5)
            .map(this::toHabitStreakDto)
            .collect(Collectors.toList());
        dashboard.setTopStreaks(topStreaks);
        
        // Recent XP activity (last 7 days)
        List<XpActivityDto> recentActivity = xpHistory.stream()
            .filter(entry -> entry.date().isAfter(java.time.LocalDate.now().minusDays(7)))
            .sorted((a, b) -> b.date().compareTo(a.date()))
            .map(this::toXpActivityDto)
            .collect(Collectors.toList());
        dashboard.setRecentActivity(recentActivity);

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get detailed analytics for a user.
     *
     * GET /api/users/{userId}/analytics/detailed
     */
    @GetMapping("/detailed")
    public ResponseEntity<DetailedAnalyticsDto> getDetailedAnalytics(@PathVariable Long userId) {
        // Validate user exists
        userService.getUser(userId);

        // Get user data
        HabitService habitService = userService.getHabitService(userId);
        GoalService goalService = userService.getGoalService(userId);

        // Build analytics
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        List<XpHistoryEntry> xpHistory = analyticsService.buildXpHistory(habitService, goalService);
        
        // Get unique habits from habit checks
        List<Habit> habits = habitService.getAllHabitChecks().stream()
            .map(HabitCheck::habit)
            .distinct()
            .toList();
        
        // Calculate streaks
        Map<Habit, HabitStreak> habitStreaksMap = analyticsService.calculateAllHabitStreaks(
            habits, habitService, currentDate);
        List<HabitStreak> habitStreaks = new ArrayList<>(habitStreaksMap.values());
        
        // Get goals
        List<Goal> goals = goalService.getAllGoals();
        
        // Calculate consistency (average of all goal consistencies)
        Map<Goal, GoalConsistency> goalConsistencyMap = analyticsService.calculateAllGoalConsistency(
            goals, goalService, currentDate);
        double consistency = goalConsistencyMap.values().stream()
            .mapToDouble(GoalConsistency::consistencyScore)
            .average()
            .orElse(0.0);
        
        // Analyze trend
        Trend trend = analyticsService.analyzeXpTrend(habitService, goalService, 14, currentDate);
        
        // Detect burnout
        BurnoutWarning burnoutWarning = analyticsService.detectBurnout(habitService, goalService, currentDate);

        // Build detailed analytics DTO
        DetailedAnalyticsDto analytics = new DetailedAnalyticsDto();
        analytics.setConsistencyScore(consistency);
        analytics.setHabitStreaks(habitStreaks.stream()
            .map(this::toHabitStreakDto)
            .collect(Collectors.toList()));
        
        // Create a single trend DTO
        TrendDto trendDto = new TrendDto();
        trendDto.setStartDate(currentDate.minusDays(14));
        trendDto.setEndDate(currentDate);
        trendDto.setDirection(trend.name());
        trendDto.setStrength(burnoutWarning.severityScore() > 50 ? "HIGH" : "LOW");
        trendDto.setDescription("XP trend over last 14 days: " + trend.name());
        analytics.setTrends(List.of(trendDto));
        
        analytics.setBurnoutWarning(toBurnoutWarningDto(burnoutWarning));
        
        analytics.setXpHistory(xpHistory.stream()
            .map(this::toXpHistoryEntryDto)
            .collect(Collectors.toList()));

        return ResponseEntity.ok(analytics);
    }

    private HabitStreakDto toHabitStreakDto(HabitStreak streak) {
        HabitStreakDto dto = new HabitStreakDto();
        dto.setHabitName(streak.habit().getName());
        dto.setCurrentStreak(streak.currentStreak());
        dto.setLongestStreak(streak.longestStreak());
        dto.setLastActivityDate(streak.streakStartDate());
        return dto;
    }

    private XpActivityDto toXpActivityDto(XpHistoryEntry entry) {
        XpActivityDto dto = new XpActivityDto();
        dto.setDate(entry.date());
        dto.setXpChange(entry.xpChange());
        dto.setSource(entry.source().name());
        return dto;
    }

    private BurnoutWarningDto toBurnoutWarningDto(BurnoutWarning warning) {
        BurnoutWarningDto dto = new BurnoutWarningDto();
        String severity = warning.severityScore() > 70 ? "HIGH" :
                         warning.severityScore() > 40 ? "MEDIUM" : "LOW";
        dto.setSeverity(severity);
        dto.setMessage(warning.isWarningActive() ?
            String.join("; ", warning.riskFactors()) :
            "No burnout indicators detected");
        dto.setIndicators(warning.riskFactors());
        return dto;
    }

    private XpHistoryEntryDto toXpHistoryEntryDto(XpHistoryEntry entry) {
        XpHistoryEntryDto dto = new XpHistoryEntryDto();
        dto.setDate(entry.date());
        dto.setXpChange(entry.xpChange());
        dto.setSource(entry.source().name());
        return dto;
    }
}

