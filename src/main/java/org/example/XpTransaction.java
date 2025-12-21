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
     * Note: Zero amounts are allowed for cases where transactions are capped or invalidated.
     *
     * @param amount the amount of XP (positive for gains, negative for losses, zero for no change)
     * @param reason a description of why this XP change occurred
     */
    public XpTransaction {
        // Zero amounts are now allowed for daily cap scenarios
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

    /**
     * Returns true if this transaction has no effect (zero amount).
     *
     * @return true if amount is zero
     */
    public boolean isNoOp() {
        return amount == 0;
    }
}

