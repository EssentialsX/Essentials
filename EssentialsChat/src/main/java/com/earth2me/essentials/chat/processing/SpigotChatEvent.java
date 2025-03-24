package com.earth2me.essentials.chat.processing;

import net.ess3.provider.AbstractChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

public class SpigotChatEvent implements AbstractChatEvent {
    private final AsyncPlayerChatEvent event;

    public SpigotChatEvent(AsyncPlayerChatEvent event) {
        this.event = event;
    }

    @Override
    public boolean isAsynchronous() {
        return event.isAsynchronous();
    }

    @Override
    public boolean isCancelled() {
        return event.isCancelled();
    }

    @Override
    public void setCancelled(boolean toCancel) {
        event.setCancelled(toCancel);
    }

    @Override
    public String getFormat() {
        return event.getFormat();
    }

    @Override
    public void setFormat(String format) {
        event.setFormat(format);
    }

    @Override
    public String getMessage() {
        return event.getMessage();
    }

    @Override
    public void setMessage(String message) {
        event.setMessage(message);
    }

    @Override
    public Player getPlayer() {
        return event.getPlayer();
    }

    @Override
    public Set<Player> recipients() {
        return Collections.unmodifiableSet(event.getRecipients());
    }

    @Override
    public void removeRecipients(Predicate<Player> predicate) {
        event.getRecipients().removeIf(predicate);
    }

    @Override
    public void addRecipient(Player player) {
        event.getRecipients().add(player);
    }
}
