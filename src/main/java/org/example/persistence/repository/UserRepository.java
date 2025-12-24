package org.example.persistence.repository;

import org.example.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    /**
     * Finds a user by username.
     *
     * @param username the username
     * @return the user if found
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Finds a user by email.
     *
     * @param email the email
     * @return the user if found
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     *
     * @param username the username
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists with the given email.
     *
     * @param email the email
     * @return true if exists
     */
    boolean existsByEmail(String email);
}

