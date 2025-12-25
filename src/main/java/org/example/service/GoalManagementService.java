package org.example.service;

import org.example.*;
import org.example.persistence.entity.GoalEntity;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.mapper.DomainEntityMapper;
import org.example.persistence.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing goals with persistence.
 * Integrates persistence layer with domain GoalService.
 */
@Service
public class GoalManagementService {
    private final GoalRepository goalRepository;

    @Autowired
    public GoalManagementService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    /**
     * Creates and persists a goal for a user.
     */
    @Transactional
    public GoalEntity createGoal(UserEntity user, Goal goal) {
        // Check if goal with same title already exists for this user
        if (goalRepository.existsByUserAndTitle(user, goal.getTitle())) {
            throw new IllegalArgumentException("Goal with title '" + goal.getTitle() + "' already exists for this user");
        }

        GoalEntity entity = DomainEntityMapper.toGoalEntity(user, goal);
        return goalRepository.save(entity);
    }

    /**
     * Gets a goal by ID for a user.
     */
    public GoalEntity getGoal(Long goalId, Long userId) {
        GoalEntity goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found: " + goalId));
        
        if (!goal.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Goal does not belong to user: " + userId);
        }
        
        return goal;
    }

    /**
     * Gets all goals for a user.
     */
    public List<GoalEntity> getUserGoals(UserEntity user) {
        return goalRepository.findByUser(user);
    }

    /**
     * Converts GoalEntity to domain Goal.
     */
    public Goal toDomainGoal(GoalEntity entity) {
        return DomainEntityMapper.toGoal(entity);
    }
}

