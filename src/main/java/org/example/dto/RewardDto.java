package org.example.dto;

import java.time.LocalDate;

/**
 * DTO for reward data.
 */
public class RewardDto {
    private String id;
    private String name;
    private String description;
    private String type;
    private int xpCost;
    private boolean unlocked;
    private LocalDate unlockedDate;
    private String imageUrl;

    public RewardDto() {
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getXpCost() { return xpCost; }
    public void setXpCost(int xpCost) { this.xpCost = xpCost; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public LocalDate getUnlockedDate() { return unlockedDate; }
    public void setUnlockedDate(LocalDate unlockedDate) { this.unlockedDate = unlockedDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

