package org.example.dto;

import java.time.LocalDateTime;

/**
 * DTO for notification data.
 */
public class NotificationDto {
    private Long id;
    private String type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledFor;
    private boolean read;
    private String actionUrl;

    public NotificationDto() {
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getScheduledFor() { return scheduledFor; }
    public void setScheduledFor(LocalDateTime scheduledFor) { this.scheduledFor = scheduledFor; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
}

