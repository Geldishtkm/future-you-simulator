package org.example.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * JPA entity representing a daily goal note.
 * This is a persistence layer entity - business logic stays in domain models.
 */
@Entity
@Table(name = "goal_notes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"goal_id", "date"})
})
public class GoalNoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "goal_id", nullable = false)
    private GoalEntity goal;

    @Column(nullable = false)
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String textNote;

    @Column(nullable = false)
    private Integer points;

    public GoalNoteEntity() {
    }

    public GoalNoteEntity(GoalEntity goal, LocalDate date, String textNote, Integer points) {
        this.goal = goal;
        this.date = date;
        this.textNote = textNote;
        this.points = points;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GoalEntity getGoal() {
        return goal;
    }

    public void setGoal(GoalEntity goal) {
        this.goal = goal;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTextNote() {
        return textNote;
    }

    public void setTextNote(String textNote) {
        this.textNote = textNote;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}

