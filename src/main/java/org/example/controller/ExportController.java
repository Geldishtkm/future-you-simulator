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
     * GET /api/users/{userId}/export/json
     */
    @GetMapping("/json")
    public ResponseEntity<ExportDataDto> exportJson(@PathVariable Long userId) {
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

        // Export data
        String jsonData = exportService.exportToJson(
            userStats, habits, goals, habitService, goalService,
            achievementService, milestoneService, analyticsService);

        ExportDataDto dto = new ExportDataDto();
        dto.setExportDate(LocalDate.now());
        dto.setFormat("JSON");
        dto.setData(jsonData);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user-data.json")
            .contentType(MediaType.APPLICATION_JSON)
            .body(dto);
    }

    /**
     * Export user data as CSV.
     *
     * GET /api/users/{userId}/export/csv
     */
    @GetMapping("/csv")
    public ResponseEntity<String> exportCsv(@PathVariable Long userId) {
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

        // Export data
        String csvData = exportService.exportToCsv(userStats, habits, goals, habitService, goalService);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=user-data.csv")
            .contentType(MediaType.TEXT_PLAIN)
            .body(csvData);
    }
}

