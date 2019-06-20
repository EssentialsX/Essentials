package com.earth2me.essentials.commands;


/**
 * <p>NotEnoughArgumentsException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class NotEnoughArgumentsException extends Exception {
    /**
     * <p>Constructor for NotEnoughArgumentsException.</p>
     */
    public NotEnoughArgumentsException() {
        super("");
    }

    /**
     * <p>Constructor for NotEnoughArgumentsException.</p>
     *
     * @param string a {@link java.lang.String} object.
     */
    public NotEnoughArgumentsException(final String string) {
        super(string);
    }

    /**
     * <p>Constructor for NotEnoughArgumentsException.</p>
     *
     * @param ex a {@link java.lang.Throwable} object.
     */
    public NotEnoughArgumentsException(final Throwable ex) {
        super("", ex);
    }
}
