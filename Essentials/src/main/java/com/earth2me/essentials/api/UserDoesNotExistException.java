package com.earth2me.essentials.api;

import net.ess3.api.TranslatableException;

import java.util.UUID;

/**
 * Thrown when the requested user does not exist.
 */
public class UserDoesNotExistException extends TranslatableException {
    public UserDoesNotExistException(final String name) {
        super("userDoesNotExist", name);
    }

    public UserDoesNotExistException(final UUID uuid) {
        super("uuidDoesNotExist", uuid.toString());
    }
}
