package org.example.controller;

import org.example.*;
import org.example.dto.HabitRecommendationDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for habit recommendation endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/recommendations/habits")
public class HabitRecommendationController {
    private final UserService userService;
    private final HabitRecommendationService recommendationService;
    private final AnalyticsService analyticsService;

    @Autowired
    public HabitRecommendationController(UserService userService, HabitRecommendationService recommendationService,
                                        AnalyticsService analyticsService) {
        this.userService = userService;
        this.recommendationService = recommendationService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get habit recommendations for a user.
     *
     * GET /api/users/{userId}/recommendations/habits
     */
    @GetMapping
    public ResponseEntity<List<HabitRecommendationDto>> getRecommendations(@PathVariable Long userId) {
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

        // Get recommendations
        LocalDate currentDate = LocalDate.now();
        List<HabitRecommendation> recommendations = recommendationService.getRecommendations(
            userStats, habits, goals, habitService, goalService, analyticsService, currentDate);

        // Convert to DTOs
        List<HabitRecommendationDto> dtos = recommendations.stream()
            .map(this::toHabitRecommendationDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private HabitRecommendationDto toHabitRecommendationDto(HabitRecommendation recommendation) {
        HabitRecommendationDto dto = new HabitRecommendationDto();
        dto.setName(recommendation.name());
        dto.setDescription(recommendation.description());
        dto.setSuggestedDifficulty(recommendation.suggestedDifficulty().name());
        dto.setCategory(recommendation.category());
        dto.setReason(recommendation.reason());
        dto.setMatchScore(recommendation.matchScore());
        return dto;
    }
}

