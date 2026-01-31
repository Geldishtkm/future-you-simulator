package org.example.controller;

import org.example.*;
import org.example.dto.AchievementDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for achievement endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/achievements")
public class AchievementController {
    private final UserService userService;
    private final AchievementService achievementService;
    private final AnalyticsService analyticsService;

    @Autowired
    public AchievementController(UserService userService, AchievementService achievementService,
                                AnalyticsService analyticsService) {
        this.userService = userService;
        this.achievementService = achievementService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get all achievements for a user.
     *
     * GET /api/users/{userId}/achievements
     */
    @GetMapping
    public ResponseEntity<List<AchievementDto>> getAchievements(@PathVariable Long userId) {
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

        // Calculate achievements
        LocalDate currentDate = LocalDate.now();
        List<Achievement> achievements = achievementService.calculateAchievements(
            userStats, habits, goals, habitService, goalService, analyticsService, currentDate);

        // Convert to DTOs
        List<AchievementDto> achievementDtos = achievements.stream()
            .map(this::toAchievementDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(achievementDtos);
    }

    /**
     * Get only unlocked achievements for a user.
     *
     * GET /api/users/{userId}/achievements/unlocked
     */
    @GetMapping("/unlocked")
    public ResponseEntity<List<AchievementDto>> getUnlockedAchievements(@PathVariable Long userId) {
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

        // Calculate achievements
        LocalDate currentDate = LocalDate.now();
        List<Achievement> achievements = achievementService.calculateAchievements(
            userStats, habits, goals, habitService, goalService, analyticsService, currentDate);

        // Filter to only unlocked and convert to DTOs
        List<AchievementDto> achievementDtos = achievements.stream()
            .filter(Achievement::isUnlocked)
            .map(this::toAchievementDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(achievementDtos);
    }

    private AchievementDto toAchievementDto(Achievement achievement) {
        AchievementDto dto = new AchievementDto();
        dto.setType(achievement.getType().name());
        dto.setName(achievement.getName());
        dto.setDescription(achievement.getDescription());
        dto.setUnlockedDate(achievement.getUnlockedDate());
        dto.setUnlocked(achievement.isUnlocked());
        return dto;
    }
}

