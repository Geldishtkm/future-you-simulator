package org.example.service;

import org.example.LeaderboardEntry;
import org.example.UserStats;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.repository.UserRepository;
import org.example.persistence.repository.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing leaderboard functionality.
 */
@Service
public class LeaderboardService {
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;

    @Autowired
    public LeaderboardService(UserRepository userRepository, UserStatsRepository userStatsRepository) {
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
    }

    /**
     * Gets the top users by XP for the leaderboard.
     *
     * @param limit the maximum number of entries to return (default 10)
     * @return a list of leaderboard entries sorted by XP (descending)
     */
    public List<LeaderboardEntry> getTopUsers(int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        if (limit > 100) {
            limit = 100; // Cap at 100
        }

        List<UserEntity> allUsers = userRepository.findAll();
        List<LeaderboardEntry> entries = new ArrayList<>();

        for (UserEntity user : allUsers) {
            UserStats stats = userStatsRepository.findByUser(user)
                .map(statsEntity -> org.example.persistence.mapper.DomainEntityMapper.toUserStats(statsEntity))
                .orElse(org.example.UserStats.createNew());
            
            entries.add(new LeaderboardEntry(
                user.getId(),
                user.getUsername(),
                stats.getTotalXp(),
                stats.getLevel(),
                0 // Rank will be set after sorting
            ));
        }

        // Sort by XP descending, then by level descending
        entries.sort(Comparator
            .comparing(LeaderboardEntry::totalXp, Comparator.reverseOrder())
            .thenComparing(LeaderboardEntry::level, Comparator.reverseOrder()));

        // Assign ranks
        for (int i = 0; i < entries.size(); i++) {
            LeaderboardEntry entry = entries.get(i);
            entries.set(i, new LeaderboardEntry(
                entry.userId(),
                entry.username(),
                entry.totalXp(),
                entry.level(),
                i + 1
            ));
        }

        return entries.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Gets the rank of a specific user.
     *
     * @param userId the user ID
     * @return the user's rank (1-based), or null if user not found
     */
    public Integer getUserRank(Long userId) {
        List<LeaderboardEntry> allEntries = getTopUsers(Integer.MAX_VALUE);
        
        return allEntries.stream()
            .filter(entry -> entry.userId().equals(userId))
            .findFirst()
            .map(LeaderboardEntry::rank)
            .orElse(null);
    }
}

