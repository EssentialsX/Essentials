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

    /**
     * @param type
     * @param message
     */
    public DiscordMessageEvent(MessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

        public String getKey() {
            return key;
        }
    }
}
