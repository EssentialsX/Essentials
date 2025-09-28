package com.earth2me.essentials.commands;

import net.ess3.api.TranslatableException;

public class PlayerExemptException extends TranslatableException {
    public PlayerExemptException(String tlKey, Object... args) {
        super(tlKey, args);
    }
}
