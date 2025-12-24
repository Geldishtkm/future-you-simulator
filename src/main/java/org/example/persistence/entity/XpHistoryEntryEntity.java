package org.example.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * JPA entity representing an XP history entry.
 * This is a persistence layer entity - business logic stays in domain models.
 */
@Entity
@Table(name = "xp_history_entries")
public class XpHistoryEntryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Integer xpChange;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private XpSourceEnum source;

    public XpHistoryEntryEntity() {
    }

    public XpHistoryEntryEntity(UserEntity user, LocalDate date, Integer xpChange, XpSourceEnum source) {
        this.user = user;
        this.date = date;
        this.xpChange = xpChange;
        this.source = source;
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

    public Integer getXpChange() {
        return xpChange;
    }

    public void setXpChange(Integer xpChange) {
        this.xpChange = xpChange;
    }

    public XpSourceEnum getSource() {
        return source;
    }

    public void setSource(XpSourceEnum source) {
        this.source = source;
    }

    /**
     * Enum for XP source types.
     */
    public enum XpSourceEnum {
        HABIT, GOAL, DECAY
    }
}

