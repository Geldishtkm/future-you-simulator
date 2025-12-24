package org.example.persistence.repository;

import org.example.persistence.entity.DailyActivityLogEntity;
import org.example.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for DailyActivityLog entities.
 */
@Repository
public interface DailyActivityLogRepository extends JpaRepository<DailyActivityLogEntity, Long> {
    /**
     * Finds an activity log by user and date.
     *
     * @param user the user
     * @param date the date
     * @return the activity log if found
     */
    Optional<DailyActivityLogEntity> findByUserAndDate(UserEntity user, LocalDate date);

    /**
     * Finds all activity logs for a user, ordered by date descending.
     *
     * @param user the user
     * @return list of activity logs
     */
    List<DailyActivityLogEntity> findByUserOrderByDateDesc(UserEntity user);

    /**
     * Finds activity logs for a user within a date range.
     *
     * @param user the user
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of activity logs
     */
    List<DailyActivityLogEntity> findByUserAndDateBetween(UserEntity user, LocalDate startDate, LocalDate endDate);
}

