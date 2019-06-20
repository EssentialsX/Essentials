package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

/**
 * <p>PlayerNotFoundException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class PlayerNotFoundException extends NoSuchFieldException {
    /**
     * <p>Constructor for PlayerNotFoundException.</p>
     */
    public PlayerNotFoundException() {
        super(tl("playerNotFound"));
    }
}
