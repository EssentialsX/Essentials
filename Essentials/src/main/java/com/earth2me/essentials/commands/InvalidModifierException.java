package com.earth2me.essentials.commands;

import net.ess3.api.TranslatableException;

public class InvalidModifierException extends TranslatableException {
    public InvalidModifierException() {
        super("invalidModifier");
    }
}
