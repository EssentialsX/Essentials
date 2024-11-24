package net.essentialsx.api.v2.services.discord;

import net.essentialsx.api.v2.ChatType;
import net.essentialsx.api.v2.events.discord.DiscordChatMessageEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

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
     * Sends a chat message to the {@link MessageType.DefaultTypes#CHAT default chat channel} with the same format
     * used for regular chat messages specified in the EssentialsX Discord configuration.
     * <p>
     * Note: Messages sent with this method will not fire a {@link DiscordChatMessageEvent}.
     * @param player      The player who send the message.
     * @param chatMessage The chat message the player has sent.
     */
    void sendChatMessage(final Player player, final String chatMessage);

    /**
     * Sends a chat message to the appropriate chat channel depending on the chat type with the format specified
     * for that type in the EssentialsX Discord configuration.
     * <p>
     * Note: Messages sent with this method will not fire a {@link DiscordChatMessageEvent}.
     * @param player      The player who send the message.
     * @param chatMessage The chat message the player has sent.
     */
    void sendChatMessage(final ChatType chatType, final Player player, final String chatMessage);

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
     * Gets an {@link InteractionMember} by their Discord ID.
     * @param id The ID of the member to look up.
     * @return A future which will complete with the member or null if none is reachable.
     */
    CompletableFuture<InteractionMember> getMemberById(final String id);

    /**
     * Gets an {@link InteractionRole} by its Discord ID.
     * @param id The ID of the role to look up.
     * @return the role or null if none by that ID exists.
     */
    InteractionRole getRole(final String id);

    /**
     * Adds or removes {@link InteractionRole roles} to the given {@link InteractionMember}.
     * @param member      The member to add/remove roles to/from.
     * @param addRoles    The roles to add to the {@link InteractionMember member}, or null to add none.
     * @param removeRoles The roles to remove from the {@link InteractionMember member}, or null to remove none.
     * @return A future which will complete when all requests operations have been completed.
     */
    CompletableFuture<Void> modifyMemberRoles(final InteractionMember member, final Collection<InteractionRole> addRoles, final Collection<InteractionRole> removeRoles);

    /**
     * Gets the Discord invite URL given in the EssentialsX Discord configuration.
     */
    String getInviteUrl();

    /**
     * Gets unstable API that is subject to change at any time.
     * @return {@link Unsafe the unsafe} instance.
     * @see Unsafe
     */
    Unsafe getUnsafe();
}
