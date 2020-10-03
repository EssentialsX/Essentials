package net.ess3.api;

import static com.earth2me.essentials.I18n.tl;

public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(final String name) {
        super(tl("userDoesNotExist", name));
    }
}
