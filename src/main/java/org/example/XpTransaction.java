package org.example;

/**
 * Represents a single XP transaction (gain or loss).
 * This class is immutable and serves as a record of XP changes.
 * XP should never be modified directly on UserStats; all changes should go through transactions.
 *
 * @param amount the amount of XP (positive for gains, negative for losses)
 * @param reason a description of why this XP change occurred
 */
public record XpTransaction(int amount, String reason) {
    /**
     * Creates a new XP transaction.
     *
     * @param amount the amount of XP (positive for gains, negative for losses)
     * @param reason a description of why this XP change occurred
     * @throws IllegalArgumentException if amount is zero
     */
    public XpTransaction {
        if (amount == 0) {
            throw new IllegalArgumentException("XP transaction amount cannot be zero");
        }
    }

    /**
     * Returns true if this transaction represents an XP gain.
     *
     * @return true if amount is positive
     */
    public boolean isGain() {
        return amount > 0;
    }

    /**
     * Returns true if this transaction represents an XP loss.
     *
     * @return true if amount is negative
     */
    public boolean isLoss() {
        return amount < 0;
    }
}

