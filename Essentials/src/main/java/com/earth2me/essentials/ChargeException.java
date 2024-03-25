package com.earth2me.essentials;

import net.ess3.api.TranslatableException;

public class ChargeException extends TranslatableException {
    public ChargeException(String tlKey, Object... args) {
        super(tlKey, args);
    }
}
