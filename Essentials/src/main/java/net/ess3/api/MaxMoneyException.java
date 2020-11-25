package net.ess3.api;

import static com.earth2me.essentials.I18n.tl;

/**
 * Thrown when a transaction would put the player's balance above the maximum balance allowed.
 */
public class MaxMoneyException extends Exception {
    public MaxMoneyException() {
        super(tl("maxMoney"));
    }
}
