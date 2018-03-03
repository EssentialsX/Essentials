package com.neximation.essentials.commands;

import static com.neximation.essentials.I18n.tl;

public class WarpNotFoundException extends Exception {
    public WarpNotFoundException() {
        super(tl("warpNotExist"));
    }

    public WarpNotFoundException(String message) {
        super(message);
    }
}
