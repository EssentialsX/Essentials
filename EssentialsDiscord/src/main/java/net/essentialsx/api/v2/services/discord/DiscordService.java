package net.essentialsx.api.v2.services.discord;

/**
 * A class which provides numerous methods to interact with EssentialsX Discord.
 */
public interface DiscordService {
    /**
     * Sends a message to a message type channel.
     * @param type               The message type/destination of this message.
     * @param message            The exact message to be sent.
     * @param allowGroupMentions Whether or not the message should allow the pinging of roles, users, or emotes.
     */
    void sendMessage(final MessageType type, final String message, final boolean allowGroupMentions);

    /**
     * Gets the {@link InteractionController} instance.
     * @return the {@link InteractionController} instance.
     */
    InteractionController getInteractionController();

    /**
     * Gets unstable API that is subject to change at any time.
     * @return {@link Unsafe the unsafe} instance.
     * @see Unsafe
     */
    Unsafe getUnsafe();
}
