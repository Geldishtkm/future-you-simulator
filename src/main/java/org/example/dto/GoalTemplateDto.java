package org.example.dto;

import java.util.List;

/**
 * DTO for goal template data.
 */
public class GoalTemplateDto {
    private String id;
    private String title;
    private String description;
    private String category;
    private int suggestedDurationDays;
    private int suggestedImportance;
    private List<String> suggestedSteps;

    public GoalTemplateDto() {
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getSuggestedDurationDays() { return suggestedDurationDays; }
    public void setSuggestedDurationDays(int suggestedDurationDays) { this.suggestedDurationDays = suggestedDurationDays; }
    public int getSuggestedImportance() { return suggestedImportance; }
    public void setSuggestedImportance(int suggestedImportance) { this.suggestedImportance = suggestedImportance; }
    public List<String> getSuggestedSteps() { return suggestedSteps; }
    public void setSuggestedSteps(List<String> suggestedSteps) { this.suggestedSteps = suggestedSteps; }
}

