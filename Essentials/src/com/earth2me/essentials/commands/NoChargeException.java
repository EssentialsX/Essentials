package com.earth2me.essentials.commands;


/**
 * <p>NoChargeException class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class NoChargeException extends Exception {
    /**
     * <p>Constructor for NoChargeException.</p>
     */
    public NoChargeException() {
        super("Will charge later");
    }
}
