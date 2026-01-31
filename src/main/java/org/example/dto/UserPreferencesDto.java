package org.example.dto;

import java.time.LocalTime;

/**
 * DTO for user preferences.
 */
public class UserPreferencesDto {
    private boolean emailNotifications;
    private boolean weeklyReportEmail;
    private LocalTime dailyReminderTime;
    private String timezone;
    private boolean showLeaderboard;
    private boolean publicProfile;
    private String theme;

    public UserPreferencesDto() {
    }

    public boolean isEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public boolean isWeeklyReportEmail() {
        return weeklyReportEmail;
    }

    public void setWeeklyReportEmail(boolean weeklyReportEmail) {
        this.weeklyReportEmail = weeklyReportEmail;
    }

    public LocalTime getDailyReminderTime() {
        return dailyReminderTime;
    }

    public void setDailyReminderTime(LocalTime dailyReminderTime) {
        this.dailyReminderTime = dailyReminderTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isShowLeaderboard() {
        return showLeaderboard;
    }

    public void setShowLeaderboard(boolean showLeaderboard) {
        this.showLeaderboard = showLeaderboard;
    }

    public boolean isPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(boolean publicProfile) {
        this.publicProfile = publicProfile;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}

