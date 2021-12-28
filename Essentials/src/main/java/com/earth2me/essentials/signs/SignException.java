package com.earth2me.essentials.signs;

import net.ess3.api.TranslatableException;

public class SignException extends TranslatableException {
    public SignException(final String message, final Object... args) {
        super(message, args);
    }

    public SignException(final Throwable cause, final String message, final Object... args) {
        super(message, args);
        setCause(cause);
    }
}
