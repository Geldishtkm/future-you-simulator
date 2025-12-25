package org.example.dto;

/**
 * DTO for user statistics.
 */
public class UserStatsDto {
    private Integer totalXp;
    private Integer level;

    public UserStatsDto() {
    }

    public UserStatsDto(Integer totalXp, Integer level) {
        this.totalXp = totalXp;
        this.level = level;
    }

    public Integer getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(Integer totalXp) {
        this.totalXp = totalXp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}

