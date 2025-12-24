package org.example.persistence.mapper;

import org.example.*;
import org.example.persistence.entity.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between domain models and JPA entities.
 * This maintains clean separation between domain logic and persistence.
 */
public class DomainEntityMapper {

    // ========== User Mappings ==========

    /**
     * Converts UserEntity to a simple user representation (if needed).
     * For now, we'll work with entities directly in the persistence layer.
     */
    public static UserEntity toUserEntity(String username, String email) {
        return new UserEntity(username, email);
    }

    // ========== UserStats Mappings ==========

    /**
     * Converts domain UserStats to UserStatsEntity.
     */
    public static UserStatsEntity toUserStatsEntity(UserEntity user, UserStats userStats) {
        return new UserStatsEntity(user, userStats.getTotalXp(), userStats.getLevel());
    }

    /**
     * Converts UserStatsEntity to domain UserStats.
     */
    public static UserStats toUserStats(UserStatsEntity entity) {
        return new UserStats(entity.getTotalXp(), entity.getLevel());
    }

    // ========== Habit Mappings ==========

    /**
     * Converts domain Habit to HabitEntity.
     */
    public static HabitEntity toHabitEntity(UserEntity user, Habit habit) {
        HabitEntity.DifficultyEnum difficultyEnum = toDifficultyEnum(habit.getDifficulty());
        return new HabitEntity(user, habit.getName(), difficultyEnum);
    }

    /**
     * Converts HabitEntity to domain Habit.
     */
    public static Habit toHabit(HabitEntity entity) {
        Difficulty difficulty = toDifficulty(entity.getDifficulty());
        return new Habit(entity.getName(), difficulty);
    }

    /**
     * Converts domain Difficulty to HabitEntity.DifficultyEnum.
     */
    private static HabitEntity.DifficultyEnum toDifficultyEnum(Difficulty difficulty) {
        return switch (difficulty) {
            case ONE -> HabitEntity.DifficultyEnum.ONE;
            case TWO -> HabitEntity.DifficultyEnum.TWO;
            case THREE -> HabitEntity.DifficultyEnum.THREE;
            case FOUR -> HabitEntity.DifficultyEnum.FOUR;
            case FIVE -> HabitEntity.DifficultyEnum.FIVE;
        };
    }

    /**
     * Converts HabitEntity.DifficultyEnum to domain Difficulty.
     */
    private static Difficulty toDifficulty(HabitEntity.DifficultyEnum difficultyEnum) {
        return switch (difficultyEnum) {
            case ONE -> Difficulty.ONE;
            case TWO -> Difficulty.TWO;
            case THREE -> Difficulty.THREE;
            case FOUR -> Difficulty.FOUR;
            case FIVE -> Difficulty.FIVE;
        };
    }

    // ========== HabitCheck Mappings ==========

    /**
     * Converts domain HabitCheck to HabitCheckEntity.
     */
    public static HabitCheckEntity toHabitCheckEntity(HabitEntity habitEntity, HabitCheck habitCheck) {
        HabitCheckEntity.HabitCheckResultEnum resultEnum = toHabitCheckResultEnum(habitCheck.result());
        return new HabitCheckEntity(habitEntity, habitCheck.date(), resultEnum);
    }

    /**
     * Converts HabitCheckEntity to domain HabitCheck.
     */
    public static HabitCheck toHabitCheck(HabitCheckEntity entity) {
        Habit habit = toHabit(entity.getHabit());
        HabitCheckResult result = toHabitCheckResult(entity.getResult());
        return new HabitCheck(habit, entity.getDate(), result);
    }

    /**
     * Converts domain HabitCheckResult to HabitCheckEntity.HabitCheckResultEnum.
     */
    private static HabitCheckEntity.HabitCheckResultEnum toHabitCheckResultEnum(HabitCheckResult result) {
        return switch (result) {
            case DONE -> HabitCheckEntity.HabitCheckResultEnum.DONE;
            case MISSED -> HabitCheckEntity.HabitCheckResultEnum.MISSED;
        };
    }

    /**
     * Converts HabitCheckEntity.HabitCheckResultEnum to domain HabitCheckResult.
     */
    private static HabitCheckResult toHabitCheckResult(HabitCheckEntity.HabitCheckResultEnum resultEnum) {
        return switch (resultEnum) {
            case DONE -> HabitCheckResult.DONE;
            case MISSED -> HabitCheckResult.MISSED;
        };
    }

    // ========== DailyActivityLog Mappings ==========

