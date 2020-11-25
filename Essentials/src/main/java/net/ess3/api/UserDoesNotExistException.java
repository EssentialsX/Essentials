package net.ess3.api;

import static com.earth2me.essentials.I18n.tl;

/**
 * @deprecated This is unused - see {@link com.earth2me.essentials.api.UserDoesNotExistException}.
 */
@Deprecated
public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(final String name) {
        super(tl("userDoesNotExist", name));
    }
}
