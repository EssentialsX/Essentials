package net.essentialsx.api.v2.events.chat;

import net.essentialsx.api.v2.ChatType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Fired when a player uses local chat
 */
public class LocalChatEvent extends ChatEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final long radius;

    public LocalChatEvent(final boolean async, final Player player, final String format, final String message, final Set<Player> recipients, final long radius) {
        super(async, ChatType.LOCAL, player, format, message, recipients);
        this.radius = radius;
    }

    /**
     * Returns local chat radius used to calculate recipients of this message.
     * <p>
     * <p>This is not a radius between players: for that use {@link ChatEvent#getRecipients()} and calculate distance
     * to player who sent the message ({@link ChatEvent#getPlayer()}).
     * @return Non-squared local chat radius.
     */
    public long getRadius() {
        return radius;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
