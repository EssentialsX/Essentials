package net.essentialsx.api.v2.events.discord;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Fired before a chat message is about to be sent to a Discord channel.
 * Should be used to block chat messages (such as staff channels) from appearing in Discord.
 */
public class DiscordChatMessageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private String message;
    private boolean cancelled = false;

    /**
     * @param player  The player which caused this event.
     * @param message The message of this event.
     */
    public DiscordChatMessageEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    /**
     * The player which which caused this chat message.
     * @return the player who caused the event.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * The message being sent in this chat event.
     * @return the message of this event.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message of this event, and thus the chat message relayed to Discord.
     * @param message the new message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
