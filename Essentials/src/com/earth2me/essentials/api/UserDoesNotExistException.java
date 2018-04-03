package com.earth2me.essentials.api;

import static com.earth2me.essentials.I18n.tlp;

import net.ess3.api.IUser;


public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(final String name) {
        this(null, name);
    }

    public UserDoesNotExistException(final IUser target, final String name) {
        super(tlp(target, "userDoesNotExist", name));
    }
}
