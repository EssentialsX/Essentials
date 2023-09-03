package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

/**
 * Thrown when a player doesn't specify a currency modifier correctly.
 */
public class InvalidModifierException extends Exception {
    public InvalidModifierException() {
        super(tl("invalidModifier"));
    }
}
