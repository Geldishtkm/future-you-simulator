package org.example.dto;

import java.time.LocalDateTime;

/**
 * DTO for activity feed entry.
 */
public class ActivityFeedEntryDto {
    private LocalDateTime timestamp;
    private String type;
    private String title;
    private String description;
    private int xpChange;
    private String source;

    public ActivityFeedEntryDto() {
    }

    // Getters and setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getXpChange() { return xpChange; }
    public void setXpChange(int xpChange) { this.xpChange = xpChange; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}

