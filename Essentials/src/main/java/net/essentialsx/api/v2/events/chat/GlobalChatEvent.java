package net.essentialsx.api.v2.events.chat;

import net.essentialsx.api.v2.ChatType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.Set;

/**
 * Fired when a player uses global chat
 */
public class GlobalChatEvent extends ChatEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public GlobalChatEvent(final boolean async, final ChatType chatType, final Player player, final String format, final String message, final Set<Player> recipients) {
        super(async, chatType, player, format, message, recipients);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
