package com.earth2me.essentials.api;

import static com.earth2me.essentials.I18n.tl;

/**
 * @deprecated This exception is unused. Use {@link net.ess3.api.InvalidWorldException} instead.
 */
@Deprecated
public class InvalidWorldException extends Exception {
    private final String world;

    public InvalidWorldException(final String world) {
        super(tl("invalidWorld"));
        this.world = world;
    }

    public String getWorld() {
        return this.world;
    }
}
