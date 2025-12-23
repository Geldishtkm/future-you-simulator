package org.example;

import java.time.LocalDate;
import java.util.List;

/**
 * Example usage of the gamified habit and goal system with anti-cheat features.
 * Demonstrates:
 * - Daily XP capping
 * - Activity tracking
 * - Anti-cheat validation (preventing duplicate rewards)
 * - Inactivity XP decay
 * - Long-term goals and daily goal progress
 * - Analytics, streaks, trends, and burnout detection
 */
public class Main {
    public static void main(String[] args) {
        // Initialize services
        HabitService habitService = new HabitService();
        GoalService goalService = new GoalService();
        
        // Create a new user starting at level 1 with 0 XP
        UserStats userStats = UserStats.createNew();
        System.out.println("=== Initial State ===");
        System.out.println("Initial stats: " + userStats);
        System.out.println("Daily XP limit: " + habitService.getDailyXpLimit().getMaxXpPerDay() + " XP per day");
        System.out.println("Daily goal XP limit: " + goalService.getDailyGoalXpLimit().getMaxXpPerGoalPerDay() + " XP per goal per day");
        System.out.println();

        // Create some habits with different difficulty levels
        Habit morningExercise = new Habit("Morning Exercise", Difficulty.THREE);
        Habit drinkWater = new Habit("Drink 8 Glasses of Water", Difficulty.ONE);
        Habit readBook = new Habit("Read for 30 Minutes", Difficulty.TWO);
        Habit meditate = new Habit("Meditate", Difficulty.FOUR);
        Habit learnNewSkill = new Habit("Learn New Skill", Difficulty.FIVE);

        // Create long-term goals
        LocalDate today = LocalDate.now();
        Goal internshipGoal = new Goal(
            "Get a Backend Internship",
            "Land an internship at a tech company working on backend systems",
            today,
            today.plusMonths(6),
            5, // High importance
            100 // 100 points to complete
        );

        Goal fitnessGoal = new Goal(
            "Run a Half Marathon",
            "Complete a half marathon race",
            today,
            today.plusMonths(4),
            4, // High importance
            80 // 80 points to complete
        );

        GoalService.NoteResult noteResult;

        // Add goals to the system
        goalService.addGoal(internshipGoal);
        goalService.addGoal(fitnessGoal);
        System.out.println("=== Goals Created ===");
        System.out.println("Goal 1: " + internshipGoal.getTitle() + " (Importance: " + internshipGoal.getImportance() + ")");
        System.out.println("Goal 2: " + fitnessGoal.getTitle() + " (Importance: " + fitnessGoal.getImportance() + ")");
        System.out.println();

        LocalDate day1 = today;
        LocalDate day2 = today.plusDays(1);
        LocalDate day3 = today.plusDays(2);
        LocalDate day4 = today.plusDays(3);

        System.out.println("=== Day 1: " + day1 + " ===");
        // Check some habits
        userStats = checkHabit(habitService, userStats, morningExercise, day1, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, drinkWater, day1, HabitCheckResult.DONE);
        
        // Add goal notes
        System.out.println("\n--- Goal Progress ---");
        noteResult = goalService.addGoalNote(userStats, internshipGoal, day1, 
            "Applied to 3 companies, updated resume", 8, habitService);
        userStats = noteResult.userStats();
        System.out.println("✓ " + internshipGoal.getTitle() + ": " + noteResult.transaction().reason() + 
            " (XP: +" + noteResult.transaction().amount() + ")");
        
        noteResult = goalService.addGoalNote(userStats, fitnessGoal, day1, 
            "Ran 5km, feeling good", 6, habitService);
        userStats = noteResult.userStats();
        System.out.println("✓ " + fitnessGoal.getTitle() + ": " + noteResult.transaction().reason() + 
            " (XP: +" + noteResult.transaction().amount() + ")");
        
        printDaySummary(habitService, userStats, day1);
        printGoalProgress(goalService, internshipGoal, fitnessGoal);
        System.out.println();

        System.out.println("=== Day 2: " + day2 + " ===");
        userStats = checkHabit(habitService, userStats, morningExercise, day2, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, readBook, day2, HabitCheckResult.DONE);
        
        System.out.println("\n--- Goal Progress ---");
        noteResult = goalService.addGoalNote(userStats, internshipGoal, day2, 
            "Completed coding challenge, sent follow-up email", 10, habitService);
        userStats = noteResult.userStats();
        System.out.println("✓ " + internshipGoal.getTitle() + ": " + noteResult.transaction().reason() + 
            " (XP: +" + noteResult.transaction().amount() + ")");
        
        noteResult = goalService.addGoalNote(userStats, fitnessGoal, day2, 
            "Rest day, did stretching", 3, habitService);
        userStats = noteResult.userStats();
        System.out.println("✓ " + fitnessGoal.getTitle() + ": " + noteResult.transaction().reason() + 
            " (XP: +" + noteResult.transaction().amount() + ")");
        
        printDaySummary(habitService, userStats, day2);
        printGoalProgress(goalService, internshipGoal, fitnessGoal);
        System.out.println();

        System.out.println("=== Day 3: " + day3 + " (Testing Goal XP Limits) ===");
        userStats = checkHabit(habitService, userStats, meditate, day3, HabitCheckResult.DONE);
        
        System.out.println("\n--- Goal Progress ---");
        // Try to add more than 10 XP to a goal (should be capped at 10)
        noteResult = goalService.addGoalNote(userStats, internshipGoal, day3, 
            "Had interview, it went well!", 15, habitService); // Requesting 15, but limit is 10
        userStats = noteResult.userStats();
        System.out.println("✓ " + internshipGoal.getTitle() + ": " + noteResult.transaction().reason() + 
            " (XP: +" + noteResult.transaction().amount() + ")");
        
        // Try to add a second note for the same goal on the same day (should fail)
        System.out.println("\n--- Anti-Cheat Test: Duplicate Goal Note ---");
        try {
            noteResult = goalService.addGoalNote(userStats, internshipGoal, day3, 
                "Another note for same goal", 5, habitService);
            System.out.println("ERROR: Should have thrown an exception!");
        } catch (IllegalStateException e) {
            System.out.println("✓ Anti-cheat validation working: " + e.getMessage());
        }
        
        printDaySummary(habitService, userStats, day3);
        printGoalProgress(goalService, internshipGoal, fitnessGoal);
        System.out.println();

        System.out.println("=== Day 4: " + day4 + " (Testing Overall Daily XP Cap Integration) ===");
        // Add goal XP first
        System.out.println("\n--- Goal Progress ---");
        noteResult = goalService.addGoalNote(userStats, internshipGoal, day4, 
            "Received positive feedback", 10, habitService);
        userStats = noteResult.userStats();
        System.out.println("✓ " + internshipGoal.getTitle() + ": " + noteResult.transaction().reason() + 
            " (XP: +" + noteResult.transaction().amount() + ")");
        
        // Now check habits - they should respect the overall daily cap
        System.out.println("\n--- Habits ---");
        userStats = checkHabit(habitService, userStats, morningExercise, day4, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, drinkWater, day4, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, readBook, day4, HabitCheckResult.DONE);
        userStats = checkHabit(habitService, userStats, meditate, day4, HabitCheckResult.DONE);
        // This should be capped because goal XP (10) + habit XP (30+10+20+40=100) = 110, but cap is 100
        userStats = checkHabit(habitService, userStats, learnNewSkill, day4, HabitCheckResult.DONE);
        
        printDaySummary(habitService, userStats, day4);
        printGoalProgress(goalService, internshipGoal, fitnessGoal);
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
        System.out.println();
        
        System.out.println("=== Goal Progress Summary ===");
        for (Goal goal : goalService.getAllGoals()) {
            double progress = goalService.calculateProgress(goal);
            int accumulated = goalService.getAccumulatedPoints(goal);
            System.out.printf("%s: %.1f%% complete (%d / %d points)%n",
                goal.getTitle(), progress, accumulated, goal.getTotalProgressPoints());
        }
        System.out.println();

        // ========== ANALYTICS & INSIGHTS ==========
        System.out.println("=== ANALYTICS & INSIGHTS ===");
        AnalyticsService analyticsService = new AnalyticsService();
        LocalDate currentDate = day4;
        List<Habit> allHabits = List.of(morningExercise, drinkWater, readBook, meditate, learnNewSkill);
        List<Goal> allGoals = goalService.getAllGoals();

        // Generate comprehensive analytics summary
        AnalyticsService.AnalyticsSummary summary = analyticsService.generateSummary(
            allHabits, allGoals, habitService, goalService, currentDate);

        // 1. Habit Streaks
        System.out.println("\n--- Habit Streaks ---");
        for (Habit habit : allHabits) {
            HabitStreak streak = summary.habitStreaks().get(habit);
            if (streak != null) {
                if (streak.hasActiveStreak()) {
                    System.out.printf("  %s: 🔥 %d day streak (longest: %d days, started: %s)%n",
                        habit.getName(), streak.currentStreak(), streak.longestStreak(), 
                        streak.streakStartDate());
                } else {
                    System.out.printf("  %s: No active streak (longest: %d days)%n",
                        habit.getName(), streak.longestStreak());
                }
            }
        }

        // 2. Goal Consistency
        System.out.println("\n--- Goal Consistency ---");
        for (Goal goal : allGoals) {
            GoalConsistency consistency = summary.goalConsistency().get(goal);
            if (consistency != null) {
                System.out.printf("  %s: %.1f/100 consistency score%n",
                    goal.getTitle(), consistency.consistencyScore());
                System.out.printf("    - Active days: %d, Total notes: %d, Avg gap: %.1f days%n",
                    consistency.activeDays(), consistency.totalNotes(), consistency.averageGapDays());
            }
        }

        // 3. XP Trend Analysis
        System.out.println("\n--- XP Trend Analysis (Last 14 Days) ---");
        Trend trend = summary.xpTrend();
        String trendEmoji = switch (trend) {
            case IMPROVING -> "📈";
            case STABLE -> "➡️";
            case DECLINING -> "📉";
        };
        System.out.println("  Trend: " + trendEmoji + " " + trend);

        // 4. XP History Summary
        System.out.println("\n--- XP History Summary ---");
        List<XpHistoryEntry> history = summary.xpHistory();
        long habitXpEntries = history.stream().filter(e -> e.source() == XpSource.HABIT).count();
        long goalXpEntries = history.stream().filter(e -> e.source() == XpSource.GOAL).count();
        int totalHabitXp = history.stream()
            .filter(e -> e.source() == XpSource.HABIT)
            .mapToInt(XpHistoryEntry::xpChange)
            .sum();
        int totalGoalXp = history.stream()
            .filter(e -> e.source() == XpSource.GOAL)
            .mapToInt(XpHistoryEntry::xpChange)
            .sum();
        
        System.out.printf("  Total XP entries: %d%n", history.size());
        System.out.printf("  Habit XP: %d entries, %+d total XP%n", habitXpEntries, totalHabitXp);
        System.out.printf("  Goal XP: %d entries, %+d total XP%n", goalXpEntries, totalGoalXp);
        
        if (!history.isEmpty()) {
            System.out.println("  Recent entries:");
            int recentCount = Math.min(5, history.size());
            for (int i = history.size() - recentCount; i < history.size(); i++) {
                XpHistoryEntry entry = history.get(i);
                System.out.printf("    %s: %+d XP from %s%n",
                    entry.date(), entry.xpChange(), entry.source());
            }
        }

        // 5. Burnout Warning
        System.out.println("\n--- Burnout Warning ---");
        BurnoutWarning burnout = summary.burnoutWarning();
        if (burnout.isWarningActive()) {
            System.out.println("  ⚠️  WARNING: Potential burnout risk detected!");
            System.out.printf("  Severity Score: %.1f/100%n", burnout.severityScore());
            System.out.println("  Risk Factors:");
            for (String factor : burnout.riskFactors()) {
                System.out.println("    - " + factor);
            }
        } else {
            System.out.println("  ✅ No burnout warning - keep up the good work!");
            if (burnout.hasRiskFactors()) {
                System.out.println("  Minor factors detected (below threshold):");
                for (String factor : burnout.riskFactors()) {
                    System.out.println("    - " + factor);
                }
            }
        }
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

    /**
     * Prints progress for the given goals.
     *
     * @param goalService the goal service
     * @param goals the goals to show progress for
     */
    private static void printGoalProgress(GoalService goalService, Goal... goals) {
        System.out.println("--- Goal Progress ---");
        for (Goal goal : goals) {
            double progress = goalService.calculateProgress(goal);
            int accumulated = goalService.getAccumulatedPoints(goal);
            System.out.printf("  %s: %.1f%% (%d / %d points)%n",
                goal.getTitle(), progress, accumulated, goal.getTotalProgressPoints());
        }
    }
}
