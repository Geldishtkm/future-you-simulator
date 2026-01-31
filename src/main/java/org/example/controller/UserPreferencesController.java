package org.example.controller;

import jakarta.validation.Valid;
import org.example.UserPreferences;
import org.example.dto.UserPreferencesDto;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user preferences endpoints.
 */
@RestController
@RequestMapping("/api/users/{userId}/preferences")
public class UserPreferencesController {
    private final UserService userService;

    @Autowired
    public UserPreferencesController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get user preferences.
     *
     * GET /api/users/{userId}/preferences
     */
    @GetMapping
    public ResponseEntity<UserPreferencesDto> getPreferences(@PathVariable Long userId) {
        // Validate user exists
        userService.getUser(userId);

        UserPreferences preferences = userService.getUserPreferences(userId);
        UserPreferencesDto dto = toUserPreferencesDto(preferences);

        return ResponseEntity.ok(dto);
    }

    /**
     * Update user preferences.
     *
     * PUT /api/users/{userId}/preferences
     */
    @PutMapping
    public ResponseEntity<UserPreferencesDto> updatePreferences(
            @PathVariable Long userId,
            @Valid @RequestBody UserPreferencesDto preferencesDto) {
        // Validate user exists
        userService.getUser(userId);

        UserPreferences preferences = toUserPreferences(preferencesDto);
        userService.updateUserPreferences(userId, preferences);

        UserPreferencesDto dto = toUserPreferencesDto(preferences);
        return ResponseEntity.ok(dto);
    }

    private UserPreferencesDto toUserPreferencesDto(UserPreferences preferences) {
        UserPreferencesDto dto = new UserPreferencesDto();
        dto.setEmailNotifications(preferences.isEmailNotifications());
        dto.setWeeklyReportEmail(preferences.isWeeklyReportEmail());
        dto.setDailyReminderTime(preferences.getDailyReminderTime());
        dto.setTimezone(preferences.getTimezone());
        dto.setShowLeaderboard(preferences.isShowLeaderboard());
        dto.setPublicProfile(preferences.isPublicProfile());
        dto.setTheme(preferences.getTheme());
        return dto;
    }

    private UserPreferences toUserPreferences(UserPreferencesDto dto) {
        return new UserPreferences(
            dto.isEmailNotifications(),
            dto.isWeeklyReportEmail(),
            dto.getDailyReminderTime(),
            dto.getTimezone(),
            dto.isShowLeaderboard(),
            dto.isPublicProfile(),
            dto.getTheme()
        );
    }
}

