package com.earth2me.essentials.api;

import java.util.UUID;

import static com.earth2me.essentials.I18n.tl;


public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(String name) {
        super(tl("userDoesNotExist", name));
    }

    public UserDoesNotExistException(UUID uuid) {
        super(tl("uuidDoesNotExist", uuid.toString()));
    }
}
