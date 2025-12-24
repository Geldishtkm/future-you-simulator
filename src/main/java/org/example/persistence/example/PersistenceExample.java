package org.example.persistence.example;

import org.example.persistence.entity.UserEntity;
import org.example.persistence.entity.UserStatsEntity;
import org.example.persistence.mapper.DomainEntityMapper;
import org.example.persistence.repository.UserRepository;
import org.example.persistence.repository.UserStatsRepository;
import org.example.UserStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Example class demonstrating how to use the persistence layer.
 * This shows the pattern of converting between domain models and entities.
 * 
 * Note: This is an example - actual services would integrate repositories
 * while keeping business logic in the domain layer.
 */
@Component
public class PersistenceExample {

    private final UserRepository userRepository;
    private final UserStatsRepository userStatsRepository;

    @Autowired
    public PersistenceExample(UserRepository userRepository, UserStatsRepository userStatsRepository) {
        this.userRepository = userRepository;
        this.userStatsRepository = userStatsRepository;
    }

    /**
     * Example: Creating a new user with initial stats.
     * Demonstrates domain → entity conversion and transaction safety.
     */
    @Transactional
    public UserEntity createUserWithStats(String username, String email) {
        // Create user entity
        UserEntity user = new UserEntity(username, email);
        user = userRepository.save(user);

        // Create initial user stats (domain model)
        UserStats initialStats = UserStats.createNew();

        // Convert to entity and save
        UserStatsEntity statsEntity = DomainEntityMapper.toUserStatsEntity(user, initialStats);
        statsEntity = userStatsRepository.save(statsEntity);
        user.setUserStats(statsEntity);

        return userRepository.save(user);
    }

    /**
     * Example: Updating user stats.
     * Demonstrates entity → domain → entity conversion pattern.
     */
    @Transactional
    public UserStatsEntity updateUserStats(UserEntity user, UserStats newStats) {
        // Load existing entity
        UserStatsEntity statsEntity = userStatsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User stats not found"));

        // Update from domain model
        statsEntity.setTotalXp(newStats.getTotalXp());
        statsEntity.setLevel(newStats.getLevel());

        // Save and return
        return userStatsRepository.save(statsEntity);
    }

    /**
     * Example: Loading user stats as domain model.
     * Demonstrates entity → domain conversion.
     */
    public UserStats loadUserStatsAsDomain(UserEntity user) {
        UserStatsEntity statsEntity = userStatsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("User stats not found"));

        return DomainEntityMapper.toUserStats(statsEntity);
    }
}

