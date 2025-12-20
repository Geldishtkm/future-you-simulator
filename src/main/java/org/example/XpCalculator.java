package org.example;

/**
 * Calculates XP rewards and penalties based on habit completion.
 * 
 * Rules:
 * - Completing a habit rewards XP based on difficulty (higher difficulty = more XP)
 * - Missing a habit penalizes XP
 * - Missing an easy habit (difficulty 1-2) incurs a higher penalty than missing harder habits
 */
public class XpCalculator {
    private static final int BASE_REWARD_MULTIPLIER = 10;
    private static final int BASE_PENALTY_EASY = -15;
    private static final int BASE_PENALTY_HARD = -5;
    private static final int EASY_DIFFICULTY_THRESHOLD = 2;

    /**
     * Calculates an XP transaction based on a habit check result.
     *
     * @param habit the habit that was checked
     * @param result whether the habit was done or missed
     * @return an XpTransaction representing the XP change
     * @throws IllegalArgumentException if habit or result is null
     */
    public XpTransaction calculateTransaction(Habit habit, HabitCheckResult result) {
        if (habit == null) {
            throw new IllegalArgumentException("Habit cannot be null");
        }
        if (result == null) {
            throw new IllegalArgumentException("HabitCheckResult cannot be null");
        }

        return switch (result) {
            case DONE -> calculateReward(habit);
            case MISSED -> calculatePenalty(habit);
        };
    }

    /**
     * Calculates the XP reward for completing a habit.
     * Reward = difficulty level * base multiplier
     *
     * @param habit the completed habit
     * @return an XpTransaction with positive amount
     */
    private XpTransaction calculateReward(Habit habit) {
        int reward = habit.getDifficulty().getValue() * BASE_REWARD_MULTIPLIER;
        String reason = String.format("Completed habit '%s' (difficulty %d)", 
                habit.getName(), habit.getDifficulty().getValue());
        return new XpTransaction(reward, reason);
    }

    /**
     * Calculates the XP penalty for missing a habit.
     * Easy habits (difficulty 1-2) have a higher penalty than harder habits.
     *
     * @param habit the missed habit
     * @return an XpTransaction with negative amount
     */
    private XpTransaction calculatePenalty(Habit habit) {
        int difficulty = habit.getDifficulty().getValue();
        int penalty;
        
        if (difficulty <= EASY_DIFFICULTY_THRESHOLD) {
            // Higher penalty for missing easy habits
            penalty = BASE_PENALTY_EASY * difficulty;
        } else {
            // Lower penalty for missing harder habits
            penalty = BASE_PENALTY_HARD * difficulty;
        }
        
        String reason = String.format("Missed habit '%s' (difficulty %d)", 
                habit.getName(), habit.getDifficulty().getValue());
        return new XpTransaction(penalty, reason);
    }
}

