package com.earth2me.essentials.api;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>UserDoesNotExistException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class UserDoesNotExistException extends Exception {
    /**
     * <p>Constructor for UserDoesNotExistException.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public UserDoesNotExistException(String name) {
        super(tl("userDoesNotExist", name));
    }
}
