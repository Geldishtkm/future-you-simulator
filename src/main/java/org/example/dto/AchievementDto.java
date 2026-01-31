package org.example.dto;

import java.time.LocalDate;

/**
 * DTO for achievement data.
 */
public class AchievementDto {
    private String type;
    private String name;
    private String description;
    private LocalDate unlockedDate;
    private boolean unlocked;

    public AchievementDto() {
    }

    public AchievementDto(String type, String name, String description, 
                         LocalDate unlockedDate, boolean unlocked) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.unlockedDate = unlockedDate;
        this.unlocked = unlocked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getUnlockedDate() {
        return unlockedDate;
    }

    public void setUnlockedDate(LocalDate unlockedDate) {
        this.unlockedDate = unlockedDate;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}

