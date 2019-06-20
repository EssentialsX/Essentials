package net.ess3.api;


/**
 * <p>InvalidNameException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class InvalidNameException extends Exception {
    /**
     * NOTE: This is not implemented yet, just here for future 3.x api support Allow serialization of the
     * InvalidNameException exception
     */
    private static final long serialVersionUID = 1485321420293663139L;

    /**
     * <p>Constructor for InvalidNameException.</p>
     *
     * @param thrwbl a {@link java.lang.Throwable} object.
     */
    public InvalidNameException(Throwable thrwbl) {
        super(thrwbl);
    }
}
