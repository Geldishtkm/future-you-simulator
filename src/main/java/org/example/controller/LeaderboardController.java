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
     * GET /api/leaderboard?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<LeaderboardDto> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId) {
        int limit = size * (page + 1);
        List<org.example.LeaderboardEntry> allEntries = leaderboardService.getTopUsers(limit);

        // Apply simple pagination
        int fromIndex = Math.max(0, page * size);
        int toIndex = Math.min(allEntries.size(), fromIndex + size);
        List<org.example.LeaderboardEntry> entries = fromIndex >= allEntries.size()
            ? List.of()
            : allEntries.subList(fromIndex, toIndex);

        // Convert to DTOs
        List<LeaderboardEntryDto> entryDtos = entries.stream()
            .map(this::toLeaderboardEntryDto)
            .collect(Collectors.toList());

        // Get total user count (approximate from all entries)
        int totalUsers = allEntries.size();

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

