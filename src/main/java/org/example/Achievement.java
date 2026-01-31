package org.example;

import java.time.LocalDate;

/**
 * Represents an achievement or badge that a user can earn.
 * Achievements are unlocked based on various milestones and accomplishments.
 */
public class Achievement {
    private final AchievementType type;
    private final String name;
    private final String description;
    private final LocalDate unlockedDate;
    private final boolean unlocked;

    /**
     * Creates a new achievement.
     *
     * @param type the type of achievement
     * @param name the name of the achievement
     * @param description the description of what the achievement represents
     * @param unlockedDate the date when the achievement was unlocked (null if not unlocked)
     * @param unlocked whether the achievement has been unlocked
     */
    public Achievement(AchievementType type, String name, String description, 
                      LocalDate unlockedDate, boolean unlocked) {
        if (type == null) {
            throw new IllegalArgumentException("AchievementType cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Achievement name cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Achievement description cannot be null or blank");
        }
        this.type = type;
        this.name = name;
        this.description = description;
        this.unlockedDate = unlockedDate;
        this.unlocked = unlocked;
    }

    /**
     * Creates a locked achievement.
     */
    public static Achievement locked(AchievementType type, String name, String description) {
        return new Achievement(type, name, description, null, false);
    }

    /**
     * Creates an unlocked achievement.
     */
    public static Achievement unlocked(AchievementType type, String name, String description, LocalDate date) {
        return new Achievement(type, name, description, date, true);
    }

    public AchievementType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getUnlockedDate() {
        return unlockedDate;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    @Override
    public String toString() {
        return String.format("Achievement{type=%s, name='%s', unlocked=%s, date=%s}", 
            type, name, unlocked, unlockedDate);
    }
}

