package net.ess3.api;

/**
 * Thrown when a transaction would put the player's balance above the maximum balance allowed.
 */
public class MaxMoneyException extends TranslatableException {
    public MaxMoneyException() {
        super("maxMoney");
    }
}
