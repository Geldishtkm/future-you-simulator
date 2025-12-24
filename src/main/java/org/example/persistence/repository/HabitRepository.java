package org.example.persistence.repository;

import org.example.persistence.entity.HabitEntity;
import org.example.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for Habit entities.
 */
@Repository
public interface HabitRepository extends JpaRepository<HabitEntity, Long> {
    /**
     * Finds all habits for a user.
     *
     * @param user the user
     * @return list of habits
     */
    List<HabitEntity> findByUser(UserEntity user);

    /**
     * Finds a habit by user and name.
     *
     * @param user the user
     * @param name the habit name
     * @return the habit if found
     */
    java.util.Optional<HabitEntity> findByUserAndName(UserEntity user, String name);
}

