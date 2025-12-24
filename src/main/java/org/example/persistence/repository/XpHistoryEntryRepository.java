package org.example.persistence.repository;

import org.example.persistence.entity.UserEntity;
import org.example.persistence.entity.XpHistoryEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for XpHistoryEntry entities.
 */
@Repository
public interface XpHistoryEntryRepository extends JpaRepository<XpHistoryEntryEntity, Long> {
    /**
     * Finds all XP history entries for a user, ordered by date.
     *
     * @param user the user
     * @return list of XP history entries
     */
    List<XpHistoryEntryEntity> findByUserOrderByDateAsc(UserEntity user);

    /**
     * Finds XP history entries for a user within a date range.
     *
     * @param user the user
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of XP history entries
     */
    List<XpHistoryEntryEntity> findByUserAndDateBetween(UserEntity user, LocalDate startDate, LocalDate endDate);
}

