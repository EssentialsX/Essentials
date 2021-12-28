package net.ess3.api;

/**
 * @deprecated You should use {@link com.earth2me.essentials.api.NoLoanPermittedException} instead of this class.
 */
@Deprecated
public class NoLoanPermittedException extends TranslatableException {
    public NoLoanPermittedException() {
        super("negativeBalanceError");
    }
}
