package com.earth2me.essentials.signs;


/**
 * <p>SignException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class SignException extends Exception {
    /**
     * <p>Constructor for SignException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public SignException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for SignException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public SignException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
