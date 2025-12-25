package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.*;
import org.example.dto.mapper.DtoMapper;
import org.example.persistence.entity.UserEntity;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management.
 * 
 * Endpoints:
 * POST   /api/users          - Create a new user
 * GET    /api/users/{id}     - Get user profile with stats
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     * 
     * POST /api/users
     * 
     * Request body:
     * {
     *   "username": "john_doe",
     *   "email": "john@example.com"
     * }
     * 
     * Response:
     * {
     *   "id": 1,
     *   "username": "john_doe",
     *   "email": "john@example.com",
     *   "stats": {
     *     "totalXp": 0,
     *     "level": 1
     *   }
     * }
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserEntity user = userService.createUser(request.getUsername(), request.getEmail());
        UserStatsDto statsDto = DtoMapper.toUserStatsDto(userService.getUserStats(user.getId()));
        UserDto userDto = DtoMapper.toUserDto(user, statsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    /**
     * Gets a user by ID with their stats.
     * 
     * GET /api/users/{id}
     * 
     * Response:
     * {
     *   "id": 1,
     *   "username": "john_doe",
     *   "email": "john@example.com",
     *   "stats": {
     *     "totalXp": 150,
     *     "level": 2
     *   }
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserEntity user = userService.getUser(id);
        UserStatsDto statsDto = DtoMapper.toUserStatsDto(userService.getUserStats(id));
        UserDto userDto = DtoMapper.toUserDto(user, statsDto);
        return ResponseEntity.ok(userDto);
    }
}

