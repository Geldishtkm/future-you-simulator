package org.example.controller;

import org.example.dto.LeaderboardDto;
import org.example.dto.LeaderboardEntryDto;
import org.example.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for leaderboard endpoints.
 */
@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    @Autowired
    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    /**
     * Get top users leaderboard.
     *
     * GET /api/leaderboard?limit=10
     */
    @GetMapping
    public ResponseEntity<LeaderboardDto> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long userId) {
        List<org.example.LeaderboardEntry> entries = leaderboardService.getTopUsers(limit);

        // Convert to DTOs
        List<LeaderboardEntryDto> entryDtos = entries.stream()
            .map(this::toLeaderboardEntryDto)
            .collect(Collectors.toList());

        // Get total user count (approximate from entries)
        int totalUsers = entries.size();

        // Get user rank if userId provided
        Integer userRank = null;
        if (userId != null) {
            userRank = leaderboardService.getUserRank(userId);
        }

        LeaderboardDto leaderboard = new LeaderboardDto(entryDtos, totalUsers, userRank);

        return ResponseEntity.ok(leaderboard);
    }

    private LeaderboardEntryDto toLeaderboardEntryDto(org.example.LeaderboardEntry entry) {
        return new LeaderboardEntryDto(
            entry.userId(),
            entry.username(),
            entry.totalXp(),
            entry.level(),
            entry.rank()
        );
    }
}

