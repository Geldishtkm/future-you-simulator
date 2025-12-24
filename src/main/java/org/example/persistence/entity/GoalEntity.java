package org.example.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a long-term goal.
 * This is a persistence layer entity - business logic stays in domain models.
 */
@Entity
@Table(name = "goals")
public class GoalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate targetDate;

    @Column(nullable = false)
    private Integer importance; // 1-5

    @Column(nullable = false)
    private Integer totalProgressPoints;

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GoalNoteEntity> notes = new ArrayList<>();

    public GoalEntity() {
    }

    public GoalEntity(UserEntity user, String title, String description, LocalDate startDate,
                     LocalDate targetDate, Integer importance, Integer totalProgressPoints) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.importance = importance;
        this.totalProgressPoints = totalProgressPoints;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public Integer getTotalProgressPoints() {
        return totalProgressPoints;
    }

    public void setTotalProgressPoints(Integer totalProgressPoints) {
        this.totalProgressPoints = totalProgressPoints;
    }

    public List<GoalNoteEntity> getNotes() {
        return notes;
    }

    public void setNotes(List<GoalNoteEntity> notes) {
        this.notes = notes;
    }
}

