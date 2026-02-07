package org.example;

import java.time.LocalDate;

/**
 * Represents a reward that can be earned by users.
 */
public record Reward(
    String id,
    String name,
    String description,
    RewardType type,
    int xpCost,
    boolean unlocked,
    LocalDate unlockedDate,
    String imageUrl
) {
    /**
     * Creates a reward.
     */
    public Reward {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("RewardType cannot be null");
        }
        if (xpCost < 0) {
            throw new IllegalArgumentException("XP cost cannot be negative");
        }
    }
}

