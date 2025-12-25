package org.example.controller;

import jakarta.validation.Valid;
import org.example.*;
import org.example.dto.*;
import org.example.dto.mapper.DtoMapper;
import org.example.persistence.entity.GoalEntity;
import org.example.service.GoalManagementService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for goal management.
 * 
 * Endpoints:
 * POST   /api/users/{userId}/goals        - Create a new goal
 * POST   /api/users/{userId}/goals/{id}/notes - Add daily note to goal
 */
@RestController
@RequestMapping("/api/users/{userId}/goals")
public class GoalController {

    private final UserService userService;
    private final GoalManagementService goalManagementService;

    @Autowired
    public GoalController(UserService userService, GoalManagementService goalManagementService) {
        this.userService = userService;
        this.goalManagementService = goalManagementService;
    }

    /**
     * Creates a new goal for a user.
     * 
     * POST /api/users/{userId}/goals
     * 
     * Request body:
     * {
     *   "title": "Get a Backend Internship",
     *   "description": "Land an internship at a tech company",
     *   "startDate": "2025-01-01",
     *   "targetDate": "2025-06-30",
     *   "importance": 5,
     *   "totalProgressPoints": 100
     * }
     * 
     * Response:
     * {
     *   "id": 1,
     *   "title": "Get a Backend Internship",
     *   "description": "Land an internship at a tech company",
     *   "startDate": "2025-01-01",
     *   "targetDate": "2025-06-30",
     *   "importance": 5,
     *   "totalProgressPoints": 100,
     *   "progressPercentage": 0.0
     * }
     */
    @PostMapping
    public ResponseEntity<GoalDto> createGoal(
            @PathVariable Long userId,
            @Valid @RequestBody CreateGoalRequest request) {
        
        // Get user
        org.example.persistence.entity.UserEntity user = userService.getUser(userId);
        
        // Create goal domain model
        Goal goal = DtoMapper.toGoal(request);
        
        // Persist goal
        GoalEntity goalEntity = goalManagementService.createGoal(user, goal);
        
        // Add goal to domain service (for business logic)
        GoalService goalService = userService.getGoalService(userId);
        goalService.addGoal(goal);
        
        // Calculate initial progress
        double progress = goalService.calculateProgress(goal);
        
        GoalDto goalDto = DtoMapper.toGoalDto(goal, goalEntity.getId(), progress);
        return ResponseEntity.status(HttpStatus.CREATED).body(goalDto);
    }

    /**
     * Adds a daily note to a goal.
     * 
     * POST /api/users/{userId}/goals/{goalId}/notes
     * 
     * Request body:
     * {
     *   "textNote": "Applied to 3 companies today",
     *   "requestedXp": 8
     * }
     * 
     * Response:
     * {
     *   "xpChange": 8,
     *   "newTotalXp": 158,
     *   "newLevel": 2,
     *   "reason": "Goal 'Get a Backend Internship' note: 8 XP assigned"
     * }
     */
    @PostMapping("/{goalId}/notes")
    public ResponseEntity<XpUpdateResultDto> addGoalNote(
            @PathVariable Long userId,
            @PathVariable Long goalId,
            @Valid @RequestBody AddGoalNoteRequest request,
            @RequestParam(required = false) LocalDate date) {
        
        // Verify user exists
        userService.getUser(userId);
        
        // Get user stats and services
        UserStats userStats = userService.getUserStats(userId);
        GoalService goalService = userService.getGoalService(userId);
        HabitService habitService = userService.getHabitService(userId);
        
        // Use provided date or today
        LocalDate noteDate = date != null ? date : LocalDate.now();
        
        // Get goal from persistence
        GoalEntity goalEntity = goalManagementService.getGoal(goalId, userId);
        Goal goal = goalManagementService.toDomainGoal(goalEntity);
        
        // Ensure goal is in domain service (for business logic)
        if (goalService.getGoal(goal.getTitle()) == null) {
            goalService.addGoal(goal);
        }
        
        // Add goal note
        GoalService.NoteResult result = goalService.addGoalNote(
            userStats, goal, noteDate, request.getTextNote(), 
            request.getRequestedXp(), habitService);
        
        // Update user stats in persistence
        userService.updateUserStats(userId, result.userStats());
        
        // Convert to DTO
        XpUpdateResultDto response = DtoMapper.toXpUpdateResultDto(
            result.transaction(), result.userStats());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets all goals for a user.
     * 
     * GET /api/users/{userId}/goals
     * 
     * Response:
     * [
     *   {
     *     "id": 1,
     *     "title": "Get a Backend Internship",
     *     "description": "Land an internship at a tech company",
     *     "startDate": "2025-01-01",
     *     "targetDate": "2025-06-30",
     *     "importance": 5,
     *     "totalProgressPoints": 100,
     *     "progressPercentage": 38.0
     *   }
     * ]
     */
    @GetMapping
    public ResponseEntity<java.util.List<GoalDto>> getUserGoals(@PathVariable Long userId) {
        // Verify user exists
        org.example.persistence.entity.UserEntity user = userService.getUser(userId);
        
        // Get all goals for user
        java.util.List<GoalEntity> goalEntities = goalManagementService.getUserGoals(user);
        GoalService goalService = userService.getGoalService(userId);
        
        // Convert to DTOs
        java.util.List<GoalDto> goals = goalEntities.stream()
                .map(entity -> {
                    Goal goal = goalManagementService.toDomainGoal(entity);
                    // Ensure goal is in domain service for progress calculation
                    if (goalService.getGoal(goal.getTitle()) == null) {
                        goalService.addGoal(goal);
                    }
                    double progress = goalService.calculateProgress(goal);
                    return DtoMapper.toGoalDto(goal, entity.getId(), progress);
                })
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(goals);
    }
}

