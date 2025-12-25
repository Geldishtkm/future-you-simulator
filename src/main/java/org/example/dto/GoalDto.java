package org.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO for goal representation.
 */
public class GoalDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate targetDate;
    private Integer importance; // 1-5
    private Integer totalProgressPoints;
    private Double progressPercentage;

    public GoalDto() {
    }

    public GoalDto(Long id, String title, String description, LocalDate startDate,
                   LocalDate targetDate, Integer importance, Integer totalProgressPoints,
                   Double progressPercentage) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.targetDate = targetDate;
        this.importance = importance;
        this.totalProgressPoints = totalProgressPoints;
        this.progressPercentage = progressPercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }
}

