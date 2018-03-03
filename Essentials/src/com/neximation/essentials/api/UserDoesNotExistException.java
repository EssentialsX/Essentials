package com.neximation.essentials.api;

import static com.neximation.essentials.I18n.tl;


public class UserDoesNotExistException extends Exception {
    public UserDoesNotExistException(String name) {
        super(tl("userDoesNotExist", name));
    }
}
