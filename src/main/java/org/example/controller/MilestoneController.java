package org.example.controller;

import org.example.*;
import org.example.dto.MilestoneDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for milestone endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/milestones")
public class MilestoneController {
    private final UserService userService;
    private final MilestoneService milestoneService;
    private final AnalyticsService analyticsService;

    @Autowired
    public MilestoneController(UserService userService, MilestoneService milestoneService,
                              AnalyticsService analyticsService) {
        this.userService = userService;
        this.milestoneService = milestoneService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get all milestones for a user.
     *
     * GET /api/users/{userId}/milestones
     */
    @GetMapping
    public ResponseEntity<List<MilestoneDto>> getMilestones(@PathVariable Long userId) {
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

        // Get milestones
        LocalDate currentDate = LocalDate.now();
        List<Milestone> milestones = milestoneService.getMilestones(
            userStats, habits, goals, habitService, goalService, analyticsService, currentDate);

        // Convert to DTOs
        List<MilestoneDto> milestoneDtos = milestones.stream()
            .map(this::toMilestoneDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(milestoneDtos);
    }

    /**
     * Get only achieved milestones for a user.
     *
     * GET /api/users/{userId}/milestones/achieved
     */
    @GetMapping("/achieved")
    public ResponseEntity<List<MilestoneDto>> getAchievedMilestones(@PathVariable Long userId) {
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

        // Get milestones
        LocalDate currentDate = LocalDate.now();
        List<Milestone> milestones = milestoneService.getMilestones(
            userStats, habits, goals, habitService, goalService, analyticsService, currentDate);

        // Filter to only achieved and convert to DTOs
        List<MilestoneDto> milestoneDtos = milestones.stream()
            .filter(Milestone::achieved)
            .map(this::toMilestoneDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(milestoneDtos);
    }

    private MilestoneDto toMilestoneDto(Milestone milestone) {
        MilestoneDto dto = new MilestoneDto();
        dto.setType(milestone.type().name());
        dto.setTitle(milestone.title());
        dto.setDescription(milestone.description());
        dto.setTargetValue(milestone.targetValue());
        dto.setCurrentValue(milestone.currentValue());
        dto.setAchieved(milestone.achieved());
        dto.setAchievedDate(milestone.achievedDate());
        dto.setProgressPercentage(milestone.getProgressPercentage());
        return dto;
    }
}

