package com.earth2me.essentials.api;

/**
 * @deprecated This exception relates to the abandoned 3.x storage refactor and is not implemented.
 */
@Deprecated
public class InvalidNameException extends Exception {
    /**
     * NOTE: This is not implemented yet, just here for future 3.x api support Allow serialization of the
     * InvalidNameException exception
     */
    private static final long serialVersionUID = 1485321420293663139L;

    public InvalidNameException(final Throwable thrwbl) {
        super(thrwbl);
    }
}
