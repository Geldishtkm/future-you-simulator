package org.example.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a user in the system.
 * This is a persistence layer entity - business logic stays in domain models.
 */
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserStatsEntity userStats;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabitEntity> habits = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GoalEntity> goals = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyActivityLogEntity> activityLogs = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<XpHistoryEntryEntity> xpHistory = new ArrayList<>();

    public UserEntity() {
    }

    public UserEntity(String username, String email) {
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserStatsEntity getUserStats() {
        return userStats;
    }

    public void setUserStats(UserStatsEntity userStats) {
        this.userStats = userStats;
    }

    public List<HabitEntity> getHabits() {
        return habits;
    }

    public void setHabits(List<HabitEntity> habits) {
        this.habits = habits;
    }

    public List<GoalEntity> getGoals() {
        return goals;
    }

    public void setGoals(List<GoalEntity> goals) {
        this.goals = goals;
    }

    public List<DailyActivityLogEntity> getActivityLogs() {
        return activityLogs;
    }

    public void setActivityLogs(List<DailyActivityLogEntity> activityLogs) {
        this.activityLogs = activityLogs;
    }

    public List<XpHistoryEntryEntity> getXpHistory() {
        return xpHistory;
    }

    public void setXpHistory(List<XpHistoryEntryEntity> xpHistory) {
        this.xpHistory = xpHistory;
    }
}

