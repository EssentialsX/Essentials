package net.ess3.api.events;

import net.essentialsx.api.v2.ChatType;
import net.essentialsx.api.v2.events.chat.ChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.Set;

import static com.earth2me.essentials.I18n.tl;

/**
 * Fired when a player uses local chat.
 */
public class LocalChatSpyEvent extends ChatEvent {
    private static final HandlerList handlers = new HandlerList();

    public LocalChatSpyEvent(final boolean async, final Player player, final String format, final String message, final Set<Player> recipients) {
        super(async, ChatType.SPY, player, tl("chatTypeLocal").concat(tl("chatTypeSpy")).concat(format), message, recipients);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
