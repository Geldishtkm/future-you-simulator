package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for exporting and importing user data.
 */
@Service
public class ExportService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Exports user data to JSON format.
     * If {@code from} and/or {@code to} are provided, habit checks and goal notes
     * are filtered to the given date range (inclusive). Other data (habits, goals,
     * stats, achievements, milestones) are exported in full.
     */
    public String exportToJson(UserStats userStats,
                               List<Habit> habits,
                               List<Goal> goals,
                               HabitService habitService,
                               GoalService goalService,
                               AchievementService achievementService,
                               MilestoneService milestoneService,
                               AnalyticsService analyticsService,
                               LocalDate from,
                               LocalDate to) {
        try {
            List<HabitCheck> habitChecks = habitService.getAllHabitChecks().stream()
                .filter(check -> isWithinRange(check.date(), from, to))
                .toList();
            List<GoalNote> goalNotes = goalService.getAllGoalNotes().stream()
                .filter(note -> isWithinRange(note.date(), from, to))
                .toList();
            
            LocalDate currentDate = LocalDate.now();
            List<Achievement> achievements = achievementService.calculateAchievements(
                userStats, habits, goals, habitService, goalService, analyticsService, currentDate);
            List<Milestone> milestones = milestoneService.getMilestones(
                userStats, habits, goals, habitService, goalService, analyticsService, currentDate);

            ExportData exportData = new ExportData(
                currentDate,
                userStats,
                habits,
                goals,
                habitChecks,
                goalNotes,
                achievements,
                milestones
            );

            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exportData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export data: " + e.getMessage(), e);
        }
    }

    /**
     * Exports user data to CSV format.
     * If {@code from} and/or {@code to} are provided, habit checks and goal notes
     * are filtered to the given date range (inclusive).
     */
    public String exportToCsv(UserStats userStats,
                              List<Habit> habits,
                              List<Goal> goals,
                              HabitService habitService,
                              GoalService goalService,
                              LocalDate from,
                              LocalDate to) {
        StringBuilder csv = new StringBuilder();
        
        // Header
        csv.append("Type,Name,Date,Value,Description\n");
        
        // User stats
        csv.append(String.format("STATS,Total XP,%s,%d,Total accumulated XP\n", 
            LocalDate.now(), userStats.getTotalXp()));
        csv.append(String.format("STATS,Level,%s,%d,Current level\n", 
            LocalDate.now(), userStats.getLevel()));
        
        // Habits
        for (Habit habit : habits) {
            csv.append(String.format("HABIT,%s,%s,%d,Habit: %s\n",
                habit.getName(), LocalDate.now(), habit.getDifficulty().getValue(), habit.getName()));
        }
        
        // Habit checks
        for (HabitCheck check : habitService.getAllHabitChecks().stream()
                .filter(check -> isWithinRange(check.date(), from, to))
                .toList()) {
            csv.append(String.format("HABIT_CHECK,%s,%s,%s,%s\n",
                check.habit().getName(),
                check.date(),
                check.result().name(),
                check.result() == HabitCheckResult.DONE ? "Completed" : "Missed"));
        }
        
        // Goals
        for (Goal goal : goals) {
            csv.append(String.format("GOAL,%s,%s,%d,Goal: %s\n",
                goal.getTitle(), goal.getStartDate(), goal.getTotalProgressPoints(), goal.getDescription()));
        }
        
        // Goal notes
        for (GoalNote note : goalService.getAllGoalNotes().stream()
                .filter(note -> isWithinRange(note.date(), from, to))
                .toList()) {
            csv.append(String.format("GOAL_NOTE,Progress,%s,%d,%s\n",
                note.date(), note.points(), note.textNote()));
        }
        
        return csv.toString();
    }

    /**
     * Returns true if the given {@code date} is within the optional {@code from}/{@code to}
     * range (inclusive). A null {@code from} means "no lower bound"; a null {@code to}
     * means "no upper bound".
     */
    private boolean isWithinRange(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null) {
            return false;
        }
        if (from != null && date.isBefore(from)) {
            return false;
        }
        if (to != null && date.isAfter(to)) {
            return false;
        }
        return true;
    }
}

