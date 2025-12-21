package org.example;

import java.time.LocalDate;

/**
 * Example usage of the gamified habit and goal system with anti-cheat features.
 * Demonstrates:
 * - Daily XP capping
 * - Activity tracking
 * - Anti-cheat validation (preventing duplicate rewards)
 * - Inactivity XP decay
 */
public class Main {
    public static void main(String[] args) {
        // Initialize the habit service (handles all business logic)
        HabitService habitService = new HabitService();
        
        // Create a new user starting at level 1 with 0 XP
        UserStats userStats = UserStats.createNew();
        System.out.println("=== Initial State ===");
        System.out.println("Initial stats: " + userStats);
        System.out.println("Daily XP limit: " + habitService.getDailyXpLimit().getMaxXpPerDay() + " XP per day");
        System.out.println();

        // Create some habits with different difficulty levels
        Habit morningExercise = new Habit("Morning Exercise", Difficulty.THREE);
        Habit drinkWater = new Habit("Drink 8 Glasses of Water", Difficulty.ONE);
        Habit readBook = new Habit("Read for 30 Minutes", Difficulty.TWO);
        Habit meditate = new Habit("Meditate", Difficulty.FOUR);
        Habit learnNewSkill = new Habit("Learn New Skill", Difficulty.FIVE);

        LocalDate today = LocalDate.now();
        LocalDate day1 = today;
        LocalDate day2 = today.plusDays(1);
        LocalDate day3 = today.plusDays(2);
        LocalDate day4 = today.plusDays(3);
        LocalDate day5 = today.plusDays(4);
        LocalDate day6 = today.plusDays(5); // For inactivity decay demo

        System.out.println("=== Day 1: " + day1 + " ===");
        userStats = checkHabit(habitService, userStats, morningExercise, day1, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, drinkWater, day1, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, readBook, day1, HabitCheckResult.DONE);
        printDaySummary(habitService, userStats, day1);
        System.out.println();

        System.out.println("=== Day 2: " + day2 + " ===");
        userStats = checkHabit(habitService, userStats, morningExercise, day2, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, drinkWater, day2, HabitCheckResult.MISSED); // Penalty
        userStats = checkHabit(habitService, userStats, meditate, day2, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, learnNewSkill, day2, HabitCheckResult.DONE);
        printDaySummary(habitService, userStats, day2);
        System.out.println();

        System.out.println("=== Day 3: " + day3 + " (Testing Daily XP Cap) ===");
        // Complete many habits to hit the daily cap (100 XP)
        userStats = checkHabit(habitService, userStats, morningExercise, day3, HabitCheckResult.DONE); // +30
        userStats = checkHabit(habitService, userStats, drinkWater, day3, HabitCheckResult.DONE); // +10 (40 total)
        userStats = checkHabit(habitService, userStats, readBook, day3, HabitCheckResult.DONE); // +20 (60 total)
        userStats = checkHabit(habitService, userStats, meditate, day3, HabitCheckResult.DONE); // +40 (100 total - CAP REACHED)
        userStats = checkHabit(habitService, userStats, learnNewSkill, day3, HabitCheckResult.DONE); // Would be +50, but capped at 0
        printDaySummary(habitService, userStats, day3);
        System.out.println();

        System.out.println("=== Day 3: Anti-Cheat Test ===");
        try {
            // Try to check the same habit twice (should fail)
            userStats = checkHabit(habitService, userStats, morningExercise, day3, HabitCheckResult.DONE);
            System.out.println("ERROR: Should have thrown an exception!");
        } catch (IllegalStateException e) {
            System.out.println("✓ Anti-cheat validation working: " + e.getMessage());
        }
        System.out.println();

        System.out.println("=== Day 4: " + day4 + " ===");
        userStats = checkHabit(habitService, userStats, morningExercise, day4, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, drinkWater, day4, HabitCheckResult.DONE);
        printDaySummary(habitService, userStats, day4);
        System.out.println();

        System.out.println("=== Day 5: " + day5 + " (No activity - preparing for decay) ===");
        System.out.println("No habits checked today.");
        printDaySummary(habitService, userStats, day5);
        System.out.println();

        System.out.println("=== Day 6: " + day6 + " (Testing Inactivity Decay) ===");
        System.out.println("Last activity: " + habitService.getLastActivityDate());
        System.out.println("Days inactive: " + java.time.temporal.ChronoUnit.DAYS.between(day4, day6));
        System.out.println("XP before decay: " + userStats.getTotalXp());
        
        // Checking a habit after inactivity will trigger decay
        userStats = checkHabit(habitService, userStats, readBook, day6, HabitCheckResult.DONE);
        printDaySummary(habitService, userStats, day6);
        System.out.println();

        // Show final summary
        System.out.println("=== Final Summary ===");
        System.out.println("Final stats: " + userStats);
        LevelCalculator levelCalculator = new LevelCalculator();
        int currentLevel = userStats.getLevel();
        int xpForNextLevel = levelCalculator.getXpRequiredForLevel(currentLevel + 1);
        int xpNeeded = xpForNextLevel - userStats.getTotalXp();
        System.out.println("XP needed for level " + (currentLevel + 1) + ": " + xpNeeded);
        System.out.println("Last activity date: " + habitService.getLastActivityDate());
    }

    /**
     * Helper method to check a habit using the HabitService.
     *
     * @param habitService the habit service
     * @param userStats the current user stats
     * @param habit the habit to check
     * @param date the date for this check
     * @param result whether the habit was done or missed
     * @return updated user stats
     */
    private static UserStats checkHabit(HabitService habitService, UserStats userStats, Habit habit, 
                                       LocalDate date, HabitCheckResult result) {
        HabitService.CheckResult checkResult = habitService.checkHabit(userStats, habit, date, result);
        
        String status = result == HabitCheckResult.DONE ? "✓" : "✗";
        XpTransaction transaction = checkResult.transaction();
        
        if (transaction.isNoOp()) {
            System.out.printf("%s %s: %s (XP: 0 - Daily cap reached)%n",
                    status, habit.getName(), transaction.reason());
        } else {
            System.out.printf("%s %s: %s (XP: %+d, Total: %d, Level: %d)%n",
                    status,
                    habit.getName(),
                    transaction.reason(),
                    transaction.amount(),
                    checkResult.userStats().getTotalXp(),
                    checkResult.userStats().getLevel());
        }
        
        return checkResult.userStats();
    }

    /**
     * Prints a summary of the day's activity.
     *
     * @param habitService the habit service
     * @param userStats the current user stats
     * @param date the date to summarize
     */
    private static void printDaySummary(HabitService habitService, UserStats userStats, LocalDate date) {
        DailyActivityLog log = habitService.getActivityLog(date);
        int dailyLimit = habitService.getDailyXpLimit().getMaxXpPerDay();
        System.out.println("--- Day Summary ---");
        System.out.println("Habits checked: " + log.getHabitCheckCount());
        System.out.println("XP gained today: " + log.getXpGained() + " / " + dailyLimit + " (daily cap)");
        System.out.println("Total XP: " + userStats.getTotalXp());
        System.out.println("Current level: " + userStats.getLevel());
    }
}
