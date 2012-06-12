package com.earth2me.essentials.chat;

import com.earth2me.essentials.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

//TODO: Translate the local/spy tags
public abstract class EssentialsChatPlayer implements Listener
{
	protected transient IEssentials ess;
	protected final static Logger logger = Logger.getLogger("Minecraft");
	protected final transient Map<String, IEssentialsChatListener> listeners;
	protected final transient Server server;
	protected final transient Map<PlayerChatEvent, ChatStore> chatStorage;

	public EssentialsChatPlayer(final Server server,
								final IEssentials ess,
								final Map<String, IEssentialsChatListener> listeners,
								final Map<PlayerChatEvent, ChatStore> chatStorage)
	{
		this.ess = ess;
		this.listeners = listeners;
		this.server = server;
		this.chatStorage = chatStorage;
	}

	public void onPlayerChat(final PlayerChatEvent event)
	{
	}

	public boolean isAborted(final PlayerChatEvent event)
	{
		if (event.isCancelled())
		{
			return true;
		}
		for (IEssentialsChatListener listener : listeners.values())
		{
			if (listener.shouldHandleThisChat(event))
			{
				return true;
			}
		}
		return false;
	}

	public String getChatType(final String message)
	{
		switch (message.charAt(0))
		{
		case '!':
			return "shout";
		case '?':
			return "question";
		//case '@':
			//return "admin";
		default:
			return "";
		}
	}

	public ChatStore getChatStore(final PlayerChatEvent event)
	{
		return chatStorage.get(event);
	}

	public void setChatStore(final PlayerChatEvent event, final ChatStore chatStore)
	{
		chatStorage.put(event, chatStore);
	}

	public ChatStore delChatStore(final PlayerChatEvent event)
	{
		return chatStorage.remove(event);
	}

	protected void charge(final User user, final Trade charge) throws ChargeException
	{
		charge.charge(user);
	}

	protected boolean charge(final PlayerChatEvent event, final ChatStore chatStore)
	{
		try
		{
			charge(chatStore.getUser(), chatStore.getCharge());
		}
		catch (ChargeException e)
		{
			ess.showError(chatStore.getUser(), e, chatStore.getLongType());
			event.setCancelled(true);
			return false;
		}
		return true;
	}

	protected void sendLocalChat(final PlayerChatEvent event, final ChatStore chatStore)
	{
		event.setCancelled(true);
		final User sender = chatStore.getUser();
		logger.info(_("localFormat", sender.getName(), event.getMessage()));
		final Location loc = sender.getLocation();
		final World world = loc.getWorld();

		if (charge(event, chatStore) == false)
		{
			return;
		}

		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			String type = _("chatTypeLocal");
			final User onlineUser = ess.getUser(onlinePlayer);
			//TODO: remove reference to op 
			if (onlineUser.isIgnoredPlayer(sender.getName()) && !sender.isOp())
			{
				continue;
			}
			if (!onlineUser.equals(sender))
			{
				if (onlineUser.isAuthorized("essentials.chat.spy"))
				{
					type = type.concat(_("chatTypeSpy"));
				}
				else
				{
					final Location playerLoc = onlineUser.getLocation();
					if (playerLoc.getWorld() != world)
					{
						continue;
					}
					final double delta = playerLoc.distanceSquared(loc);
					if (delta > chatStore.getRadius())
					{
						continue;
					}
				}
			}

			String message = String.format(event.getFormat(), type.concat(sender.getDisplayName()), event.getMessage());
			for (IEssentialsChatListener listener : listeners.values())
			{
				message = listener.modifyMessage(event, onlinePlayer, message);
			}
			onlineUser.sendMessage(message);
		}
	}
}
