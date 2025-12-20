package org.example;

/**
 * Example usage of the gamified habit and goal system.
 * Demonstrates creating habits, checking them, and tracking user progress.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize calculators
        XpCalculator xpCalculator = new XpCalculator();
        LevelCalculator levelCalculator = new LevelCalculator();

        // Create a new user starting at level 1 with 0 XP
        UserStats userStats = UserStats.createNew();
        System.out.println("Initial stats: " + userStats);
        System.out.println();

        // Create some habits with different difficulty levels
        Habit morningExercise = new Habit("Morning Exercise", Difficulty.THREE);
        Habit drinkWater = new Habit("Drink 8 Glasses of Water", Difficulty.ONE);
        Habit readBook = new Habit("Read for 30 Minutes", Difficulty.TWO);
        Habit meditate = new Habit("Meditate", Difficulty.FOUR);
        Habit learnNewSkill = new Habit("Learn New Skill", Difficulty.FIVE);

        System.out.println("=== Day 1 ===");
        // Complete some habits
        userStats = checkHabit(userStats, morningExercise, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        userStats = checkHabit(userStats, drinkWater, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        userStats = checkHabit(userStats, readBook, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        System.out.println("End of day stats: " + userStats);
        System.out.println();

        System.out.println("=== Day 2 ===");
        // Complete some, miss some
        userStats = checkHabit(userStats, morningExercise, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        userStats = checkHabit(userStats, drinkWater, HabitCheckResult.MISSED, xpCalculator, levelCalculator); // Higher penalty for easy habit
        userStats = checkHabit(userStats, meditate, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        userStats = checkHabit(userStats, learnNewSkill, HabitCheckResult.MISSED, xpCalculator, levelCalculator); // Lower penalty for hard habit
        System.out.println("End of day stats: " + userStats);
        System.out.println();

        System.out.println("=== Day 3 ===");
        // Complete all habits
        userStats = checkHabit(userStats, morningExercise, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        userStats = checkHabit(userStats, drinkWater, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        userStats = checkHabit(userStats, readBook, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        userStats = checkHabit(userStats, meditate, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        userStats = checkHabit(userStats, learnNewSkill, HabitCheckResult.DONE, xpCalculator, levelCalculator);
        System.out.println("End of day stats: " + userStats);
        System.out.println();

        // Show level progression info
        System.out.println("=== Level Information ===");
        int currentLevel = userStats.getLevel();
        int xpForNextLevel = levelCalculator.getXpRequiredForLevel(currentLevel + 1);
        int xpNeeded = xpForNextLevel - userStats.getTotalXp();
        System.out.println("Current level: " + currentLevel);
        System.out.println("Total XP: " + userStats.getTotalXp());
        System.out.println("XP needed for level " + (currentLevel + 1) + ": " + xpNeeded);
    }

    /**
     * Helper method to check a habit and update user stats.
     *
     * @param userStats the current user stats
     * @param habit the habit to check
     * @param result whether the habit was done or missed
     * @param xpCalculator the XP calculator
     * @param levelCalculator the level calculator
     * @return updated user stats
     */
    private static UserStats checkHabit(UserStats userStats, Habit habit, HabitCheckResult result,
                                        XpCalculator xpCalculator, LevelCalculator levelCalculator) {
        XpTransaction transaction = xpCalculator.calculateTransaction(habit, result);
        UserStats newStats = userStats.applyTransaction(transaction, levelCalculator);
        
        String status = result == HabitCheckResult.DONE ? "✓" : "✗";
        System.out.printf("%s %s: %s (XP: %+d, Total: %d, Level: %d)%n",
                status,
                habit.getName(),
                transaction.reason(),
                transaction.amount(),
                newStats.getTotalXp(),
                newStats.getLevel());
        
        return newStats;
    }
}
