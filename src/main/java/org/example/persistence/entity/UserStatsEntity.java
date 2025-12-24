package org.example.persistence.entity;

import jakarta.persistence.*;

/**
 * JPA entity representing user statistics.
 * This is a persistence layer entity - business logic stays in domain models.
 */
@Entity
@Table(name = "user_stats")
public class UserStatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(nullable = false)
    private Integer totalXp;

    @Column(nullable = false)
    private Integer level;

    public UserStatsEntity() {
    }

    public UserStatsEntity(UserEntity user, Integer totalXp, Integer level) {
        this.user = user;
        this.totalXp = totalXp;
        this.level = level;
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

