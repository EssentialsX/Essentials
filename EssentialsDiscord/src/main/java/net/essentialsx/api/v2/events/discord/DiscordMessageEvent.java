package net.essentialsx.api.v2.events.discord;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Fired before a message is about to be sent to a discord channel.
 */
public class DiscordMessageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private MessageType type;
    private String message;
    private boolean allowGroupMentions;
    private String avatarUrl;
    private String name;
    private final UUID uuid;

    /**
     * @param type               The message type/destination of this event.
     * @param message            The raw message content of this event.
     * @param allowGroupMentions If the message should allow the pinging of channels, users, or emotes.
     */
    public DiscordMessageEvent(final MessageType type, final String message, final boolean allowGroupMentions) {
        this(type, message, allowGroupMentions, null, null, null);
    }

    /**
     * @param type               The message type/destination of this event.
     * @param message            The raw message content of this event.
     * @param allowGroupMentions If the message should allow the pinging of channels, users, or emotes.
     * @param avatarUrl          The avatar url to use for this message (if supported) or null to use the default bot avatar.
     * @param name               The name to use for this message (if supported) or null to use the default bot name.
     * @param uuid               The UUID of the player which caused this event or null if this wasn't a player triggered event.
     */
    public DiscordMessageEvent(final MessageType type, final String message, final boolean allowGroupMentions, final String avatarUrl, final String name, final UUID uuid) {
        this.type = type;
        this.message = message;
        this.allowGroupMentions = allowGroupMentions;
        this.avatarUrl = avatarUrl;
        this.name = name;
        this.uuid = uuid;
    }

    /**
     * Gets the type of this message. This also defines its destination.
     * @return The message type.
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Sets the message type and therefore its destination.
     * @param type The new message type.
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * Gets the raw message content that is about to be sent to discord.
     * @return The raw message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the raw message content to be sent to discord
     * @param message The new message content.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Checks if this message allows pinging of roles/@here/@everyone.
     * @return true if this message is allowed to ping of roles/@here/@everyone.
     */
    public boolean isAllowGroupMentions() {
        return allowGroupMentions;
    }

    /**
     * Sets if this message is allowed to ping roles/@here/@everyone.
     * @param allowGroupMentions If pinging of roles/@here/@everyone should be allowed.
     */
    public void setAllowGroupMentions(boolean allowGroupMentions) {
        this.allowGroupMentions = allowGroupMentions;
    }

    /**
     * Gets the avatar url to use for this message, or null if none is specified.
     * @return The avatar url or null.
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Sets the avatar url for this message, or null to use the bot's avatar.
     * @param avatarUrl The avatar url or null.
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * Gets the name to use for this message, or null if none is specified.
     * @return The name or null.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this message, or null to use the bot's name.
     * @param name The name or null.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the UUID of the player which caused this event, or null if it wasn't a player triggered event.
     * @return The UUID or null.
     */
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Indicates the type of message being sent and its literal channel name used in the config.
     */
    public final static class MessageType {
        private final String key;
        private final boolean player;

        /**
         * Creates a {@link MessageType} which will send channels to the specified channel key.
         * @param key The channel key defined in the {@code message-types} section of the config.
         */
        public MessageType(final String key) {
            this(key, false);
        }

        /**
         * Internal constructor used by EssentialsX Discord
         */
        private MessageType(String key, boolean player) {
            this.key = key;
            this.player = player;
        }

        /**
         * Gets the key used in {@code message-types} section of the config.
         * @return The config key.
         */
        public String getKey() {
            return key;
        }

        /**
         * Checks if this message type should be beholden to player-specific config settings.
         * @return true if message type should be beholden to player-specific config settings.
         */
        public boolean isPlayer() {
            return player;
        }

        @Override
        public String toString() {
            return key;
        }

        /**
         * Default {@link MessageType MessageTypes} provided and documented by EssentialsX Discord.
         */
        public static final class DefaultTypes {
            public final static MessageType JOIN = new MessageType("join", true);
            public final static MessageType LEAVE = new MessageType("leave", true);
            public final static MessageType CHAT = new MessageType("chat", true);
            public final static MessageType DEATH = new MessageType("death", true);
            public final static MessageType AFK = new MessageType("afk", true);
            public final static MessageType KICK = new MessageType("kick", false);
            public final static MessageType MUTE = new MessageType("mute", false);
            private final static MessageType[] VALUES = new MessageType[]{JOIN, LEAVE, CHAT, DEATH, AFK, KICK, MUTE};

            /**
             * Gets an array of all the default {@link MessageType MessageTypes}.
             * @return An array of all the default {@link MessageType MessageTypes}.
             */
            public static MessageType[] values() {
                return VALUES;
            }
        }
    }
}
