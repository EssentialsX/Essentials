package com.earth2me.essentials.api;

import static com.earth2me.essentials.I18n.tl;


/**
 * <p>InvalidWorldException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class InvalidWorldException extends Exception {
    private final String world;

    /**
     * <p>Constructor for InvalidWorldException.</p>
     *
     * @param world a {@link java.lang.String} object.
     */
    public InvalidWorldException(final String world) {
        super(tl("invalidWorld"));
        this.world = world;
    }

    /**
     * <p>Getter for the field <code>world</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getWorld() {
        return this.world;
    }
}
