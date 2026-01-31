package org.example;

/**
 * Represents a single entry in the leaderboard.
 */
public record LeaderboardEntry(
    Long userId,
    String username,
    int totalXp,
    int level,
    int rank
) {
    /**
     * Creates a leaderboard entry.
     */
    public LeaderboardEntry {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (totalXp < 0) {
            throw new IllegalArgumentException("Total XP cannot be negative");
        }
        if (level < 1) {
            throw new IllegalArgumentException("Level must be at least 1");
        }
        if (rank < 1) {
            throw new IllegalArgumentException("Rank must be at least 1");
        }
    }
}

