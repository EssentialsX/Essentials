package net.ess3.provider;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AbstractAsyncChatEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();

    private final String legacyMessage;
    private final Set<Player> playerRecipients;

    public AbstractAsyncChatEvent(boolean async, Player sender, String message, Set<Player> recipients) {
        super(sender, async);
        this.legacyMessage = message;
        this.playerRecipients = recipients;
    }

    public String getMessage() {
        return legacyMessage;
    }

    public Set<Player> getRecipients() {
        return playerRecipients;
    };

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
