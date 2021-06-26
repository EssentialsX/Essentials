package net.essentialsx.api.v2.services.discord;

/**
 * Represents a interaction channel argument as a guild channel.
 */
public interface InteractionChannel {
    /**
     * Gets the name of this channel.
     * @return this channel's name.
     */
    String getName();

    /**
     * Gets the ID of this channel.
     * @return this channel's ID.
     */
    String getId();
}
