package org.example.dto.mapper;

import org.example.*;
import org.example.dto.*;
import org.example.persistence.entity.*;

/**
 * Mapper class for converting between DTOs and domain models.
 * This maintains clean separation between API layer and domain layer.
 */
public class DtoMapper {

    // ========== User Mappings ==========

    /**
     * Converts UserEntity to UserDto.
     */
    public static UserDto toUserDto(UserEntity entity, UserStatsDto statsDto) {
        return new UserDto(entity.getId(), entity.getUsername(), entity.getEmail(), statsDto);
    }

    /**
     * Converts UserStats to UserStatsDto.
     */
    public static UserStatsDto toUserStatsDto(UserStats userStats) {
        return new UserStatsDto(userStats.getTotalXp(), userStats.getLevel());
    }

    // ========== Habit Mappings ==========

    /**
     * Converts Habit to HabitDto.
     */
    public static HabitDto toHabitDto(Habit habit, Long id) {
        return new HabitDto(id, habit.getName(), habit.getDifficulty().getValue());
    }

    /**
     * Converts CreateHabitRequest to domain Habit.
     */
    public static Habit toHabit(CreateHabitRequest request) {
        Difficulty difficulty = Difficulty.values()[request.getDifficulty() - 1];
        return new Habit(request.getName(), difficulty);
    }

    // ========== Goal Mappings ==========

    /**
     * Converts Goal to GoalDto.
     */
    public static GoalDto toGoalDto(Goal goal, Long id, Double progressPercentage) {
        return new GoalDto(
            id,
            goal.getTitle(),
            goal.getDescription(),
            goal.getStartDate(),
            goal.getTargetDate(),
            goal.getImportance(),
            goal.getTotalProgressPoints(),
            progressPercentage
        );
    }

    /**
     * Converts CreateGoalRequest to domain Goal.
     */
    public static Goal toGoal(CreateGoalRequest request) {
        return new Goal(
            request.getTitle(),
            request.getDescription(),
            request.getStartDate(),
            request.getTargetDate(),
            request.getImportance(),
            request.getTotalProgressPoints()
        );
    }

    // ========== XP Update Result Mappings ==========

    /**
     * Converts XP transaction result to XpUpdateResultDto.
     */
    public static XpUpdateResultDto toXpUpdateResultDto(XpTransaction transaction, UserStats newStats) {
        return new XpUpdateResultDto(
            transaction.amount(),
            newStats.getTotalXp(),
            newStats.getLevel(),
            transaction.reason()
        );
    }
}

