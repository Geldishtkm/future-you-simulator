package org.example;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Main service for managing long-term goals and daily goal progress.
 * Provides anti-cheat validation, daily goal XP limits, and progress tracking.
 * 
 * Goal XP contributes to overall user XP and respects the daily XP cap from HabitService.
 */
public class GoalService {
    private final GoalProgressCalculator progressCalculator;
    private final DailyGoalXpLimit dailyGoalXpLimit;
    private final LevelCalculator levelCalculator;
    private final Map<String, Goal> goals; // Using title as key for simplicity
    private final Map<LocalDate, Map<Goal, GoalNote>> dailyGoalNotes; // date -> (goal -> note)
    private final Map<LocalDate, Map<Goal, Integer>> dailyGoalXp; // date -> (goal -> xp assigned)

    /**
     * Creates a new GoalService with default settings.
     * Default: 10 XP per goal per day limit.
     */
    public GoalService() {
        this(new GoalProgressCalculator(), DailyGoalXpLimit.defaultLimit(), new LevelCalculator());
    }

    /**
     * Creates a new GoalService with custom settings.
     *
     * @param progressCalculator the progress calculator to use
     * @param dailyGoalXpLimit the daily goal XP limit configuration
     * @param levelCalculator the level calculator to use
     * @throws IllegalArgumentException if any parameter is null
     */
    public GoalService(GoalProgressCalculator progressCalculator, 
                      DailyGoalXpLimit dailyGoalXpLimit,
                      LevelCalculator levelCalculator) {
        if (progressCalculator == null) {
            throw new IllegalArgumentException("GoalProgressCalculator cannot be null");
        }
        if (dailyGoalXpLimit == null) {
            throw new IllegalArgumentException("DailyGoalXpLimit cannot be null");
        }
        if (levelCalculator == null) {
            throw new IllegalArgumentException("LevelCalculator cannot be null");
        }
        this.progressCalculator = progressCalculator;
        this.dailyGoalXpLimit = dailyGoalXpLimit;
        this.levelCalculator = levelCalculator;
        this.goals = new HashMap<>();
        this.dailyGoalNotes = new HashMap<>();
        this.dailyGoalXp = new HashMap<>();
    }

    /**
     * Adds a new goal to the system.
     *
     * @param goal the goal to add
     * @throws IllegalArgumentException if goal is null or a goal with the same title already exists
     */
    public void addGoal(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (goals.containsKey(goal.getTitle())) {
            throw new IllegalArgumentException("A goal with title '" + goal.getTitle() + "' already exists");
        }
        goals.put(goal.getTitle(), goal);
    }

    /**
     * Gets a goal by title.
     *
     * @param title the goal title
     * @return the goal, or null if not found
     */
    public Goal getGoal(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        return goals.get(title);
    }

    /**
     * Gets all goals.
     *
     * @return a list of all goals
     */
    public List<Goal> getAllGoals() {
        return new ArrayList<>(goals.values());
    }

