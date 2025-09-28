package net.ess3.api;

/**
 * @deprecated This is unused - see {@link com.earth2me.essentials.api.UserDoesNotExistException}.
 */
@Deprecated
public class UserDoesNotExistException extends TranslatableException {
    public UserDoesNotExistException(final String name) {
        super("userDoesNotExist", name);
    }
}
