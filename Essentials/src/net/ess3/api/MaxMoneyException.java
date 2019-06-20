package net.ess3.api;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>MaxMoneyException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class MaxMoneyException extends Exception {
    /**
     * <p>Constructor for MaxMoneyException.</p>
     */
    public MaxMoneyException() {
        super(tl("maxMoney"));
    }
}
