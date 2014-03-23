package net.ess3.api.events;

import static com.earth2me.essentials.I18n.tl;
import java.util.IllegalFormatException;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class LocalChatSpyEvent extends Event implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	private String message;
	private String format;
	private Player player;
	private final Set<Player> recipients;

	public LocalChatSpyEvent(final boolean async, final Player who, final String format, final String message, final Set<Player> players)
	{
		super(async);
		this.format = tl("chatTypeLocal").concat(tl("chatTypeSpy")).concat(format);
		this.message = message;
		recipients = players;
		player = who;
	}

	/**
	 * Gets the message that the player is attempting to send. This message will be used with {@link #getFormat()}.
	 *
	 * @return Message the player is attempting to send
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Sets the message that the player will send. This message will be used with {@link #getFormat()}.
	 *
	 * @param message New message that the player will send
	 */
	public void setMessage(String message)
	{
		this.message = message;
	}

	/**
	 * Gets the format to use to display this chat message. When this event finishes execution, the first format
	 * parameter is the {@link Player#getDisplayName()} and the second parameter is {@link #getMessage()}
	 *
	 * @return {@link String#format(String, Object...)} compatible format string
	 */
	public String getFormat()
	{
		return format;
	}

	/**
	 * Sets the format to use to display this chat message. When this event finishes execution, the first format
	 * parameter is the {@link Player#getDisplayName()} and the second parameter is {@link #getMessage()}
	 *
	 * @param format {@link String#format(String, Object...)} compatible format string
	 * @throws IllegalFormatException if the underlying API throws the exception
	 * @throws NullPointerException if format is null
	 * @see String#format(String, Object...)
	 */
	public void setFormat(final String format) throws IllegalFormatException, NullPointerException
	{
		// Oh for a better way to do this!
		try
		{
			String.format(format, player, message);
		}
		catch (RuntimeException ex)
		{
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
	public Set<Player> getRecipients()
	{
		return recipients;
	}

	/**
	 * Returns the player involved in this event
	 *
	 * @return Player who is involved in this event
	 */
	public final Player getPlayer()
	{
		return player;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel)
	{
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
