package org.example.persistence.repository;

import org.example.persistence.entity.HabitCheckEntity;
import org.example.persistence.entity.HabitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for HabitCheck entities.
 */
@Repository
public interface HabitCheckRepository extends JpaRepository<HabitCheckEntity, Long> {
    /**
     * Finds all habit checks for a habit.
     *
     * @param habit the habit
     * @return list of habit checks
     */
    List<HabitCheckEntity> findByHabit(HabitEntity habit);

    /**
     * Finds a habit check by habit and date.
     *
     * @param habit the habit
     * @param date the date
     * @return the habit check if found
     */
    Optional<HabitCheckEntity> findByHabitAndDate(HabitEntity habit, LocalDate date);

    /**
     * Checks if a habit check exists for a habit on a specific date.
     *
     * @param habit the habit
     * @param date the date
     * @return true if exists
     */
    boolean existsByHabitAndDate(HabitEntity habit, LocalDate date);
}

