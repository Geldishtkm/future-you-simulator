package org.example.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing daily activity log.
 * This is a persistence layer entity - business logic stays in domain models.
 */
@Entity
@Table(name = "daily_activity_logs", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "date"})
})
public class DailyActivityLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer xpGained;

    @OneToMany(mappedBy = "activityLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabitCheckEntity> habitChecks = new ArrayList<>();

    public DailyActivityLogEntity() {
    }

    public DailyActivityLogEntity(UserEntity user, LocalDate date, Integer xpGained) {
        this.user = user;
        this.date = date;
        this.xpGained = xpGained;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getXpGained() {
        return xpGained;
    }

    public void setXpGained(Integer xpGained) {
        this.xpGained = xpGained;
    }

    public List<HabitCheckEntity> getHabitChecks() {
        return habitChecks;
    }

    public void setHabitChecks(List<HabitCheckEntity> habitChecks) {
        this.habitChecks = habitChecks;
    }
}

