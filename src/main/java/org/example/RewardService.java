package org.example;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing rewards.
 */
@Service
public class RewardService {
    private final List<Reward> availableRewards = new ArrayList<>();

    public RewardService() {
        initializeRewards();
    }

    private void initializeRewards() {
        availableRewards.add(new Reward(
            "reward-1", "Starter Badge", "Your first reward!", RewardType.BADGE, 0, true, null, null
        ));
        availableRewards.add(new Reward(
            "reward-2", "XP Boost", "Get 100 bonus XP", RewardType.BONUS_XP, 500, false, null, null
        ));
        availableRewards.add(new Reward(
            "reward-3", "Dark Theme", "Unlock dark theme", RewardType.THEME, 1000, false, null, null
        ));
        availableRewards.add(new Reward(
            "reward-4", "Champion Title", "Display 'Champion' title", RewardType.TITLE, 2000, false, null, null
        ));
        availableRewards.add(new Reward(
            "reward-5", "Custom Avatar", "Unlock custom avatar", RewardType.AVATAR, 3000, false, null, null
        ));
    }

    /**
     * Gets all available rewards.
     */
    public List<Reward> getAllRewards() {
        return new ArrayList<>(availableRewards);
    }

    /**
     * Gets rewards that can be purchased with current XP.
     */
    public List<Reward> getAffordableRewards(int currentXp) {
        return availableRewards.stream()
            .filter(r -> !r.unlocked() && r.xpCost() <= currentXp)
            .toList();
    }

    /**
     * Purchases a reward with XP.
     */
    public Reward purchaseReward(String rewardId, UserStats userStats) {
        Reward reward = availableRewards.stream()
            .filter(r -> r.id().equals(rewardId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Reward not found: " + rewardId));

        if (reward.unlocked()) {
            throw new IllegalArgumentException("Reward already unlocked");
        }

        if (userStats.getTotalXp() < reward.xpCost()) {
            throw new IllegalArgumentException("Not enough XP to purchase this reward");
        }

        // In production, this would update the database
        // For now, we'll create a new unlocked reward
        return new Reward(
            reward.id(), reward.name(), reward.description(), reward.type(),
            reward.xpCost(), true, LocalDate.now(), reward.imageUrl()
        );
    }

    /**
     * Gets unlocked rewards for a user.
     */
    public List<Reward> getUnlockedRewards(UserStats userStats) {
        // In production, this would query the database
        // For now, we'll return rewards based on XP milestones
        List<Reward> unlocked = new ArrayList<>();
        
        for (Reward reward : availableRewards) {
            if (reward.unlocked() || userStats.getTotalXp() >= reward.xpCost()) {
                unlocked.add(reward);
            }
        }
        
        return unlocked;
    }
}

