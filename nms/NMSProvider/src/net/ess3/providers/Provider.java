package net.ess3.providers;

/**
 * <p>Provider interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface Provider {
    /**
     * <p>tryProvider.</p>
     *
     * @return a boolean.
     */
    boolean tryProvider();

    /**
     * <p>getHumanName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getHumanName();
}