    /**
     * Adds a daily goal note and applies XP.
     * This method enforces all business rules:
     * - Prevents adding more than one note per goal per day
     * - Enforces daily goal XP limit
     * - Respects overall daily XP cap from HabitService
     * - Applies XP via transaction system
     *
     * @param userStats the current user stats
     * @param goal the goal to add a note for
     * @param date the date of this note
     * @param textNote the text content of the note
     * @param requestedXp the XP the user wants to assign (will be capped if needed)
     * @param habitService the habit service to check overall daily XP cap (must not be null)
     * @return a NoteResult containing updated stats, the note, and the XP transaction
     * @throws IllegalArgumentException if any parameter is null or goal not found
     * @throws IllegalStateException if attempting to add a second note for the same goal on the same day
     */
    public NoteResult addGoalNote(UserStats userStats, Goal goal, LocalDate date, 
                                 String textNote, int requestedXp, HabitService habitService) {
        if (userStats == null) {
            throw new IllegalArgumentException("UserStats cannot be null");
        }
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (habitService == null) {
            throw new IllegalArgumentException("HabitService cannot be null");
        }
        if (!goals.containsKey(goal.getTitle())) {
            throw new IllegalArgumentException("Goal '" + goal.getTitle() + "' not found");
        }
        if (requestedXp < 0) {
            throw new IllegalArgumentException("Requested XP cannot be negative");
        }

        // Anti-cheat: Prevent adding more than one note per goal per day
        Map<Goal, GoalNote> notesForDate = dailyGoalNotes.getOrDefault(date, new HashMap<>());
        if (notesForDate.containsKey(goal)) {
            throw new IllegalStateException(
                String.format("A note for goal '%s' has already been added on %s. Only one note per goal per day is allowed.",
                    goal.getTitle(), date));
        }

        // Enforce daily goal XP limit
        Map<Goal, Integer> xpForDate = dailyGoalXp.getOrDefault(date, new HashMap<>());
        int xpAlreadyAssignedToGoal = xpForDate.getOrDefault(goal, 0);
        int remainingGoalXpCapacity = dailyGoalXpLimit.getMaxXpPerGoalPerDay() - xpAlreadyAssignedToGoal;
        
        int goalXpToAssign = Math.min(requestedXp, Math.max(0, remainingGoalXpCapacity));

        // Check overall daily XP cap from HabitService
        DailyActivityLog todayLog = habitService.getActivityLog(date);
        int xpAlreadyGainedToday = todayLog.getXpGained();
        int overallDailyXpLimit = habitService.getDailyXpLimit().getMaxXpPerDay();
        int remainingOverallXpCapacity = overallDailyXpLimit - xpAlreadyGainedToday;

        // Apply both limits (goal-specific and overall daily cap)
        int finalXp = Math.min(goalXpToAssign, Math.max(0, remainingOverallXpCapacity));

        // Create goal note (points are the XP assigned, not separate)
        GoalNote note = new GoalNote(goal, date, textNote, finalXp);

        // Store the note
        if (!dailyGoalNotes.containsKey(date)) {
            dailyGoalNotes.put(date, new HashMap<>());
        }
        dailyGoalNotes.get(date).put(goal, note);

        // Track XP assigned to this goal on this date
        if (!dailyGoalXp.containsKey(date)) {
            dailyGoalXp.put(date, new HashMap<>());
        }
        dailyGoalXp.get(date).put(goal, xpAlreadyAssignedToGoal + finalXp);

        // Create XP transaction
        String reason;
        if (finalXp == 0) {
            reason = String.format("Goal '%s' note added, but XP capped at 0 (daily limits reached)", goal.getTitle());
        } else if (finalXp < requestedXp) {
            reason = String.format("Goal '%s' note: %d XP assigned (requested %d, capped due to daily limits)",
                    goal.getTitle(), finalXp, requestedXp);
        } else {
            reason = String.format("Goal '%s' note: %d XP assigned", goal.getTitle(), finalXp);
        }
        XpTransaction transaction = new XpTransaction(finalXp, reason);

        // Apply XP transaction
        UserStats updatedStats = userStats;
        if (finalXp > 0) {
            updatedStats = userStats.applyTransaction(transaction, levelCalculator);
            // Record goal XP in HabitService's activity log so it counts towards daily cap
            habitService.recordGoalXp(date, finalXp);
        }

        return new NoteResult(updatedStats, note, transaction);
    }

    /**
     * Gets all notes for a specific goal.
     *
     * @param goal the goal
     * @return a list of all notes for this goal, ordered by date
     */
    public List<GoalNote> getGoalNotes(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        return dailyGoalNotes.values().stream()
                .flatMap(map -> map.values().stream())
                .filter(note -> note.goal().equals(goal))
                .sorted((a, b) -> a.date().compareTo(b.date()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the note for a specific goal on a specific date.
     *
     * @param goal the goal
     * @param date the date
     * @return the note, or null if no note exists for that date
     */
    public GoalNote getGoalNote(Goal goal, LocalDate date) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        Map<Goal, GoalNote> notesForDate = dailyGoalNotes.get(date);
        return notesForDate != null ? notesForDate.get(goal) : null;
    }

    /**
     * Calculates the current progress for a goal.
     *
     * @param goal the goal
     * @return the progress percentage (0.0 to 100.0)
     */
    public double calculateProgress(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        List<GoalNote> notes = getGoalNotes(goal);
        return progressCalculator.calculateProgressFromNotes(goal, notes);
    }

    /**
     * Gets the accumulated points for a goal.
     *
     * @param goal the goal
     * @return the total accumulated points
     */
    public int getAccumulatedPoints(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Goal cannot be null");
        }
        List<GoalNote> notes = getGoalNotes(goal);
        return progressCalculator.calculateAccumulatedPoints(notes);
    }

    /**
     * Gets the daily goal XP limit configuration.
     *
     * @return the daily goal XP limit
     */
    public DailyGoalXpLimit getDailyGoalXpLimit() {
        return dailyGoalXpLimit;
    }

    /**
     * Result of adding a goal note.
     * Contains the updated user stats, the note that was created, and the XP transaction that was applied.
     *
     * @param userStats the updated user stats after the note
     * @param note the goal note that was created
     * @param transaction the XP transaction that was applied (may be capped)
     */
    public record NoteResult(UserStats userStats, GoalNote note, XpTransaction transaction) {
    }
}

