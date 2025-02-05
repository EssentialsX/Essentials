package net.ess3.provider;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface AbstractChatEvent {
    Pattern URL_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_\\.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");

    boolean isAsynchronous();

    boolean isCancelled();

    void setCancelled(boolean toCancel);

    String getFormat();

    void setFormat(String format);

    String getMessage();

    void setMessage(String message);

    Player getPlayer();

    Set<Player> recipients();

    void removeRecipients(Predicate<Player> predicate);

    void addRecipient(Player player);
}
