package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

/**
 * <p>WarpNotFoundException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class WarpNotFoundException extends Exception {
    /**
     * <p>Constructor for WarpNotFoundException.</p>
     */
    public WarpNotFoundException() {
        super(tl("warpNotExist"));
    }

    /**
     * <p>Constructor for WarpNotFoundException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public WarpNotFoundException(String message) {
        super(message);
    }
}
