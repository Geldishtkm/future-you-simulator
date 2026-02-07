package org.example.controller;

import org.example.*;
import org.example.dto.ShareableContentDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for social sharing endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/share")
public class SocialSharingController {
    private final UserService userService;
    private final SocialSharingService socialSharingService;
    private final AchievementService achievementService;
    private final MilestoneService milestoneService;
    private final AnalyticsService analyticsService;

    @Autowired
    public SocialSharingController(UserService userService, SocialSharingService socialSharingService,
                                  AchievementService achievementService, MilestoneService milestoneService,
                                  AnalyticsService analyticsService) {
        this.userService = userService;
        this.socialSharingService = socialSharingService;
        this.achievementService = achievementService;
        this.milestoneService = milestoneService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get all shareable content for a user.
     *
     * GET /api/users/{userId}/share
     */
    @GetMapping
    public ResponseEntity<List<ShareableContentDto>> getShareableContent(@PathVariable Long userId) {
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

        // Generate shareable content
        LocalDate currentDate = LocalDate.now();
        List<ShareableContent> content = socialSharingService.getAllShareableContent(
            userStats, habits, goals, habitService, goalService,
            achievementService, milestoneService, analyticsService, currentDate);

        // Convert to DTOs
        List<ShareableContentDto> dtos = content.stream()
            .map(this::toShareableContentDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    private ShareableContentDto toShareableContentDto(ShareableContent content) {
        ShareableContentDto dto = new ShareableContentDto();
        dto.setType(content.type().name());
        dto.setTitle(content.title());
        dto.setMessage(content.message());
        dto.setImageUrl(content.imageUrl());
        dto.setDate(content.date());
        dto.setValue(content.value());
        dto.setAchievementName(content.achievementName());
        dto.setShareUrl("/share/" + content.type().name().toLowerCase());
        return dto;
    }
}

