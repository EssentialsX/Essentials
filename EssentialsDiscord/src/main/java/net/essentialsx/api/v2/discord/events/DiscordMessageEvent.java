package net.essentialsx.api.v2.discord.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired before a message is about to be sent to a discord channel.
 */
public class DiscordMessageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private MessageType type;
    private String message;
    private boolean allowGroupMentions;

    /**
     * @param type    The message type/destination of this event.
     * @param message The raw message content of this event.
     */
    public DiscordMessageEvent(MessageType type, String message, boolean allowGroupMentions) {
        this.type = type;
        this.message = message;
        this.allowGroupMentions = allowGroupMentions;
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
    public enum MessageType {
        JOIN("join"),
        LEAVE("leave"),
        CHAT("chat"),
        DEATH("death"),
        KICK("kick"),
        MUTE("mute");

        private final String key;

        MessageType(String key) {
            this.key = key;
        }

        /**
         * Gets the key used in {@code message-types} section of the config.
         * @return The config key.
         */
        public String getKey() {
            return key;
        }
    }
}
