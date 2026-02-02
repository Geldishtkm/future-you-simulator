package org.example.service;

import org.example.Goal;
import org.example.GoalTemplate;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing goal templates.
 */
@Service
public class GoalTemplateService {

    /**
     * Gets all available goal templates.
     */
    public List<GoalTemplate> getAllTemplates() {
        List<GoalTemplate> templates = new ArrayList<>();

        // Career templates
        templates.add(new GoalTemplate(
            "career-1",
            "Get a Job/Internship",
            "Land a job or internship in your field",
            "Career",
            180,
            5,
            new String[]{"Update resume", "Build portfolio", "Apply to 10+ companies", "Practice interviews"}
        ));

        templates.add(new GoalTemplate(
            "career-2",
            "Learn a New Skill",
            "Master a new professional skill",
            "Career",
            90,
            4,
            new String[]{"Choose skill", "Find learning resources", "Practice daily", "Build project"}
        ));

        // Health templates
        templates.add(new GoalTemplate(
            "health-1",
            "Lose Weight",
            "Achieve your target weight",
            "Health",
            180,
            4,
            new String[]{"Set target weight", "Create meal plan", "Exercise routine", "Track progress"}
        ));

        templates.add(new GoalTemplate(
            "health-2",
            "Build Muscle",
            "Gain muscle mass and strength",
            "Health",
            180,
            4,
            new String[]{"Design workout plan", "Nutrition plan", "Track workouts", "Measure progress"}
        ));

        templates.add(new GoalTemplate(
            "health-3",
            "Run a Marathon",
            "Complete a full marathon",
            "Health",
            365,
            5,
            new String[]{"Start training plan", "Build endurance", "Increase distance weekly", "Race day prep"}
        ));

        // Learning templates
        templates.add(new GoalTemplate(
            "learning-1",
            "Read 12 Books",
            "Read one book per month",
            "Learning",
            365,
            3,
            new String[]{"Create reading list", "Set monthly targets", "Join book club", "Take notes"}
        ));

        templates.add(new GoalTemplate(
            "learning-2",
            "Learn a Language",
            "Become conversational in a new language",
            "Learning",
            365,
            4,
            new String[]{"Choose language", "Daily practice", "Find conversation partners", "Immerse yourself"}
        ));

        // Financial templates
        templates.add(new GoalTemplate(
            "finance-1",
            "Save Emergency Fund",
            "Build 6 months of expenses",
            "Finance",
            365,
            5,
            new String[]{"Calculate target amount", "Set monthly savings", "Open savings account", "Track progress"}
        ));

        templates.add(new GoalTemplate(
            "finance-2",
            "Pay Off Debt",
            "Eliminate all credit card debt",
            "Finance",
            365,
            5,
            new String[]{"List all debts", "Create payment plan", "Cut expenses", "Increase income"}
        ));

        // Personal Development templates
        templates.add(new GoalTemplate(
            "personal-1",
            "Build Daily Meditation Habit",
            "Meditate every day for 30 days",
            "Personal Development",
            30,
            3,
            new String[]{"Start with 5 minutes", "Use guided apps", "Create quiet space", "Track consistency"}
        ));

        templates.add(new GoalTemplate(
            "personal-2",
            "Write a Book",
            "Complete your first book manuscript",
            "Personal Development",
            365,
            5,
            new String[]{"Outline chapters", "Set daily word count", "Write consistently", "Edit and revise"}
        ));

        return templates;
    }

    /**
     * Gets templates by category.
     */
    public List<GoalTemplate> getTemplatesByCategory(String category) {
        return getAllTemplates().stream()
            .filter(t -> t.category().equalsIgnoreCase(category))
            .toList();
    }

    /**
     * Gets a template by ID.
     */
    public GoalTemplate getTemplateById(String id) {
        return getAllTemplates().stream()
            .filter(t -> t.id().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Template not found: " + id));
    }

    /**
     * Creates a Goal from a template.
     */
    public Goal createGoalFromTemplate(GoalTemplate template, LocalDate startDate) {
        LocalDate targetDate = startDate.plusDays(template.suggestedDurationDays());
        return new Goal(
            template.title(),
            template.description(),
            startDate,
            targetDate,
            template.suggestedImportance(),
            100 // Default progress points
        );
    }
}

