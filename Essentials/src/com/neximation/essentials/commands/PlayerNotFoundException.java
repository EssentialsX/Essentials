package com.neximation.essentials.commands;

import static com.neximation.essentials.I18n.tl;

public class PlayerNotFoundException extends NoSuchFieldException {
    public PlayerNotFoundException() {
        super(tl("playerNotFound"));
    }
}
