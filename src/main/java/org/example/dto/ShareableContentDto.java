package org.example.dto;

import java.time.LocalDate;

/**
 * DTO for shareable content.
 */
public class ShareableContentDto {
    private String type;
    private String title;
    private String message;
    private String imageUrl;
    private LocalDate date;
    private int value;
    private String achievementName;
    private String shareUrl;

    public ShareableContentDto() {
    }

    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public String getAchievementName() { return achievementName; }
    public void setAchievementName(String achievementName) { this.achievementName = achievementName; }
    public String getShareUrl() { return shareUrl; }
    public void setShareUrl(String shareUrl) { this.shareUrl = shareUrl; }
}

