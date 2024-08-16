package net.ess3.provider;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class AbstractAsyncChatEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final String legacyMessage;
    private final Set<Player> playerRecipients;

    public AbstractAsyncChatEvent(Player player, String message, Set<Player> recipients) {
        super(true);
        this.player = player;
        this.legacyMessage = message;
        this.playerRecipients = recipients;
    }

    public Player getPlayer() {
        return player;
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
