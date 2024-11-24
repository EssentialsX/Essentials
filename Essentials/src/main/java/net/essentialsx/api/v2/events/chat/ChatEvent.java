package net.essentialsx.api.v2.events.chat;

import net.essentialsx.api.v2.ChatType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.util.IllegalFormatException;
import java.util.Set;

/**
 * This handles common boilerplate for other ChatEvents
 */
public abstract class ChatEvent extends Event implements Cancellable {
    private final ChatType chatType;
    private final Player player;
    private final Set<Player> recipients;
    private String message;
    private String format;

    private boolean cancelled = false;

    public ChatEvent(final boolean async, final ChatType chatType, final Player player,
        final String format, final String message, final Set<Player> recipients) {
        super(async);

        this.chatType = chatType;
        this.player = player;
        this.format = format;
        this.message = message;
        this.recipients = recipients;
    }

    /**
     * Gets the message that the player is attempting to send. This message will be used with
     * {@link #getFormat()}.
     *
     * @return Message the player is attempting to send
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message that the player will send. This message will be used with
     * {@link #getFormat()}.
     *
     * @param message New message that the player will send
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Gets the format to use to display this chat message. When this event finishes execution, the
     * first format parameter is the {@link Player#getDisplayName()} and the second parameter is
     * {@link #getMessage()}
     *
     * @return {@link String#format(String, Object...)} compatible format string
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the format to use to display this chat message. When this event finishes execution, the
     * first format parameter is the {@link Player#getDisplayName()} and the second parameter is
     * {@link #getMessage()}
     *
     * @param format {@link String#format(String, Object...)} compatible format string
     * @throws IllegalFormatException if the underlying API throws the exception
     * @throws NullPointerException   if format is null
     * @see String#format(String, Object...)
     */
    public void setFormat(final String format) throws IllegalFormatException, NullPointerException {
        // Oh for a better way to do this!
        try {
            String.format(format, player, message);
        } catch (final RuntimeException ex) {
            ex.fillInStackTrace();
            throw ex;
        }

        this.format = format;
    }

    /**
     * Gets a set of recipients that this chat message will be displayed to.
     *
     * @return All Players who will see this chat message
     */
    public Set<Player> getRecipients() {
        return recipients;
    }

    /**
     * Returns the player involved in this event
     *
     * @return Player who is involved in this event
     */
    public final Player getPlayer() {
        return player;
    }

    /**
     * Returns the type of chat this event is fired for
     *
     * @return Type of chat this event is fired for
     */
    public ChatType getChatType() {
        return chatType;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(final boolean cancel) {
        this.cancelled = cancel;
    }
}
