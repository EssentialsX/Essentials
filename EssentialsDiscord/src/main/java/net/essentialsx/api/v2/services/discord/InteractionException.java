package net.essentialsx.api.v2.services.discord;

/**
 * Thrown when an error occurs during an operation dealing with Discord interactions.
 */
public class InteractionException extends Exception {
    public InteractionException(String message) {
        super(message);
    }
}
