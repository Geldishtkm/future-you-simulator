package org.example.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request DTO for creating a new goal.
 * 
 * Example request:
 * {
 *   "title": "Get a Backend Internship",
 *   "description": "Land an internship at a tech company",
 *   "startDate": "2025-01-01",
 *   "targetDate": "2025-06-30",
 *   "importance": 5,
 *   "totalProgressPoints": 100
 * }
 */
public class CreateGoalRequest {
    @NotBlank(message = "Goal title is required")
    private String title;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    @NotNull(message = "Importance is required")
    @Min(value = 1, message = "Importance must be between 1 and 5")
    @Max(value = 5, message = "Importance must be between 1 and 5")
    private Integer importance;

    @NotNull(message = "Total progress points is required")
    @Min(value = 1, message = "Total progress points must be positive")
    private Integer totalProgressPoints;

    public CreateGoalRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public Integer getTotalProgressPoints() {
        return totalProgressPoints;
    }

    public void setTotalProgressPoints(Integer totalProgressPoints) {
        this.totalProgressPoints = totalProgressPoints;
    }
}

