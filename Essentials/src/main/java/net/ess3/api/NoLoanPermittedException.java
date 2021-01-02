package net.ess3.api;

import static com.earth2me.essentials.I18n.tl;

/**
 * @deprecated You should use {@link com.earth2me.essentials.api.NoLoanPermittedException} instead of this class.
 */
@Deprecated
public class NoLoanPermittedException extends Exception {
    public NoLoanPermittedException() {
        super(tl("negativeBalanceError"));
    }
}
