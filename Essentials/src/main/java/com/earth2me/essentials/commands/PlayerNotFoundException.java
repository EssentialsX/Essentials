package com.earth2me.essentials.commands;

import net.ess3.api.TranslatableException;

public class PlayerNotFoundException extends TranslatableException {
    public PlayerNotFoundException() {
        super("playerNotFound");
    }
}
