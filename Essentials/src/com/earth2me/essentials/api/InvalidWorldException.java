package com.earth2me.essentials.api;

import static com.earth2me.essentials.I18n.tlp;

import net.ess3.api.IUser;


public class InvalidWorldException extends Exception {
    private final String world;

    public InvalidWorldException(final String world) {
        this(null, world);
    }

    public InvalidWorldException(final IUser target, final String world) {
        super(tlp(target, "invalidWorld"));
        this.world = world;
    }

    public String getWorld() {
        return this.world;
    }
}
