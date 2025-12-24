package org.example.persistence.repository;

import org.example.persistence.entity.UserEntity;
import org.example.persistence.entity.UserStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for UserStats entities.
 */
@Repository
public interface UserStatsRepository extends JpaRepository<UserStatsEntity, Long> {
    /**
     * Finds user stats by user.
     *
     * @param user the user
     * @return the user stats if found
     */
    Optional<UserStatsEntity> findByUser(UserEntity user);
}

