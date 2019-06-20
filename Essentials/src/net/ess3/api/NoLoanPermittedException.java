package net.ess3.api;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>NoLoanPermittedException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class NoLoanPermittedException extends Exception {
    /**
     * <p>Constructor for NoLoanPermittedException.</p>
     */
    public NoLoanPermittedException() {
        super(tl("negativeBalanceError"));
    }
}