    /**
     * Converts domain DailyActivityLog to DailyActivityLogEntity.
     * Note: This method requires a map of habit names to HabitEntity instances
     * to properly link habit checks. The caller should provide this map.
     *
     * @param user the user entity
     * @param log the domain daily activity log
     * @param habitEntityMap map of habit names to HabitEntity instances (must contain all habits in the log)
     * @return the DailyActivityLogEntity
     */
    public static DailyActivityLogEntity toDailyActivityLogEntity(UserEntity user, DailyActivityLog log,
                                                                  Map<String, HabitEntity> habitEntityMap) {
        DailyActivityLogEntity entity = new DailyActivityLogEntity(user, log.getDate(), log.getXpGained());
        
        // Map habit checks - requires habit entities to be provided
        List<HabitCheckEntity> habitCheckEntities = log.getHabitChecks().stream()
                .map(check -> {
                    HabitEntity habitEntity = habitEntityMap.get(check.habit().getName());
                    if (habitEntity == null) {
                        throw new IllegalArgumentException(
                            "Habit entity not found for habit: " + check.habit().getName() +
                            ". All habits must be provided in the habitEntityMap.");
                    }
                    HabitCheckEntity checkEntity = toHabitCheckEntity(habitEntity, check);
                    checkEntity.setActivityLog(entity); // Set bidirectional relationship
                    return checkEntity;
                })
                .collect(Collectors.toList());
        
        entity.setHabitChecks(habitCheckEntities);
        return entity;
    }

    /**
     * Converts DailyActivityLogEntity to domain DailyActivityLog.
     */
    public static DailyActivityLog toDailyActivityLog(DailyActivityLogEntity entity) {
        List<HabitCheck> habitChecks = entity.getHabitChecks().stream()
                .map(DomainEntityMapper::toHabitCheck)
                .collect(Collectors.toList());
        
        return new DailyActivityLog(entity.getDate(), entity.getXpGained(), habitChecks);
    }


    // ========== Goal Mappings ==========

    /**
     * Converts domain Goal to GoalEntity.
     */
    public static GoalEntity toGoalEntity(UserEntity user, Goal goal) {
        return new GoalEntity(
            user,
            goal.getTitle(),
            goal.getDescription(),
            goal.getStartDate(),
            goal.getTargetDate(),
            goal.getImportance(),
            goal.getTotalProgressPoints()
        );
    }

    /**
     * Converts GoalEntity to domain Goal.
     */
    public static Goal toGoal(GoalEntity entity) {
        return new Goal(
            entity.getTitle(),
            entity.getDescription(),
            entity.getStartDate(),
            entity.getTargetDate(),
            entity.getImportance(),
            entity.getTotalProgressPoints()
        );
    }

    // ========== GoalNote Mappings ==========

    /**
     * Converts domain GoalNote to GoalNoteEntity.
     */
    public static GoalNoteEntity toGoalNoteEntity(GoalEntity goalEntity, GoalNote goalNote) {
        return new GoalNoteEntity(
            goalEntity,
            goalNote.date(),
            goalNote.textNote(),
            goalNote.points()
        );
    }

    /**
     * Converts GoalNoteEntity to domain GoalNote.
     */
    public static GoalNote toGoalNote(GoalNoteEntity entity) {
        Goal goal = toGoal(entity.getGoal());
        return new GoalNote(goal, entity.getDate(), entity.getTextNote(), entity.getPoints());
    }

    // ========== XpHistoryEntry Mappings ==========

    /**
     * Converts domain XpHistoryEntry to XpHistoryEntryEntity.
     */
    public static XpHistoryEntryEntity toXpHistoryEntryEntity(UserEntity user, XpHistoryEntry entry) {
        XpHistoryEntryEntity.XpSourceEnum sourceEnum = toXpSourceEnum(entry.source());
        return new XpHistoryEntryEntity(user, entry.date(), entry.xpChange(), sourceEnum);
    }

    /**
     * Converts XpHistoryEntryEntity to domain XpHistoryEntry.
     */
    public static XpHistoryEntry toXpHistoryEntry(XpHistoryEntryEntity entity) {
        XpSource source = toXpSource(entity.getSource());
        return new XpHistoryEntry(entity.getDate(), entity.getXpChange(), source);
    }

    /**
     * Converts domain XpSource to XpHistoryEntryEntity.XpSourceEnum.
     */
    private static XpHistoryEntryEntity.XpSourceEnum toXpSourceEnum(XpSource source) {
        return switch (source) {
            case HABIT -> XpHistoryEntryEntity.XpSourceEnum.HABIT;
            case GOAL -> XpHistoryEntryEntity.XpSourceEnum.GOAL;
            case DECAY -> XpHistoryEntryEntity.XpSourceEnum.DECAY;
        };
    }

    /**
     * Converts XpHistoryEntryEntity.XpSourceEnum to domain XpSource.
     */
    private static XpSource toXpSource(XpHistoryEntryEntity.XpSourceEnum sourceEnum) {
        return switch (sourceEnum) {
            case HABIT -> XpSource.HABIT;
            case GOAL -> XpSource.GOAL;
            case DECAY -> XpSource.DECAY;
        };
    }
}

