package com.earth2me.essentials.signs;

import net.ess3.api.TranslatableException;

public class SignException extends TranslatableException {
    public SignException(final String tlKey, final Object... args) {
        super(tlKey, args);
    }

    public SignException(final Throwable cause, final String tlKey, final Object... args) {
        super(tlKey, args);
        setCause(cause);
    }
}
