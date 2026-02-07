package org.example.controller;

import org.example.*;
import org.example.dto.RewardDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for reward endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/rewards")
public class RewardController {
    private final UserService userService;
    private final RewardService rewardService;

    @Autowired
    public RewardController(UserService userService, RewardService rewardService) {
        this.userService = userService;
        this.rewardService = rewardService;
    }

    /**
     * Get all available rewards.
     *
     * GET /api/users/{userId}/rewards
     */
    @GetMapping
    public ResponseEntity<List<RewardDto>> getAllRewards(@PathVariable Long userId) {
        // Validate user exists
        userService.getUser(userId);

        List<Reward> rewards = rewardService.getAllRewards();
        List<RewardDto> dtos = rewards.stream()
            .map(this::toRewardDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Get affordable rewards (based on current XP).
     *
     * GET /api/users/{userId}/rewards/affordable
     */
    @GetMapping("/affordable")
    public ResponseEntity<List<RewardDto>> getAffordableRewards(@PathVariable Long userId) {
        // Validate user exists
        userService.getUser(userId);

        UserStats userStats = userService.getUserStats(userId);
        List<Reward> rewards = rewardService.getAffordableRewards(userStats.getTotalXp());
        List<RewardDto> dtos = rewards.stream()
            .map(this::toRewardDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Get unlocked rewards.
     *
     * GET /api/users/{userId}/rewards/unlocked
     */
    @GetMapping("/unlocked")
    public ResponseEntity<List<RewardDto>> getUnlockedRewards(@PathVariable Long userId) {
        // Validate user exists
        userService.getUser(userId);

        UserStats userStats = userService.getUserStats(userId);
        List<Reward> rewards = rewardService.getUnlockedRewards(userStats);
        List<RewardDto> dtos = rewards.stream()
            .map(this::toRewardDto)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    /**
     * Purchase a reward.
     *
     * POST /api/users/{userId}/rewards/{rewardId}/purchase
     */
    @PostMapping("/{rewardId}/purchase")
    public ResponseEntity<RewardDto> purchaseReward(@PathVariable Long userId, @PathVariable String rewardId) {
        // Validate user exists
        userService.getUser(userId);

        UserStats userStats = userService.getUserStats(userId);
        Reward reward = rewardService.purchaseReward(rewardId, userStats);

        return ResponseEntity.ok(toRewardDto(reward));
    }

    private RewardDto toRewardDto(Reward reward) {
        RewardDto dto = new RewardDto();
        dto.setId(reward.id());
        dto.setName(reward.name());
        dto.setDescription(reward.description());
        dto.setType(reward.type().name());
        dto.setXpCost(reward.xpCost());
        dto.setUnlocked(reward.unlocked());
        dto.setUnlockedDate(reward.unlockedDate());
        dto.setImageUrl(reward.imageUrl());
        return dto;
    }
}

