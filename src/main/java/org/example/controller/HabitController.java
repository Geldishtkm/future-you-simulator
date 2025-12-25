package org.example.controller;

import jakarta.validation.Valid;
import org.example.*;
import org.example.dto.*;
import org.example.dto.mapper.DtoMapper;
import org.example.persistence.entity.HabitEntity;
import org.example.service.HabitManagementService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for habit management.
 * 
 * Endpoints:
 * POST   /api/users/{userId}/habits              - Create a new habit
 * POST   /api/users/{userId}/habits/{id}/complete - Mark habit as completed for today
 * POST   /api/users/{userId}/habits/{id}/miss     - Mark habit as missed
 */
@RestController
@RequestMapping("/api/users/{userId}/habits")
public class HabitController {

    private final UserService userService;
    private final HabitManagementService habitManagementService;

    @Autowired
    public HabitController(UserService userService, HabitManagementService habitManagementService) {
        this.userService = userService;
        this.habitManagementService = habitManagementService;
    }

    /**
     * Creates a new habit for a user.
     * 
     * POST /api/users/{userId}/habits
     * 
     * Request body:
     * {
     *   "name": "Morning Exercise",
     *   "difficulty": 3
     * }
     * 
     * Response:
     * {
     *   "id": 1,
     *   "name": "Morning Exercise",
     *   "difficulty": 3
     * }
     */
    @PostMapping
    public ResponseEntity<HabitDto> createHabit(
            @PathVariable Long userId,
            @Valid @RequestBody CreateHabitRequest request) {
        
        // Get user
        org.example.persistence.entity.UserEntity user = userService.getUser(userId);
        
        // Create habit domain model
        Habit habit = DtoMapper.toHabit(request);
        
        // Persist habit
        HabitEntity habitEntity = habitManagementService.createHabit(user, habit);
        
        // Convert to DTO
        HabitDto habitDto = DtoMapper.toHabitDto(habit, habitEntity.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(habitDto);
    }

    /**
     * Marks a habit as completed for today.
     * 
     * POST /api/users/{userId}/habits/{habitId}/complete
     * 
     * Response:
     * {
     *   "xpChange": 30,
     *   "newTotalXp": 130,
     *   "newLevel": 2,
     *   "reason": "Completed habit 'Morning Exercise' (difficulty 3)"
     * }
     */
    @PostMapping("/{habitId}/complete")
    public ResponseEntity<XpUpdateResultDto> completeHabit(
            @PathVariable Long userId,
            @PathVariable Long habitId,
            @RequestParam(required = false) LocalDate date) {
        
        // Verify user exists
        userService.getUser(userId);
        
        // Get user stats and services
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        
        // Use provided date or today
        LocalDate checkDate = date != null ? date : LocalDate.now();
        
        // Get habit from persistence
        HabitEntity habitEntity = habitManagementService.getHabit(habitId, userId);
        Habit habit = habitManagementService.toDomainHabit(habitEntity);
        
        // Check habit
        HabitService.CheckResult result = habitService.checkHabit(
            userStats, habit, checkDate, HabitCheckResult.DONE);
        
        // Update user stats in persistence
        userService.updateUserStats(userId, result.userStats());
        
        // Convert to DTO
        XpUpdateResultDto response = DtoMapper.toXpUpdateResultDto(
            result.transaction(), result.userStats());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Marks a habit as missed.
     * 
     * POST /api/users/{userId}/habits/{habitId}/miss
     * 
     * Response:
     * {
     *   "xpChange": -15,
     *   "newTotalXp": 115,
     *   "newLevel": 2,
     *   "reason": "Missed habit 'Morning Exercise' (difficulty 1)"
     * }
     */
    @PostMapping("/{habitId}/miss")
    public ResponseEntity<XpUpdateResultDto> missHabit(
            @PathVariable Long userId,
            @PathVariable Long habitId,
            @RequestParam(required = false) LocalDate date) {
        
        // Verify user exists
        userService.getUser(userId);
        
        // Get user stats and services
        UserStats userStats = userService.getUserStats(userId);
        HabitService habitService = userService.getHabitService(userId);
        
        // Use provided date or today
        LocalDate checkDate = date != null ? date : LocalDate.now();
        
        // Get habit from persistence
        HabitEntity habitEntity = habitManagementService.getHabit(habitId, userId);
        Habit habit = habitManagementService.toDomainHabit(habitEntity);
        
        // Check habit as missed
        HabitService.CheckResult result = habitService.checkHabit(
            userStats, habit, checkDate, HabitCheckResult.MISSED);
        
        // Update user stats in persistence
        userService.updateUserStats(userId, result.userStats());
        
        // Convert to DTO
        XpUpdateResultDto response = DtoMapper.toXpUpdateResultDto(
            result.transaction(), result.userStats());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all habits for a user.
     * 
     * GET /api/users/{userId}/habits
     * 
     * Response:
     * [
     *   {
     *     "id": 1,
     *     "name": "Morning Exercise",
     *     "difficulty": 3
     *   },
     *   {
     *     "id": 2,
     *     "name": "Read for 30 Minutes",
     *     "difficulty": 2
     *   }
     * ]
     */
    @GetMapping
    public ResponseEntity<java.util.List<HabitDto>> getUserHabits(@PathVariable Long userId) {
        // Verify user exists
        org.example.persistence.entity.UserEntity user = userService.getUser(userId);
        
        // Get all habits for user
        java.util.List<HabitEntity> habitEntities = habitManagementService.getUserHabits(user);
        
        // Convert to DTOs
        java.util.List<HabitDto> habits = habitEntities.stream()
                .map(entity -> {
                    Habit habit = habitManagementService.toDomainHabit(entity);
                    return DtoMapper.toHabitDto(habit, entity.getId());
                })
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(habits);
    }
}

