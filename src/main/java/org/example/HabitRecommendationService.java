package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for recommending habits to users based on their activity.
 */
@Service
public class HabitRecommendationService {

    /**
     * Gets habit recommendations for a user.
     */
    public List<HabitRecommendation> getRecommendations(UserStats userStats,
                                                        List<Habit> currentHabits,
                                                        List<Goal> goals,
                                                        HabitService habitService,
                                                        GoalService goalService,
                                                        AnalyticsService analyticsService,
                                                        LocalDate currentDate) {
        List<HabitRecommendation> recommendations = new ArrayList<>();

        // Analyze current habits
        List<String> currentHabitNames = currentHabits.stream()
            .map(Habit::getName)
            .map(String::toLowerCase)
            .collect(Collectors.toList());

        // Analyze goals to suggest related habits
        for (Goal goal : goals) {
            String goalTitle = goal.getTitle().toLowerCase();
            
            if (goalTitle.contains("exercise") || goalTitle.contains("fitness") || goalTitle.contains("workout")) {
                if (!currentHabitNames.contains("exercise") && !currentHabitNames.contains("workout")) {
                    recommendations.add(new HabitRecommendation(
                        "Daily Exercise",
                        "Do at least 30 minutes of physical activity",
                        Difficulty.THREE,
                        "Health",
                        "Based on your fitness goals",
                        0.9
                    ));
                }
            }
            
            if (goalTitle.contains("read") || goalTitle.contains("book") || goalTitle.contains("learn")) {
                if (!currentHabitNames.contains("read") && !currentHabitNames.contains("reading")) {
                    recommendations.add(new HabitRecommendation(
                        "Daily Reading",
                        "Read for at least 20 minutes",
                        Difficulty.TWO,
                        "Learning",
                        "Based on your learning goals",
                        0.85
                    ));
                }
            }
            
            if (goalTitle.contains("meditate") || goalTitle.contains("mindfulness")) {
                if (!currentHabitNames.contains("meditate") && !currentHabitNames.contains("meditation")) {
                    recommendations.add(new HabitRecommendation(
                        "Daily Meditation",
                        "Meditate for at least 10 minutes",
                        Difficulty.TWO,
                        "Wellness",
                        "Based on your wellness goals",
                        0.8
                    ));
                }
            }
        }

        // Analyze consistency to suggest easier habits if struggling
        Map<Goal, GoalConsistency> consistency = analyticsService.calculateAllGoalConsistency(
            goals, goalService, currentDate);
        double avgConsistency = consistency.values().stream()
            .mapToDouble(GoalConsistency::consistencyScore)
            .average()
            .orElse(0.0);

        if (avgConsistency < 0.5 && currentHabits.size() > 0) {
            recommendations.add(new HabitRecommendation(
                "Start Small",
                "Focus on one easy habit to build consistency",
                Difficulty.ONE,
                "General",
                "Your consistency is low - start with easier habits",
                0.7
            ));
        }

        // Suggest popular habits if user has few habits
        if (currentHabits.size() < 3) {
            if (!currentHabitNames.contains("water") && !currentHabitNames.contains("drink")) {
                recommendations.add(new HabitRecommendation(
                    "Drink Water",
                    "Drink 8 glasses of water daily",
                    Difficulty.ONE,
                    "Health",
                    "Popular starter habit for building consistency",
                    0.75
                ));
            }
            
            if (!currentHabitNames.contains("journal") && !currentHabitNames.contains("journaling")) {
                recommendations.add(new HabitRecommendation(
                    "Daily Journaling",
                    "Write in your journal for 5 minutes",
                    Difficulty.ONE,
                    "Personal Development",
                    "Great for reflection and self-awareness",
                    0.7
                ));
            }
        }

        // Analyze streaks to suggest complementary habits
        Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
            currentHabits, habitService, currentDate);
        
        boolean hasExerciseHabit = currentHabits.stream()
            .anyMatch(h -> h.getName().toLowerCase().contains("exercise") || 
                         h.getName().toLowerCase().contains("workout"));
        
        if (hasExerciseHabit && !currentHabitNames.contains("stretch")) {
            recommendations.add(new HabitRecommendation(
                "Daily Stretching",
                "Stretch for 10 minutes",
                Difficulty.ONE,
                "Health",
                "Complements your exercise routine",
                0.8
            ));
        }

        // Remove duplicates and sort by match score
        return recommendations.stream()
            .distinct()
            .sorted((a, b) -> Double.compare(b.matchScore(), a.matchScore()))
            .limit(10)
            .collect(Collectors.toList());
    }
}

