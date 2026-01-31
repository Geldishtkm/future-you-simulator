package org.example.dto;

import java.util.List;

/**
 * DTO for leaderboard data.
 */
public class LeaderboardDto {
    private List<LeaderboardEntryDto> entries;
    private int totalUsers;
    private Integer userRank;

    public LeaderboardDto() {
    }

    public LeaderboardDto(List<LeaderboardEntryDto> entries, int totalUsers, Integer userRank) {
        this.entries = entries;
        this.totalUsers = totalUsers;
        this.userRank = userRank;
    }

    public List<LeaderboardEntryDto> getEntries() {
        return entries;
    }

    public void setEntries(List<LeaderboardEntryDto> entries) {
        this.entries = entries;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Integer getUserRank() {
        return userRank;
    }

    public void setUserRank(Integer userRank) {
        this.userRank = userRank;
    }
}

