package org.example.controller;

import org.example.*;
import org.example.dto.ExportDataDto;
import org.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);
    private static final int MAX_DATE_RANGE_DAYS = 365; // Maximum 1 year range
    
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
        logger.info("Export JSON requested for user {} with date range: {} to {}", userId, from, to);
        
        // Validate date range
        validateDateRange(from, to);

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
        
        logger.info("Successfully exported JSON data for user {}: {} habits, {} goals", 
            userId, habits.size(), goals.size());

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
        logger.info("Export CSV requested for user {} with date range: {} to {}", userId, from, to);
        
        // Validate date range
        validateDateRange(from, to);

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
        
        logger.info("Successfully exported CSV data for user {}: {} habits, {} goals", 
            userId, habits.size(), goals.size());

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.TEXT_PLAIN)
            .body(csvData);
    }

    /**
     * Validates the date range parameters.
     * 
     * @param from the start date (optional)
     * @param to the end date (optional)
     * @throws IllegalArgumentException if the date range is invalid
     */
    private void validateDateRange(LocalDate from, LocalDate to) {
        LocalDate today = LocalDate.now();
        
        // Check if from date is after to date
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(
                String.format("Invalid date range: 'from' date (%s) must be on or before 'to' date (%s)", 
                    from, to));
        }
        
        // Check if dates are in the future
        if (from != null && from.isAfter(today)) {
            throw new IllegalArgumentException(
                String.format("Invalid date: 'from' date (%s) cannot be in the future", from));
        }
        
        if (to != null && to.isAfter(today)) {
            throw new IllegalArgumentException(
                String.format("Invalid date: 'to' date (%s) cannot be in the future", to));
        }
        
        // Check if date range is too large
        if (from != null && to != null) {
            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(from, to);
            if (daysBetween > MAX_DATE_RANGE_DAYS) {
                throw new IllegalArgumentException(
                    String.format("Date range too large: maximum %d days allowed, requested %d days", 
                        MAX_DATE_RANGE_DAYS, daysBetween));
            }
        }
    }

    /**
     * Builds a filename for the export based on the date range.
     * 
     * @param baseName the base name for the file
     * @param extension the file extension (without dot)
     * @param from the start date (optional)
     * @param to the end date (optional)
     * @return the constructed filename
     */
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

