package org.example.service;

import org.example.*;
import org.example.persistence.entity.HabitEntity;
import org.example.persistence.entity.UserEntity;
import org.example.persistence.mapper.DomainEntityMapper;
import org.example.persistence.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing habits with persistence.
 * Integrates persistence layer with domain HabitService.
 */
@Service
public class HabitManagementService {
    private final HabitRepository habitRepository;

    @Autowired
    public HabitManagementService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    /**
     * Creates and persists a habit for a user.
     */
    @Transactional
    public HabitEntity createHabit(UserEntity user, Habit habit) {
        // Check if habit with same name already exists for this user
        if (habitRepository.findByUserAndName(user, habit.getName()).isPresent()) {
            throw new IllegalArgumentException("Habit with name '" + habit.getName() + "' already exists for this user");
        }

        HabitEntity entity = DomainEntityMapper.toHabitEntity(user, habit);
        return habitRepository.save(entity);
    }

    /**
     * Gets a habit by ID for a user.
     */
    public HabitEntity getHabit(Long habitId, Long userId) {
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + habitId));
        
        if (!habit.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Habit does not belong to user: " + userId);
        }
        
        return habit;
    }

    /**
     * Gets all habits for a user.
     */
    public List<HabitEntity> getUserHabits(UserEntity user) {
        return habitRepository.findByUser(user);
    }

    /**
     * Converts HabitEntity to domain Habit.
     */
    public Habit toDomainHabit(HabitEntity entity) {
        return DomainEntityMapper.toHabit(entity);
    }
}

