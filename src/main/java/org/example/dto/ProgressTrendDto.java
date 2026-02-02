package org.example.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for progress trend data.
 */
public class ProgressTrendDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private String metric;
    private List<DataPointDto> dataPoints;
    private double averageValue;
    private double growthRate;
    private String direction;

    public ProgressTrendDto() {
    }

    public static class DataPointDto {
        private LocalDate date;
        private double value;

        public DataPointDto() {
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }

    // Getters and setters
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }
    public List<DataPointDto> getDataPoints() { return dataPoints; }
    public void setDataPoints(List<DataPointDto> dataPoints) { this.dataPoints = dataPoints; }
    public double getAverageValue() { return averageValue; }
    public void setAverageValue(double averageValue) { this.averageValue = averageValue; }
    public double getGrowthRate() { return growthRate; }
    public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}

