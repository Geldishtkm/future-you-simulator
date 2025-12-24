package org.example.persistence.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a habit.
 * This is a persistence layer entity - business logic stays in domain models.
 */
@Entity
@Table(name = "habits")
public class HabitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private DifficultyEnum difficulty;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HabitCheckEntity> habitChecks = new ArrayList<>();

    public HabitEntity() {
    }

    public HabitEntity(UserEntity user, String name, DifficultyEnum difficulty) {
        this.user = user;
        this.name = name;
        this.difficulty = difficulty;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DifficultyEnum getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyEnum difficulty) {
        this.difficulty = difficulty;
    }

    public List<HabitCheckEntity> getHabitChecks() {
        return habitChecks;
    }

    public void setHabitChecks(List<HabitCheckEntity> habitChecks) {
        this.habitChecks = habitChecks;
    }

    /**
     * Enum for difficulty levels (stored as ordinal in database).
     */
    public enum DifficultyEnum {
        ONE, TWO, THREE, FOUR, FIVE
    }
}

