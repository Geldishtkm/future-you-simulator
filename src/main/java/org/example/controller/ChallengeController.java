package org.example.controller;

import org.example.*;
import org.example.dto.ChallengeDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for challenge endpoints.
 */
@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {
    private final ChallengeService challengeService;
    private final UserService userService;
    private final AnalyticsService analyticsService;

    @Autowired
    public ChallengeController(ChallengeService challengeService, UserService userService,
                              AnalyticsService analyticsService) {
        this.challengeService = challengeService;
        this.userService = userService;
        this.analyticsService = analyticsService;
    }

    /**
     * Get all challenges.
     *
     * GET /api/challenges
     */
    @GetMapping
    public ResponseEntity<List<ChallengeDto>> getAllChallenges() {
        List<Challenge> challenges = challengeService.getAllChallenges();
        List<ChallengeDto> dtos = challenges.stream()
            .map(this::toChallengeDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get active challenges.
     *
     * GET /api/challenges/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<ChallengeDto>> getActiveChallenges() {
        LocalDate currentDate = LocalDate.now();
        List<Challenge> challenges = challengeService.getActiveChallenges(currentDate);
        List<ChallengeDto> dtos = challenges.stream()
            .map(this::toChallengeDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get a challenge by ID.
     *
     * GET /api/challenges/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChallengeDto> getChallengeById(@PathVariable String id) {
        Challenge challenge = challengeService.getChallengeById(id);
        return ResponseEntity.ok(toChallengeDto(challenge));
    }

    /**
     * Join a challenge.
     *
     * POST /api/challenges/{id}/join
     */
    @PostMapping("/{id}/join")
    public ResponseEntity<ChallengeDto> joinChallenge(@PathVariable String id, @RequestParam String username) {
        Challenge challenge = challengeService.joinChallenge(id, username);
        return ResponseEntity.ok(toChallengeDto(challenge));
    }

    /**
     * Get user's progress in a challenge.
     *
     * GET /api/users/{userId}/challenges/{challengeId}/progress
     */
    @GetMapping("/users/{userId}/challenges/{challengeId}/progress")
    public ResponseEntity<ChallengeDto> getChallengeProgress(@PathVariable Long userId, @PathVariable String challengeId) {
        // Validate user exists
        userService.getUser(userId);

        // Get user data
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);

        // Get unique habits from habit checks
        List<Habit> habits = habitService.getAllHabitChecks().stream()
            .map(HabitCheck::habit)
            .distinct()
            .toList();

        // Update challenge progress
        LocalDate currentDate = LocalDate.now();
        Challenge challenge = challengeService.updateChallengeProgress(
            challengeId, userStats, habits, habitService, analyticsService, currentDate);

        return ResponseEntity.ok(toChallengeDto(challenge));
    }

    private ChallengeDto toChallengeDto(Challenge challenge) {
        ChallengeDto dto = new ChallengeDto();
        dto.setId(challenge.id());
        dto.setTitle(challenge.title());
        dto.setDescription(challenge.description());
        dto.setType(challenge.type().name());
        dto.setTargetValue(challenge.targetValue());
        dto.setCurrentValue(challenge.currentValue());
        dto.setStartDate(challenge.startDate());
        dto.setEndDate(challenge.endDate());
        dto.setActive(challenge.active());
        dto.setParticipants(challenge.participants());
        dto.setReward(challenge.reward());
        dto.setProgressPercentage(challenge.getProgressPercentage());
        return dto;
    }
}

