package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for managing challenges.
 */
@Service
public class ChallengeService {
    private final List<Challenge> challenges = new ArrayList<>();

    /**
     * Creates a new challenge.
     */
    public Challenge createChallenge(String title, String description, ChallengeType type,
                                     int targetValue, LocalDate startDate, LocalDate endDate, String reward) {
        String id = "challenge-" + System.currentTimeMillis();
        Challenge challenge = new Challenge(
            id, title, description, type, targetValue, 0, startDate, endDate, true,
            new ArrayList<>(), reward
        );
        challenges.add(challenge);
        return challenge;
    }

    /**
     * Gets all active challenges.
     */
    public List<Challenge> getActiveChallenges(LocalDate currentDate) {
        return challenges.stream()
            .filter(c -> c.isCurrentlyActive(currentDate))
            .toList();
    }

    /**
     * Gets all challenges.
     */
    public List<Challenge> getAllChallenges() {
        return new ArrayList<>(challenges);
    }

    /**
     * Gets a challenge by ID.
     */
    public Challenge getChallengeById(String id) {
        return challenges.stream()
            .filter(c -> c.id().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Challenge not found: " + id));
    }

    /**
     * Joins a user to a challenge.
     */
    public Challenge joinChallenge(String challengeId, String username) {
        Challenge challenge = getChallengeById(challengeId);
        List<String> newParticipants = new ArrayList<>(challenge.participants());
        if (!newParticipants.contains(username)) {
            newParticipants.add(username);
        }
        return new Challenge(
            challenge.id(), challenge.title(), challenge.description(), challenge.type(),
            challenge.targetValue(), challenge.currentValue(), challenge.startDate(),
            challenge.endDate(), challenge.active(), newParticipants, challenge.reward()
        );
    }

    /**
     * Updates challenge progress for a user.
     */
    public Challenge updateChallengeProgress(String challengeId, UserStats userStats,
                                            List<Habit> habits, HabitService habitService,
                                            AnalyticsService analyticsService, LocalDate currentDate) {
        Challenge challenge = getChallengeById(challengeId);
        int currentValue = 0;

        switch (challenge.type()) {
            case XP_CHALLENGE:
                currentValue = userStats.getTotalXp();
                break;
            case LEVEL_CHALLENGE:
                currentValue = userStats.getLevel();
                break;
            case STREAK_CHALLENGE:
                Map<Habit, HabitStreak> streaks = analyticsService.calculateAllHabitStreaks(
                    habits, habitService, currentDate);
                currentValue = streaks.values().stream()
                    .mapToInt(s -> Math.max(s.currentStreak(), s.longestStreak()))
                    .max()
                    .orElse(0);
                break;
            case HABIT_CHALLENGE:
                currentValue = (int) habitService.getAllHabitChecks().stream()
                    .filter(check -> check.result() == HabitCheckResult.DONE)
                    .count();
                break;
            case CONSISTENCY_CHALLENGE:
                // This would need goal consistency calculation
                currentValue = (int) (userStats.getTotalXp() / 100); // Simplified
                break;
        }

        return new Challenge(
            challenge.id(), challenge.title(), challenge.description(), challenge.type(),
            challenge.targetValue(), currentValue, challenge.startDate(),
            challenge.endDate(), challenge.active(), challenge.participants(), challenge.reward()
        );
    }

    /**
     * Gets default challenges.
     */
    public List<Challenge> getDefaultChallenges() {
        List<Challenge> defaults = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        defaults.add(createChallenge(
            "Weekly XP Challenge",
            "Earn 500 XP this week",
            ChallengeType.XP_CHALLENGE,
            500,
            today,
            today.plusDays(7),
            "500 bonus XP"
        ));

        defaults.add(createChallenge(
            "30-Day Streak",
            "Maintain a 30-day streak",
            ChallengeType.STREAK_CHALLENGE,
            30,
            today,
            today.plusDays(30),
            "Achievement Badge"
        ));

        return defaults;
    }
}

