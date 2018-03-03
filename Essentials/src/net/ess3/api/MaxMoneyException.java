package net.ess3.api;

import static com.neximation.essentials.I18n.tl;


public class MaxMoneyException extends Exception {
    public MaxMoneyException() {
        super(tl("maxMoney"));
    }
}
