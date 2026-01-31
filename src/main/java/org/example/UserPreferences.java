package org.example;

import java.time.LocalTime;

/**
 * Represents user preferences and settings.
 */
public class UserPreferences {
    private boolean emailNotifications;
    private boolean weeklyReportEmail;
    private LocalTime dailyReminderTime;
    private String timezone;
    private boolean showLeaderboard;
    private boolean publicProfile;
    private String theme;

    public UserPreferences() {
        this.emailNotifications = false;
        this.weeklyReportEmail = false;
        this.dailyReminderTime = LocalTime.of(9, 0); // Default 9 AM
        this.timezone = "UTC";
        this.showLeaderboard = true;
        this.publicProfile = false;
        this.theme = "light";
    }

    public UserPreferences(boolean emailNotifications, boolean weeklyReportEmail,
                          LocalTime dailyReminderTime, String timezone,
                          boolean showLeaderboard, boolean publicProfile, String theme) {
        this.emailNotifications = emailNotifications;
        this.weeklyReportEmail = weeklyReportEmail;
        this.dailyReminderTime = dailyReminderTime != null ? dailyReminderTime : LocalTime.of(9, 0);
        this.timezone = timezone != null ? timezone : "UTC";
        this.showLeaderboard = showLeaderboard;
        this.publicProfile = publicProfile;
        this.theme = theme != null ? theme : "light";
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

