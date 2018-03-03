package net.ess3.api;

import static com.neximation.essentials.I18n.tl;


public class NoLoanPermittedException extends Exception {
    public NoLoanPermittedException() {
        super(tl("negativeBalanceError"));
    }
}
