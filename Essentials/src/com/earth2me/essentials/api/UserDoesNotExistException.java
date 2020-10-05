package com.earth2me.essentials.api;

import java.util.UUID;

import static com.earth2me.essentials.I18n.tl;

/**
 * Thrown when the requested user does not exist.
 */
public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(final String name) {
        super(tl("userDoesNotExist", name));
    }

    public UserDoesNotExistException(final UUID uuid) {
        super(tl("uuidDoesNotExist", uuid.toString()));
    }
}
