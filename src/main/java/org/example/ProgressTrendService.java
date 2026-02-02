package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for analyzing progress trends.
 */
@Service
public class ProgressTrendService {

    /**
     * Generates XP trend over a period.
     */
    public ProgressTrend generateXpTrend(HabitService habitService, GoalService goalService,
                                        AnalyticsService analyticsService, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<XpHistoryEntry> history = analyticsService.buildXpHistory(habitService, goalService);
        List<XpHistoryEntry> periodHistory = history.stream()
            .filter(entry -> !entry.date().isBefore(startDate) && !entry.date().isAfter(endDate))
            .sorted(Comparator.comparing(XpHistoryEntry::date))
            .collect(Collectors.toList());

        // Group by date and sum XP
        Map<LocalDate, Integer> xpByDate = periodHistory.stream()
            .collect(Collectors.groupingBy(
                XpHistoryEntry::date,
                Collectors.summingInt(XpHistoryEntry::xpChange)
            ));

        // Create data points
        List<ProgressTrend.DataPoint> dataPoints = new ArrayList<>();
        int cumulativeXp = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            cumulativeXp += xpByDate.getOrDefault(date, 0);
            dataPoints.add(new ProgressTrend.DataPoint(date, cumulativeXp));
        }

        // Calculate average and growth rate
        double averageValue = dataPoints.stream()
            .mapToDouble(ProgressTrend.DataPoint::value)
            .average()
            .orElse(0.0);

        double growthRate = dataPoints.size() > 1
            ? ((dataPoints.get(dataPoints.size() - 1).value() - dataPoints.get(0).value()) / dataPoints.get(0).value()) * 100
            : 0.0;

        // Determine direction
        TrendDirection direction = determineDirection(dataPoints);

        return new ProgressTrend(
            startDate, endDate, "XP", dataPoints, averageValue, growthRate, direction
        );
    }

    /**
     * Generates consistency trend.
     */
    public ProgressTrend generateConsistencyTrend(List<Goal> goals, GoalService goalService,
                                                 AnalyticsService analyticsService, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<ProgressTrend.DataPoint> dataPoints = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(7)) {
            Map<Goal, GoalConsistency> consistency = analyticsService.calculateAllGoalConsistency(
                goals, goalService, date);
            double avgConsistency = consistency.values().stream()
                .mapToDouble(GoalConsistency::consistencyScore)
                .average()
                .orElse(0.0);
            dataPoints.add(new ProgressTrend.DataPoint(date, avgConsistency * 100));
        }

        double averageValue = dataPoints.stream()
            .mapToDouble(ProgressTrend.DataPoint::value)
            .average()
            .orElse(0.0);

        double growthRate = dataPoints.size() > 1
            ? ((dataPoints.get(dataPoints.size() - 1).value() - dataPoints.get(0).value()) / Math.max(dataPoints.get(0).value(), 1)) * 100
            : 0.0;

        TrendDirection direction = determineDirection(dataPoints);

        return new ProgressTrend(
            startDate, endDate, "Consistency", dataPoints, averageValue, growthRate, direction
        );
    }

    private TrendDirection determineDirection(List<ProgressTrend.DataPoint> dataPoints) {
        if (dataPoints.size() < 2) {
            return TrendDirection.STABLE;
        }

        double firstValue = dataPoints.get(0).value();
        double lastValue = dataPoints.get(dataPoints.size() - 1).value();
        double change = lastValue - firstValue;
        double percentChange = Math.abs(change / Math.max(firstValue, 1)) * 100;

        if (percentChange < 5) {
            return TrendDirection.STABLE;
        } else if (change > 0) {
            return TrendDirection.INCREASING;
        } else {
            return TrendDirection.DECREASING;
        }
    }
}

