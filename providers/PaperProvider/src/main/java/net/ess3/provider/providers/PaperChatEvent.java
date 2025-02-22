package net.ess3.provider.providers;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.ess3.provider.AbstractChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PaperChatEvent implements AbstractChatEvent {
    private final AsyncChatEvent event;
    private final LegacyComponentSerializer serializer;
    private String fakeFormat;

    public PaperChatEvent(final AsyncChatEvent event, final LegacyComponentSerializer serializer) {
        this.event = event;
        this.serializer = serializer;
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
        return fakeFormat;
    }

    @Override
    public void setFormat(String format) {
        this.fakeFormat = format;
    }

    @Override
    public String getMessage() {
        return serializer.serialize(event.message());
    }

    @Override
    public void setMessage(String message) {
        event.message(serializer.deserialize(message));
    }

    @Override
    public Player getPlayer() {
        return event.getPlayer();
    }

    @Override
    public Set<Player> recipients() {
        final Set<Player> recipients = new HashSet<>();
        for (final Audience recipient : event.viewers()) {
            if (recipient instanceof Player) {
                recipients.add((Player) recipient);
            }
        }
        return Collections.unmodifiableSet(recipients);
    }

    @Override
    public void removeRecipients(Predicate<Player> predicate) {
        event.viewers().removeIf(recipient -> recipient instanceof Player && predicate.test((Player) recipient));
    }

    @Override
    public void addRecipient(Player player) {
        event.viewers().add(player);
    }
}
