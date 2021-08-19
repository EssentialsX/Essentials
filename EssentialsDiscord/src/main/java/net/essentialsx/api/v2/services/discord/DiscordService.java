package net.essentialsx.api.v2.services.discord;

import net.essentialsx.api.v2.events.discord.DiscordChatMessageEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * A class which provides numerous methods to interact with EssentialsX Discord.
 */
public interface DiscordService {
    /**
     * Sends a message to a message type channel.
     * @param type               The message type/destination of this message.
     * @param message            The exact message to be sent.
     * @param allowGroupMentions Whether the message should allow the pinging of roles, @here, or @everyone.
     */
    void sendMessage(final MessageType type, final String message, final boolean allowGroupMentions);

    /**
     * Sends a chat messages to the {@link MessageType.DefaultTypes#CHAT default chat channel} with the same format
     * used for regular chat messages specified in the EssentialsX Discord configuration.
     * <p>
     * Note: Messages sent with this method will not fire a {@link DiscordChatMessageEvent}.
     * @param player      The player who send the message.
     * @param chatMessage The chat message the player has sent.
     */
    void sendChatMessage(final Player player, final String chatMessage);

    /**
     * Checks if a {@link MessageType} by the given key is already registered.
     * @param key The {@link MessageType} key to check.
     * @return true if a {@link MessageType} with the provided key is registered, otherwise false.
     */
    boolean isRegistered(final String key);

    /**
     * Registers a message type to be used in the future.
     * <p>
     * In the future, this method will automatically populate the message type in the EssentialsX Discord config.
     * @param type The {@link MessageType} to be registered.
     */
    void registerMessageType(final Plugin plugin, final MessageType type);

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
