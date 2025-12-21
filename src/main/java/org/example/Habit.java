package org.example;

/**
 * Represents a habit that a user can track.
 * Each habit has a name and a difficulty level.
 */
public class Habit {
    private final String name;
    private final Difficulty difficulty;

    /**
     * Creates a new habit.
     *
     * @param name the name of the habit (must not be null or blank)
     * @param difficulty the difficulty level of the habit (must not be null)
     * @throws IllegalArgumentException if name is null or blank, or difficulty is null
     */
    public Habit(String name, Difficulty difficulty) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Habit name cannot be null or blank");
        }
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }
        this.name = name;
        this.difficulty = difficulty;
    }

    /**
     * Returns the name of this habit.
     *
     * @return the habit name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the difficulty level of this habit.
     *
     * @return the difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Habit habit = (Habit) o;
        return name.equals(habit.name) && difficulty == habit.difficulty;
    }

    @Override
    public int hashCode() {
        return name.hashCode() * 31 + difficulty.hashCode();
    }

    @Override
    public String toString() {
        return "Habit{name='" + name + "', difficulty=" + difficulty.getValue() + "}";
    }
}

