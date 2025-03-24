package com.earth2me.essentials.commands;

import net.ess3.api.TranslatableException;

public class WarpNotFoundException extends TranslatableException {
    public WarpNotFoundException() {
        super("warpNotExist");
    }
}
