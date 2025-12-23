package org.example;

import java.time.LocalDate;

/**
 * Represents a single entry in the XP history.
 * Tracks when XP changed, by how much, and from what source.
 *
 * @param date the date when the XP change occurred
 * @param xpChange the amount of XP changed (positive for gains, negative for losses)
 * @param source the source of the XP change
 */
public record XpHistoryEntry(LocalDate date, int xpChange, XpSource source) {
    /**
     * Creates a new XP history entry.
     *
     * @param date the date when the XP change occurred (must not be null)
     * @param xpChange the amount of XP changed (can be positive, negative, or zero)
     * @param source the source of the XP change (must not be null)
     * @throws IllegalArgumentException if date or source is null
     */
    public XpHistoryEntry {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
    }

    /**
     * Returns true if this entry represents an XP gain.
     *
     * @return true if xpChange is positive
     */
    public boolean isGain() {
        return xpChange > 0;
    }

    /**
     * Returns true if this entry represents an XP loss.
     *
     * @return true if xpChange is negative
     */
    public boolean isLoss() {
        return xpChange < 0;
    }
}

