package org.example.persistence.repository;

import org.example.persistence.entity.GoalEntity;
import org.example.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Goal entities.
 */
@Repository
public interface GoalRepository extends JpaRepository<GoalEntity, Long> {
    /**
     * Finds all goals for a user.
     *
     * @param user the user
     * @return list of goals
     */
    List<GoalEntity> findByUser(UserEntity user);

    /**
     * Finds a goal by user and title.
     *
     * @param user the user
     * @param title the goal title
     * @return the goal if found
     */
    Optional<GoalEntity> findByUserAndTitle(UserEntity user, String title);

    /**
     * Checks if a goal exists for a user with the given title.
     *
     * @param user the user
     * @param title the goal title
     * @return true if exists
     */
    boolean existsByUserAndTitle(UserEntity user, String title);
}

