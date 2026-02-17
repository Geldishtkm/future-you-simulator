package org.example.controller;

import org.example.*;
import org.example.dto.ExportDataDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for export endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/export")
public class ExportController {
    private final UserService userService;
    private final ExportService exportService;
    private final AchievementService achievementService;
    private final MilestoneService milestoneService;
    private final AnalyticsService analyticsService;

    @Autowired
    public ExportController(UserService userService, ExportService exportService,
                           AchievementService achievementService, MilestoneService milestoneService,
                           AnalyticsService analyticsService) {
        this.userService = userService;
        this.exportService = exportService;
        this.achievementService = achievementService;
        this.milestoneService = milestoneService;
        this.analyticsService = analyticsService;
    }

    /**
     * Export user data as JSON.
     *
     * GET /api/users/{userId}/export/json?from=2025-01-01&to=2025-01-31
     */
    @GetMapping("/json")
    public ResponseEntity<ExportDataDto> exportJson(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        // Validate date range if both are provided
        if (from != null && to != null && from.isAfter(to)) {
            return ResponseEntity.badRequest().build();
        }

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

        // Export data (filtered by date range if provided)
        String jsonData = exportService.exportToJson(
            userStats, habits, goals, habitService, goalService,
            achievementService, milestoneService, analyticsService,
            from, to);

        String filename = buildFilename("user-data", "json", from, to);

        ExportDataDto dto = new ExportDataDto();
        dto.setExportDate(LocalDate.now());
        dto.setFormat("JSON");
        dto.setData(jsonData);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto);
    }

    /**
     * Export user data as CSV.
     *
     * GET /api/users/{userId}/export/csv?from=2025-01-01&to=2025-01-31
     */
    @GetMapping("/csv")
    public ResponseEntity<String> exportCsv(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        // Validate date range if both are provided
        if (from != null && to != null && from.isAfter(to)) {
            return ResponseEntity.badRequest()
                .body("Invalid date range: 'from' must be on or before 'to'.");
        }

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

        // Export data (filtered by date range if provided)
        String csvData = exportService.exportToCsv(userStats, habits, goals, habitService, goalService, from, to);

        String filename = buildFilename("user-data", "csv", from, to);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.TEXT_PLAIN)
            .body(csvData);
    }

    private String buildFilename(String baseName, String extension, LocalDate from, LocalDate to) {
        StringBuilder sb = new StringBuilder(baseName);
        if (from != null || to != null) {
            if (from != null) {
                sb.append("-from-").append(from);
            }
            if (to != null) {
                sb.append("-to-").append(to);
            }
        }
        sb.append(".").append(extension);
        return sb.toString();
    }
}

