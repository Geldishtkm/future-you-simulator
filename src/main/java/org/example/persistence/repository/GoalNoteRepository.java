package org.example.persistence.repository;

import org.example.persistence.entity.GoalEntity;
import org.example.persistence.entity.GoalNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for GoalNote entities.
 */
@Repository
public interface GoalNoteRepository extends JpaRepository<GoalNoteEntity, Long> {
    /**
     * Finds all goal notes for a goal, ordered by date.
     *
     * @param goal the goal
     * @return list of goal notes
     */
    List<GoalNoteEntity> findByGoalOrderByDateAsc(GoalEntity goal);

    /**
     * Finds a goal note by goal and date.
     *
     * @param goal the goal
     * @param date the date
     * @return the goal note if found
     */
    Optional<GoalNoteEntity> findByGoalAndDate(GoalEntity goal, LocalDate date);

    /**
     * Checks if a goal note exists for a goal on a specific date.
     *
     * @param goal the goal
     * @param date the date
     * @return true if exists
     */
    boolean existsByGoalAndDate(GoalEntity goal, LocalDate date);
}

