package org.example.dto;

/**
 * DTO for a single leaderboard entry.
 */
public class LeaderboardEntryDto {
    private Long userId;
    private String username;
    private int totalXp;
    private int level;
    private int rank;

    public LeaderboardEntryDto() {
    }

    public LeaderboardEntryDto(Long userId, String username, int totalXp, int level, int rank) {
        this.userId = userId;
        this.username = username;
        this.totalXp = totalXp;
        this.level = level;
        this.rank = rank;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(int totalXp) {
        this.totalXp = totalXp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}

