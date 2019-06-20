package com.earth2me.essentials;


/**
 * <p>ChargeException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class ChargeException extends Exception {
    /**
     * <p>Constructor for ChargeException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public ChargeException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for ChargeException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public ChargeException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
