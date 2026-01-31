package org.example.service;

import org.example.*;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.entity.UserStatsEntity;
import org.example.persistence.mapper.DomainEntityMapper;
import org.example.persistence.repository.UserRepository;
import org.example.persistence.repository.UserStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing users and their associated services.
 * This service coordinates between persistence and domain services.
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;
    
    // In-memory storage for user services (in production, this would be managed differently)
    private final Map<Long, HabitService> habitServices = new HashMap<>();
    private final Map<Long, GoalService> goalServices = new HashMap<>();
    private final Map<Long, UserPreferences> userPreferences = new HashMap<>();

    @Autowired
    public UserService(UserRepository userRepository, UserStatsRepository userStatsRepository) {
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
    }

    /**
     * Creates a new user with initial stats.
     */
    @Transactional
    public UserEntity createUser(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        UserEntity user = new UserEntity(username, email);
        user = userRepository.save(user);

        // Create initial user stats
        UserStats initialStats = UserStats.createNew();
        UserStatsEntity statsEntity = DomainEntityMapper.toUserStatsEntity(user, initialStats);
        statsEntity = userStatsRepository.save(statsEntity);
        user.setUserStats(statsEntity);
        user = userRepository.save(user);

        // Initialize services for this user
        habitServices.put(user.getId(), new HabitService());
        goalServices.put(user.getId(), new GoalService());
        userPreferences.put(user.getId(), new UserPreferences());

        return user;
    }

    /**
     * Gets a user by ID.
     */
    public UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    /**
     * Gets user stats as domain model.
     */
    public UserStats getUserStats(Long userId) {
        UserEntity user = getUser(userId);
        UserStatsEntity statsEntity = userStatsRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("User stats not found for user: " + userId));
        return DomainEntityMapper.toUserStats(statsEntity);
    }

    /**
     * Updates user stats.
     */
    @Transactional
    public void updateUserStats(Long userId, UserStats newStats) {
        UserEntity user = getUser(userId);
        UserStatsEntity statsEntity = userStatsRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("User stats not found for user: " + userId));
        
        statsEntity.setTotalXp(newStats.getTotalXp());
        statsEntity.setLevel(newStats.getLevel());
        userStatsRepository.save(statsEntity);
    }

    /**
     * Gets the HabitService for a user.
     */
    public HabitService getHabitService(Long userId) {
        return habitServices.computeIfAbsent(userId, k -> new HabitService());
    }

    /**
     * Gets the GoalService for a user.
     */
    public GoalService getGoalService(Long userId) {
        return goalServices.computeIfAbsent(userId, k -> new GoalService());
    }

    /**
     * Gets user preferences.
     */
    public UserPreferences getUserPreferences(Long userId) {
        return userPreferences.computeIfAbsent(userId, k -> new UserPreferences());
    }

    /**
     * Updates user preferences.
     */
    public void updateUserPreferences(Long userId, UserPreferences preferences) {
        getUser(userId); // Validate user exists
        userPreferences.put(userId, preferences);
    }
}

