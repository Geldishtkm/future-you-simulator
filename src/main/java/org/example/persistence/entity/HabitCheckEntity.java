package org.example.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * JPA entity representing a habit check on a specific date.
 * This is a persistence layer entity - business logic stays in domain models.
 */
@Entity
@Table(name = "habit_checks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"habit_id", "date"})
})
public class HabitCheckEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "habit_id", nullable = false)
    private HabitEntity habit;

    @ManyToOne
    @JoinColumn(name = "activity_log_id")
    private DailyActivityLogEntity activityLog;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private HabitCheckResultEnum result;

    public HabitCheckEntity() {
    }

    public HabitCheckEntity(HabitEntity habit, LocalDate date, HabitCheckResultEnum result) {
        this.habit = habit;
        this.date = date;
        this.result = result;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HabitEntity getHabit() {
        return habit;
    }

    public void setHabit(HabitEntity habit) {
        this.habit = habit;
    }

    public DailyActivityLogEntity getActivityLog() {
        return activityLog;
    }

    public void setActivityLog(DailyActivityLogEntity activityLog) {
        this.activityLog = activityLog;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public HabitCheckResultEnum getResult() {
        return result;
    }

    public void setResult(HabitCheckResultEnum result) {
        this.result = result;
    }

    /**
     * Enum for habit check results.
     */
    public enum HabitCheckResultEnum {
        DONE, MISSED
    }
}

