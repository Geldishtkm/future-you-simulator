package org.example.strategy;

/**
 * Types of recommendations that can be generated.
 */
public enum RecommendationType {
    /**
     * Recommendation to reduce effort or difficulty to prevent burnout.
     */
    REDUCE_BURNOUT_RISK,

    /**
     * Recommendation to improve consistency in habits/goals.
     */
    IMPROVE_CONSISTENCY,

    /**
     * Recommendation to add goals or focus on goal completion.
     */
    ADD_GOAL_FOCUS,

    /**
     * Recommendation to adjust habit difficulty (increase or decrease).
     */
    ADJUST_HABIT_DIFFICULTY,

    /**
     * Recommendation to add new habits for skill growth.
     */
    ADD_HABITS_FOR_GROWTH,

    /**
     * Recommendation to balance effort to avoid stagnation.
     */
    BALANCE_EFFORT,

    /**
     * General optimization recommendation.
     */
    OPTIMIZE_STRATEGY
}

